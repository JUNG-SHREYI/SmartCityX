package smartcityx.core;

import java.lang.reflect.Array;

/**
 * Custom dynamic array list implementation avoiding java.util.ArrayList.
 * Supports O(1) amortized appends, indexed access, insertion, and deletion.
 *
 * @param <T> Element type
 */
public class MyArrayList<T> {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public MyArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 0) initialCapacity = DEFAULT_CAPACITY;
        elements = new Object[initialCapacity];
        size = 0;
    }

    public void add(T element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
    }

    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(size + 1);
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }
        elements[index] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (T) elements[index];
    }

    public T set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        @SuppressWarnings("unchecked")
        T old = (T) elements[index];
        elements[index] = element;
        return old;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        T old = (T) elements[index];
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[--size] = null;
        return old;
    }

    public boolean removeElement(T element) {
        for (int i = 0; i < size; i++) {
            if ((element == null && elements[i] == null) || (element != null && element.equals(elements[i]))) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    public boolean contains(T element) {
        for (int i = 0; i < size; i++) {
            if ((element == null && elements[i] == null) || (element != null && element.equals(elements[i]))) {
                return true;
            }
        }
        return false;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCap = Math.max(elements.length * 2, minCapacity);
            if (newCap < DEFAULT_CAPACITY) newCap = DEFAULT_CAPACITY;
            Object[] newArr = new Object[newCap];
            for (int i = 0; i < size; i++) {
                newArr[i] = elements[i];
            }
            elements = newArr;
        }
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(Class<T> clazz) {
        T[] arr = (T[]) Array.newInstance(clazz, size);
        for (int i = 0; i < size; i++) {
            arr[i] = (T) elements[i];
        }
        return arr;
    }
}
