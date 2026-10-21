package dsu;

public class DisjointSetUnion {
    private final int[] parent;
    private final int[] rank;
    private final boolean pathCompression;
    private final boolean unionByRank;
    private long operations;

    public DisjointSetUnion(int size) {
        this(size, true, true);
    }

    public DisjointSetUnion(int size, boolean pathCompression, boolean unionByRank) {
        this.pathCompression = pathCompression;
        this.unionByRank = unionByRank;
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public int find(int value) {
        operations++;
        int root = value;
        while (parent[root] != root) {
            root = parent[root];
            operations++;
        }
        if (pathCompression) {
            while (parent[value] != root) {
                int next = parent[value];
                parent[value] = root;
                value = next;
                operations++;
            }
        }
        return root;
    }

    public boolean union(int first, int second) {
        operations++;
        int firstRoot = find(first);
        int secondRoot = find(second);
        if (firstRoot == secondRoot) {
            return false;
        }
        if (!unionByRank) {
            parent[secondRoot] = firstRoot;
        } else if (rank[firstRoot] < rank[secondRoot]) {
            parent[firstRoot] = secondRoot;
        } else if (rank[firstRoot] > rank[secondRoot]) {
            parent[secondRoot] = firstRoot;
        } else {
            parent[secondRoot] = firstRoot;
            rank[firstRoot]++;
        }
        return true;
    }

    public long getOperations() {
        return operations;
    }
}