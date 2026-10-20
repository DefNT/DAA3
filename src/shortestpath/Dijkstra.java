package shortestpath;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    private static final long INF = Long.MAX_VALUE;

    public static class Result {
        private final long[] distances;
        private final int[] previous;
        private final long operations;

        public Result(long[] distances, int[] previous, long operations) {
            this.distances = distances;
            this.previous = previous;
            this.operations = operations;
        }

        public long getDistanceTo(int vertex) {
            return distances[vertex];
        }
        public long getOperations() {
            return operations;
        }
        public boolean isReachable(int vertex) {
            return distances[vertex] != INF;
        }

        public List<Integer> getPath(int destination) {
            if (destination < 0 || destination >= distances.length) {
                throw new IllegalArgumentException("Invalid vertex: " + destination);
            }
            List<Integer> path = new ArrayList<>();
            if (distances[destination] == INF) {
                return path;
            }
            for (int current = destination; current != -1; current = previous[current]) {
                path.add(current);
            }
            Collections.reverse(path);
            return path;
        }
    }

    private static class QueueEntry {
        final int vertex;
        final long distance;

        QueueEntry(int vertex, long distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    public static Result findPaths(Graph graph, int source) {
        if (source < 0 || source >= graph.getVertexCount()) {
            throw new IllegalArgumentException("Invalid source vertex");
        }
        for (Edge edge : graph.getEdges()) {
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException("Dijkstra requires non-negative weights");
            }
        }

        int vertices = graph.getVertexCount();
        long[] distances = new long[vertices];
        int[] previous = new int[vertices];
        Arrays.fill(distances, INF);
        Arrays.fill(previous, -1);
        distances[source] = 0;

        PriorityQueue<QueueEntry> queue =
                new PriorityQueue<>(Comparator.comparingLong(entry -> entry.distance));
        queue.add(new QueueEntry(source, 0));
        long operations = 1;

        while (!queue.isEmpty()) {
            QueueEntry current = queue.remove();
            operations++;
            if (current.distance != distances[current.vertex]) {
                continue;
            }
            for (Edge edge : graph.getAdjacentEdges(current.vertex)) {
                operations++;
                long newDistance = current.distance + edge.getWeight();
                if (newDistance < distances[edge.getTo()]) {
                    distances[edge.getTo()] = newDistance;
                    previous[edge.getTo()] = current.vertex;
                    queue.add(new QueueEntry(edge.getTo(), newDistance));
                    operations++;
                }
            }
        }
        return new Result(distances, previous, operations);
    }
}