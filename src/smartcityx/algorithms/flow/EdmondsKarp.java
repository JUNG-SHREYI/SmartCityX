package smartcityx.algorithms.flow;

import smartcityx.core.MyArrayList;
import smartcityx.core.MyQueue;

/**
 * Ford-Fulkerson and Edmonds-Karp Max Flow Algorithm.
 * Uses BFS (Edmonds-Karp) for shortest augmenting path to guarantee O(V * E^2) convergence.
 */
public class EdmondsKarp {

    public static class FlowEdge {
        public final int u;
        public final int v;
        public final double capacity;
        public double flow;

        public FlowEdge(int u, int v, double capacity) {
            this.u = u;
            this.v = v;
            this.capacity = capacity;
            this.flow = 0;
        }

        public double remainingCapacity() {
            return capacity - flow;
        }
    }

    public static class FlowResult {
        public final double maxFlow;
        public final double[][] residualCapacity;
        public final MyArrayList<FlowEdge> edges;

        public FlowResult(double maxFlow, double[][] residualCapacity, MyArrayList<FlowEdge> edges) {
            this.maxFlow = maxFlow;
            this.residualCapacity = residualCapacity;
            this.edges = edges;
        }
    }

    public static FlowResult computeMaxFlow(int numVertices, double[][] capacityMatrix, int source, int sink) {
        double[][] residual = new double[numVertices][numVertices];
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                residual[i][j] = capacityMatrix[i][j];
            }
        }

        int[] parent = new int[numVertices];
        double totalFlow = 0;

        while (bfsAugmentingPath(numVertices, residual, source, sink, parent)) {
            double pathFlow = Double.POSITIVE_INFINITY;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residual[u][v]);
            }

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }

            totalFlow += pathFlow;
        }

        MyArrayList<FlowEdge> edges = new MyArrayList<>();
        for (int u = 0; u < numVertices; u++) {
            for (int v = 0; v < numVertices; v++) {
                if (capacityMatrix[u][v] > 0) {
                    FlowEdge e = new FlowEdge(u, v, capacityMatrix[u][v]);
                    e.flow = capacityMatrix[u][v] - residual[u][v];
                    if (e.flow < 0) e.flow = 0;
                    edges.add(e);
                }
            }
        }

        return new FlowResult(totalFlow, residual, edges);
    }

    private static boolean bfsAugmentingPath(int n, double[][] residual, int source, int sink, int[] parent) {
        boolean[] visited = new boolean[n];
        MyQueue<Integer> queue = new MyQueue<>();
        queue.enqueue(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int u = queue.dequeue();

            for (int v = 0; v < n; v++) {
                if (!visited[v] && residual[u][v] > 1e-9) {
                    parent[v] = u;
                    visited[v] = true;
                    if (v == sink) return true;
                    queue.enqueue(v);
                }
            }
        }
        return false;
    }
}
