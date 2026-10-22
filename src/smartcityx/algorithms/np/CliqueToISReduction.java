package smartcityx.algorithms.np;

import smartcityx.core.MyArrayList;
import smartcityx.core.Pair;

/**
 * Polynomial-time Karp Reduction: CLIQUE <=p INDEPENDENT SET.
 * Given graph G and target clique size k:
 *   - Construct complement graph G' = (V, E') where (u, v) in E' <=> (u, v) not in E.
 * Invariant: G has a clique of size k <=> G' has an independent set of size k.
 */
public class CliqueToISReduction {

    public static class ComplementGraph {
        public final int numVertices;
        public final boolean[][] adjMatrix;
        public final MyArrayList<Pair<Integer, Integer>> edges;

        public ComplementGraph(int numVertices, boolean[][] adjMatrix, MyArrayList<Pair<Integer, Integer>> edges) {
            this.numVertices = numVertices;
            this.adjMatrix = adjMatrix;
            this.edges = edges;
        }
    }

    public static ComplementGraph reduce(int numVertices, boolean[][] origAdjMatrix) {
        boolean[][] compAdj = new boolean[numVertices][numVertices];
        MyArrayList<Pair<Integer, Integer>> compEdges = new MyArrayList<>();

        for (int i = 0; i < numVertices; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                if (!origAdjMatrix[i][j]) {
                    compAdj[i][j] = true;
                    compAdj[j][i] = true;
                    compEdges.add(Pair.of(i, j));
                }
            }
        }

        return new ComplementGraph(numVertices, compAdj, compEdges);
    }
}
