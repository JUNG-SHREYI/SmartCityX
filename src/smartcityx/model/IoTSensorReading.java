package smartcityx.model;

/**
 * Represents a streaming observation emitted from city-wide IoT telemetry devices.
 */
public class IoTSensorReading {
    private final String sensorId;
    private final String sensorType; // "AQI_PM25", "TRAFFIC_FLOW", "WATER_PRESSURE", "GRID_LOAD_MW", "NOISE_DB"
    private final String sector;
    private final double value;
    private final String unit;
    private final long timestamp;

    public IoTSensorReading(String sensorId, String sensorType, String sector, double value, String unit, long timestamp) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.sector = sector;
        this.value = value;
        this.unit = unit;
        this.timestamp = timestamp;
    }

    public String getSensorId() { return sensorId; }
    public String getSensorType() { return sensorType; }
    public String getSector() { return sector; }
    public double getValue() { return value; }
    public String getUnit() { return unit; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + sensorId + " - " + sector + "] " + sensorType + ": " + String.format("%.2f", value) + " " + unit;
    }
}
