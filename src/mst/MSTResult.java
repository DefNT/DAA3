package mst;

import graph.Edge;
import java.util.List;

public class MSTResult {
    private final long weight;
    private final boolean connected;
    private final long operations;
    private final List<Edge> edges;

    public MSTResult(long weight, boolean connected, long operations, List<Edge> edges) {
        this.weight = weight;
        this.connected = connected;
        this.operations = operations;
        this.edges = edges;
    }

    public long getWeight() {
        return weight;
    }
    public boolean isConnected() {
        return connected;
    }
    public long getOperations() {
        return operations;
    }
    public List<Edge> getEdges() {
        return edges;
    }
}