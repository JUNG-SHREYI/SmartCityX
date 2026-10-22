package smartcityx.model;

/**
 * Represents a physical or electrical/water asset in the municipal inventory.
 */
public class CityInfrastructure {
    private final String assetId;
    private final String name;
    private final String sector;
    private final String assetType; // "POWER_SUBSTATION", "WATER_TREATMENT", "TRAFFIC_JUNCTION", "BRIDGE", "HOSPITAL"
    private final double capacity;  // MW, Mega-liters/day, Vehicles/hr, etc.
    private double healthIndex;     // 0.0 to 100.0%
    private String status;          // "OPERATIONAL", "MAINTENANCE_REQUIRED", "CRITICAL_FAIL"

    public CityInfrastructure(String assetId, String name, String sector, String assetType,
                              double capacity, double healthIndex, String status) {
        this.assetId = assetId;
        this.name = name;
        this.sector = sector;
        this.assetType = assetType;
        this.capacity = capacity;
        this.healthIndex = healthIndex;
        this.status = status;
    }

    public String getAssetId() { return assetId; }
    public String getName() { return name; }
    public String getSector() { return sector; }
    public String getAssetType() { return assetType; }
    public double getCapacity() { return capacity; }
    public double getHealthIndex() { return healthIndex; }
    public void setHealthIndex(double healthIndex) { this.healthIndex = healthIndex; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "[" + assetId + "] " + name + " (" + assetType + ") in " + sector + " | Health: " + healthIndex + "%";
    }
}
