package graph.dagsp;
import graph.model.*;
import graph.metrics.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

public class DagSP {
    private final Graph g;
    private final List<Integer> topo;
    private final SimpleMetrics metrics;

    public DagSP(Graph g, List<Integer> topo, SimpleMetrics metrics) {
        this.g = g;
        this.topo = topo;
        this.metrics = metrics;
    }

    public PathResult shortestFrom(int src) {
        metrics.tic("dagsp.shortest");
        int n = g.n;
        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);
        dist[src] = 0;
        metrics.inc("dagsp.init");

        for (int u : topo) {
            if (dist[u] == INF) continue;
            for (Edge e : g.adj.get(u)) {
                metrics.inc("dagsp.relaxations");
                int v = e.to;
                long nd = dist[u] + e.weight;
                if (nd < dist[v]) {
                    dist[v] = nd;
                    parent[v] = u;
                }
            }
        }

        metrics.toc("dagsp.shortest");
        return new PathResult(dist, parent);
    }

    public PathResult longestPathGlobal() {
        metrics.tic("dagsp.longest");
        int n = g.n;
        long NEG = Long.MIN_VALUE / 4;
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, NEG);
        Arrays.fill(parent, -1);

        Map<Integer, Integer> indeg = new HashMap<>();
        for (int u = 0; u < n; u++) indeg.put(u, 0);
        for (int u = 0; u < n; u++)
            for (Edge e : g.adj.get(u))
                indeg.put(e.to, indeg.get(e.to) + 1);
        for (int u = 0; u < n; u++)
            if (indeg.get(u) == 0) dist[u] = 0;

        for (int u : topo) {
            if (dist[u] == NEG) continue;
            for (Edge e : g.adj.get(u)) {
                metrics.inc("dagsp.relaxations");
                int v = e.to;
                long nd = dist[u] + e.weight;
                if (nd > dist[v]) {
                    dist[v] = nd;
                    parent[v] = u;
                }
            }
        }

        int best = 0;
        long bestVal = NEG;
        for (int i = 0; i < n; i++) {
            if (dist[i] > bestVal) {
                bestVal = dist[i];
                best = i;
            }
        }

        metrics.toc("dagsp.longest");
        return new PathResult(dist, parent, best, bestVal);
    }

    public static class PathResult {
        public final long[] dist;
        public final int[] parent;
        public final int bestNode;
        public final long bestVal;

        public PathResult(long[] dist, int[] parent) {
            this(dist, parent, -1, Long.MIN_VALUE);
        }

        public PathResult(long[] dist, int[] parent, int bestNode, long bestVal) {
            this.dist = dist;
            this.parent = parent;
            this.bestNode = bestNode;
            this.bestVal = bestVal;
        }

        public List<Integer> reconstruct(int target) {
            List<Integer> path = new ArrayList<>();
            for (int v = target; v != -1; v = parent[v]) {
                path.add(v);
            }
            Collections.reverse(path);
            return path;
        }
    }
}