package graph.model;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public final int n;
    public final List<List<Edge>> adj;
    public Graph(int n) {
        this.n = n;
        this.adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }
    public void addEdge(int u, int v, long w) {
        adj.get(u).add(new Edge(u, v, w));
    }
}