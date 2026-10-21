package tests;

import connectivity.KosarajuSCC;
import connectivity.TopologicalSort;
import dsu.DisjointSetUnion;
import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tests {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testMST();
        testDSU();
        testTopologicalSort();
        testSCC();
        testDijkstra();
        testRandomGraphs();

        System.out.println();
        System.out.println("Passed: " + passed + ", Failed: " + failed);

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testMST() {
        Graph small = new Graph(4, false);
        small.addEdge(0, 1, 1);
        small.addEdge(1, 2, 2);
        small.addEdge(2, 3, 3);
        small.addEdge(0, 3, 6);
        small.addEdge(0, 2, 2);
        check("small graph with cycles: Kruskal weight = 6",
                Kruskal.findMST(small).getWeight() == 6);
        check("small graph with cycles: Prim weight = 6",
                Prim.findMST(small).getWeight() == 6);

        Graph square = new Graph(4, false);
        square.addEdge(0, 1, 1);
        square.addEdge(1, 2, 1);
        square.addEdge(2, 3, 1);
        square.addEdge(3, 0, 1);
        square.addEdge(0, 2, 2);
        MSTResult kruskal = Kruskal.findMST(square);
        MSTResult prim = Prim.findMST(square);
        check("multiple valid MSTs: both weights = 3",
                kruskal.getWeight() == 3 && prim.getWeight() == 3);
        check("multiple valid MSTs: both use V-1 edges",
                kruskal.getEdges().size() == 3 && prim.getEdges().size() == 3);
        check("multiple valid MSTs: both report connected",
                kruskal.isConnected() && prim.isConnected());

        Graph disconnected = new Graph(4, false);
        disconnected.addEdge(0, 1, 2);
        disconnected.addEdge(2, 3, 3);
        MSTResult disconnectedKruskal = Kruskal.findMST(disconnected);
        MSTResult disconnectedPrim = Prim.findMST(disconnected);
        check("disconnected graph: detected by Kruskal and Prim",
                !disconnectedKruskal.isConnected()
                        && !disconnectedPrim.isConnected());
        check("disconnected graph: same forest weight = 5",
                disconnectedKruskal.getWeight() == 5
                        && disconnectedPrim.getWeight() == 5);

        Graph duplicateWeights = new Graph(3, false);
        duplicateWeights.addEdge(0, 1, 4);
        duplicateWeights.addEdge(1, 2, 4);
        duplicateWeights.addEdge(0, 2, 4);
        check("duplicate edge weights: MST weight = 8",
                Kruskal.findMST(duplicateWeights).getWeight() == 8
                        && Prim.findMST(duplicateWeights).getWeight() == 8);

        Graph one = new Graph(1, false);
        MSTResult oneKruskal = Kruskal.findMST(one);
        MSTResult onePrim = Prim.findMST(one);
        check("single vertex: connected, weight 0, no edges",
                oneKruskal.isConnected() && onePrim.isConnected()
                        && oneKruskal.getWeight() == 0
                        && onePrim.getWeight() == 0
                        && oneKruskal.getEdges().isEmpty()
                        && onePrim.getEdges().isEmpty());
    }

    private static void testDSU() {
        boolean[] options = {true, false};
        boolean ok = true;

        for (boolean compression : options) {
            for (boolean rank : options) {
                DisjointSetUnion sets =
                        new DisjointSetUnion(5, compression, rank);
                if (!sets.union(0, 1) || !sets.union(1, 2)) {
                    ok = false;
                }
                if (sets.union(0, 2)) {
                    ok = false;
                }
                if (sets.find(0) != sets.find(2)) {
                    ok = false;
                }
                if (sets.find(0) == sets.find(4)) {
                    ok = false;
                }
            }
        }

        check("DSU: find/union work for all four optimization variants", ok);
    }

    private static void testTopologicalSort() {
        Graph dag = new Graph(4, true);
        dag.addEdge(0, 1, 1);
        dag.addEdge(0, 2, 1);
        dag.addEdge(1, 3, 1);
        dag.addEdge(2, 3, 1);

        TopologicalSort.Result result = TopologicalSort.sort(dag);
        check("small DAG: acyclic with valid order",
                result.isAcyclic() && validOrder(dag, result.getOrder()));

        Graph cycle = new Graph(3, true);
        cycle.addEdge(0, 1, 1);
        cycle.addEdge(1, 2, 1);
        cycle.addEdge(2, 0, 1);
        TopologicalSort.Result cyclic = TopologicalSort.sort(cycle);
        check("cyclic graph: cycle detected, no full order",
                !cyclic.isAcyclic() && cyclic.getOrder().size() < 3);

        Graph one = new Graph(1, true);
        TopologicalSort.Result single = TopologicalSort.sort(one);
        check("single vertex: topological order is [0]",
                single.isAcyclic() && single.getOrder().equals(Arrays.asList(0)));
    }

    private static void testSCC() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);
        graph.addEdge(3, 4, 1);

        List<List<Integer>> components = KosarajuSCC.find(graph).getComponents();
        Set<Set<Integer>> actual = new HashSet<>();

        for (List<Integer> component : components) {
            actual.add(new HashSet<>(component));
        }

        Set<Set<Integer>> expected = new HashSet<>();
        expected.add(new HashSet<>(Arrays.asList(0, 1)));
        expected.add(new HashSet<>(Arrays.asList(2, 3)));
        expected.add(new HashSet<>(Arrays.asList(4)));

        check("SCC: expected groups {0,1}, {2,3}, {4}",
                actual.equals(expected));
        check("SCC: every vertex belongs to exactly one component",
                everyVertexOnce(components, 5));

        Graph one = new Graph(1, true);
        check("SCC: single vertex gives one component",
                KosarajuSCC.find(one).getComponents().size() == 1);
    }

    private static void testDijkstra() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 1);
        graph.addEdge(2, 1, 2);
        graph.addEdge(1, 3, 1);

        Dijkstra.Result result = Dijkstra.findPaths(graph, 0);
        check("Dijkstra: distances are 0, 3, 1, 4",
                result.getDistanceTo(0) == 0
                        && result.getDistanceTo(1) == 3
                        && result.getDistanceTo(2) == 1
                        && result.getDistanceTo(3) == 4);
        check("Dijkstra: path to 3 is [0, 2, 1, 3]",
                result.getPath(3).equals(Arrays.asList(0, 2, 1, 3)));
        check("Dijkstra: unreachable vertex handled",
                !result.isReachable(4) && result.getPath(4).isEmpty());

        Graph one = new Graph(1, true);
        Dijkstra.Result single = Dijkstra.findPaths(one, 0);
        check("Dijkstra: single vertex has distance 0",
                single.getDistanceTo(0) == 0
                        && single.getPath(0).equals(Arrays.asList(0)));

        Graph zeroWeights = new Graph(3, true);
        zeroWeights.addEdge(0, 1, 0);
        zeroWeights.addEdge(1, 2, 0);
        check("Dijkstra: zero-weight edges are allowed",
                Dijkstra.findPaths(zeroWeights, 0).getDistanceTo(2) == 0);

        Graph negative = new Graph(2, true);
        negative.addEdge(0, 1, -1);
        boolean rejected = false;
        try {
            Dijkstra.findPaths(negative, 0);
        } catch (IllegalArgumentException exception) {
            rejected = true;
        }
        check("Dijkstra: negative weight rejected", rejected);
    }

    private static void testRandomGraphs() {
        boolean mstSame = true;
        boolean orderValid = true;
        boolean sccOk = true;
        boolean dijkstraOk = true;

        for (long seed = 1; seed <= 20; seed++) {
            Graph undirected =
                    GraphGenerator.connectedUndirected(50, 200, seed);
            MSTResult kruskal = Kruskal.findMST(undirected);
            MSTResult prim = Prim.findMST(undirected);
            if (kruskal.getWeight() != prim.getWeight()
                    || !kruskal.isConnected() || !prim.isConnected()) {
                mstSame = false;
            }

            Graph dag = GraphGenerator.dag(50, 200, seed);
            TopologicalSort.Result topo = TopologicalSort.sort(dag);
            if (!topo.isAcyclic() || !validOrder(dag, topo.getOrder())) {
                orderValid = false;
            }

            Graph directed = GraphGenerator.randomDirected(50, 120, seed);
            if (!everyVertexOnce(KosarajuSCC.find(directed).getComponents(), 50)) {
                sccOk = false;
            }

            Graph weighted = GraphGenerator.randomDirected(30, 90, seed);
            Dijkstra.Result dijkstra = Dijkstra.findPaths(weighted, 0);
            long[] expected = bellmanFord(weighted, 0);

            for (int vertex = 0; vertex < 30; vertex++) {
                if (expected[vertex] == Long.MAX_VALUE) {
                    if (dijkstra.isReachable(vertex)) {
                        dijkstraOk = false;
                    }
                } else if (!dijkstra.isReachable(vertex)
                        || dijkstra.getDistanceTo(vertex) != expected[vertex]) {
                    dijkstraOk = false;
                }
            }
        }

        check("20 random connected graphs: Kruskal and Prim agree",
                mstSame);
        check("20 random DAGs: topological order is valid",
                orderValid);
        check("20 random directed graphs: SCC membership is complete",
                sccOk);
        check("20 random directed graphs: Dijkstra matches Bellman-Ford",
                dijkstraOk);

        int[][] sizes = {{10, 30}, {100, 500}, {1000, 5000}};
        boolean generatorsOk = true;

        for (int[] size : sizes) {
            Graph undirected = GraphGenerator.connectedUndirected(
                    size[0], size[1], 42);
            Graph dag = GraphGenerator.dag(size[0], size[1], 43);

            if (undirected.getEdges().size() != size[1]
                    || !Kruskal.findMST(undirected).isConnected()
                    || !TopologicalSort.sort(dag).isAcyclic()) {
                generatorsOk = false;
            }
        }

        check("generators: required edge counts, connected graphs and DAGs",
                generatorsOk);
    }

    private static boolean validOrder(Graph graph, List<Integer> order) {
        int vertices = graph.getVertexCount();
        if (order.size() != vertices) {
            return false;
        }

        int[] position = new int[vertices];
        Arrays.fill(position, -1);

        for (int i = 0; i < order.size(); i++) {
            int vertex = order.get(i);

            if (vertex < 0 || vertex >= vertices || position[vertex] != -1) {
                return false;
            }

            position[vertex] = i;
        }

        for (Edge edge : graph.getEdges()) {
            if (position[edge.getFrom()] >= position[edge.getTo()]) {
                return false;
            }
        }

        return true;
    }

    private static boolean everyVertexOnce(
            List<List<Integer>> components, int vertices) {
        int[] count = new int[vertices];

        for (List<Integer> component : components) {
            for (int vertex : component) {
                if (vertex < 0 || vertex >= vertices) {
                    return false;
                }
                count[vertex]++;
            }
        }

        for (int value : count) {
            if (value != 1) {
                return false;
            }
        }

        return true;
    }

    private static long[] bellmanFord(Graph graph, int source) {
        long[] distance = new long[graph.getVertexCount()];
        Arrays.fill(distance, Long.MAX_VALUE);
        distance[source] = 0;

        for (int round = 1; round < graph.getVertexCount(); round++) {
            boolean changed = false;

            for (Edge edge : graph.getEdges()) {
                if (distance[edge.getFrom()] != Long.MAX_VALUE) {
                    long candidate = distance[edge.getFrom()] + edge.getWeight();
                    if (candidate < distance[edge.getTo()]) {
                        distance[edge.getTo()] = candidate;
                        changed = true;
                    }
                }
            }

            if (!changed) {
                break;
            }
        }

        return distance;
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }
}