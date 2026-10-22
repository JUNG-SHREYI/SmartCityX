package smartcityx.algorithms.dp;

/**
 * Matrix-Chain Multiplication (MCM) Dynamic Programming Algorithm.
 * Optimizes the sequence of spatial matrix transformations and batch analytics pipelines in O(N^3).
 * Recurrence:
 *     M[i, j] = min_{i <= k < j} ( M[i, k] + M[k+1, j] + p_{i-1} * p_k * p_j )
 */
public class MatrixChainMultiplication {

    public static class McmResult {
        public final long minScalarMultiplications;
        public final long[][] costTable;
        public final int[][] splitTable;
        public final String optimalParenthesization;

        public McmResult(long minScalarMultiplications, long[][] costTable, int[][] splitTable, String optimalParenthesization) {
            this.minScalarMultiplications = minScalarMultiplications;
            this.costTable = costTable;
            this.splitTable = splitTable;
            this.optimalParenthesization = optimalParenthesization;
        }
    }

    /**
     * Solves MCM for matrix dimensions array p of length n+1 representing n matrices: A1..(p0xp1), A2..(p1xp2)...
     */
    public static McmResult solve(int[] p) {
        int n = p.length - 1; // number of matrices
        long[][] m = new long[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        for (int i = 1; i <= n; i++) {
            m[i][i] = 0;
        }

        for (int l = 2; l <= n; l++) { // l is chain length
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                m[i][j] = Long.MAX_VALUE;

                for (int k = i; k <= j - 1; k++) {
                    long q = m[i][k] + m[k + 1][j] + (long) p[i - 1] * p[k] * p[j];
                    if (q < m[i][j]) {
                        m[i][j] = q;
                        s[i][j] = k;
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        buildOptimalParens(s, 1, n, sb);

        return new McmResult(m[1][n], m, s, sb.toString());
    }

    private static void buildOptimalParens(int[][] s, int i, int j, StringBuilder sb) {
        if (i == j) {
            sb.append("A").append(i);
        } else {
            sb.append("(");
            buildOptimalParens(s, i, s[i][j], sb);
            sb.append(" × ");
            buildOptimalParens(s, s[i][j] + 1, j, sb);
            sb.append(")");
        }
    }
}
