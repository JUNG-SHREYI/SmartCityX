package smartcityx.model;

/**
 * Represents a civic issue, service request, or emergency report filed by a citizen or automated hotline.
 */
public class CitizenReport {
    private final String reportId;
    private final String citizenName;
    private final String category;      // e.g. "FIRE", "WATER_LEAK", "POWER_OUTAGE", "TRAFFIC_COLLISION", "POTHOLE"
    private final String locationSector; // e.g. "Sector-4", "Zone-B", "North-Hub"
    private final int priority;          // 1 (Critical/Emergency) to 5 (Routine)
    private final String description;
    private final long timestamp;
    private String status;               // "OPEN", "DISPATCHED", "RESOLVED"
    private String assignedCrewId;
    private String assignmentMethod;

    public CitizenReport(String reportId, String citizenName, String category, String locationSector,
                         int priority, String description, long timestamp) {
        this.reportId = reportId;
        this.citizenName = citizenName;
        this.category = category;
        this.locationSector = locationSector;
        this.priority = priority;
        this.description = description;
        this.timestamp = timestamp;
        this.status = "OPEN";
    }

    public String getReportId() { return reportId; }
    public String getCitizenName() { return citizenName; }
    public String getCategory() { return category; }
    public String getLocationSector() { return locationSector; }
    public int getPriority() { return priority; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAssignedCrewId() { return assignedCrewId; }
    public String getAssignmentMethod() { return assignmentMethod; }

    public void assignCrew(String crewId, String method) {
        this.assignedCrewId = crewId;
        this.assignmentMethod = method;
        this.status = "DISPATCHED";
    }

    @Override
    public String toString() {
        return "[" + reportId + "] " + category + " at " + locationSector + " (P" + priority + "): " + description;
    }
}
