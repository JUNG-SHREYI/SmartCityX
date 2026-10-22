package smartcityx.algorithms.np;

import smartcityx.core.MyArrayList;
import smartcityx.core.Pair;

/**
 * Polynomial-time Karp Reduction: 3-SAT <=p CLIQUE.
 * Given a 3-CNF formula with k clauses, constructs graph G where:
 *   - Each clause creates 3 vertices (one per literal).
 *   - Edges connect pairs of literals from DIFFERENT clauses that are NOT contradictory (e.g., xi and ¬xi have no edge).
 * Invariant: Formula is satisfiable if and only if G contains a clique of size k.
 */
public class SatToCliqueReduction {

    public static class CliqueNode {
        public final int id;
        public final int clauseIndex;
        public final int literal; // e.g. +1 for x1, -2 for ¬x2

        public CliqueNode(int id, int clauseIndex, int literal) {
            this.id = id;
            this.clauseIndex = clauseIndex;
            this.literal = literal;
        }

        @Override
        public String toString() {
            String s = (literal < 0 ? "¬x" + (-literal) : "x" + literal);
            return "v" + id + "(" + s + ", C" + clauseIndex + ")";
        }
    }

    public static class ReducedCliqueGraph {
        public final int targetCliqueSize; // k (number of clauses)
        public final MyArrayList<CliqueNode> nodes;
        public final boolean[][] adjMatrix;
        public final MyArrayList<Pair<Integer, Integer>> edges;

        public ReducedCliqueGraph(int targetCliqueSize, MyArrayList<CliqueNode> nodes,
                                  boolean[][] adjMatrix, MyArrayList<Pair<Integer, Integer>> edges) {
            this.targetCliqueSize = targetCliqueSize;
            this.nodes = nodes;
            this.adjMatrix = adjMatrix;
            this.edges = edges;
        }
    }

    public static ReducedCliqueGraph reduce(DpllSatSolver.SatFormula formula) {
        int k = formula.clauses.size();
        MyArrayList<CliqueNode> nodes = new MyArrayList<>();

        int nodeId = 0;
        for (int cIdx = 0; cIdx < k; cIdx++) {
            DpllSatSolver.Clause c = formula.clauses.get(cIdx);
            for (int lit : c.literals) {
                nodes.add(new CliqueNode(nodeId++, cIdx, lit));
            }
        }

        int totalNodes = nodes.size();
        boolean[][] adj = new boolean[totalNodes][totalNodes];
        MyArrayList<Pair<Integer, Integer>> edgeList = new MyArrayList<>();

        for (int i = 0; i < totalNodes; i++) {
            CliqueNode u = nodes.get(i);
            for (int j = i + 1; j < totalNodes; j++) {
                CliqueNode v = nodes.get(j);

                // Connect if in DIFFERENT clauses and NOT contradictory
                if (u.clauseIndex != v.clauseIndex && u.literal != -v.literal) {
                    adj[i][j] = true;
                    adj[j][i] = true;
                    edgeList.add(Pair.of(i, j));
                }
            }
        }

        return new ReducedCliqueGraph(k, nodes, adj, edgeList);
    }
}
