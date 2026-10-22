package smartcityx.algorithms.suffix;

import smartcityx.core.MyArrayList;
import smartcityx.core.Pair;

/**
 * Suffix Array construction via Prefix Doubling / Manber-Myers algorithm (O(N log^2 N) / O(N log N))
 * and binary search matching in O(M log N).
 */
public class SuffixArray {

    private final String text;
    private final int n;
    private final int[] sa;

    public SuffixArray(String text) {
        this.text = text;
        this.n = text.length();
        this.sa = buildSuffixArray(text);
    }

    public int[] getSA() {
        return sa;
    }

    public String getText() {
        return text;
    }

    private static class SuffixRank implements Comparable<SuffixRank> {
        int index;
        int rank0;
        int rank1;

        public SuffixRank(int index, int rank0, int rank1) {
            this.index = index;
            this.rank0 = rank0;
            this.rank1 = rank1;
        }

        @Override
        public int compareTo(SuffixRank o) {
            if (this.rank0 != o.rank0) return Integer.compare(this.rank0, o.rank0);
            return Integer.compare(this.rank1, o.rank1);
        }
    }

    private int[] buildSuffixArray(String s) {
        int n = s.length();
        SuffixRank[] suffixes = new SuffixRank[n];
        for (int i = 0; i < n; i++) {
            int nextRank = (i + 1 < n) ? s.charAt(i + 1) : -1;
            suffixes[i] = new SuffixRank(i, s.charAt(i), nextRank);
        }
        quickSort(suffixes, 0, n - 1);

        int[] ind = new int[n];
        for (int k = 4; k < 2 * n; k *= 2) {
            int rank = 0;
            int prevRank0 = suffixes[0].rank0;
            int prevRank1 = suffixes[0].rank1;
            suffixes[0].rank0 = rank;
            ind[suffixes[0].index] = 0;

            for (int i = 1; i < n; i++) {
                if (suffixes[i].rank0 == prevRank0 && suffixes[i].rank1 == prevRank1) {
                    suffixes[i].rank0 = rank;
                } else {
                    prevRank0 = suffixes[i].rank0;
                    prevRank1 = suffixes[i].rank1;
                    suffixes[i].rank0 = ++rank;
                }
                ind[suffixes[i].index] = i;
            }

            for (int i = 0; i < n; i++) {
                int nextIndex = suffixes[i].index + k / 2;
                suffixes[i].rank1 = (nextIndex < n) ? suffixes[ind[nextIndex]].rank0 : -1;
            }
            quickSort(suffixes, 0, n - 1);
        }

        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = suffixes[i].index;
        }
        return result;
    }

    private void quickSort(SuffixRank[] arr, int low, int high) {
        if (low < high) {
            int p = partition(arr, low, high);
            quickSort(arr, low, p - 1);
            quickSort(arr, p + 1, high);
        }
    }

    private int partition(SuffixRank[] arr, int low, int high) {
        SuffixRank pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j].compareTo(pivot) <= 0) {
                i++;
                SuffixRank tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
            }
        }
        SuffixRank tmp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = tmp;
        return i + 1;
    }

    /**
     * Binary searches for pattern in O(M log N).
     *
     * @param pattern Pattern to locate
     * @return Range of indices [low, high] in suffix array containing the pattern prefix
     */
    public Pair<Integer, Integer> searchRange(String pattern) {
        if (pattern == null || pattern.length() == 0) return null;
        int m = pattern.length();

        // Find lower bound
        int low = 0, high = n - 1;
        int firstOccur = -1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int suffixIdx = sa[mid];
            String suffixSub = text.substring(suffixIdx, Math.min(suffixIdx + m, n));
            int cmp = suffixSub.compareTo(pattern);
            if (cmp >= 0) {
                if (cmp == 0) firstOccur = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        if (firstOccur == -1) return null;

        // Find upper bound
        low = firstOccur;
        high = n - 1;
        int lastOccur = firstOccur;
        while (low <= high) {
            int mid = (low + high) / 2;
            int suffixIdx = sa[mid];
            String suffixSub = text.substring(suffixIdx, Math.min(suffixIdx + m, n));
            int cmp = suffixSub.compareTo(pattern);
            if (cmp <= 0) {
                if (cmp == 0) lastOccur = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return Pair.of(firstOccur, lastOccur);
    }

    public MyArrayList<Integer> findAllOccurrences(String pattern) {
        MyArrayList<Integer> positions = new MyArrayList<>();
        Pair<Integer, Integer> range = searchRange(pattern);
        if (range != null) {
            for (int i = range.first; i <= range.second; i++) {
                positions.add(sa[i]);
            }
        }
        return positions;
    }
}
