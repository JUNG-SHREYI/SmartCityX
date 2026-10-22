package smartcityx.algorithms.strings;

import smartcityx.core.MyArrayList;

/**
 * Linear-time Z-Algorithm for exact pattern matching and longest prefix analysis.
 * Constructs the Z-array in O(N) using the sliding [L, R] Z-box invariant.
 */
public class ZAlgorithm {

    /**
     * Constructs the Z-array for string S.
     * Z[i] is the length of the longest common prefix between S and S[i..N-1].
     */
    public static int[] calculateZ(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i > r) {
                l = r = i;
                while (r < n && s.charAt(r - l) == s.charAt(r)) {
                    r++;
                }
                z[i] = r - l;
                r--;
            } else {
                int k = i - l;
                if (z[k] < r - i + 1) {
                    z[i] = z[k];
                } else {
                    l = i;
                    while (r < n && s.charAt(r - l) == s.charAt(r)) {
                        r++;
                    }
                    z[i] = r - l;
                    r--;
                }
            }
        }
        return z;
    }

    /**
     * Searches for occurrences of pattern in text using the concatenated string: pattern + "$" + text.
     */
    public static MyArrayList<Integer> search(String text, String pattern) {
        MyArrayList<Integer> matches = new MyArrayList<>();
        if (text == null || pattern == null || pattern.length() == 0 || pattern.length() > text.length()) {
            return matches;
        }

        String concat = pattern + "$" + text;
        int[] z = calculateZ(concat);
        int patLen = pattern.length();

        for (int i = patLen + 1; i < concat.length(); i++) {
            if (z[i] == patLen) {
                matches.add(i - patLen - 1);
            }
        }
        return matches;
    }
}
