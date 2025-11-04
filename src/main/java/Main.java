import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graph.dagsp.DagSP;
import graph.metrics.SimpleMetrics;
import graph.model.Condensation;
import graph.model.Graph;
import graph.scc.TarjanSCC;
import graph.topo.KahnTopoSorter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "data/data1.json";

        ObjectMapper om = new ObjectMapper();
        JsonNode root;
        try {
            root = om.readTree(new File(path));
        } catch (IOException e) {
            System.err.println("Cannot read JSON file: " + path);
            e.printStackTrace();
            return;
        }

        boolean directed = root.path("directed").asBoolean(true);
        int n = root.path("n").asInt(-1);
        if (n <= 0) {
            System.err.println("Invalid or missing field 'n' in JSON");
            return;
        }

        Graph g = new Graph(n);
        for (JsonNode e : root.path("edges")) {
            int u = e.path("u").asInt(-1);
            int v = e.path("v").asInt(-1);
            long w = e.path("w").asLong(0);
            if (u < 0 || v < 0 || u >= n || v >= n) {
                System.err.println("Invalid edge in JSON: u=" + u + " v=" + v);
                continue;
            }
            g.addEdge(u, v, w);
            if (!directed) g.addEdge(v, u, w);
        }

        Integer sourceNode = null;
        if (root.has("source") && !root.path("source").isNull()) {
            sourceNode = root.path("source").asInt();
            if (sourceNode < 0 || sourceNode >= n) {
                System.err.println("Invalid 'source' in JSON, ignoring");
                sourceNode = null;
            }
        }

        String weightModel = root.path("weight_model").asText("edge");

        SimpleMetrics metrics = new SimpleMetrics();

        TarjanSCC tarjan = new TarjanSCC(g, metrics);
        tarjan.run();
        List<List<Integer>> sccs = tarjan.components();
        System.out.println("SCC count: " + sccs.size());
        for (int i = 0; i < sccs.size(); i++) {
            List<Integer> comp = sccs.get(i);
            System.out.println("Comp " + i + ": " + comp + " size=" + comp.size());
        }

        Condensation cond = new Condensation(g, sccs);
        System.out.println("\nCondensation graph nodes: " + cond.dag.n);
        for (int u = 0; u < cond.dag.n; u++) {
            System.out.print("C" + u + " -> [");
            boolean first = true;
            for (int i = 0; i < cond.dag.adj.get(u).size(); i++) {
                int v = cond.dag.adj.get(u).get(i).to;
                long w = cond.dag.adj.get(u).get(i).weight;
                if (!first) System.out.print(", ");
                System.out.print(v + "(w=" + w + ")");
                first = false;
            }
            System.out.println("] members=" + cond.compMembers.get(u));
        }
        KahnTopoSorter topo = new KahnTopoSorter(cond.dag, metrics);
        List<Integer> topoOrder = topo.sort();
        if (topoOrder.isEmpty()) {
            System.out.println("\nTopological sort failed (cycle in condensation graph)");
        } else {
            System.out.println("\nTopological order of components: " + topoOrder);
            List<Integer> expanded = new ArrayList<>();
            for (int c : topoOrder) expanded.addAll(cond.compMembers.get(c));
            System.out.println("Derived order of original tasks: " + expanded);
        }

        DagSP dagsp = new DagSP(cond.dag, topoOrder, metrics);

        if (sourceNode != null) {
            int srcComp = cond.compIndex[sourceNode];
            DagSP.PathResult spr = dagsp.shortestFrom(srcComp);
            System.out.println("\nShortest distances from source component " + srcComp + ":");
            for (int i = 0; i < spr.dist.length; i++) {
                long d = spr.dist[i];
                if (d == Long.MAX_VALUE / 4) System.out.println(i + ": INF");
                else System.out.println(i + ": " + d);
            }
            int sampleTarget = spr.dist.length - 1;
            System.out.println("Sample shortest path to " + sampleTarget + ": " + spr.reconstruct(sampleTarget));
        } else {
            System.out.println("\nNo source specified, skipping single-source shortest paths.");
        }

        DagSP.PathResult longest = dagsp.longestPathGlobal();
        System.out.println("\nCritical path (component-level) length: " + longest.bestVal);
        System.out.println("Critical path (component-level) end node: " + longest.bestNode);
        List<Integer> crit = longest.reconstruct(longest.bestNode);
        System.out.println("Critical path (component-level) nodes: " + crit);
        List<Integer> critExpanded = new ArrayList<>();
        for (int c : crit) critExpanded.addAll(cond.compMembers.get(c));
        System.out.println("Critical path expanded to original tasks: " + critExpanded);

        System.out.println("\nMetrics snapshot: " + metrics.snapshot());
        System.out.println("\nWeight model (from JSON): " + weightModel);

    }
}
