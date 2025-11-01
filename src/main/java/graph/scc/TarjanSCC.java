package graph.scc;

import graph.model.Edge;
import graph.model.Graph;
import graph.metrics.SimpleMetrics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class TarjanSCC {
    private final Graph g;
    private final int n;
    private int index = 0;
    private final int[] indices;
    private final int[] lowlink;
    private final boolean[] onstack;
    private final Deque<Integer> stack;
    private final List<List<Integer>> comps;
    private final SimpleMetrics metrics;
    public TarjanSCC(Graph g, SimpleMetrics metrics) {
        this.g = g;
        this.n = g.n;
        this.metrics = metrics;
        this.indices = new int[n];
        Arrays.fill(this.indices, -1);
        this.lowlink = new int[n];
        this.onstack = new boolean[n];
        this.stack = new ArrayDeque<>();
        this.comps = new ArrayList<>();
    }
    public void run() {
        metrics.tic("scc");
        for (int v = 0; v < n; v++) if (indices[v] == -1) strongconnect(v);
        metrics.toc("scc");
    }
    private void strongconnect(int v) {
        indices[v] = index;
        lowlink[v] = index;
        index++;
        stack.push(v);
        onstack[v] = true;
        metrics.inc("scc.visits");
        for (Edge e : g.adj.get(v)) {
            metrics.inc("scc.edges");
            int w = e.to;
            if (indices[w] == -1) {
                strongconnect(w);
                lowlink[v] = Math.min(lowlink[v], lowlink[w]);
            } else if (onstack[w]) {
                lowlink[v] = Math.min(lowlink[v], indices[w]);
            }
        }
        if (lowlink[v] == indices[v]) {
            List<Integer> comp = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onstack[w] = false;
                comp.add(w);
            } while (w != v);
            comps.add(comp);
        }
    }
    public List<List<Integer>> components() { return comps; }
}