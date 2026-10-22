package smartcityx.algorithms.flow;

import smartcityx.core.MyArrayList;

/**
 * Ford-Fulkerson algorithm using DFS for finding augmenting paths in residual network.
 */
public class FordFulkerson {

    public static double computeMaxFlow(int n, double[][] capacity, int source, int sink) {
        double[][] residual = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                residual[i][j] = capacity[i][j];
            }
        }

        double totalFlow = 0;
        boolean[] visited = new boolean[n];

        while (true) {
            for (int i = 0; i < n; i++) visited[i] = false;
            double pushed = dfs(source, sink, Double.POSITIVE_INFINITY, residual, visited, n);
            if (pushed <= 1e-9) break;
            totalFlow += pushed;
        }

        return totalFlow;
    }

    private static double dfs(int u, int sink, double flow, double[][] residual, boolean[] visited, int n) {
        if (u == sink) return flow;
        visited[u] = true;

        for (int v = 0; v < n; v++) {
            if (!visited[v] && residual[u][v] > 1e-9) {
                double currentPush = Math.min(flow, residual[u][v]);
                double pushed = dfs(v, sink, currentPush, residual, visited, n);
                if (pushed > 1e-9) {
                    residual[u][v] -= pushed;
                    residual[v][u] += pushed;
                    return pushed;
                }
            }
        }
        return 0;
    }
}
