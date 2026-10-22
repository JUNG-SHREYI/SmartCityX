package smartcityx.model;

/**
 * Represents a specialized municipal emergency or maintenance response crew.
 */
public class ServiceCrew {
    private final String crewId;
    private final String crewName;
    private final String specialty; // "FIRE_RESCUE", "ELECTRICAL_GRID", "WATER_MAINS", "ROAD_WORKS", "HAZMAT", "TRAFFIC_POLICE"
    private final String baseStationSector;
    private String status;           // "AVAILABLE", "DISPATCHED", "OFF_DUTY"

    public ServiceCrew(String crewId, String crewName, String specialty, String baseStationSector) {
        this.crewId = crewId;
        this.crewName = crewName;
        this.specialty = specialty;
        this.baseStationSector = baseStationSector;
        this.status = "AVAILABLE";
    }

    public String getCrewId() { return crewId; }
    public String getCrewName() { return crewName; }
    public String getSpecialty() { return specialty; }
    public String getBaseStationSector() { return baseStationSector; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "[" + crewId + "] " + crewName + " (" + specialty + ") at " + baseStationSector + " - " + status;
    }
}
