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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class Experiments {
    private static final long SEED = 42;
    private static final int REPEATS = 5;
    private static final int WARM_UP_RUNS = 20;
    private static final int BATCH_SIZE = 20;

    private static final int[][] REQUIRED_SIZES = {
            {10, 30},
            {100, 500},
            {1000, 5000}
    };

    private static final int FIXED_VERTEX_COUNT = 1000;
    private static final int[] EDGE_COUNTS = {
            2000, 5000, 10000, 20000, 50000
    };

    private interface Task {
        long[] run();
    }

    private static class Measurement {
        private final double averageNanos;
        private final long operations;
        private final long result;

        Measurement(double averageNanos, long operations, long result) {
            this.averageNanos = averageNanos;
            this.operations = operations;
            this.result = result;
        }
    }

    private static Measurement measure(Task task) {
        for (int i = 0; i < WARM_UP_RUNS; i++) {
            task.run();
        }

        long totalNanos = 0;
        long[] last = null;

        for (int repetition = 0; repetition < REPEATS; repetition++) {
            long start = System.nanoTime();
            for (int i = 0; i < BATCH_SIZE; i++) {
                last = task.run();
            }
            totalNanos += System.nanoTime() - start;
        }

        return new Measurement(
                totalNanos / (double) (REPEATS * BATCH_SIZE),
                last[0],
                last[1]);
    }

    private static void runMst(
            BufferedWriter writer,
            String experiment,
            Graph graph,
            int vertices,
            int edges) throws IOException {

        Measurement kruskal = measure(() -> {
            MSTResult result = Kruskal.findMST(graph);
            return new long[]{result.getOperations(), result.getWeight()};
        });

        Measurement prim = measure(() -> {
            MSTResult result = Prim.findMST(graph);
            return new long[]{result.getOperations(), result.getWeight()};
        });

        if (kruskal.result != prim.result) {
            throw new IllegalStateException(
                    "Kruskal and Prim returned different MST weights");
        }
    }

    private static void runDijkstra(
            BufferedWriter writer,
            String experiment,
            Graph graph,
            String graphType,
            int vertices,
            int edges) throws IOException {

        Measurement measurement = measure(() -> {
            Dijkstra.Result result = Dijkstra.findPaths(graph, 0);
            long reachable = 0;
            for (int vertex = 0; vertex < vertices; vertex++) {
                if (result.isReachable(vertex)) {
                    reachable++;
                }
            }
            return new long[]{result.getOperations(), reachable};
        });
    }

    private static void runTopological(
            BufferedWriter writer,
            String experiment,
            Graph graph,
            String graphType,
            int vertices,
            int edges) throws IOException {

        Measurement measurement = measure(() -> {
            TopologicalSort.Result result = TopologicalSort.sort(graph);
            return new long[]{result.getOperations(), result.getOrder().size()};
        });
    }

    private static void runScc(
            BufferedWriter writer,
            String experiment,
            Graph graph,
            String graphType,
            int vertices,
            int edges) throws IOException {

        Measurement measurement = measure(() -> {
            KosarajuSCC.Result result = KosarajuSCC.find(graph);
            return new long[]{result.getOperations(),
                    result.getComponents().size()};
        });
    }

    private static void runDsuComparison(
            BufferedWriter writer,
            Graph graph,
            int vertices,
            int edges) throws IOException {

        boolean[] options = {true, false};

        for (boolean compression : options) {
            for (boolean rank : options) {
                Measurement measurement = measure(() -> {
                    DisjointSetUnion sets = new DisjointSetUnion(
                            vertices, compression, rank);

                    for (Edge edge : graph.getEdges()) {
                        sets.union(edge.getFrom(), edge.getTo());
                    }

                    for (int vertex = 0; vertex < vertices; vertex++) {
                        sets.find(vertex);
                    }

                    return new long[]{sets.getOperations(), 0};
                });
            }
        }
    }

    private static Graph cyclicDirected(
            int vertices, int edgeCount, long seed) {

        long maximumEdges = (long) vertices * (vertices - 1);
        if (vertices < 2 || edgeCount < vertices
                || edgeCount > maximumEdges) {
            throw new IllegalArgumentException(
                    "A cyclic graph needs V >= 2 and V <= E <= V(V-1)");
        }

        Graph graph = new Graph(vertices, true);
        Random random = new Random(seed);
        Set<Long> used = new HashSet<>();

        for (int vertex = 0; vertex < vertices; vertex++) {
            int next = (vertex + 1) % vertices;
            used.add(edgeKey(vertex, next));
            graph.addEdge(vertex, next, random.nextInt(100) + 1);
        }

        while (used.size() < edgeCount) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);

            if (from == to) {
                continue;
            }

            if (used.add(edgeKey(from, to))) {
                graph.addEdge(from, to, random.nextInt(100) + 1);
            }
        }

        return graph;
    }

    private static long edgeKey(int from, int to) {
        return ((long) from << 32) | (to & 0xffffffffL);
    }
}