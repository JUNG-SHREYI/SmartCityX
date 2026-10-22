package smartcityx.core;

/**
 * Custom LIFO Stack implementation.
 *
 * @param <T> Element type
 */
public class MyStack<T> {
    private final MyArrayList<T> list;

    public MyStack() {
        this.list = new MyArrayList<>();
    }

    public void push(T item) {
        list.add(item);
    }

    public T pop() {
        if (isEmpty()) return null;
        return list.remove(list.size() - 1);
    }

    public T peek() {
        if (isEmpty()) return null;
        return list.get(list.size() - 1);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void clear() {
        list.clear();
    }
}
