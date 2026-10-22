package smartcityx.algorithms.suffix;

/**
 * Suffix Array Induced Sorting (SA-IS).
 * Constructs Suffix Array in strictly linear O(N) time and O(N) space
 * using S-type / L-type suffix classification, bucket sorting, and LMS (Left-Most S-type) induction.
 */
public class SAIS {

    private static final boolean S_TYPE = true;
    private static final boolean L_TYPE = false;

    public static int[] buildSA(String s) {
        if (s == null) return new int[0];
        int n = s.length();
        if (n == 0) return new int[0];
        if (n == 1) return new int[]{0};

        int[] sInt = new int[n + 1];
        int maxChar = 0;
        for (int i = 0; i < n; i++) {
            sInt[i] = (int) s.charAt(i) + 1; // 1-based to reserve 0 for sentinel $
            if (sInt[i] > maxChar) maxChar = sInt[i];
        }
        sInt[n] = 0; // Sentinel 0 ($)

        int[] sa = sais(sInt, maxChar + 1);

        // Discard sentinel at sa[0]
        int[] result = new int[n];
        for (int i = 1; i <= n; i++) {
            result[i - 1] = sa[i];
        }
        return result;
    }

    private static int[] sais(int[] s, int alphabetSize) {
        int n = s.length;
        if (n == 1) return new int[]{0};

        boolean[] type = new boolean[n];
        type[n - 1] = S_TYPE;
        for (int i = n - 2; i >= 0; i--) {
            if (s[i] < s[i + 1]) {
                type[i] = S_TYPE;
            } else if (s[i] > s[i + 1]) {
                type[i] = L_TYPE;
            } else {
                type[i] = type[i + 1];
            }
        }

        int[] bucket = new int[alphabetSize];
        for (int x : s) bucket[x]++;

        int[] sa = new int[n];
        for (int i = 0; i < n; i++) sa[i] = -1;

        int[] lms = new int[n];
        int lmsCount = 0;
        for (int i = 1; i < n; i++) {
            if (type[i] == S_TYPE && type[i - 1] == L_TYPE) {
                lms[lmsCount++] = i;
            }
        }

        // Phase 1: Induce sort LMS suffixes
        int[] bTail = getBucketTails(bucket);
        for (int i = 0; i < lmsCount; i++) {
            int idx = lms[i];
            sa[bTail[s[idx]]--] = idx;
        }

        induceSort(s, sa, type, bucket);

        // Phase 2: Name LMS substrings and form reduced problem
        int[] lmsNames = new int[n];
        for (int i = 0; i < n; i++) lmsNames[i] = -1;

        int currentName = 0;
        int prevLMS = -1;
        for (int i = 0; i < n; i++) {
            int idx = sa[i];
            if (isLMS(idx, type)) {
                if (prevLMS != -1 && !areLmsEqual(s, prevLMS, idx, type)) {
                    currentName++;
                }
                lmsNames[idx] = currentName;
                prevLMS = idx;
            }
        }

        int[] reducedS = new int[lmsCount];
        int pos = 0;
        for (int i = 0; i < n; i++) {
            if (lmsNames[i] != -1) {
                reducedS[pos++] = lmsNames[i];
            }
        }

        int[] reducedSA;
        if (currentName + 1 == lmsCount) {
            reducedSA = new int[lmsCount];
            for (int i = 0; i < lmsCount; i++) {
                reducedSA[reducedS[i]] = i;
            }
        } else {
            reducedSA = sais(reducedS, currentName + 1);
        }

        // Phase 3: Induce sort all suffixes from sorted LMS suffixes
        for (int i = 0; i < n; i++) sa[i] = -1;
        bTail = getBucketTails(bucket);
        for (int i = lmsCount - 1; i >= 0; i--) {
            int lmsIdx = lms[reducedSA[i]];
            sa[bTail[s[lmsIdx]]--] = lmsIdx;
        }

        induceSort(s, sa, type, bucket);
        return sa;
    }

    private static void induceSort(int[] s, int[] sa, boolean[] type, int[] bucket) {
        int n = s.length;
        // Step 1: Induce L-types from left to right
        int[] bHead = getBucketHeads(bucket);
        for (int i = 0; i < n; i++) {
            int j = sa[i] - 1;
            if (j >= 0 && type[j] == L_TYPE) {
                sa[bHead[s[j]]++] = j;
            }
        }

        // Step 2: Induce S-types from right to left
        int[] bTail = getBucketTails(bucket);
        for (int i = n - 1; i >= 0; i--) {
            int j = sa[i] - 1;
            if (j >= 0 && type[j] == S_TYPE) {
                sa[bTail[s[j]]--] = j;
            }
        }
    }

    private static boolean isLMS(int i, boolean[] type) {
        return i > 0 && type[i] == S_TYPE && type[i - 1] == L_TYPE;
    }

    private static boolean areLmsEqual(int[] s, int i, int j, boolean[] type) {
        while (true) {
            if (s[i] != s[j] || type[i] != type[j]) return false;
            i++; j++;
            if (isLMS(i, type) && isLMS(j, type)) return s[i] == s[j];
            if (isLMS(i, type) || isLMS(j, type)) return false;
        }
    }

    private static int[] getBucketHeads(int[] bucket) {
        int[] heads = new int[bucket.length];
        int sum = 0;
        for (int i = 0; i < bucket.length; i++) {
            heads[i] = sum;
            sum += bucket[i];
        }
        return heads;
    }

    private static int[] getBucketTails(int[] bucket) {
        int[] tails = new int[bucket.length];
        int sum = 0;
        for (int i = 0; i < bucket.length; i++) {
            sum += bucket[i];
            tails[i] = sum - 1;
        }
        return tails;
    }
}
