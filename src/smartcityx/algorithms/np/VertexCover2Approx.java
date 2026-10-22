package smartcityx.algorithms.np;

import smartcityx.core.MyArrayList;
import smartcityx.core.Pair;

/**
 * 2-Approximation Algorithm for Minimum Vertex Cover via Maximal Matching.
 * Guarantees:
 *   |C_approx| <= 2 * |C_opt|
 *
 * Used for placing surveillance cameras, air quality monitors, or emergency sirens
 * such that every road segment / corridor is monitored with at most 2x optimal cost.
 * Time Complexity: O(V + E).
 */
public class VertexCover2Approx {

    public static class ApproxCoverResult {
        public final MyArrayList<Integer> coverVertices;
        public final MyArrayList<Pair<Integer, Integer>> maximalMatchingEdges;
        public final double approximationFactor;

        public ApproxCoverResult(MyArrayList<Integer> coverVertices,
                                 MyArrayList<Pair<Integer, Integer>> maximalMatchingEdges) {
            this.coverVertices = coverVertices;
            this.maximalMatchingEdges = maximalMatchingEdges;
            this.approximationFactor = 2.0;
        }
    }

    public static ApproxCoverResult computeCover(int numVertices, MyArrayList<Pair<Integer, Integer>> edges) {
        boolean[] inCover = new boolean[numVertices];
        MyArrayList<Pair<Integer, Integer>> matching = new MyArrayList<>();

        // Greedily pick unmatched edges and add both endpoints to cover
        for (int i = 0; i < edges.size(); i++) {
            Pair<Integer, Integer> edge = edges.get(i);
            int u = edge.first;
            int v = edge.second;

            if (!inCover[u] && !inCover[v]) {
                inCover[u] = true;
                inCover[v] = true;
                matching.add(edge);
            }
        }

        MyArrayList<Integer> coverList = new MyArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            if (inCover[i]) {
                coverList.add(i);
            }
        }

        return new ApproxCoverResult(coverList, matching);
    }
}
