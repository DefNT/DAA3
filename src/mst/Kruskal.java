package mst;

import dsu.DisjointSetUnion;
import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class Kruskal {
    public static MSTResult findMST(Graph graph) {
        return findMST(graph, new DisjointSetUnion(graph.getVertexCount()));
    }

    public static MSTResult findMST(Graph graph, DisjointSetUnion sets) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException("MST needs an undirected graph");
        }
        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        long[] comparisons = {0};
        sortedEdges.sort((first, second) -> {
            comparisons[0]++;
            return Integer.compare(first.getWeight(), second.getWeight());
        });

        List<Edge> chosen = new ArrayList<>();
        long totalWeight = 0;
        for (Edge edge : sortedEdges) {
            if (sets.union(edge.getFrom(), edge.getTo())) {
                totalWeight += edge.getWeight();
                chosen.add(edge);
            }
        }

        int vertices = graph.getVertexCount();
        boolean connected = vertices <= 1 || chosen.size() == vertices - 1;
        long operations = comparisons[0] + sortedEdges.size() + sets.getOperations();
        return new MSTResult(totalWeight, connected, operations, chosen);
    }
}