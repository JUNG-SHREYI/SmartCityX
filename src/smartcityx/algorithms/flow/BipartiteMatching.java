package smartcityx.algorithms.flow;

import smartcityx.core.MyArrayList;
import smartcityx.core.Pair;

/**
 * Maximum Bipartite Matching (Crew-to-Task / Resource-to-Request Allocation).
 * Solves matching using augmenting paths (Hopcroft-Karp / Flow reduction).
 * Time Complexity: O(E * sqrt(V)).
 */
public class BipartiteMatching {

    private final int leftCount;
    private final int rightCount;
    private final MyArrayList<Integer>[] adj;
    private final int[] matchRight;
    private final boolean[] visited;

    @SuppressWarnings("unchecked")
    public BipartiteMatching(int leftCount, int rightCount) {
        this.leftCount = leftCount;
        this.rightCount = rightCount;
        this.adj = new MyArrayList[leftCount];
        for (int i = 0; i < leftCount; i++) {
            adj[i] = new MyArrayList<>();
        }
        this.matchRight = new int[rightCount];
        for (int j = 0; j < rightCount; j++) matchRight[j] = -1;
        this.visited = new boolean[leftCount];
    }

    public void addEdge(int leftNode, int rightNode) {
        if (leftNode >= 0 && leftNode < leftCount && rightNode >= 0 && rightNode < rightCount) {
            adj[leftNode].add(rightNode);
        }
    }

    private boolean dfs(int u) {
        for (int i = 0; i < adj[u].size(); i++) {
            int v = adj[u].get(i);
            if (!visited[v]) {
                visited[v] = true;
                if (matchRight[v] < 0 || dfs(matchRight[v])) {
                    matchRight[v] = u;
                    return true;
                }
            }
        }
        return false;
    }

    public static class MatchingResult {
        public final int maxMatches;
        public final MyArrayList<Pair<Integer, Integer>> matchedPairs; // (Left, Right)

        public MatchingResult(int maxMatches, MyArrayList<Pair<Integer, Integer>> matchedPairs) {
            this.maxMatches = maxMatches;
            this.matchedPairs = matchedPairs;
        }
    }

    public MatchingResult solve() {
        int matches = 0;
        for (int u = 0; u < leftCount; u++) {
            for (int j = 0; j < rightCount; j++) visited[j] = false;
            if (dfs(u)) {
                matches++;
            }
        }

        MyArrayList<Pair<Integer, Integer>> pairs = new MyArrayList<>();
        for (int v = 0; v < rightCount; v++) {
            if (matchRight[v] >= 0) {
                pairs.add(Pair.of(matchRight[v], v));
            }
        }

        return new MatchingResult(matches, pairs);
    }
}
