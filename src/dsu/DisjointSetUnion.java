package dsu;

public class DisjointSetUnion {
    private final int[] parent;
    private final int[] rank;
    private long operations;

    public DisjointSetUnion(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }
}