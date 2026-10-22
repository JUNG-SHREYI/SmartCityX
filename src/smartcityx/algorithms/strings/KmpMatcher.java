package smartcityx.algorithms.strings;

import smartcityx.core.MyArrayList;

/**
 * Knuth-Morris-Pratt (KMP) String Matching Algorithm.
 * Computes Longest Proper Prefix which is also Suffix (LPS/π array) in O(M).
 * Searches text in O(N) time with O(M) auxiliary space.
 * Guarantee: The text pointer i never regresses.
 */
public class KmpMatcher {

    /**
     * Computes the LPS (failure function) array for the given pattern.
     */
    public static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1]; // Fallback without advancing i
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Searches for all occurrences of pattern in text using KMP.
     *
     * @param text The source text to search within
     * @param pattern The pattern to search for
     * @return List of 0-based start indices of matches
     */
    public static MyArrayList<Integer> search(String text, String pattern) {
        MyArrayList<Integer> matches = new MyArrayList<>();
        if (text == null || pattern == null || pattern.length() == 0 || pattern.length() > text.length()) {
            return matches;
        }

        int n = text.length();
        int m = pattern.length();
        int[] lps = computeLPS(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                matches.add(i - j);
                j = lps[j - 1]; // Reset pattern pointer via LPS
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }
}
