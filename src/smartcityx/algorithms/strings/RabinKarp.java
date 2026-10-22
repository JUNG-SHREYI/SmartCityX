package smartcityx.algorithms.strings;

import smartcityx.core.MyArrayList;

/**
 * Rabin-Karp String Matching Algorithm using Horner's polynomial rolling hash.
 * Average Time Complexity: O(N + M). Worst Case: O(N * M) (mitigated via large prime modulo & full verification).
 * Space Complexity: O(1).
 */
public class RabinKarp {

    private static final int RADIX = 256;
    private static final long MOD = 1_000_000_007L;

    public static MyArrayList<Integer> search(String text, String pattern) {
        MyArrayList<Integer> matches = new MyArrayList<>();
        if (text == null || pattern == null || pattern.length() == 0 || pattern.length() > text.length()) {
            return matches;
        }

        int n = text.length();
        int m = pattern.length();

        long highPow = 1;
        for (int i = 0; i < m - 1; i++) {
            highPow = (highPow * RADIX) % MOD;
        }

        long patternHash = 0;
        long windowHash = 0;

        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * RADIX + pattern.charAt(i)) % MOD;
            windowHash = (windowHash * RADIX + text.charAt(i)) % MOD;
        }

        for (int i = 0; i <= n - m; i++) {
            if (patternHash == windowHash) {
                // Character by character verification to prevent false positives from hash collisions
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches.add(i);
                }
            }

            // Rolling hash update for next window
            if (i < n - m) {
                windowHash = (windowHash - text.charAt(i) * highPow) % MOD;
                if (windowHash < 0) windowHash += MOD;
                windowHash = (windowHash * RADIX + text.charAt(i + m)) % MOD;
            }
        }
        return matches;
    }
}
