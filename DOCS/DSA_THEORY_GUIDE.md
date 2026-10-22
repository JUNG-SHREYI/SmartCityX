# SmartCityX — DSA Comprehensive Theory & Asymptotic Analysis Guide

This guide details the mathematical foundations, recurrence relations, complexity proofs, and algorithmic mechanics for all 17 algorithms implemented in the **SmartCityX** platform.

---

## 📑 Table of Contents
1. [Module 1 (CO1): String Algorithms](#module-1-co1-string-algorithms)
2. [Module 2 (CO2): Suffix Structures & Document Similarity](#module-2-co2-suffix-structures--document-similarity)
3. [Module 3 (CO3): Advanced Dynamic Programming](#module-3-co3-advanced-dynamic-programming)
4. [Module 4 (CO4): Network Flow & Matching](#module-4-co4-network-flow--matching)
5. [Module 5 (CO5): NP-Completeness & Approximation](#module-5-co5-np-completeness--approximation)
6. [Module 6 (CO6): Randomized & Parallel Algorithms](#module-6-co6-randomized--parallel-algorithms)

---

## Module 1 (CO1): String Algorithms

### 1. Knuth-Morris-Pratt (KMP) Algorithm
* **Purpose in SmartCityX:** Searching citizen incident logs, civic service tickets, and infrastructure asset codes.
* **Mathematical Invariant:** The Longest Proper Prefix which is also a Suffix ($\pi$ / LPS array) is defined as:
  $$\pi[i] = \max \{ k : k < i+1 \text{ and } P[0..k-1] = P[i-k+1..i] \}$$
* **Time Complexity:** $\mathcal{O}(N + M)$ total time. Preprocessing pattern takes $\mathcal{O}(M)$ and scanning text takes $\mathcal{O}(N)$.
* **Key Invariant Proof:** The text pointer $i$ strictly increases by 1 on each iteration. The pattern pointer $j$ advances at most $N$ times and decrements at most $N$ times via $\pi[j-1]$. Hence, total character comparisons are bounded by $2N$, proving $\mathcal{O}(N)$ text search without pointer regression.

### 2. Z-Algorithm
* **Purpose in SmartCityX:** Identifying recurring anomaly patterns and duplicate telemetry error descriptions.
* **Definition:** $Z[i]$ is the length of the longest substring starting from $S[i]$ that matches the prefix of $S$.
* **Time Complexity:** $\mathcal{O}(N)$.
* **Sliding Window Invariant:** Maintains the interval $[L, R]$ (the $Z$-box) representing the rightmost substring matching a prefix. For index $i \le R$, if $Z[i - L] < R - i + 1$, then $Z[i] = Z[i - L]$ without extra comparisons; otherwise, comparisons continue from $R+1$ and $[L, R]$ is updated.

### 3. Rabin-Karp Rolling Hash
* **Purpose in SmartCityX:** Fast multi-pattern search across utility asset IDs and traffic zone tags.
* **Mathematical Formulation:** Horner's polynomial rolling hash modulo a large prime $q = 10^9 + 7$ with base $d = 256$:
  $$H(S[i..i+m-1]) = \left( \sum_{k=0}^{m-1} S[i+k] \cdot d^{m-1-k} \right) \bmod q$$
* **$O(1)$ Window Update:**
  $$H_{i+1} = \left( (H_i - S[i] \cdot d^{m-1}) \cdot d + S[i+m] \right) \bmod q$$
* **Complexity:** Average time $\mathcal{O}(N + M)$, Space $\mathcal{O}(1)$.

### 4. Aho-Corasick Multi-Pattern Automaton
* **Purpose in SmartCityX:** Real-time multi-keyword emergency triage (e.g. "FIRE", "GAS_LEAK", "WATER_BURST", "GRID_FAIL", "HAZMAT").
* **Construction:** Builds a Trie over dictionary patterns, establishes BFS failure transitions $\text{fail}(u)$, and dictionary output links.
* **Complexity:** Time $\mathcal{O}(N + \sum |P_i| + \text{matches})$, Space $\mathcal{O}(\sum |P_i| \cdot |\Sigma|)$.

### 5. Bittu's Algorithm (Invention Algorithm)
* **Purpose in SmartCityX:** Ultra-fast rolling bigram cosine vector screening on municipal log streams with zero GC allocations.
* **Vector Dimension:** $256 \times 256 = 65,536$ state space.
* **$O(1)$ Delta Algebra:**
  $$\Delta(\text{dot}) = -P[\text{leavingKey}] + P[\text{enteringKey}]$$
  $$\Delta(\|W\|^2) = -(2 \cdot c_{\text{oldLeave}} - 1) + (2 \cdot c_{\text{oldEnter}} + 1)$$
* **Screening Condition:** When $\cos(W, P) \ge 0.999999999$, an exact character verification step confirms the match.

---

## Module 2 (CO2): Suffix Structures & Document Similarity

### 1. Suffix Array & SA-IS Algorithm
* **Purpose in SmartCityX:** Indexing large municipal operational bylaws, zoning plans, and environmental frameworks.
* **SA-IS Mechanics:** Classifies suffixes as S-type ($S[i] < S[i+1]$) or L-type ($S[i] > S[i+1]$), identifies Left-Most S-type (LMS) characters, bucket sorts LMS substrings, solves the recursively reduced problem, and induces all suffix ranks in strictly linear $\mathcal{O}(N)$ time and space.

### 2. Kasai's LCP Array Algorithm
* **Purpose in SmartCityX:** Computing Longest Common Prefix lengths to detect duplicated civic grievances and common complaint templates.
* **Key Invariant:** For two consecutive suffixes in original text index $i$ and $i+1$:
  $$\text{LCP}[\text{Rank}[i+1]] \ge \text{LCP}[\text{Rank}[i]] - 1$$
* **Complexity:** $\mathcal{O}(N)$ time and $\mathcal{O}(N)$ space.

### 3. Suffix Automaton (SAM / DAWG)
* **Purpose in SmartCityX:** Minimal Deterministic Acyclic Word Graph containing all substrings of city documents.
* **Properties:** Number of states $\le 2N - 1$, Number of transitions $\le 3N - 4$. Supports $\mathcal{O}(M)$ substring queries and distinct substring counts in linear time.

---

## Module 3 (CO3): Advanced Dynamic Programming

### 1. Levenshtein & Damerau-Levenshtein Edit Distance
* **Purpose in SmartCityX:** Typo-tolerant citizen search portal (e.g. auto-correcting "WATR_BURST" $\to$ "WATER_BURST").
* **Recurrence Relation:**
  $$D[i, j] = \min \begin{cases}
  D[i-1, j] + 1 & (\text{Deletion}) \\
  D[i, j-1] + 1 & (\text{Insertion}) \\
  D[i-1, j-1] + \text{cost} & (\text{Substitution / Match}) \\
  D[i-2, j-2] + 1 & (\text{Transposition if } S_1[i]=S_2[j-1] \land S_1[i-1]=S_2[j])
  \end{cases}$$
* **Complexity:** $\mathcal{O}(N \cdot M)$ time and space.

### 2. Bitmask DP (Traveling Salesman Problem - Emergency Dispatch)
* **Purpose in SmartCityX:** Optimal emergency vehicle dispatch visiting $N$ disaster/incident sectors and returning to base.
* **State Definition:** $\text{DP}[\text{mask}][u]$ = minimum travel distance to visit all vertices in bitmask ending at vertex $u$.
* **Recurrence:**
  $$\text{DP}[\text{mask} \mid (1 \ll v)][v] = \min_{u \in \text{mask}} \left( \text{DP}[\text{mask}][u] + \text{dist}[u][v] \right)$$
* **Complexity:** $\mathcal{O}(2^N \cdot N^2)$ time, $\mathcal{O}(2^N \cdot N)$ space.

### 3. Matrix-Chain Multiplication (MCM)
* **Purpose in SmartCityX:** Optimal parenthesization order for chained spatial analytics and municipal sensor transformation matrices.
* **Recurrence:**
  $$M[i, j] = \min_{i \le k < j} \left( M[i, k] + M[k+1, j] + p_{i-1} \cdot p_k \cdot p_j \right)$$
* **Complexity:** $\mathcal{O}(N^3)$ time, $\mathcal{O}(N^2)$ space.

---

## Module 4 (CO4): Network Flow & Matching

### 1. Edmonds-Karp & Dinic's Algorithm
* **Purpose in SmartCityX:** Modeling city-wide water pipelines, power transmission lines, and evacuation corridors.
* **Edmonds-Karp:** Finds shortest augmenting paths via BFS in $\mathcal{O}(V \cdot E^2)$.
* **Dinic's Algorithm:** Builds a BFS Level Graph and finds Blocking Flows via DFS in $\mathcal{O}(V^2 \cdot E)$ (and $\mathcal{O}(E \sqrt{V})$ on unit capacity graphs).

### 2. Max-Flow Min-Cut Theorem
* **Purpose in SmartCityX:** Isolating critical utility grid bottlenecks whose disruption disconnects municipal sectors.
* **Theorem:** In any network, the maximum flow from source $s$ to sink $t$ equals the minimum capacity among all $s-t$ cuts:
  $$\max \text{flow}(s, t) = \min \text{capacity}(S, T)$$
* **Extraction:** Find all reachable vertices $S$ from $s$ in the residual graph; any original edge $(u, v)$ where $u \in S, v \notin S$ is a critical cut edge.

### 3. Bipartite Matching (Crew-to-Incident Dispatch)
* **Purpose in SmartCityX:** Optimal 1-to-1 matching of available specialized response units to high-priority emergency incidents.
* **Reduction:** Formulated via augmenting paths in bipartite graph $G = (L \cup R, E)$ in $\mathcal{O}(E \sqrt{V})$ time.

---

## Module 5 (CO5): NP-Completeness & Approximation

### 1. DPLL 3-SAT Solver
* **Purpose in SmartCityX:** Solving municipal planning conflicts, traffic light phase mutual exclusion, and crew shift constraints.
* **Core Heuristics:** Unit Propagation (assigning literals from single-literal clauses) and recursive backtracking.

### 2. Karp Reduction Chain ($3\text{-SAT} \le_p \text{CLIQUE} \le_p \text{IS} \le_p \text{VC}$)
* **$3\text{-SAT} \le_p \text{CLIQUE}$:** Constructs 3 vertices per clause. Edges connect literals from different clauses that are non-contradictory. Formula is satisfiable $\iff G$ has a $k$-clique.
* **$\text{CLIQUE} \le_p \text{INDEPENDENT-SET}$:** On complement graph $\overline{G}$, a $k$-clique in $G$ becomes an Independent Set of size $k$ in $\overline{G}$.
* **$\text{INDEPENDENT-SET} \le_p \text{VERTEX-COVER}$:** Fundamental theorem states that $S \subseteq V$ is an Independent Set $\iff (V \setminus S)$ is a Vertex Cover of size $|V| - k$.

### 3. Vertex Cover 2-Approximation (Maximal Matching)
* **Purpose in SmartCityX:** Placing minimum surveillance cameras or air-quality sensors to cover all road segments.
* **Algorithm:** Repeatedly select an edge with unselected endpoints, add both endpoints to cover $C$, and remove incident edges.
* **Approximation Ratio Proof:** Any valid cover $C^*$ must pick at least one endpoint from each edge in matching $M$ ($|C^*| \ge |M|$). Since our algorithm selects $2|M|$ vertices:
  $$|C| = 2|M| \le 2|C^*| \implies \text{Approximation Ratio} \le 2$$

---

## Module 6 (CO6): Randomized & Parallel Algorithms

### 1. Randomized QuickSort
* **Purpose in SmartCityX:** Ranking citizen service backlogs and municipal maintenance work orders.
* **Mechanism:** Randomly uniformly selects pivot to avoid $\mathcal{O}(N^2)$ worst-case on presorted arrays, guaranteeing expected $\mathcal{O}(N \log N)$ time.

### 2. Reservoir Sampling (Algorithm R)
* **Purpose in SmartCityX:** Uniformly sampling $k$ representative readings from an unbounded, continuous stream of IoT sensor observations.
* **Invariant Proof:** At step $n \ge k$, the $n$-th item is selected with probability $k/n$. An existing item remains in the reservoir with probability:
  $$P(\text{remains}) = 1 - \frac{k}{n} \cdot \frac{1}{k} = \frac{n-1}{n}$$
  By induction, every item observed so far has exact probability $\frac{k}{N}$ of being present in the sample.

### 3. Miller-Rabin Primality Test
* **Purpose in SmartCityX:** Cryptographic key verification and tamper-proof telemetry token generation in municipal SCADA networks.
* **Error Bound:** For $k$ random independent witnesses, the probability of false prime declaration is $< \left(\frac{1}{4}\right)^k$.

### 4. Blelloch Parallel Prefix Scan
* **Purpose in SmartCityX:** Computing cumulative energy and water consumption tallies in parallel across thousands of smart meters.
* **Phases:**
  1. **Up-Sweep (Reduce Tree):** Aggregates subtree sums ($\mathcal{O}(N)$ work, $\mathcal{O}(\log N)$ depth).
  2. **Down-Sweep (Distribute Tree):** Propagates exclusive prefix sums down from root ($\mathcal{O}(N)$ work, $\mathcal{O}(\log N)$ depth).

### 5. Brent's Theorem Evaluation Engine
* **Mathematical Bound:** For an algorithm with total work $W$ and span (depth) $D$ executed across $p$ processing cores:
  $$T_p \le \frac{W - D}{p} + D \le \frac{W}{p} + D$$
* **Speedup & Efficiency:**
  $$S_p = \frac{W}{T_p}, \quad E_p = \frac{S_p}{p}$$
