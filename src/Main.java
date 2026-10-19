import connectivity.KosarajuSCC;
import connectivity.TopologicalSort;
import graph.Graph;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;

import java.util.List;

public class Main {
    private static final String[] DISTRICTS = {"Center", "North", "East", "South", "West"};

    public static void main(String[] args) {
        mstDemo();
    }

    private static void mstDemo() {
        System.out.println("--- Minimum spanning tree ---");
        Graph roads = new Graph(5, false);
        roads.addEdge(0, 1, 4);
        roads.addEdge(0, 2, 3);
        roads.addEdge(1, 2, 1);
        roads.addEdge(1, 3, 5);
        roads.addEdge(2, 3, 8);
        roads.addEdge(3, 4, 2);
        roads.addEdge(2, 4, 9);

        MSTResult kruskal = Kruskal.findMST(roads);
        MSTResult prim = Prim.findMST(roads);
        System.out.println("Kruskal: weight = " + kruskal.getWeight() + ", connected = " + kruskal.isConnected());
        System.out.println("Prim: weight = " + prim.getWeight() + ", connected = " + prim.isConnected());
        System.out.println();
    }
}