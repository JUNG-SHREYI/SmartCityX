package smartcityx.algorithms.strings;

import smartcityx.core.MyArrayList;

/**
 * =========================================================================
 * BITTU'S ALGORITHM (INVENTION ALGORITHM)
 * High-Performance Rolling Bigram Cosine Vector Matcher with O(1) Dynamic Algebra.
 * =========================================================================
 *
 * Mathematical Foundations:
 * -------------------------
 * Maps text and pattern windows into a 65,536-dimensional bigram frequency space:
 *     Vector Key:  K(c1, c2) = (c1 << 8) | c2  (for Extended ASCII: 0..255)
 *
 * Cosine Similarity Invariant:
 *     Cos(W, P) = (W · P) / ( ||W|| * ||P|| )
 *
 * When sliding the window by 1 character:
 *     - Leaving Bigram  (text[start], text[start+1]) with previous frequency count c_old:
 *           dot      <- dot - P[leavingKey]
 *           ||W||^2  <- ||W||^2 - (2 * c_old - 1)
 *           W[leavingKey] <- c_old - 1
 *
 *     - Entering Bigram (text[start+m-1], text[start+m]) with previous frequency count c_old:
 *           dot      <- dot + P[enteringKey]
 *           ||W||^2  <- ||W||^2 + (2 * c_old + 1)
 *           W[enteringKey] <- c_old + 1
 *
 * Both dot product and window magnitude squared are updated in pure O(1) arithmetic
 * on a flat primitive int[] array without heap allocations or GC pressure.
 */
public class BittuAlgorithm {

    private static final int ALPHABET = 256;
    private static final int TABLE_SIZE = ALPHABET * ALPHABET; // 65,536 dimensions
    private static final double SIMILARITY_THRESHOLD = 0.999999999;

    private static int key(char c1, char c2) {
        return ((c1 & 0xFF) << 8) | (c2 & 0xFF);
    }

    private static int[] bigramCounts(String s) {
        int[] counts = new int[TABLE_SIZE];
        for (int i = 0; i + 1 < s.length(); i++) {
            counts[key(s.charAt(i), s.charAt(i + 1))]++;
        }
        return counts;
    }

    private static boolean naiveVerify(String text, int start, String pattern) {
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(start + i) != pattern.charAt(i)) return false;
        }
        return true;
    }

    /**
     * Searches for exact matches of pattern in text using the fast rolling cosine vector filter.
     *
     * @param text The corpus text
     * @param pattern The pattern to find
     * @return Start indices of all exact confirmed matches
     */
    public static MyArrayList<Integer> search(String text, String pattern) {
        MyArrayList<Integer> matches = new MyArrayList<>();
        if (text == null || pattern == null) return matches;

        int n = text.length();
        int m = pattern.length();

        if (m < 2 || m > n) {
            if (m == 1 && n >= 1) {
                // Fallback for single char
                for (int i = 0; i < n; i++) {
                    if (text.charAt(i) == pattern.charAt(0)) matches.add(i);
                }
            }
            return matches;
        }

        int[] patternCounts = bigramCounts(pattern);
        long patternMagSq = 0;
        for (int v : patternCounts) {
            if (v != 0) patternMagSq += (long) v * v;
        }
        double patternMag = Math.sqrt(patternMagSq);

        int[] windowCounts = bigramCounts(text.substring(0, m));

        long dot = 0, windowMagSq = 0;
        for (int k = 0; k < TABLE_SIZE; k++) {
            int wv = windowCounts[k];
            if (wv != 0) {
                dot += (long) wv * patternCounts[k];
                windowMagSq += (long) wv * wv;
            }
        }

        for (int start = 0; start <= n - m; start++) {
            double windowMag = Math.sqrt((double) windowMagSq);
            double cosine = (windowMag == 0 || patternMag == 0) ? 0.0 : dot / (windowMag * patternMag);

            if (cosine >= SIMILARITY_THRESHOLD) {
                if (naiveVerify(text, start, pattern)) {
                    matches.add(start);
                }
            }

            if (start < n - m) {
                int leavingKey = key(text.charAt(start), text.charAt(start + 1));
                int enteringKey = key(text.charAt(start + m - 1), text.charAt(start + m));

                // Remove leaving bigram contribution: O(1)
                int oldLeave = windowCounts[leavingKey];
                dot -= patternCounts[leavingKey];
                windowMagSq -= (2L * oldLeave - 1);
                windowCounts[leavingKey] = oldLeave - 1;

                // Add entering bigram contribution: O(1)
                int oldEnter = windowCounts[enteringKey];
                dot += patternCounts[enteringKey];
                windowMagSq += (2L * oldEnter + 1);
                windowCounts[enteringKey] = oldEnter + 1;
            }
        }
        return matches;
    }
}
