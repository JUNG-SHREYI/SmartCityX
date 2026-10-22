package smartcityx.core;

import java.util.Comparator;

/**
 * Custom Min-Heap priority queue implementation.
 *
 * @param <T> Element type
 */
public class MyMinHeap<T> {
    private Object[] heap;
    private int size;
    private final Comparator<T> comparator;
    private static final int DEFAULT_CAPACITY = 16;

    public MyMinHeap(Comparator<T> comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    public MyMinHeap(int capacity, Comparator<T> comparator) {
        this.heap = new Object[Math.max(capacity, DEFAULT_CAPACITY)];
        this.size = 0;
        this.comparator = comparator;
    }

    public void insert(T item) {
        if (size >= heap.length) {
            resize();
        }
        heap[size] = item;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (size == 0) return null;
        return (T) heap[0];
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (size == 0) return null;
        T min = (T) heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0) {
            siftDown(0);
        }
        return min;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) heap[i] = null;
        size = 0;
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        int curr = index;
        while (curr > 0) {
            int parent = (curr - 1) / 2;
            if (comparator.compare((T) heap[curr], (T) heap[parent]) < 0) {
                swap(curr, parent);
                curr = parent;
            } else {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int index) {
        int curr = index;
        while (curr * 2 + 1 < size) {
            int left = curr * 2 + 1;
            int right = curr * 2 + 2;
            int smallest = curr;

            if (left < size && comparator.compare((T) heap[left], (T) heap[smallest]) < 0) {
                smallest = left;
            }
            if (right < size && comparator.compare((T) heap[right], (T) heap[smallest]) < 0) {
                smallest = right;
            }

            if (smallest != curr) {
                swap(curr, smallest);
                curr = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        Object tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private void resize() {
        Object[] newHeap = new Object[heap.length * 2];
        for (int i = 0; i < size; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }
}
