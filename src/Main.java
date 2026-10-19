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
        connectivityDemo();
        shortestPathDemo();
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

    private static void connectivityDemo() {
        System.out.println("--- Connectivity ---");
        Graph dag = new Graph(5, true);
        dag.addEdge(0, 1, 1);
        dag.addEdge(0, 2, 1);
        dag.addEdge(1, 3, 1);
        dag.addEdge(2, 3, 1);
        dag.addEdge(3, 4, 1);
        TopologicalSort.Result topo = TopologicalSort.sort(dag);
        System.out.println("DAG acyclic = " + topo.isAcyclic() + ", order = " + names(topo.getOrder()));

        Graph cyclic = new Graph(5, true);
        cyclic.addEdge(0, 1, 1);
        cyclic.addEdge(1, 2, 1);
        cyclic.addEdge(2, 0, 1);
        cyclic.addEdge(2, 3, 1);
        cyclic.addEdge(3, 4, 1);
        cyclic.addEdge(4, 3, 1);
        System.out.println("Cyclic graph acyclic = " + TopologicalSort.sort(cyclic).isAcyclic()
                + " (topological order impossible)");

        List<List<Integer>> components = KosarajuSCC.find(cyclic).getComponents();
        System.out.println("Strongly connected components (" + components.size() + "):");
        for (List<Integer> component : components) {
            System.out.println("  " + names(component));
        }
        System.out.println();
    }

    private static void shortestPathDemo() {
        System.out.println("--- Shortest paths from Center ---");
        Graph city = new Graph(5, true);
        city.addEdge(0, 1, 4);
        city.addEdge(0, 2, 2);
        city.addEdge(2, 1, 1);
        city.addEdge(1, 3, 3);
        city.addEdge(4, 3, 1);

        Dijkstra.Result result = Dijkstra.findPaths(city, 0);
        for (int vertex = 0; vertex < DISTRICTS.length; vertex++) {
            if (result.isReachable(vertex)) {
                System.out.println(DISTRICTS[vertex] + ": distance = " + result.getDistanceTo(vertex)
                        + ", path = " + names(result.getPath(0, vertex)));
            } else {
                System.out.println(DISTRICTS[vertex] + ": unreachable");
            }
        }
    }

    private static String names(List<Integer> vertices) {
        StringBuilder text = new StringBuilder("[");
        for (int i = 0; i < vertices.size(); i++) {
            if (i > 0) {
                text.append(" -> ");
            }
            text.append(DISTRICTS[vertices.get(i)]);
        }
        return text.append("]").toString();
    }
}