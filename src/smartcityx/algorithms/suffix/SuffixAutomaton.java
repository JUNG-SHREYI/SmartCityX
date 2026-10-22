package smartcityx.algorithms.suffix;

/**
 * Suffix Automaton (Directed Acyclic Word Graph / SAM).
 * Compact DFA representing all substrings of a string in linear O(N) states and transitions.
 * Supports O(M) substring verification, counting, and pattern containment queries.
 */
public class SuffixAutomaton {

    private static final int MAX_ALPHABET = 128;

    public static class State {
        public int len;
        public int link;
        public int[] next = new int[MAX_ALPHABET];
        public boolean isClone;

        public State() {
            len = 0;
            link = -1;
            for (int i = 0; i < MAX_ALPHABET; i++) next[i] = -1;
            isClone = false;
        }
    }

    private final State[] st;
    private int sz;
    private int last;

    public SuffixAutomaton(String s) {
        int maxStates = 2 * s.length() + 2;
        st = new State[maxStates];
        for (int i = 0; i < maxStates; i++) st[i] = new State();

        st[0].len = 0;
        st[0].link = -1;
        sz = 1;
        last = 0;

        for (int i = 0; i < s.length(); i++) {
            extend(s.charAt(i));
        }
    }

    private void extend(char c) {
        int charIdx = (c < MAX_ALPHABET) ? c : 0;
        int cur = sz++;
        st[cur].len = st[last].len + 1;

        int p = last;
        while (p != -1 && st[p].next[charIdx] == -1) {
            st[p].next[charIdx] = cur;
            p = st[p].link;
        }

        if (p == -1) {
            st[cur].link = 0;
        } else {
            int q = st[p].next[charIdx];
            if (st[p].len + 1 == st[q].len) {
                st[cur].link = q;
            } else {
                int clone = sz++;
                st[clone].len = st[p].len + 1;
                System.arraycopy(st[q].next, 0, st[clone].next, 0, MAX_ALPHABET);
                st[clone].link = st[q].link;
                st[clone].isClone = true;

                while (p != -1 && st[p].next[charIdx] == q) {
                    st[p].next[charIdx] = clone;
                    p = st[p].link;
                }
                st[q].link = st[cur].link = clone;
            }
        }
        last = cur;
    }

    public boolean containsSubstring(String pattern) {
        if (pattern == null || pattern.length() == 0) return true;
        int cur = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            int idx = (c < MAX_ALPHABET) ? c : 0;
            if (st[cur].next[idx] == -1) return false;
            cur = st[cur].next[idx];
        }
        return true;
    }

    public int getDistinctSubstringsCount() {
        int[] dp = new int[sz];
        return countPaths(0, dp) - 1; // Exclude empty string
    }

    private int countPaths(int u, int[] dp) {
        if (dp[u] != 0) return dp[u];
        int count = 1;
        for (int c = 0; c < MAX_ALPHABET; c++) {
            if (st[u].next[c] != -1) {
                count += countPaths(st[u].next[c], dp);
            }
        }
        dp[u] = count;
        return count;
    }

    public int getStateCount() {
        return sz;
    }
}
