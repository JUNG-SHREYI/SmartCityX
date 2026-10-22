package smartcityx.algorithms.parallel_random;

import java.util.Random;

/**
 * Randomized QuickSort with uniform random pivot selection.
 * Guarantees expected O(N log N) runtime on all inputs, eliminating worst-case O(N^2) adversarial orderings.
 * Space: O(log N) recursion stack.
 */
public class RandomizedQuickSort {

    private static final Random RNG = new Random();

    public static <T extends Comparable<T>> void sort(T[] array) {
        if (array == null || array.length <= 1) return;
        sort(array, 0, array.length - 1);
    }

    private static <T extends Comparable<T>> void sort(T[] array, int low, int high) {
        if (low < high) {
            int pivotIndex = low + RNG.nextInt(high - low + 1);
            swap(array, pivotIndex, high);

            int p = partition(array, low, high);
            sort(array, low, p - 1);
            sort(array, p + 1, high);
        }
    }

    private static <T extends Comparable<T>> int partition(T[] array, int low, int high) {
        T pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (array[j].compareTo(pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
