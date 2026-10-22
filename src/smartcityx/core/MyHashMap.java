package smartcityx.core;

/**
 * Custom hash map implementation using separate chaining collision resolution.
 * Supports O(1) average lookup, insertion, and deletion.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class MyHashMap<K, V> {

    public static class Entry<K, V> {
        public final K key;
        public V value;
        public Entry<K, V> next;

        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K, V>[] table;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        table = new Entry[DEFAULT_CAPACITY];
        size = 0;
    }

    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return (h ^ (h >>> 16)) & (table.length - 1);
    }

    public V put(K key, V value) {
        if ((float) (size + 1) / table.length > LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if ((key == null && current.key == null) || (key != null && key.equals(current.key))) {
                V old = current.value;
                current.value = value;
                return old;
            }
            current = current.next;
        }

        table[index] = new Entry<>(key, value, table[index]);
        size++;
        return null;
    }

    public V get(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if ((key == null && current.key == null) || (key != null && key.equals(current.key))) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if ((key == null && current.key == null) || (key != null && key.equals(current.key))) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public V remove(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if ((key == null && current.key == null) || (key != null && key.equals(current.key))) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public MyArrayList<K> keys() {
        MyArrayList<K> keyList = new MyArrayList<>(size);
        for (Entry<K, V> head : table) {
            Entry<K, V> curr = head;
            while (curr != null) {
                keyList.add(curr.key);
                curr = curr.next;
            }
        }
        return keyList;
    }

    public MyArrayList<V> values() {
        MyArrayList<V> valList = new MyArrayList<>(size);
        for (Entry<K, V> head : table) {
            Entry<K, V> curr = head;
            while (curr != null) {
                valList.add(curr.value);
                curr = curr.next;
            }
        }
        return valList;
    }

    public MyArrayList<Entry<K, V>> entries() {
        MyArrayList<Entry<K, V>> entryList = new MyArrayList<>(size);
        for (Entry<K, V> head : table) {
            Entry<K, V> curr = head;
            while (curr != null) {
                entryList.add(curr);
                curr = curr.next;
            }
        }
        return entryList;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table;
        table = new Entry[oldTable.length * 2];
        size = 0;

        for (Entry<K, V> head : oldTable) {
            Entry<K, V> curr = head;
            while (curr != null) {
                put(curr.key, curr.value);
                curr = curr.next;
            }
        }
    }

    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }
}
