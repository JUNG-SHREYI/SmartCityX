package smartcityx.service;

import smartcityx.algorithms.strings.*;
import smartcityx.core.MyArrayList;
import smartcityx.model.CitizenReport;
import smartcityx.model.CityInfrastructure;

/**
 * Service orchestrating string pattern matching algorithms (M1 - CO1) across civic datasets.
 */
public class CivicSearchService {

    private final AhoCorasick emergencyTriageAutomaton;

    public CivicSearchService() {
        emergencyTriageAutomaton = new AhoCorasick();
        // Register city emergency and high-priority alert keywords
        emergencyTriageAutomaton.insert("FIRE");
        emergencyTriageAutomaton.insert("GAS_LEAK");
        emergencyTriageAutomaton.insert("WATER_BURST");
        emergencyTriageAutomaton.insert("GRID_FAIL");
        emergencyTriageAutomaton.insert("COLLISION");
        emergencyTriageAutomaton.insert("AMBULANCE");
        emergencyTriageAutomaton.insert("HAZMAT");
        emergencyTriageAutomaton.insert("FLOOD");
        emergencyTriageAutomaton.insert("ELECTRICAL_ARC");
        emergencyTriageAutomaton.insert("SINKHOLE");
        emergencyTriageAutomaton.buildAutomaton();
    }

    public MyArrayList<CitizenReport> searchReportsByKmp(MyArrayList<CitizenReport> reports, String query) {
        MyArrayList<CitizenReport> matches = new MyArrayList<>();
        if (query == null || query.isEmpty()) return matches;
        String qUpper = query.toUpperCase();

        for (int i = 0; i < reports.size(); i++) {
            CitizenReport r = reports.get(i);
            String fullText = (r.getDescription() + " " + r.getCategory() + " " + r.getLocationSector()).toUpperCase();
            if (KmpMatcher.search(fullText, qUpper).size() > 0) {
                matches.add(r);
            }
        }
        return matches;
    }

    public MyArrayList<CitizenReport> searchReportsByRabinKarp(MyArrayList<CitizenReport> reports, String query) {
        MyArrayList<CitizenReport> matches = new MyArrayList<>();
        if (query == null || query.isEmpty()) return matches;
        String qUpper = query.toUpperCase();

        for (int i = 0; i < reports.size(); i++) {
            CitizenReport r = reports.get(i);
            String fullText = (r.getDescription() + " " + r.getCategory() + " " + r.getLocationSector()).toUpperCase();
            if (RabinKarp.search(fullText, qUpper).size() > 0) {
                matches.add(r);
            }
        }
        return matches;
    }

    public MyArrayList<CitizenReport> searchReportsByBittu(MyArrayList<CitizenReport> reports, String query) {
        MyArrayList<CitizenReport> matches = new MyArrayList<>();
        if (query == null || query.isEmpty()) return matches;
        String qUpper = query.toUpperCase();

        for (int i = 0; i < reports.size(); i++) {
            CitizenReport r = reports.get(i);
            String fullText = (r.getDescription() + " " + r.getCategory() + " " + r.getLocationSector()).toUpperCase();
            if (BittuAlgorithm.search(fullText, qUpper).size() > 0) {
                matches.add(r);
            }
        }
        return matches;
    }

    public MyArrayList<CityInfrastructure> searchAssetsByZAlgorithm(MyArrayList<CityInfrastructure> assets, String codePrefix) {
        MyArrayList<CityInfrastructure> matches = new MyArrayList<>();
        if (codePrefix == null || codePrefix.isEmpty()) return matches;
        String qUpper = codePrefix.toUpperCase();

        for (int i = 0; i < assets.size(); i++) {
            CityInfrastructure asset = assets.get(i);
            String fullText = (asset.getAssetId() + " " + asset.getName() + " " + asset.getSector()).toUpperCase();
            if (ZAlgorithm.search(fullText, qUpper).size() > 0) {
                matches.add(asset);
            }
        }
        return matches;
    }

    public MyArrayList<AhoCorasick.MatchResult> triageEmergencyKeywords(String text) {
        return emergencyTriageAutomaton.searchInText(text.toUpperCase());
    }
}
