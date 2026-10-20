package connectivity;

import graph.Edge;
import graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TopologicalSort {
    public static class Result {
        private final List<Integer> order;
        private final boolean acyclic;
        private final long operations;

        public Result(List<Integer> order, boolean acyclic, long operations) {
            this.order = order;
            this.acyclic = acyclic;
            this.operations = operations;
        }

        public List<Integer> getOrder() {
            return order;
        }
        public boolean isAcyclic() {
            return acyclic;
        }
        public long getOperations() {
            return operations;
        }
    }

    public static Result sort(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("Topological sort needs a directed graph");
        }
        int vertices = graph.getVertexCount();
        int[] indegree = new int[vertices];
        long operations = 0;
        for (Edge edge : graph.getEdges()) {
            indegree[edge.getTo()]++;
            operations++;
        }

        Queue<Integer> ready = new ArrayDeque<>();
        for (int vertex = 0; vertex < vertices; vertex++) {
            if (indegree[vertex] == 0) {
                ready.add(vertex);
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            int vertex = ready.remove();
            order.add(vertex);
            for (Edge edge : graph.getAdjacentEdges(vertex)) {
                indegree[edge.getTo()]--;
                operations++;
                if (indegree[edge.getTo()] == 0) {
                    ready.add(edge.getTo());
                }
            }
        }
        return new Result(order, order.size() == vertices, operations);
    }
}