package smartcityx.algorithms.flow;

import smartcityx.core.MyArrayList;
import smartcityx.core.MyQueue;

/**
 * Dinic's Algorithm for Maximum Network Flow.
 * Employs Level Graph construction via BFS and Blocking Flow augmentation via DFS with dead-end elimination.
 * Time Complexity: O(V^2 * E) on general graphs, and O(E * sqrt(V)) on unit networks.
 */
public class DinicAlgorithm {

    public static class Edge {
        public int to;
        public int rev;
        public double cap;
        public double flow;

        public Edge(int to, int rev, double cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
            this.flow = 0;
        }
    }

    private final int n;
    private final MyArrayList<Edge>[] adj;
    private final int[] level;
    private final int[] ptr;

    @SuppressWarnings("unchecked")
    public DinicAlgorithm(int n) {
        this.n = n;
        this.adj = new MyArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new MyArrayList<>();
        this.level = new int[n];
        this.ptr = new int[n];
    }

    public void addEdge(int from, int to, double cap) {
        Edge a = new Edge(to, adj[to].size(), cap);
        Edge b = new Edge(from, adj[from].size(), 0); // Residual reverse edge
        adj[from].add(a);
        adj[to].add(b);
    }

    private boolean bfsLevelGraph(int s, int t) {
        for (int i = 0; i < n; i++) level[i] = -1;
        level[s] = 0;
        MyQueue<Integer> q = new MyQueue<>();
        q.enqueue(s);

        while (!q.isEmpty()) {
            int v = q.dequeue();
            for (int i = 0; i < adj[v].size(); i++) {
                Edge e = adj[v].get(i);
                if (e.cap - e.flow > 1e-9 && level[e.to] == -1) {
                    level[e.to] = level[v] + 1;
                    q.enqueue(e.to);
                }
            }
        }
        return level[t] != -1;
    }

    private double dfsBlockingFlow(int v, int t, double pushed) {
        if (pushed <= 1e-9) return 0;
        if (v == t) return pushed;

        for (int cid = ptr[v]; cid < adj[v].size(); cid = ++ptr[v]) {
            Edge e = adj[v].get(cid);
            int tr = e.to;
            if (level[v] + 1 != level[tr] || e.cap - e.flow <= 1e-9) continue;

            double trPushed = dfsBlockingFlow(tr, t, Math.min(pushed, e.cap - e.flow));
            if (trPushed <= 1e-9) continue;

            e.flow += trPushed;
            adj[tr].get(e.rev).flow -= trPushed;
            return trPushed;
        }
        return 0;
    }

    public double computeMaxFlow(int s, int t) {
        double flow = 0;
        while (bfsLevelGraph(s, t)) {
            for (int i = 0; i < n; i++) ptr[i] = 0;
            while (true) {
                double pushed = dfsBlockingFlow(s, t, Double.POSITIVE_INFINITY);
                if (pushed <= 1e-9) break;
                flow += pushed;
            }
        }
        return flow;
    }

    public MyArrayList<Edge> getEdgesFrom(int u) {
        return adj[u];
    }
}
