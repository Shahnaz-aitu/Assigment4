package graph.metrics;

import java.util.HashMap;
import java.util.Map;

public class SimpleMetrics {
    private final Map<String, Long> counters = new HashMap<>();
    private final Map<String, Long> times = new HashMap<>();
    private final Map<String, Long> start = new HashMap<>();
    public void inc(String k) { counters.put(k, counters.getOrDefault(k, 0L) + 1); }
    public void add(String k, long v) { counters.put(k, counters.getOrDefault(k, 0L) + v); }
    public long get(String k) { return counters.getOrDefault(k, 0L); }
    public void tic(String phase) { start.put(phase, System.nanoTime()); }
    public void toc(String phase) { long s = start.getOrDefault(phase, 0L); times.put(phase, System.nanoTime() - s); }
    public long time(String phase) { return times.getOrDefault(phase, 0L); }
    public Map<String, Long> snapshot() { Map<String, Long> out = new HashMap<>(counters); times.forEach((k,v)->out.put("time."+k, v)); return out; }
}
