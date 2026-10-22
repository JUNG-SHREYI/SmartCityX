package smartcityx.algorithms.parallel_random;

/**
 * Blelloch Work-Efficient Parallel Prefix Scan (Exclusive Prefix Sum).
 * Algorithmic phases:
 *   1. Up-Sweep (Reduce tree): O(N) work, O(log N) depth.
 *   2. Down-Sweep (Distribute tree): O(N) work, O(log N) depth.
 * Total Work: O(N), Total Span (Depth): O(log N).
 */
public class BlellochScan {

    /**
     * Computes exclusive prefix sum of input array in-place.
     * Note: Pads array to next power of 2 for tree traversal.
     */
    public static long[] exclusiveScan(long[] input) {
        if (input == null || input.length == 0) return new long[0];
        int originalLen = input.length;

        // Pad to next power of 2
        int n = 1;
        while (n < originalLen) n *= 2;

        long[] tree = new long[n];
        System.arraycopy(input, 0, tree, 0, originalLen);

        // 1. Up-Sweep (Reduce) Phase
        for (int d = 0; d < log2(n); d++) {
            int step = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < n; k += step) {
                tree[k + step - 1] += tree[k + half - 1];
            }
        }

        // Set root to identity (0 for addition)
        tree[n - 1] = 0;

        // 2. Down-Sweep Phase
        for (int d = log2(n) - 1; d >= 0; d--) {
            int step = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < n; k += step) {
                long t = tree[k + half - 1];
                tree[k + half - 1] = tree[k + step - 1];
                tree[k + step - 1] += t;
            }
        }

        long[] result = new long[originalLen];
        System.arraycopy(tree, 0, result, 0, originalLen);
        return result;
    }

    private static int log2(int n) {
        int r = 0;
        while ((1 << r) < n) r++;
        return r;
    }
}
