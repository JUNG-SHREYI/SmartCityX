package smartcityx.algorithms.strings;

import smartcityx.core.MyArrayList;
import smartcityx.core.MyQueue;

/**
 * Aho-Corasick Multi-Pattern Trie Matching Automaton.
 * Constructs Trie + Failure Links + Output Links in O(sum of pattern lengths * alphabet).
 * Searches text in O(N + total occurrences).
 */
public class AhoCorasick {

    private static final int ALPHABET_SIZE = 128;

    public static class Node {
        public Node[] children = new Node[ALPHABET_SIZE];
        public Node failureLink = null;
        public MyArrayList<String> output = new MyArrayList<>();
    }

    public static class MatchResult {
        public final int index;
        public final String keyword;

        public MatchResult(int index, String keyword) {
            this.index = index;
            this.keyword = keyword;
        }

        @Override
        public String toString() {
            return keyword + "@" + index;
        }
    }

    private final Node root;

    public AhoCorasick() {
        this.root = new Node();
    }

    public void insert(String pattern) {
        if (pattern == null || pattern.length() == 0) return;
        Node current = root;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            int idx = c < ALPHABET_SIZE ? c : 0;
            if (current.children[idx] == null) {
                current.children[idx] = new Node();
            }
            current = current.children[idx];
        }
        current.output.add(pattern);
    }

    public void buildAutomaton() {
        MyQueue<Node> queue = new MyQueue<>();

        // Depth 1 nodes fail to root
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            if (root.children[i] != null) {
                root.children[i].failureLink = root;
                queue.enqueue(root.children[i]);
            }
        }

        // BFS to build failure links for depth >= 2
        while (!queue.isEmpty()) {
            Node current = queue.dequeue();

            for (int c = 0; c < ALPHABET_SIZE; c++) {
                Node child = current.children[c];
                if (child != null) {
                    Node f = current.failureLink;
                    while (f != null && f.children[c] == null) {
                        f = f.failureLink;
                    }
                    child.failureLink = (f != null) ? f.children[c] : root;

                    // Propagate outputs from failure link
                    if (child.failureLink != null) {
                        for (int k = 0; k < child.failureLink.output.size(); k++) {
                            child.output.add(child.failureLink.output.get(k));
                        }
                    }
                    queue.enqueue(child);
                }
            }
        }
    }

    public MyArrayList<MatchResult> searchInText(String text) {
        MyArrayList<MatchResult> results = new MyArrayList<>();
        if (text == null) return results;

        Node current = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int idx = c < ALPHABET_SIZE ? c : 0;

            while (current != null && current.children[idx] == null) {
                current = current.failureLink;
            }
            if (current == null) {
                current = root;
                continue;
            }
            current = current.children[idx];

            if (current.output.size() > 0) {
                for (int k = 0; k < current.output.size(); k++) {
                    String pat = current.output.get(k);
                    results.add(new MatchResult(i - pat.length() + 1, pat));
                }
            }
        }
        return results;
    }
}
