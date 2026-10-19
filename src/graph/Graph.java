package graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int vertexCount;
    private final boolean directed;
    private final List<Edge> edges = new ArrayList<>();
    private final List<List<Edge>> adjacency = new ArrayList<>();

    public Graph(int vertexCount, boolean directed) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cant be negative");
        }
        this.vertexCount = vertexCount;
        this.directed = directed;
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
    }
}