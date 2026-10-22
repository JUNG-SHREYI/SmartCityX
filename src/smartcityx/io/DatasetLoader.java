package smartcityx.io;

import smartcityx.core.MyArrayList;
import smartcityx.model.*;

/**
 * High-fidelity dataset loader and synthetic generator for the SmartCityX platform.
 * Provides heterogeneous datasets across civic complaints, infrastructure inventory, IoT streams, crews, and bylaws.
 */
public class DatasetLoader {

    public static class SmartCityDataBundle {
        public final MyArrayList<CitizenReport> reports;
        public final MyArrayList<CityInfrastructure> assets;
        public final MyArrayList<IoTSensorReading> sensorStreams;
        public final MyArrayList<ServiceCrew> crews;
        public final MyArrayList<CityDocument> documents;

        public SmartCityDataBundle(MyArrayList<CitizenReport> reports,
                                   MyArrayList<CityInfrastructure> assets,
                                   MyArrayList<IoTSensorReading> sensorStreams,
                                   MyArrayList<ServiceCrew> crews,
                                   MyArrayList<CityDocument> documents) {
            this.reports = reports;
            this.assets = assets;
            this.sensorStreams = sensorStreams;
            this.crews = crews;
            this.documents = documents;
        }
    }

    public static SmartCityDataBundle loadDefaultCityData() {
        MyArrayList<CitizenReport> reports = generateCitizenReports();
        MyArrayList<CityInfrastructure> assets = generateInfrastructureAssets();
        MyArrayList<IoTSensorReading> sensors = generateIoTSensorStreams();
        MyArrayList<ServiceCrew> crews = generateServiceCrews();
        MyArrayList<CityDocument> docs = generateCityDocuments();

        return new SmartCityDataBundle(reports, assets, sensors, crews, docs);
    }

    private static MyArrayList<CitizenReport> generateCitizenReports() {
        MyArrayList<CitizenReport> list = new MyArrayList<>();

        String[] citizens = {"Aarav Sharma", "Priya Patel", "Vikram Rao", "Sneha Nair", "Rohan Gupta",
                             "Ananya Deshmukh", "Rajesh Varma", "Deepa Krishnan", "Karthik Iyer", "Meera Joshi"};
        String[] sectors = {"Sector-1 (Downtown)", "Sector-2 (North-Industrial)", "Sector-3 (East-Tech-Hub)",
                            "Sector-4 (West-Residential)", "Sector-5 (South-Port)", "Sector-6 (Metro-Core)",
                            "Sector-7 (Riverfront)", "Sector-8 (University-Campus)", "Sector-9 (Aerocity)", "Sector-10 (Greenfield)"};

        // Emergency incidents
        list.add(new CitizenReport("REP-1001", "Aarav Sharma", "FIRE", "Sector-1 (Downtown)", 1, "Major electrical transformer explosion sparking fire near downtown metro terminal.", System.currentTimeMillis() - 3600000));
        list.add(new CitizenReport("REP-1002", "Priya Patel", "GAS_LEAK", "Sector-2 (North-Industrial)", 1, "Pungent chemical sulfur odor detected near industrial gas storage tank #4.", System.currentTimeMillis() - 3200000));
        list.add(new CitizenReport("REP-1003", "Vikram Rao", "WATER_BURST", "Sector-4 (West-Residential)", 2, "High pressure water main burst flooding main arterial avenue and basement parkings.", System.currentTimeMillis() - 2800000));
        list.add(new CitizenReport("REP-1004", "Sneha Nair", "TRAFFIC_COLLISION", "Sector-6 (Metro-Core)", 2, "Multi-vehicle pileup blocking three lanes on central flyover. Ambulance required.", System.currentTimeMillis() - 2400000));
        list.add(new CitizenReport("REP-1005", "Rohan Gupta", "GRID_FAIL", "Sector-3 (East-Tech-Hub)", 1, "Substation feeder 11kV line tripped causing complete blackout across data centers.", System.currentTimeMillis() - 2000000));
        list.add(new CitizenReport("REP-1006", "Ananya Deshmukh", "HAZMAT", "Sector-5 (South-Port)", 1, "Hazardous corrosive fluid container leaking on dock berth #12.", System.currentTimeMillis() - 1600000));
        list.add(new CitizenReport("REP-1007", "Rajesh Varma", "SINKHOLE", "Sector-7 (Riverfront)", 2, "Sudden 4-meter road collapse and sinkhole opened after heavy storm runoff.", System.currentTimeMillis() - 1200000));
        list.add(new CitizenReport("REP-1008", "Deepa Krishnan", "FLOOD", "Sector-10 (Greenfield)", 2, "Stormwater retention canal overflowing into municipal housing blocks.", System.currentTimeMillis() - 800000));
        list.add(new CitizenReport("REP-1009", "Karthik Iyer", "POTHOLE", "Sector-8 (University-Campus)", 4, "Deep pothole cluster damaging commuter bicycles near campus gate 2.", System.currentTimeMillis() - 500000));
        list.add(new CitizenReport("REP-1010", "Meera Joshi", "STREETLIGHT_OUT", "Sector-9 (Aerocity)", 4, "Entire street lighting circuit dark along airport perimeter expressway.", System.currentTimeMillis() - 200000));

        // Generate synthetic bulk reports for large scale matching
        for (int i = 11; i <= 100; i++) {
            String cat = (i % 5 == 0) ? "WATER_BURST" : (i % 7 == 0) ? "GRID_FAIL" : (i % 3 == 0) ? "POTHOLE" : "TRAFFIC_JAM";
            int prio = (i % 5 == 0 || i % 7 == 0) ? 2 : 4;
            String citizen = citizens[i % citizens.length];
            String sector = sectors[i % sectors.length];
            String desc = "Municipal service observation #" + i + " reported for " + cat + " in " + sector + " with status logging tag SCX-TKT-" + (5000 + i);
            list.add(new CitizenReport("REP-" + (1000 + i), citizen, cat, sector, prio, desc, System.currentTimeMillis() - (i * 60000L)));
        }

        return list;
    }

    private static MyArrayList<CityInfrastructure> generateInfrastructureAssets() {
        MyArrayList<CityInfrastructure> list = new MyArrayList<>();

        list.add(new CityInfrastructure("INF-PWR-01", "Central Thermal & Solar Grid Substation", "Sector-1 (Downtown)", "POWER_SUBSTATION", 500.0, 94.5, "OPERATIONAL"));
        list.add(new CityInfrastructure("INF-PWR-02", "North Industrial Feeder Substation", "Sector-2 (North-Industrial)", "POWER_SUBSTATION", 350.0, 68.0, "MAINTENANCE_REQUIRED"));
        list.add(new CityInfrastructure("INF-PWR-03", "East Silicon Park Substation", "Sector-3 (East-Tech-Hub)", "POWER_SUBSTATION", 450.0, 98.2, "OPERATIONAL"));
        list.add(new CityInfrastructure("INF-WTR-01", "Riverfront Water Treatment Plant", "Sector-7 (Riverfront)", "WATER_TREATMENT", 200.0, 92.0, "OPERATIONAL"));
        list.add(new CityInfrastructure("INF-WTR-02", "West Hill High-Pressure Pumping Station", "Sector-4 (West-Residential)", "WATER_TREATMENT", 120.0, 55.4, "MAINTENANCE_REQUIRED"));
        list.add(new CityInfrastructure("INF-TRF-01", "Central Ring Expressway Interchange", "Sector-6 (Metro-Core)", "TRAFFIC_JUNCTION", 12000.0, 88.0, "OPERATIONAL"));
        list.add(new CityInfrastructure("INF-TRF-02", "Port Maritime Freight Corridor", "Sector-5 (South-Port)", "TRAFFIC_JUNCTION", 6000.0, 91.5, "OPERATIONAL"));
        list.add(new CityInfrastructure("INF-BRG-01", "Golden River Suspension Bridge", "Sector-7 (Riverfront)", "BRIDGE", 4500.0, 72.0, "OPERATIONAL"));
        list.add(new CityInfrastructure("INF-HSP-01", "City Apex Trauma Center & Emergency Hospital", "Sector-1 (Downtown)", "HOSPITAL", 850.0, 99.0, "OPERATIONAL"));
        list.add(new CityInfrastructure("INF-HSP-02", "East Valley Super-Speciality Hospital", "Sector-3 (East-Tech-Hub)", "HOSPITAL", 600.0, 96.5, "OPERATIONAL"));

        for (int i = 11; i <= 30; i++) {
            String type = (i % 3 == 0) ? "POWER_SUBSTATION" : (i % 3 == 1) ? "WATER_TREATMENT" : "TRAFFIC_JUNCTION";
            list.add(new CityInfrastructure("INF-ASSET-" + i, "Auxiliary Municipal Station " + i, "Sector-" + (i % 10 + 1), type, 100.0 + (i * 15), 80.0 + (i % 20), "OPERATIONAL"));
        }

        return list;
    }

    private static MyArrayList<IoTSensorReading> generateIoTSensorStreams() {
        MyArrayList<IoTSensorReading> list = new MyArrayList<>();
        java.util.Random rnd = new java.util.Random(42);

        String[] types = {"AQI_PM25", "TRAFFIC_FLOW", "WATER_PRESSURE", "GRID_LOAD_MW", "NOISE_DB"};
        String[] units = {"µg/m³", "veh/min", "bar", "MW", "dB"};

        for (int i = 1; i <= 500; i++) {
            int tIdx = i % types.length;
            double baseVal = (tIdx == 0) ? 45.0 : (tIdx == 1) ? 120.0 : (tIdx == 2) ? 4.5 : (tIdx == 3) ? 280.0 : 65.0;
            double jitter = rnd.nextDouble() * 20.0 - 10.0;
            list.add(new IoTSensorReading("IOT-SNS-" + String.format("%04d", i), types[tIdx], "Sector-" + (i % 10 + 1), Math.max(1.0, baseVal + jitter), units[tIdx], System.currentTimeMillis() - (i * 5000L)));
        }

        return list;
    }

    private static MyArrayList<ServiceCrew> generateServiceCrews() {
        MyArrayList<ServiceCrew> list = new MyArrayList<>();

        list.add(new ServiceCrew("CRW-01", "Squad Alpha 1", "FIRE_RESCUE", "Sector-1 (Downtown)"));
        list.add(new ServiceCrew("CRW-02", "Squad Alpha 2", "FIRE_RESCUE", "Sector-6 (Metro-Core)"));
        list.add(new ServiceCrew("CRW-03", "Volt Response Unit 1", "ELECTRICAL_GRID", "Sector-3 (East-Tech-Hub)"));
        list.add(new ServiceCrew("CRW-04", "Volt Response Unit 2", "ELECTRICAL_GRID", "Sector-2 (North-Industrial)"));
        list.add(new ServiceCrew("CRW-05", "Hydro Maintenance Crew 1", "WATER_MAINS", "Sector-4 (West-Residential)"));
        list.add(new ServiceCrew("CRW-06", "Hydro Maintenance Crew 2", "WATER_MAINS", "Sector-7 (Riverfront)"));
        list.add(new ServiceCrew("CRW-07", "Chemical & Hazmat Team", "HAZMAT", "Sector-2 (North-Industrial)"));
        list.add(new ServiceCrew("CRW-08", "Civil Highway Repair Squad", "ROAD_WORKS", "Sector-6 (Metro-Core)"));
        list.add(new ServiceCrew("CRW-09", "Traffic Flow Incident Unit", "TRAFFIC_POLICE", "Sector-1 (Downtown)"));
        list.add(new ServiceCrew("CRW-10", "Rapid Medical Paramedics", "FIRE_RESCUE", "Sector-1 (Downtown)"));

        for (int i = 11; i <= 20; i++) {
            list.add(new ServiceCrew("CRW-" + i, "Auxiliary Crew " + i, (i % 2 == 0 ? "ELECTRICAL_GRID" : "WATER_MAINS"), "Sector-" + (i % 10 + 1)));
        }

        return list;
    }

    private static MyArrayList<CityDocument> generateCityDocuments() {
        MyArrayList<CityDocument> list = new MyArrayList<>();

        list.add(new CityDocument("DOC-URB-01", "Municipal Master Plan & Zoning Regulations 2030", "URBAN_PLANNING",
                "SMART_CITY_REGULATION_MUNICIPAL_FRAMEWORK: All high-density residential developments in Sector-4 and Sector-8 must reserve 25% land for green stormwater retention ponds. Emergency vehicle access corridors must maintain a minimum clear width of 7.5 meters without parking obstruction. Substation buffer zones shall not contain combustible materials. All critical infrastructure assets must undergo bi-annual acoustic and thermal sensor surveillance."));

        list.add(new CityDocument("DOC-WTR-02", "Integrated Water Resource & Flood Protocol", "WATER_RESOURCES",
                "SMART_CITY_WATER_SECURITY_STANDARD: Water treatment plants must maintain dual redundant intake valves. When river water levels surpass the amber threshold of 14.5 meters, automated sluice gates G1 through G4 must open sequentially to divert excess runoff into stormwater retention canal C2. Water pressure in residential trunk lines must be dynamically balanced between 3.5 bar and 5.2 bar using variable frequency pumps."));

        list.add(new CityDocument("DOC-PWR-03", "Smart Electrical Grid Balancing & Outage Containment", "ENERGY_UTILITIES",
                "SMART_CITY_ENERGY_CONTINUITY_DIRECTIVE: Substation feeder lines must utilize automated reclosers for momentary arc faults. In the event of primary grid loss, hospital trauma centers and emergency dispatch headquarters must auto-switch to solar-battery microgrids within 15 milliseconds. High-voltage transmission lines exceeding 132kV require real-time dynamic thermal rating (DTR) monitoring."));

        list.add(new CityDocument("DOC-EMG-04", "City Emergency Response & Multi-Agency Dispatch Standard", "EMERGENCY_SERVICES",
                "SMART_CITY_EMERGENCY_TRIAGE_DISPATCH_PROTOCOL: Priority 1 calls involving active fires, gas leaks, structural collapses, or severe traffic collisions require simultaneous notification of Fire Rescue, Emergency Paramedics, and Traffic Police. Evacuation routes along central flyover and coastal corridor must be assigned priority traffic signal greens via SCADA traffic overrides."));

        return list;
    }
}
