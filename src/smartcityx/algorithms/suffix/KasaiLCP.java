package smartcityx.algorithms.suffix;

/**
 * Kasai's Algorithm for computing the Longest Common Prefix (LCP) array in O(N) time.
 * Invariant: As we move from suffix i to suffix i+1, LCP decreases by at most 1:
 *     LCP[Rank[i+1]] >= LCP[Rank[i]] - 1
 */
public class KasaiLCP {

    /**
     * Computes the LCP array given a string and its valid Suffix Array.
     *
     * @param text The input text
     * @param sa The suffix array of the text
     * @return LCP array of length N, where lcp[i] is the length of common prefix between SA[i] and SA[i-1] (lcp[0]=0).
     */
    public static int[] buildLCP(String text, int[] sa) {
        int n = text.length();
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int[] lcp = new int[n];
        int h = 0;

        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1]; // Preceding suffix in lexicographical order
                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--; // Invariant reduction
                }
            } else {
                lcp[0] = 0;
            }
        }
        return lcp;
    }

    /**
     * Finds the longest repeated substring in the text using SA + LCP.
     */
    public static String findLongestRepeatedSubstring(String text, int[] sa, int[] lcp) {
        int maxLen = 0;
        int maxIdx = -1;

        for (int i = 1; i < text.length(); i++) {
            if (lcp[i] > maxLen) {
                maxLen = lcp[i];
                maxIdx = sa[i];
            }
        }

        if (maxLen > 0 && maxIdx != -1) {
            return text.substring(maxIdx, maxIdx + maxLen);
        }
        return "";
    }
}
