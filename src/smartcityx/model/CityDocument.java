package smartcityx.model;

/**
 * Represents municipal infrastructure documentation, operational protocols, zoning bylaws, or environmental regulations.
 */
public class CityDocument {
    private final String docId;
    private final String title;
    private final String department; // "TRANSPORTATION", "WATER_RESOURCES", "EMERGENCY_SERVICES", "URBAN_PLANNING", "ENVIRONMENT"
    private final String fullText;

    public CityDocument(String docId, String title, String department, String fullText) {
        this.docId = docId;
        this.title = title;
        this.department = department;
        this.fullText = fullText;
    }

    public String getDocId() { return docId; }
    public String getTitle() { return title; }
    public String getDepartment() { return department; }
    public String getFullText() { return fullText; }

    @Override
    public String toString() {
        return "[" + docId + "] " + title + " (" + department + ")";
    }
}
