package graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GraphGenerator {

    public static Graph connectedUndirected(int vertices, int edgeCount, long seed) {
        Graph graph = new Graph(vertices, false);
        Random random = new Random(seed);
        Set<Long> used = new HashSet<>();
        for (int vertex = 1; vertex < vertices; vertex++) {
            addUnique(graph, used, vertex, random.nextInt(vertex), random.nextInt(100) + 1);
        }
        while (graph.getEdges().size() < edgeCount) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);
            if (from != to) {
                addUnique(graph, used, from, to, random.nextInt(100) + 1);
            }
        }
        return graph;
    }

    private static void addUnique(Graph graph, Set<Long> used, int from, int to, int weight) {
        int first = graph.isDirected() ? from : Math.min(from, to);
        int second = graph.isDirected() ? to : Math.max(from, to);
        long key = ((long) first << 32) | second;
        if (used.add(key)) {
            graph.addEdge(from, to, weight);
        }
    }
}