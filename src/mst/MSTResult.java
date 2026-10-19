package mst;

public class MSTResult {
    private final long weight;
    private final boolean connected;
    private final long operations;

    public MSTResult(long weight, boolean connected, long operations) {
        this.weight = weight;
        this.connected = connected;
        this.operations = operations;
    }

    public long getWeight() {
        return weight;
    }
    public boolean isConnected() {
        return connected;
    }
    public long getOperations() {
        return operations;
    }
}