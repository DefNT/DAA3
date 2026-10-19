package mst;

import graph.Edge;
import graph.Graph;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Prim {
    public static MSTResult findMST(Graph graph) {
        int vertices = graph.getVertexCount();
        if (vertices == 0) {
            return new MSTResult(0, true, 0);
        }

        boolean[] visited = new boolean[vertices];
        PriorityQueue<Edge> queue =
                new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));
        queue.add(new Edge(-1, 0, 0));

        long totalWeight = 0;
        long operations = 1;
        int visitedCount = 0;

        while (!queue.isEmpty()) {
            Edge edge = queue.remove();
            operations++;
            int vertex = edge.getTo();
            if (visited[vertex]) {
                continue;
            }
            visited[vertex] = true;
            visitedCount++;
            totalWeight += edge.getWeight();

            for (Edge next : graph.getAdjacentEdges(vertex)) {
                operations++;
                if (!visited[next.getTo()]) {
                    queue.add(next);
                    operations++;
                }
            }
        }

        return new MSTResult(totalWeight, visitedCount == vertices, operations);
    }
}