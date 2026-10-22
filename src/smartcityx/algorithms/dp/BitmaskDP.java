package smartcityx.algorithms.dp;

import smartcityx.core.MyArrayList;

/**
 * Bitmask Dynamic Programming for Emergency Vehicle & Maintenance Crew Routing (TSP formulation).
 * Finds shortest round-trip visiting all incident nodes.
 * Time Complexity: O(2^N * N^2). Space Complexity: O(2^N * N).
 */
public class BitmaskDP {

    public static class RouteResult {
        public final double minCost;
        public final MyArrayList<Integer> route;

        public RouteResult(double minCost, MyArrayList<Integer> route) {
            this.minCost = minCost;
            this.route = route;
        }
    }

    public static RouteResult solveTSP(double[][] dist) {
        int n = dist.length;
        if (n == 0) return new RouteResult(0, new MyArrayList<>());
        if (n == 1) {
            MyArrayList<Integer> r = new MyArrayList<>();
            r.add(0); r.add(0);
            return new RouteResult(0, r);
        }

        int numStates = 1 << n;
        double[][] dp = new double[numStates][n];
        int[][] parent = new int[numStates][n];

        for (int mask = 0; mask < numStates; mask++) {
            for (int i = 0; i < n; i++) {
                dp[mask][i] = Double.POSITIVE_INFINITY;
                parent[mask][i] = -1;
            }
        }

        // Start at node 0
        dp[1][0] = 0;

        for (int mask = 1; mask < numStates; mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0 || dp[mask][u] == Double.POSITIVE_INFINITY) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) != 0) continue; // Already visited

                    int nextMask = mask | (1 << v);
                    double newDist = dp[mask][u] + dist[u][v];
                    if (newDist < dp[nextMask][v]) {
                        dp[nextMask][v] = newDist;
                        parent[nextMask][v] = u;
                    }
                }
            }
        }

        // Return to start (node 0) from all visited (mask = (1 << n) - 1)
        int finalMask = (1 << n) - 1;
        double minTotal = Double.POSITIVE_INFINITY;
        int lastNode = -1;

        for (int i = 1; i < n; i++) {
            if (dp[finalMask][i] != Double.POSITIVE_INFINITY) {
                double total = dp[finalMask][i] + dist[i][0];
                if (total < minTotal) {
                    minTotal = total;
                    lastNode = i;
                }
            }
        }

        // Reconstruct path
        MyArrayList<Integer> path = new MyArrayList<>();
        if (lastNode == -1) {
            return new RouteResult(Double.POSITIVE_INFINITY, path);
        }

        path.add(0); // return to start
        int currNode = lastNode;
        int currMask = finalMask;

        while (currNode != 0 && currNode != -1) {
            path.add(0, currNode);
            int prev = parent[currMask][currNode];
            currMask ^= (1 << currNode);
            currNode = prev;
        }
        path.add(0, 0); // initial start

        return new RouteResult(minTotal, path);
    }
}
