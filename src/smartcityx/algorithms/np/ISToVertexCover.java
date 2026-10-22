package smartcityx.algorithms.np;

import smartcityx.core.MyArrayList;

/**
 * Polynomial-time Karp Reduction: INDEPENDENT SET <=p VERTEX COVER.
 * Fundamental Theorem:
 *   For any graph G = (V, E) and subset S subset V:
 *   S is an Independent Set in G <=> (V \ S) is a Vertex Cover in G.
 *
 * Invariant:
 *   G has an Independent Set of size k <=> G has a Vertex Cover of size (|V| - k).
 */
public class ISToVertexCover {

    public static class VertexCoverReductionResult {
        public final int originalGraphVertices;
        public final int targetIndependentSetSize;
        public final int targetVertexCoverSize; // |V| - k

        public VertexCoverReductionResult(int originalGraphVertices, int targetIndependentSetSize) {
            this.originalGraphVertices = originalGraphVertices;
            this.targetIndependentSetSize = targetIndependentSetSize;
            this.targetVertexCoverSize = originalGraphVertices - targetIndependentSetSize;
        }
    }

    public static VertexCoverReductionResult reduce(int numVertices, int k) {
        return new VertexCoverReductionResult(numVertices, k);
    }

    public static MyArrayList<Integer> deriveVertexCoverFromIS(int numVertices, MyArrayList<Integer> independentSet) {
        boolean[] inIS = new boolean[numVertices];
        for (int i = 0; i < independentSet.size(); i++) {
            inIS[independentSet.get(i)] = true;
        }

        MyArrayList<Integer> vertexCover = new MyArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            if (!inIS[i]) {
                vertexCover.add(i);
            }
        }
        return vertexCover;
    }
}
