# Assignment 3

## Overview

This Java project models a city's districts as graph vertices and roads or infrastructure connections as weighted edges. It implements graph algorithms for minimum-cost infrastructure planning, directed connectivity analysis, and shortest-route planning

The application implements:

- Disjoint Set Union(DSU), using path compression and union by rank
- Kruskal's and Prim's minimum spanning tree(MST) algorithms
- Kahn's topological sorting algorithm, including cycle detection
- Kosaraju's algorithm for strongly connected components(SCCs)
- Dijkstra's shortest-path algorithm for non-negative edge weights
- Fixed-seed graph generation, benchmarking, and plotting

The graph is represented by an edge list and adjacency lists. Vertex IDs are integers from `0` to `V - 1`. An undirected edge is stored once in the main edge list and in both endpoint adjacency lists. A directed edge appears in the adjacency list of its source vertex only

## 2. Project Structure

```text
assignment-3/
├── src/
│   ├── graph/
│   │   ├── Edge.java
│   │   ├── Graph.java
│   │   └── GraphGenerator.java
│   ├── dsu/
│   │   └── DisjointSetUnion.java
│   ├── mst/
│   │   ├── Kruskal.java
│   │   ├── Prim.java
│   │   └── MSTResult.java
│   ├── connectivity/
│   │   ├── TopologicalSort.java
│   │   └── KosarajuSCC.java
│   ├── shortestpath/
│   │   └── Dijkstra.java
│   ├── tests/
│   │   └── Tests.java
│   ├── Main.java
│   └── Experiments.java
├── results/
│   ├── tables/
│   │   └── benchmark_results.csv
│   └── plots/
│       ├── plot_results.py
│       ├── time_vs_vertices.png
│       └── time_vs_edges.png
├── .gitignore
├── DAA3.iml
└── README.md
```

## Implementation

### Graph representation and generators

`Edge` stores a source vertex, destination vertex, and integer weight. `Graph` supports directed and undirected graphs and provides the edge list and adjacency lists. `GraphGenerator` creates reproducible test graphs using `java.util.Random` and fixed seeds. Generated edges have weights from 1 to 100

The undirected generator first creates a spanning tree, which guarantees connectivity, then adds unique edges until the requested edge count is reached. The DAG generator orients each edge from a lower-numbered vertex to a higher-numbered vertex, so directed cycles cannot occur. The benchmark's cyclic directed generator creates a directed ring through all vertices, then adds further unique directed edges. The ring guarantees that the graph contains a cycle and that all vertices belong to one strongly connected component

### Disjoint Set Union (DSU)

`DisjointSetUnion` supports `find` and `union` and initializes each vertex as its own set. It implements two optimizations:

- **Path compression:** during `find`, visited parent pointers are updated to point directly to the root, shortening future searches
- **Union by rank:** when merging two sets, the root with lower rank is attached below the root with higher rank. If their ranks are equal, one root is attached below the other and the surviving root's rank increases

### Kruskal and Prim

Kruskal sorts the edge list by weight and uses DSU to reject edges that would create a cycle. Prim grows a tree by repeatedly selecting the lowest-weight available edge from a priority queue. Both return an `MSTResult` containing the selected edges, their total weight, a connectivity flag, and an operation counter

For connected, undirected weighted graphs, both algorithms should return the same minimum spanning tree weight and select `V - 1` edges. For disconnected graphs, the implementations return the minimum spanning trees of each component and report that the graph is not connected

### Topological ordering and strongly connected components

`TopologicalSort` implements Kahn's algorithm. It returns an ordering and an `isAcyclic` flag. A valid DAG produces an ordering containing all vertices. If the ordering contains fewer than `V` vertices, a directed cycle exists and a complete topological order is impossible.

`KosarajuSCC` performs a first depth-first search (DFS) to record finishing order, reverses the directed edges, and performs a second DFS in reverse finishing order. Each DFS tree in the second pass forms one strongly connected component.

### Dijkstra's shortest paths

`Dijkstra` uses an adjacency list and Java's priority queue. It computes shortest distances from a selected source vertex, stores predecessor vertices, and can reconstruct a path to each reachable destination. Unreachable vertices are marked as unreachable and have no path

Dijkstra requires non-negative edge weights. With negative edges, a path discovered later may improve the distance of a vertex that has already been processed, so the algorithm's greedy selection rule is no longer generally correct. This implementation checks the graph's edge list and throws `IllegalArgumentException` if any edge has a negative weight

## Complexity Analysis

Let `V` be the number of vertices and `E` the number of edges. The graph uses adjacency lists; the priority queues use binary heaps. Prim and Dijkstra use lazy priority queues, which may contain duplicate entries

| Algorithm | Time complexity | Space complexity | Explanation |
|---|---|---|---|
| DSU initialization | `O(V)` | `O(V)` | Creates one set per vertex |
| DSU `find`/`union` with both optimizations | Amortized `O(α(V))` per operation | `O(V)` | `α(V)` is the inverse Ackermann function and is very small for practical input sizes |
| DSU without either optimization | A single `find` can take `O(V)` | `O(V)` | Without rank, trees can become tall. Union by rank alone keeps height to `O(log V)` |
| Kruskal | `O(E log E + E α(V) + V)` | `O(V + E)` | Sorting the edges is the main cost. |
| Prim with a lazy priority queue | `O(V + E log E)` | `O(V + E)` | Candidate edges are stored in the priority queue |
| Kosaraju SCC | `O(V + E)` | `O(V + E)` | Two DFS passes and a reversed graph |
| Kahn topological sort | `O(V + E)` | `O(V)` additional space | Processes vertices and edges a constant number of times |
| Dijkstra with a lazy binary heap | `O((V + E) log E)` | `O(V + E)` | Processes reachable edges and priority-queue entries |

In general, increasing `V` adds initialization and vertex-processing work. Increasing `E` adds work to sorting, edge scans, and priority queues. The exact runtime also depends on graph structure and JVM/system conditions

## Experimental Setup

The benchmark uses a fixed base seed of `42`. Generated graphs are reproducible when the source code and seed stay the same. Weights are positive integers from 1 to 100

### Graph sizes

| Test | Vertices (`V`) | Edges (`E`) |
|---|---:|---:|
| Small | 10 | 30 |
| Medium | 100 | 500 |
| Large | 1,000 | 5,000 |
| Extra edge-count experiment | 1,000 | 2,000; 5,000; 10,000; 20,000; 50,000 |

The undirected generator first builds a spanning tree, ensuring that the graph is connected, and then adds unique edges. DAG edges always go from a lower-numbered vertex to a higher-numbered one, which prevents cycles. The cyclic directed generator creates a directed ring through every vertex and then adds more edges. Therefore, the generated cyclic graph is strongly connected and has one SCC

For each required size, the benchmark runs Kruskal, Prim, and Dijkstra on the connected undirected graph. Topological sort and SCC are tested on both a DAG and a cyclic directed graph. DSU is measured with both optimizations enabled or disabled in all four combinations. An extra experiment holds `V = 1000` fixed and increases `E`

### Timing and operation counts

`Experiments.java` performs 20 warm-up invocations, then five timed repetitions of 20 executions each. It uses `System.nanoTime()` and reports the average nanoseconds per execution. Graph generation and graph-property checks are outside the timed sections

Operation counters are specific to each implementation, so they should not be treated as directly equivalent across different algorithms. Kruskal counts sorting comparisons, edge scanning, and DSU work. Prim and Dijkstra count selected queue and edge operations, but not all internal heap comparisons. Topological sort counts edge examinations. Kosaraju counts edge examinations and reversed-edge construction. DSU counts DSU calls and parent traversals/updates

Timing measurements depend on the processor, JVM, operating-system scheduling, and background load. The results below are one measured run

## Results

Times below are in microseconds. Timings are measurements from one run and can vary between computers and runs

### Kruskal, Prim, and Dijkstra

| `V` | `E` | Kruskal time(ops) | Prim time(ops) | MST weight(both) | Dijkstra time(ops) | Dijkstra reachable vertices |
|---:|---:|---:|---:|---:|---:|---:|
| 10 | 30 | 19.650 (275) | 22.238 (122) | 221 | 20.522 (94) | 10 |
| 100 | 500 | 69.773 (6,817) | 146.374 (2,002) | 1,320 | 42.746 (1,386) | 100 |
| 1,000 | 5,000 | 916.515 (80,719) | 1,258.397 (20,002) | 12,593 | 512.591 (13,914) | 1,000 |

Kruskal and Prim return the same MST weight for all three required graph sizes. All vertices are reachable from source `0` in the connected undirected graphs

### Topological sort and SCC

| `V` | `E` | Topological DAG time | DAG ordered vertices | Topological cyclic time | Cyclic ordered vertices | SCC DAG time | SCCs in DAG | SCC cyclic time | SCCs in cyclic graph |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 10 | 30 | 12.681 | 10 | 4.467 | 0 | 17.030 | 10 | 7.433 | 1 |
| 100 | 500 | 16.203 | 100 | 4.715 | 0 | 40.741 | 100 | 174.697 | 1 |
| 1,000 | 5,000 | 248.195 | 1,000 | 45.343 | 0 | 378.119 | 1,000 | 316.333 | 1 |

Every DAG has a complete topological order and one SCC per vertex. The cyclic graph has no initial zero-indegree vertices, so Kahn's algorithm detects the cycle after the indegree scan and returns an empty order. The ring connects all vertices, so the cyclic graph has one SCC

### DSU optimization comparison

Operation counts for the 1,000-vertex, 5,000-edge graph:

| Path compression | Union by rank | DSU operations |
|:---:|:---:|---:|
| Yes | Yes | 25,991 |
| Yes | No | 39,011 |
| No | Yes | 25,991 |
| No | No | 4,779,911 |

The unoptimized version performs far more parent traversals. Path compression flattens paths, while union by rank prevents the trees from becoming too tall. This difference is clearest in the operation count; small runtime differences are more affected by timing noise

### Execution time versus edge count (`V = 1,000`)

| `E` | Kruskal | Prim | Dijkstra | Topological DAG | Topological cyclic | SCC DAG | SCC cyclic |
|---:|---:|---:|---:|---:|---:|---:|---:|
| 2,000 | 234.110 | 292.395 | 442.659 | 260.750 | 37.189 | 341.082 | 261.510 |
| 5,000 | 503.999 | 1,166.157 | 743.890 | 102.435 | 14.866 | 214.678 | 122.409 |
| 10,000 | 1,389.770 | 1,956.655 | 453.862 | 87.537 | 14.570 | 516.693 | 245.897 |
| 20,000 | 2,627.509 | 4,639.879 | 685.735 | 198.711 | 24.214 | 577.801 | 512.863 |
| 50,000 | 5,322.957 | 11,211.864 | 1,107.954 | 526.942 | 65.567 | 1,890.504 | 2,288.021 |

Kruskal, Prim, and SCC generally take longer as the number of edges increases. Some timing values fluctuate, especially for small tasks, because measurements are affected by JVM and system activity

### Plots

**Execution time versus number of vertices**

![Execution time versus number of vertices](results/plots/time_vs_vertices.png)

**Execution time versus number of edges, with `V = 1000`**

![Execution time versus number of edges](results/plots/time_vs_edges.png)

## Comparison and Discussion

- **Kruskal vs. Prim:** Both find the same MST weight. Kruskal sorts the edge list, while Prim grows a tree with a priority queue. Their relative speed depends on the graph and runtime conditions; these measurements do not prove that one is always faster
- **DSU:** Path compression and union by rank reduce parent traversals and keep trees shallow. The unoptimized DSU has a much higher operation count at `V = 1000`
- **SCC:** Kosaraju runs in `O(V + E)`. Its work grows with the graph, while the number of components depends on connectivity: the generated DAG has `V` components and the generated cyclic graph has one
- **Topological sort:** The generated DAGs produce full orders. Cyclic graphs are detected, and a full order is impossible
- **Dijkstra:** It finds shortest routes when all edge weights are non-negative. It reports unreachable vertices when no route exists; tests check this behavior

Theoretical complexity describes how work grows with the input. Actual times also depend on graph structure, memory allocation, JVM behavior, and background system load

## Testing

The test runner is `src/tests/Tests.java`. It uses a small `check()` helper rather than JUnit, so the project has no external Java testing dependencies. A successful run ends with:

```text
PASS: small graph with cycles: Kruskal weight = 6
PASS: small graph with cycles: Prim weight = 6
PASS: multiple valid MSTs: both weights = 3
PASS: multiple valid MSTs: both use V-1 edges
PASS: multiple valid MSTs: both report connected
PASS: disconnected graph: detected by Kruskal and Prim
PASS: disconnected graph: same forest weight = 5
PASS: duplicate edge weights: MST weight = 8
PASS: single vertex: connected, weight 0, no edges
PASS: DSU: find/union work for all four optimization variants
PASS: small DAG: acyclic with valid order
PASS: cyclic graph: cycle detected, no full order
PASS: single vertex: topological order is [0]
PASS: SCC: expected groups {0,1}, {2,3}, {4}
PASS: SCC: every vertex belongs to exactly one component
PASS: SCC: single vertex gives one component
PASS: Dijkstra: distances are 0, 3, 1, 4
PASS: Dijkstra: path to 3 is [0, 2, 1, 3]
PASS: Dijkstra: unreachable vertex handled
PASS: Dijkstra: single vertex has distance 0
PASS: Dijkstra: zero-weight edges are allowed
PASS: Dijkstra: negative weight rejected
PASS: 20 random connected graphs: Kruskal and Prim agree
PASS: 20 random DAGs: topological order is valid
PASS: 20 random directed graphs: SCC membership is complete
PASS: 20 random directed graphs: Dijkstra matches Bellman-Ford
PASS: generators: required edge counts, connected graphs and DAGs

Passed: 27, Failed: 0
```

The tests cover:

- Small weighted graphs containing cycles
- Disconnected undirected graphs and a single-vertex graph
- Multiple valid MSTs and duplicate edge weights
- Agreement between Kruskal and Prim on sample graphs and 20 generated connected graphs
- DSU `find` and `union` for all four path-compression/union-by-rank configurations
- Valid topological order for DAGs, directed-cycle detection, and a single-vertex DAG
- Expected SCC groups and verification that every vertex belongs to exactly one component
- Dijkstra distances and reconstructed paths, unreachable vertices, zero-weight edges, a single vertex, and rejection of negative weights
- Dijkstra distances compared with a Bellman–Ford reference on 20 random directed graphs with non-negative weights
- Required graph sizes, edge counts, connectedness of generated undirected graphs, and acyclicity of generated DAGs

## Conclusion

The project implements and tests the required graph algorithms. The tests check their correctness on small, disconnected, cyclic, and randomly generated graphs. The experiments measure execution time and operation counts for different graph sizes to compare practical performance with theoretical complexity

The results show that each algorithm is designed for a different task, and the best choice depends on the graph structure and the problem being solved
- Kruskal is a good choice for finding a minimum-cost network when the graph is represented as an edge list. It sorts the edges by weight and uses DSU to avoid cycles. It can also produce a minimum spanning forest when the graph is disconnected
- Prim finds a minimum spanning tree by gradually connecting new vertices using a priority queue. Like Kruskal, it produces a minimum-cost network for a connected, weighted, undirected graph. Its performance depends on the graph representation and priority-queue implementation
- DSU helps Kruskal process edges efficiently. Path compression shortens paths to the root, while union by rank keeps the trees shallow. Using both optimizations generally reduces the number of operations compared with an unoptimized implementation
- Kosaraju's algorithm identifies strongly connected components in directed graphs. Its linear time complexity, (O(V+E)), makes it suitable for analyzing large networks and determining which locations can reach one another through directed paths
- Kahn's topological sorting algorithm finds a valid order for processing dependencies in a directed acyclic graph. It can also detect cycles when it cannot include every vertex in the result
- Dijkstra's algorithm finds shortest paths from a selected source when all edge weights are non-negative. It is useful for route planning, but it must not be used with negative edge weights because its greedy approach does not guarantee correct shortest paths in that case

The experiments also demonstrate that increasing the number of vertices and edges generally increases the amount of work required. However, measured execution times can change because of JVM behavior, system activity, and differences in graph structure
