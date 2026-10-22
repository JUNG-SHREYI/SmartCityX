package smartcityx.model;

/**
 * Represents a vertex in a city utility flow network (Power transmission, Water grid, Evacuation routes).
 */
public class UtilityNetworkNode {
    private final int id;
    private final String nodeCode;
    private final String name;
    private final String nodeType; // "SOURCE", "SUBSTATION", "PUMPING_STATION", "DISTRICT_HUB", "SINK"
    private final double maxCapacity;

    public UtilityNetworkNode(int id, String nodeCode, String name, String nodeType, double maxCapacity) {
        this.id = id;
        this.nodeCode = nodeCode;
        this.name = name;
        this.nodeType = nodeType;
        this.maxCapacity = maxCapacity;
    }

    public int getId() { return id; }
    public String getNodeCode() { return nodeCode; }
    public String getName() { return name; }
    public String getNodeType() { return nodeType; }
    public double getMaxCapacity() { return maxCapacity; }

    @Override
    public String toString() {
        return "[" + nodeCode + " (" + id + ")] " + name + " (" + nodeType + ") Cap=" + maxCapacity;
    }
}
