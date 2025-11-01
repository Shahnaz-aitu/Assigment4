package graph.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Condensation {
    public final Graph dag;
    public final int[] compIndex;
    public final Map<Integer, List<Integer>> compMembers = new HashMap<>();
    public Condensation(Graph orig, List<List<Integer>> comps) {
        int m = comps.size();
        this.dag = new Graph(m);
        this.compIndex = new int[orig.n];
        for (int i = 0; i < comps.size(); i++) {
            List<Integer> member = comps.get(i);
            compMembers.put(i, new ArrayList<>(member));
            for (int v : member) compIndex[v] = i;
        }
        Set<Long> seen = new HashSet<>();
        for (int u = 0; u < orig.n; u++) {
            for (Edge e : orig.adj.get(u)) {
                int cu = compIndex[u];
                int cv = compIndex[e.to];
                if (cu != cv) {
                    long key = ((long)cu << 32) | (cv & 0xffffffffL);
                    if (!seen.contains(key)) {
                        dag.addEdge(cu, cv, e.weight);
                        seen.add(key);
                    }
                }
            }
        }
    }
}