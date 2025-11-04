package graph.topo;

import graph.model.Edge;
import graph.model.Graph;
import graph.metrics.SimpleMetrics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class KahnTopoSorter {
    private final Graph g;
    private final SimpleMetrics metrics;
    public KahnTopoSorter(Graph g, SimpleMetrics metrics) { this.g = g; this.metrics = metrics; }
    public List<Integer> sort() {
        metrics.tic("topo");
        int n = g.n;
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) for (Edge e : g.adj.get(u)) indeg[e.to]++;
        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) { q.add(i); metrics.inc("topo.pushes"); }
        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.removeFirst();
            metrics.inc("topo.pops");
            order.add(u);
            for (Edge e : g.adj.get(u)) {
                indeg[e.to]--;
                if (indeg[e.to] == 0) { q.add(e.to); metrics.inc("topo.pushes"); }
            }
        }
        metrics.toc("topo");
        if (order.size() != n) return new ArrayList<>();
        return order;
    }
}