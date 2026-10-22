# SmartCityX — Top 25 Viva & Oral Defense Questions

This cheatsheet provides exact answers to the most common, tricky, and concept-heavy viva questions asked by evaluators and professors during DSA Project Reviews.

---

## 📌 Module 1: String Algorithms (CO1)

### Q1: Why does KMP never move the text pointer $i$ backwards?
**Answer:** KMP uses the precomputed $\pi$ / LPS array. When a mismatch occurs at pattern index $j$, the LPS value $\pi[j-1]$ indicates the length of the longest proper prefix of the pattern that is also a suffix of the currently matched window. We can slide the pattern forward and continue matching from $j = \pi[j-1]$ without resetting $i$, achieving strictly linear $\mathcal{O}(N+M)$ comparisons.

### Q2: What are spurious hits in Rabin-Karp and how do you handle them?
**Answer:** A spurious hit occurs when two different substrings produce the same polynomial hash modulo $q$ due to a hash collision. In SmartCityX, whenever the window hash matches the pattern hash ($H_W = H_P$), we perform an exact $\mathcal{O}(M)$ character-by-character verification to eliminate false positives. Using a large prime like $10^9 + 7$ minimizes collisions.

### Q3: How does the Z-Algorithm achieve $\mathcal{O}(N)$ using the $[L, R]$ Z-box?
**Answer:** The Z-box $[L, R]$ stores the rightmost substring matching a prefix of string $S$. For any index $i \le R$, the corresponding offset in the prefix is $k = i - L$. If $Z[k] < R - i + 1$, we copy $Z[k]$ directly in $\mathcal{O}(1)$. Only when $Z[k] \ge R - i + 1$ do we perform additional character comparisons starting from $R+1$, updating the box $[L, R]$.

### Q4: How does Aho-Corasick match multiple keywords in one single pass?
**Answer:** Aho-Corasick combines a Trie of dictionary patterns with failure transitions computed via BFS (similar to KMP). If a character transition does not exist in the current node, it follows the failure link to the longest matching prefix node. It searches text in $\mathcal{O}(N + \text{matches})$ regardless of how many keywords are in the dictionary.

### Q5: What is Bittu's Algorithm and why is its dynamic update $\mathcal{O}(1)$?
**Answer:** Bittu's Algorithm maps substrings into a 65,536-dimensional bigram frequency space ($256 \times 256$). When sliding the window by 1 character, only one bigram leaves and one enters. The dot product and window magnitude squared $\|W\|^2$ are updated via $O(1)$ delta algebra:
$$\Delta(\text{dot}) = -P[\text{leave}] + P[\text{enter}], \quad \Delta(\|W\|^2) = -(2 \cdot c_{\text{oldLeave}} - 1) + (2 \cdot c_{\text{oldEnter}} + 1)$$
This executes on flat primitive arrays without heap allocation or garbage collection overhead.

---

## 📌 Module 2: Suffix Structures (CO2)

### Q6: Why is SA-IS preferred over standard Prefix Doubling for large-scale datasets?
**Answer:** Standard Prefix Doubling (Manber-Myers) runs in $\mathcal{O}(N \log^2 N)$ or $\mathcal{O}(N \log N)$ time. SA-IS (Suffix Array Induced Sorting) classifies suffixes as S-type or L-type, names Left-Most S-type (LMS) substrings, and induces suffix ranks in strictly linear $\mathcal{O}(N)$ time and $\mathcal{O}(N)$ working memory.

### Q7: Explain Kasai's theorem and why $\text{LCP}[\text{Rank}[i+1]] \ge \text{LCP}[\text{Rank}[i]] - 1$.
**Answer:** If suffix $i$ has an LCP of length $h$ with its lexicographical predecessor, dropping the first character from both produces suffix $i+1$ and a corresponding suffix that share a common prefix of length $h - 1$. Suffix $i+1$'s immediate predecessor in the suffix array must share at least that prefix. Thus, the comparison index $h$ decreases by at most 1 per step, bounding total comparisons to $\mathcal{O}(N)$.

### Q8: What is a Suffix Automaton (SAM) and what is its maximum state bound?
**Answer:** A Suffix Automaton is the minimal Deterministic Finite Automaton (DFA) that recognizes all substrings of a string. For a string of length $N$, SAM contains at most $2N - 1$ states and $3N - 4$ transitions, allowing any substring query of length $M$ to be resolved in $\mathcal{O}(M)$ time.

---

## 📌 Module 3: Advanced Dynamic Programming (CO3)

### Q9: How do you reconstruct the optimal alignment path in Levenshtein DP?
**Answer:** We backtrack from cell $(N, M)$ back to $(0, 0)$ in the 2D DP matrix. If $D[i, j] = D[i-1, j-1] + \text{cost}$, it corresponds to a Match or Substitution. If $D[i, j] = D[i-1, j] + 1$, it represents a Deletion. If $D[i, j] = D[i, j-1] + 1$, it represents an Insertion.

### Q10: Why does Bitmask DP for TSP run in $\mathcal{O}(2^N \cdot N^2)$ instead of brute-force $\mathcal{O}(N!)$?
**Answer:** Brute-force evaluates all $N!$ permutations. Bitmask DP memoizes subproblems using state $(\text{mask}, u)$, where `mask` is an integer whose $i$-th bit represents whether vertex $i$ has been visited, and $u$ is the current endpoint. There are $2^N \cdot N$ total states, each with $N$ transitions, resulting in $\mathcal{O}(2^N \cdot N^2)$ operations.

### Q11: What is the recurrence for Matrix-Chain Multiplication (MCM)?
**Answer:**
$$M[i, j] = \min_{i \le k < j} \left( M[i, k] + M[k+1, j] + p_{i-1} \cdot p_k \cdot p_j \right)$$
It computes the optimal split point $k$ for parenthesizing matrix chains in $\mathcal{O}(N^3)$ time and $\mathcal{O}(N^2)$ space.

---

## 📌 Module 4: Network Flow (CO4)

### Q12: Why is Edmonds-Karp $\mathcal{O}(V \cdot E^2)$ while standard Ford-Fulkerson can be non-polynomial?
**Answer:** Ford-Fulkerson using DFS can make tiny increments of flow and requires $\mathcal{O}(E \cdot |f_{\max}|)$ time. Edmonds-Karp uses BFS to select the shortest augmenting path (minimum number of edges). In Edmonds-Karp, the distance from source to any vertex in the residual network never decreases, and each edge can become critical at most $V/2$ times, strictly bounding total augmentations to $\mathcal{O}(V \cdot E)$ and total time to $\mathcal{O}(V \cdot E^2)$.

### Q13: How does Dinic's algorithm improve upon Edmonds-Karp?
**Answer:** Dinic constructs a Level Graph using BFS and pushes all possible Blocking Flows simultaneously using DFS with dead-end elimination before advancing to the next level graph. This reduces time to $\mathcal{O}(V^2 \cdot E)$ on general graphs and $\mathcal{O}(E \sqrt{V})$ on unit networks.

### Q14: How does Max-Flow Min-Cut isolate critical municipal bottlenecks?
**Answer:** By the Max-Flow Min-Cut theorem, $\max \text{flow}(s, t) = \min \text{cut}(S, T)$. After computing max flow, we identify all vertices reachable from $s$ in the residual graph (set $S$). Any edge $(u, v)$ with $u \in S$ and $v \notin S$ with positive original capacity is a critical cut bottleneck whose disruption will directly restrict flow.

---

## 📌 Module 5: NP-Completeness & Approximation (CO5)

### Q15: What are Unit Propagation and Pure Literal Elimination in DPLL SAT?
**Answer:**
* **Unit Propagation:** If a clause contains only one unassigned literal, that literal must evaluate to True to satisfy the clause.
* **Pure Literal Elimination:** If a variable appears with only one polarity across all clauses, assigning it that polarity immediately satisfies all containing clauses without risk of conflict.

### Q16: Outline the Karp reduction $3\text{-SAT} \le_p \text{CLIQUE}$.
**Answer:** For a 3-CNF formula with $k$ clauses:
1. Create 3 vertices per clause (one for each literal).
2. Connect two vertices with an edge if they belong to different clauses and are non-contradictory ($x_i$ and $\neg x_i$ are never connected).
3. The formula is satisfiable $\iff$ the graph contains a clique of size $k$.

### Q17: Why is Maximal Matching a 2-Approximation for Minimum Vertex Cover?
**Answer:**
1. A maximal matching $M$ consists of independent edges with disjoint endpoints.
2. Any valid vertex cover $C^*$ must select at least one endpoint from each edge in $M$, meaning $|C^*| \ge |M|$.
3. Our algorithm selects both endpoints of every edge in $M$, yielding $|C| = 2|M|$.
4. Therefore, $|C| = 2|M| \le 2|C^*|$, proving the approximation ratio $\le 2$.

---

## 📌 Module 6: Parallel & Randomized Algorithms (CO6)

### Q18: What is the core invariant of Reservoir Sampling (Algorithm R)?
**Answer:** For an unbounded stream where the total length $N$ is unknown, each incoming item at index $i \ge k$ is placed into the reservoir of size $k$ with probability $k/i$. By induction, every item seen so far has an exact uniform probability $P = k/N$ of being in the sample.

### Q19: What are the two phases of Blelloch's Parallel Prefix Scan?
**Answer:**
1. **Up-Sweep (Reduce Tree):** Traverses bottom-up to compute subtree sums in $\mathcal{O}(N)$ work and $\mathcal{O}(\log N)$ depth.
2. **Down-Sweep (Distribute Tree):** Replaces the root with 0 and propagates exclusive prefix sums downward, also in $\mathcal{O}(N)$ work and $\mathcal{O}(\log N)$ depth.

### Q20: Explain Brent's Theorem: $T_p \le (W - D)/p + D$.
**Answer:** If an algorithm has total work $W$ and span/depth $D$, scheduling its operations across $p$ parallel processors requires at most $T_p \le \frac{W - D}{p} + D \le \frac{W}{p} + D$ time steps. It defines the theoretical upper bound on speedup $S_p = W / T_p$.
