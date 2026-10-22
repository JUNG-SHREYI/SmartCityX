package smartcityx.algorithms.flow;

import smartcityx.core.MyArrayList;
import smartcityx.core.MyQueue;
import smartcityx.core.Pair;

/**
 * Max-Flow Min-Cut Theorem Analyzer.
 * Extracts the critical infrastructure cut edges (bottlenecks) separating source from sink.
 * Invariant: Max-Flow value equals Min-Cut capacity:
 *     max_flow(s, t) = min_cut(S, T)
 */
public class MaxFlowMinCut {

    public static class MinCutResult {
        public final double cutCapacity;
        public final boolean[] reachableFromSource;
        public final MyArrayList<Pair<Integer, Integer>> criticalCutEdges;

        public MinCutResult(double cutCapacity, boolean[] reachableFromSource,
                            MyArrayList<Pair<Integer, Integer>> criticalCutEdges) {
            this.cutCapacity = cutCapacity;
            this.reachableFromSource = reachableFromSource;
            this.criticalCutEdges = criticalCutEdges;
        }
    }

    public static MinCutResult findMinCut(int n, double[][] originalCapacity, int source, int sink) {
        EdmondsKarp.FlowResult flowRes = EdmondsKarp.computeMaxFlow(n, originalCapacity, source, sink);
        double[][] residual = flowRes.residualCapacity;

        // BFS to find all vertices reachable from source in the residual graph
        boolean[] reachable = new boolean[n];
        MyQueue<Integer> queue = new MyQueue<>();
        queue.enqueue(source);
        reachable[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            for (int v = 0; v < n; v++) {
                if (!reachable[v] && residual[u][v] > 1e-9) {
                    reachable[v] = true;
                    queue.enqueue(v);
                }
            }
        }

        // Cut edges are those from reachable (S) to non-reachable (T) with original capacity > 0
        MyArrayList<Pair<Integer, Integer>> cutEdges = new MyArrayList<>();
        double totalCutCap = 0;

        for (int u = 0; u < n; u++) {
            if (reachable[u]) {
                for (int v = 0; v < n; v++) {
                    if (!reachable[v] && originalCapacity[u][v] > 0) {
                        cutEdges.add(Pair.of(u, v));
                        totalCutCap += originalCapacity[u][v];
                    }
                }
            }
        }

        return new MinCutResult(totalCutCap, reachable, cutEdges);
    }
}
