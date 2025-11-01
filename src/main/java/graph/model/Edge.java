package graph.model;

public class Edge {
    public final int from;
    public final int to;
    public final long weight;
    public Edge(int from, int to, long weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
}