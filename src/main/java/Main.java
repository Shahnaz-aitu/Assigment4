import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graph.metrics.SimpleMetrics;
import graph.model.Condensation;
import graph.model.Graph;
import graph.scc.TarjanSCC;

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

    }
}
