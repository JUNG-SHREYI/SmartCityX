package smartcityx.core;

/**
 * Custom FIFO Queue implementation based on MyLinkedList.
 *
 * @param <T> Element type
 */
public class MyQueue<T> {
    private final MyLinkedList<T> list;

    public MyQueue() {
        this.list = new MyLinkedList<>();
    }

    public void enqueue(T item) {
        list.addLast(item);
    }

    public T dequeue() {
        return list.removeFirst();
    }

    public T peek() {
        return list.getFirst();
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
