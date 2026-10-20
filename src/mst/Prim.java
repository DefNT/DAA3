package mst;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {
    public static MSTResult findMST(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException("MST needs an undirected graph");
        }
        int vertices = graph.getVertexCount();
        boolean[] visited = new boolean[vertices];
        PriorityQueue<Edge> queue =
                new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));
        List<Edge> chosen = new ArrayList<>();

        long totalWeight = 0;
        long operations = 0;
        int components = 0;

        for (int start = 0; start < vertices; start++) {
            if (visited[start]) {
                continue;
            }
            components++;
            queue.add(new Edge(-1, start, 0));
            operations++;

            while (!queue.isEmpty()) {
                Edge edge = queue.remove();
                operations++;
                int vertex = edge.getTo();
                if (visited[vertex]) {
                    continue;
                }
                visited[vertex] = true;
                if (edge.getFrom() != -1) {
                    totalWeight += edge.getWeight();
                    chosen.add(edge);
                }

                for (Edge next : graph.getAdjacentEdges(vertex)) {
                    operations++;
                    if (!visited[next.getTo()]) {
                        queue.add(next);
                        operations++;
                    }
                }
            }
        }

        return new MSTResult(totalWeight, components <= 1, operations, chosen);
    }
}