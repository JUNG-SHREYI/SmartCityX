package smartcityx.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import smartcityx.io.DatasetLoader;
import smartcityx.model.CitizenReport;
import smartcityx.model.CityDocument;
import smartcityx.model.CityInfrastructure;
import smartcityx.model.IoTSensorReading;
import smartcityx.model.ServiceCrew;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

/** Serves a structured city dataset to the React dashboard. */
public final class SmartCityXWebServer {
    private static final Path WEB_ROOT = Paths.get("web", "dist").toAbsolutePath().normalize();
    private static final DatasetLoader.SmartCityDataBundle CITY_DATA = DatasetLoader.loadDefaultCityData();

    private SmartCityXWebServer() {
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/", SmartCityXWebServer::handleRequest);
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("SmartCityX dashboard running at http://127.0.0.1:" + port);
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/api/health".equals(path) && "GET".equals(exchange.getRequestMethod())) {
            sendJson(exchange, 200, "{\"status\":\"ok\"}");
            return;
        }
        if ("/api/dashboard".equals(path)) {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            } else {
                sendJson(exchange, 200, dashboardJson());
            }
            return;
        }
        if ("/api/reports".equals(path)) {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            } else {
                createReport(exchange);
            }
            return;
        }
        if (path.startsWith("/api/reports/") && path.endsWith("/assignment")) {
            if (!"PUT".equals(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            } else {
                assignReport(exchange, path.substring("/api/reports/".length(), path.length() - "/assignment".length()));
            }
            return;
        }
        if (path.startsWith("/api/reports/") && path.indexOf('/', "/api/reports/".length()) < 0) {
            if (!"DELETE".equals(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            } else {
                deleteReport(exchange, path.substring("/api/reports/".length()));
            }
            return;
        }
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        serveStaticFile(exchange, path);
    }

    private static synchronized String dashboardJson() {
        StringBuilder json = new StringBuilder(64000);
        json.append("{\"source\":\"SmartCityX project dataset\",\"generatedAt\":")
                .append(System.currentTimeMillis()).append(",\"reports\":[");
        boolean firstReport = true;
        for (int i = 0; i < CITY_DATA.reports.size(); i++) {
            CitizenReport report = CITY_DATA.reports.get(i);
            if (!isDashboardReport(report)) continue;
            if (!firstReport) json.append(',');
            firstReport = false;
            json.append("{\"id\":").append(quote(report.getReportId()))
                    .append(",\"citizen\":").append(quote(report.getCitizenName()))
                    .append(",\"category\":").append(quote(report.getCategory()))
                    .append(",\"sector\":").append(quote(report.getLocationSector()))
                    .append(",\"priority\":").append(report.getPriority())
                    .append(",\"description\":").append(quote(report.getDescription()))
                    .append(",\"timestamp\":").append(report.getTimestamp())
                    .append(",\"status\":").append(quote(report.getStatus()))
                    .append(",\"assignedCrewId\":").append(nullableQuote(report.getAssignedCrewId()))
                    .append(",\"assignedCrewName\":").append(nullableQuote(crewName(report.getAssignedCrewId())))
                    .append(",\"assignmentMethod\":").append(nullableQuote(report.getAssignmentMethod())).append('}');
        }
        json.append("],\"assets\":[");
        for (int i = 0; i < CITY_DATA.assets.size(); i++) {
            if (i > 0) json.append(',');
            CityInfrastructure asset = CITY_DATA.assets.get(i);
            json.append("{\"id\":").append(quote(asset.getAssetId()))
                    .append(",\"name\":").append(quote(asset.getName()))
                    .append(",\"sector\":").append(quote(asset.getSector()))
                    .append(",\"type\":").append(quote(asset.getAssetType()))
                    .append(",\"capacity\":").append(asset.getCapacity())
                    .append(",\"health\":").append(asset.getHealthIndex())
                    .append(",\"status\":").append(quote(asset.getStatus())).append('}');
        }
        json.append("],\"sensors\":[");
        for (int i = 0; i < CITY_DATA.sensorStreams.size(); i++) {
            if (i > 0) json.append(',');
            IoTSensorReading sensor = CITY_DATA.sensorStreams.get(i);
            json.append("{\"id\":").append(quote(sensor.getSensorId()))
                    .append(",\"type\":").append(quote(sensor.getSensorType()))
                    .append(",\"sector\":").append(quote(sensor.getSector()))
                    .append(",\"value\":").append(sensor.getValue())
                    .append(",\"unit\":").append(quote(sensor.getUnit()))
                    .append(",\"timestamp\":").append(sensor.getTimestamp()).append('}');
        }
        json.append("],\"crews\":[");
        for (int i = 0; i < CITY_DATA.crews.size(); i++) {
            if (i > 0) json.append(',');
            ServiceCrew crew = CITY_DATA.crews.get(i);
            json.append("{\"id\":").append(quote(crew.getCrewId()))
                    .append(",\"name\":").append(quote(crew.getCrewName()))
                    .append(",\"specialty\":").append(quote(crew.getSpecialty()))
                    .append(",\"station\":").append(quote(crew.getBaseStationSector()))
                    .append(",\"status\":").append(quote(crew.getStatus())).append('}');
        }
        json.append("],\"documents\":[");
        for (int i = 0; i < CITY_DATA.documents.size(); i++) {
            if (i > 0) json.append(',');
            CityDocument document = CITY_DATA.documents.get(i);
            json.append("{\"id\":").append(quote(document.getDocId()))
                    .append(",\"title\":").append(quote(document.getTitle()))
                    .append(",\"department\":").append(quote(document.getDepartment()))
                    .append(",\"text\":").append(quote(document.getFullText())).append('}');
        }
        return json.append("]}").toString();
    }

    private static synchronized void createReport(HttpExchange exchange) throws IOException {
        String body = readBody(exchange.getRequestBody());
        String citizen = formValue(body, "citizen").trim();
        String category = formValue(body, "category").trim();
        String sector = formValue(body, "sector").trim();
        String description = formValue(body, "description").trim();
        String allocation = formValue(body, "allocation").trim();
        String crewId = formValue(body, "crewId").trim();
        int priority;
        try {
            priority = Integer.parseInt(formValue(body, "priority"));
        } catch (NumberFormatException ex) {
            sendJson(exchange, 400, "{\"error\":\"Choose a priority from 1 to 5.\"}");
            return;
        }
        if (category.isEmpty() || sector.isEmpty() || description.isEmpty() || priority < 1 || priority > 5) {
            sendJson(exchange, 400, "{\"error\":\"Category, location, description, and a valid priority are required.\"}");
            return;
        }
        if (normalizeCategory(category).isEmpty()) {
            sendJson(exchange, 400, "{\"error\":\"Enter a valid problem category.\"}");
            return;
        }
        if (!"AUTO".equals(allocation) && !"MANUAL".equals(allocation)) {
            sendJson(exchange, 400, "{\"error\":\"Choose automatic or manual team assignment.\"}");
            return;
        }

        ServiceCrew crew = null;
        if ("MANUAL".equals(allocation)) {
            crew = findAvailableCrew(crewId);
            if (crew == null) {
                sendJson(exchange, 409, "{\"error\":\"That team is no longer available. Refresh the dashboard and choose another team.\"}");
                return;
            }
        }
        String id = nextReportId();
        CitizenReport report = new CitizenReport(id, citizen.isEmpty() ? "Anonymous" : citizen,
                normalizeCategory(category), sector, priority, description, System.currentTimeMillis());
        if (crew == null && "AUTO".equals(allocation)) {
            crew = chooseCrew(report);
        }
        if (crew != null) {
            report.assignCrew(crew.getCrewId(), "MANUAL".equals(allocation) ? "MANUAL" : "AUTO");
            crew.setStatus("DISPATCHED");
        }
        CITY_DATA.reports.add(report);
        String response = "{\"report\":" + reportJson(report)
                + ",\"message\":" + quote(crew == null
                ? "Report submitted. No response team is currently available."
                : "Report submitted and " + crew.getCrewName() + " was assigned.") + "}";
        sendJson(exchange, 201, response);
    }

    private static synchronized void assignReport(HttpExchange exchange, String reportId) throws IOException {
        String crewId = formValue(readBody(exchange.getRequestBody()), "crewId").trim();
        CitizenReport report = findReport(reportId);
        if (report == null) {
            sendJson(exchange, 404, "{\"error\":\"Report not found.\"}");
            return;
        }
        if (report.getAssignedCrewId() != null) {
            sendJson(exchange, 409, "{\"error\":\"This report already has a response team assigned.\"}");
            return;
        }
        ServiceCrew crew = findAvailableCrew(crewId);
        if (crew == null) {
            sendJson(exchange, 409, "{\"error\":\"That team is no longer available. Refresh the dashboard and choose another team.\"}");
            return;
        }
        report.assignCrew(crew.getCrewId(), "MANUAL");
        crew.setStatus("DISPATCHED");
        sendJson(exchange, 200, "{\"report\":" + reportJson(report)
                + ",\"message\":" + quote(crew.getCrewName() + " was assigned to " + report.getReportId() + ".") + "}");
    }

    private static synchronized void deleteReport(HttpExchange exchange, String reportId) throws IOException {
        for (int i = 0; i < CITY_DATA.reports.size(); i++) {
            CitizenReport report = CITY_DATA.reports.get(i);
            if (!report.getReportId().equals(reportId)) continue;
            ServiceCrew assignedCrew = findCrew(report.getAssignedCrewId());
            if (assignedCrew != null && "DISPATCHED".equals(assignedCrew.getStatus())) {
                assignedCrew.setStatus("AVAILABLE");
            }
            CITY_DATA.reports.remove(i);
            String message = assignedCrew == null
                    ? report.getReportId() + " was deleted."
                    : report.getReportId() + " was deleted and " + assignedCrew.getCrewName() + " is available again.";
            sendJson(exchange, 200, "{\"message\":" + quote(message) + "}");
            return;
        }
        sendJson(exchange, 404, "{\"error\":\"Report not found.\"}");
    }

    private static String reportJson(CitizenReport report) {
        return "{\"id\":" + quote(report.getReportId())
                + ",\"citizen\":" + quote(report.getCitizenName())
                + ",\"category\":" + quote(report.getCategory())
                + ",\"sector\":" + quote(report.getLocationSector())
                + ",\"priority\":" + report.getPriority()
                + ",\"description\":" + quote(report.getDescription())
                + ",\"timestamp\":" + report.getTimestamp()
                + ",\"status\":" + quote(report.getStatus())
                + ",\"assignedCrewId\":" + nullableQuote(report.getAssignedCrewId())
                + ",\"assignedCrewName\":" + nullableQuote(crewName(report.getAssignedCrewId()))
                + ",\"assignmentMethod\":" + nullableQuote(report.getAssignmentMethod()) + "}";
    }

    private static boolean isDashboardReport(CitizenReport report) {
        String id = report.getReportId();
        if (!id.startsWith("REP-")) return true;
        try {
            int sequence = Integer.parseInt(id.substring(4));
            return sequence <= 1010 || sequence >= 1101;
        } catch (NumberFormatException ex) {
            return true;
        }
    }

    private static String nextReportId() {
        int largestId = 1000;
        for (int i = 0; i < CITY_DATA.reports.size(); i++) {
            String id = CITY_DATA.reports.get(i).getReportId();
            if (id.startsWith("REP-")) {
                try {
                    largestId = Math.max(largestId, Integer.parseInt(id.substring(4)));
                } catch (NumberFormatException ignored) {
                    // Ignore non-numeric report suffixes when generating the next numeric ID.
                }
            }
        }
        return "REP-" + (largestId + 1);
    }

    private static ServiceCrew chooseCrew(CitizenReport report) {
        ServiceCrew best = null;
        int bestScore = -1;
        String incident = (report.getCategory() + " " + report.getDescription()).toUpperCase();
        for (int i = 0; i < CITY_DATA.crews.size(); i++) {
            ServiceCrew crew = CITY_DATA.crews.get(i);
            if (!"AVAILABLE".equals(crew.getStatus())) continue;
            int score = specialtyScore(incident, crew.getSpecialty());
            if (score < 0) continue;
            if (crew.getBaseStationSector().equalsIgnoreCase(report.getLocationSector())) score += 2;
            if (score > bestScore) {
                best = crew;
                bestScore = score;
            }
        }
        return best;
    }

    private static int specialtyScore(String incident, String specialty) {
        boolean fire = incident.contains("FIRE") || incident.contains("RESCUE") || incident.contains("MEDICAL");
        boolean electrical = incident.contains("POWER") || incident.contains("GRID") || incident.contains("ELECTRIC") || incident.contains("BLACKOUT");
        boolean water = incident.contains("WATER") || incident.contains("FLOOD") || incident.contains("PIPE");
        boolean hazmat = incident.contains("HAZMAT") || incident.contains("GAS") || incident.contains("CHEMICAL") || incident.contains("HAZARDOUS") || incident.contains("SPILL");
        boolean roads = incident.contains("POTHOLE") || incident.contains("SINKHOLE") || incident.contains("ROAD") || incident.contains("BRIDGE");
        boolean traffic = incident.contains("TRAFFIC") || incident.contains("COLLISION") || incident.contains("CRASH") || incident.contains("ACCIDENT");
        if (specialty.equals("FIRE_RESCUE") && fire) return 3;
        if (specialty.equals("ELECTRICAL_GRID") && electrical) return 3;
        if (specialty.equals("WATER_MAINS") && water) return 3;
        if (specialty.equals("HAZMAT") && hazmat) return 3;
        if (specialty.equals("ROAD_WORKS") && roads) return 3;
        if (specialty.equals("TRAFFIC_POLICE") && traffic) return 3;
        return -1;
    }

    private static String normalizeCategory(String category) {
        return category.trim().toUpperCase().replaceAll("[^A-Z0-9]+", "_").replaceAll("^_|_$", "");
    }

    private static CitizenReport findReport(String reportId) {
        for (int i = 0; i < CITY_DATA.reports.size(); i++) {
            CitizenReport report = CITY_DATA.reports.get(i);
            if (report.getReportId().equals(reportId)) return report;
        }
        return null;
    }

    private static ServiceCrew findAvailableCrew(String crewId) {
        for (int i = 0; i < CITY_DATA.crews.size(); i++) {
            ServiceCrew crew = CITY_DATA.crews.get(i);
            if (crew.getCrewId().equals(crewId) && "AVAILABLE".equals(crew.getStatus())) return crew;
        }
        return null;
    }

    private static String crewName(String crewId) {
        if (crewId == null) return null;
        for (int i = 0; i < CITY_DATA.crews.size(); i++) {
            ServiceCrew crew = CITY_DATA.crews.get(i);
            if (crew.getCrewId().equals(crewId)) return crew.getCrewName();
        }
        return null;
    }

    private static ServiceCrew findCrew(String crewId) {
        if (crewId == null) return null;
        for (int i = 0; i < CITY_DATA.crews.size(); i++) {
            ServiceCrew crew = CITY_DATA.crews.get(i);
            if (crew.getCrewId().equals(crewId)) return crew;
        }
        return null;
    }

    private static String nullableQuote(String value) {
        return value == null ? "null" : quote(value);
    }

    private static String formValue(String body, String key) throws IOException {
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int separator = pair.indexOf('=');
            String encodedKey = separator < 0 ? pair : pair.substring(0, separator);
            if (key.equals(decode(encodedKey))) {
                return decode(separator < 0 ? "" : pair.substring(separator + 1));
            }
        }
        return "";
    }

    private static String decode(String value) throws IOException {
        return URLDecoder.decode(value, "UTF-8");
    }

    private static String readBody(InputStream input) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int count;
        while ((count = input.read(buffer)) != -1) {
            bytes.write(buffer, 0, count);
        }
        return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
    }

    private static String quote(String value) {
        StringBuilder escaped = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"' || c == '\\') {
                escaped.append('\\').append(c);
            } else if (c == '\n') {
                escaped.append("\\n");
            } else if (c == '\r') {
                escaped.append("\\r");
            } else if (c == '\t') {
                escaped.append("\\t");
            } else if (c < 0x20) {
                escaped.append(String.format("\\u%04x", (int) c));
            } else {
                escaped.append(c);
            }
        }
        return escaped.append('"').toString();
    }

    private static void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = gzipIfRequested(exchange, body.getBytes(StandardCharsets.UTF_8));
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.getResponseHeaders().set("Vary", "Accept-Encoding");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream response = exchange.getResponseBody()) {
            response.write(bytes);
        }
    }

    private static byte[] gzipIfRequested(HttpExchange exchange, byte[] bytes) throws IOException {
        String acceptedEncodings = exchange.getRequestHeaders().getFirst("Accept-Encoding");
        if (acceptedEncodings == null || !acceptedEncodings.contains("gzip") || bytes.length <= 1024) {
            return bytes;
        }
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(compressed)) {
            gzip.write(bytes);
        }
        exchange.getResponseHeaders().set("Content-Encoding", "gzip");
        exchange.getResponseHeaders().set("Vary", "Accept-Encoding");
        return compressed.toByteArray();
    }

    private static void serveStaticFile(HttpExchange exchange, String requestPath) throws IOException {
        String relative = requestPath.equals("/") ? "index.html" : requestPath.substring(1);
        Path file = WEB_ROOT.resolve(relative).normalize();
        if (!file.startsWith(WEB_ROOT) || !Files.isRegularFile(file)) {
            file = WEB_ROOT.resolve("index.html");
        }
        if (!Files.isRegularFile(file)) {
            sendJson(exchange, 503, "{\"error\":\"Dashboard is not built. Run start.bat to build it.\"}");
            return;
        }
        String contentType = Files.probeContentType(file);
        if (contentType == null) contentType = "application/octet-stream";
        byte[] bytes = gzipIfRequested(exchange, Files.readAllBytes(file));
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Cache-Control", requestPath.startsWith("/assets/")
                ? "public, max-age=31536000, immutable" : "no-cache");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream response = exchange.getResponseBody()) {
            response.write(bytes);
        }
    }
}
