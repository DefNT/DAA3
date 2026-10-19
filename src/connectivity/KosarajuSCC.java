package connectivity;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KosarajuSCC {
    public static class Result {
        private final List<List<Integer>> components;
        private final long operations;

        public Result(List<List<Integer>> components, long operations) {
            this.components = components;
            this.operations = operations;
        }

        public List<List<Integer>> getComponents() {
            return components;
        }
        public long getOperations() {
            return operations;
        }
    }

    public static Result find(Graph graph) {
        int vertices = graph.getVertexCount();
        boolean[] visited = new boolean[vertices];
        List<Integer> finishOrder = new ArrayList<>();
        long[] operations = {0};

        for (int vertex = 0; vertex < vertices; vertex++) {
            if (!visited[vertex]) {
                firstDFS(graph, vertex, visited, finishOrder, operations);
            }
        }

        List<List<Integer>> reversed = new ArrayList<>();
        for (int vertex = 0; vertex < vertices; vertex++) {
            reversed.add(new ArrayList<>());
        }
        for (Edge edge : graph.getEdges()) {
            reversed.get(edge.getTo()).add(edge.getFrom());
        }

        Arrays.fill(visited, false);
        List<List<Integer>> components = new ArrayList<>();
        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            int vertex = finishOrder.get(i);
            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                secondDFS(reversed, vertex, visited, component, operations);
                components.add(component);
            }
        }
        return new Result(components, operations[0]);
    }

    private static void firstDFS(Graph graph, int vertex, boolean[] visited,
                                 List<Integer> finishOrder, long[] operations) {
        visited[vertex] = true;
        for (Edge edge : graph.getAdjacentEdges(vertex)) {
            operations[0]++;
            if (!visited[edge.getTo()]) {
                firstDFS(graph, edge.getTo(), visited, finishOrder, operations);
            }
        }
        finishOrder.add(vertex);
    }

    private static void secondDFS(List<List<Integer>> reversed, int vertex,
                                  boolean[] visited, List<Integer> component,
                                  long[] operations) {
        visited[vertex] = true;
        component.add(vertex);
        for (int next : reversed.get(vertex)) {
            operations[0]++;
            if (!visited[next]) {
                secondDFS(reversed, next, visited, component, operations);
            }
        }
    }
}