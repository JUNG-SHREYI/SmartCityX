package smartcityx.algorithms.dp;

import smartcityx.core.MyArrayList;
import smartcityx.core.Pair;

/**
 * Levenshtein and Damerau-Levenshtein Dynamic Programming Distance.
 * Computes minimum edit distance (Insert, Delete, Substitute, Transpose) in O(N * M) time and space.
 * Exposes 2D DP matrix and step-by-step backtrace path for graphical visualization.
 */
public class LevenshteinDistance {

    public static class DpTraceResult {
        public final int distance;
        public final int[][] dpTable;
        public final String source;
        public final String target;
        public final MyArrayList<Pair<Integer, Integer>> backtracePath;
        public final MyArrayList<String> operations;

        public DpTraceResult(int distance, int[][] dpTable, String source, String target,
                             MyArrayList<Pair<Integer, Integer>> backtracePath, MyArrayList<String> operations) {
            this.distance = distance;
            this.dpTable = dpTable;
            this.source = source;
            this.target = target;
            this.backtracePath = backtracePath;
            this.operations = operations;
        }
    }

    public static int computeDistance(String s1, String s2) {
        return computeWithTrace(s1, s2).distance;
    }

    public static DpTraceResult computeWithTrace(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                int del = dp[i - 1][j] + 1;
                int ins = dp[i][j - 1] + 1;
                int sub = dp[i - 1][j - 1] + cost;

                int min = Math.min(del, Math.min(ins, sub));

                // Damerau adjacent transposition
                if (i > 1 && j > 1 && s1.charAt(i - 1) == s2.charAt(j - 2) && s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    min = Math.min(min, dp[i - 2][j - 2] + 1);
                }

                dp[i][j] = min;
            }
        }

        // Backtrack path from (n, m) to (0, 0)
        MyArrayList<Pair<Integer, Integer>> path = new MyArrayList<>();
        MyArrayList<String> ops = new MyArrayList<>();

        int currI = n;
        int currJ = m;
        path.add(Pair.of(currI, currJ));

        while (currI > 0 || currJ > 0) {
            if (currI > 0 && currJ > 0) {
                int cost = (s1.charAt(currI - 1) == s2.charAt(currJ - 1)) ? 0 : 1;
                if (dp[currI][currJ] == dp[currI - 1][currJ - 1] + cost) {
                    if (cost == 0) {
                        ops.add("MATCH '" + s1.charAt(currI - 1) + "'");
                    } else {
                        ops.add("SUBSTITUTE '" + s1.charAt(currI - 1) + "' -> '" + s2.charAt(currJ - 1) + "'");
                    }
                    currI--;
                    currJ--;
                    path.add(Pair.of(currI, currJ));
                    continue;
                }
            }

            if (currI > 0 && dp[currI][currJ] == dp[currI - 1][currJ] + 1) {
                ops.add("DELETE '" + s1.charAt(currI - 1) + "'");
                currI--;
                path.add(Pair.of(currI, currJ));
            } else if (currJ > 0 && dp[currI][currJ] == dp[currI][currJ - 1] + 1) {
                ops.add("INSERT '" + s2.charAt(currJ - 1) + "'");
                currJ--;
                path.add(Pair.of(currI, currJ));
            } else {
                currI--;
                currJ--;
                path.add(Pair.of(currI, currJ));
            }
        }

        return new DpTraceResult(dp[n][m], dp, s1, s2, path, ops);
    }
}
