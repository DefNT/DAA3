package mst;

import dsu.DisjointSetUnion;
import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class Kruskal {
    public static MSTResult findMST(Graph graph) {
        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        long[] comparisons = {0};
        sortedEdges.sort((first, second) -> {
            comparisons[0]++;
            return Integer.compare(first.getWeight(), second.getWeight());
        });

        DisjointSetUnion sets = new DisjointSetUnion(graph.getVertexCount());
        long totalWeight = 0;
        int chosenEdges = 0;

        for (Edge edge : sortedEdges) {
            if (sets.union(edge.getFrom(), edge.getTo())) {
                totalWeight += edge.getWeight();
                chosenEdges++;
            }
        }

        int vertices = graph.getVertexCount();
        boolean connected = vertices <= 1 || chosenEdges == vertices - 1;
        long operations = comparisons[0] + sortedEdges.size() + sets.getOperations();
        return new MSTResult(totalWeight, connected, operations);
    }
}