package tests;

import dsu.DisjointSetUnion;
import graph.Graph;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;

public class Tests {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testMST();
        testDSU();

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