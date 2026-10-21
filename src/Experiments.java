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
}