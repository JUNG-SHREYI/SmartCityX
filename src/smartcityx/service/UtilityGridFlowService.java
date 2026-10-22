package smartcityx.service;

import smartcityx.algorithms.flow.*;
import smartcityx.core.MyArrayList;
import smartcityx.core.Pair;
import smartcityx.model.ServiceCrew;
import smartcityx.model.CitizenReport;

/**
 * Service orchestrating Network Flow (M4 - CO4) algorithms for utility grid distribution & crew matching.
 */
public class UtilityGridFlowService {

    public EdmondsKarp.FlowResult calculateUtilityMaxFlow(int n, double[][] capacityMatrix, int source, int sink) {
        return EdmondsKarp.computeMaxFlow(n, capacityMatrix, source, sink);
    }

    public double calculateLargeGridFlowDinic(int n, double[][] capacityMatrix, int source, int sink) {
        DinicAlgorithm dinic = new DinicAlgorithm(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (capacityMatrix[i][j] > 0) {
                    dinic.addEdge(i, j, capacityMatrix[i][j]);
                }
            }
        }
        return dinic.computeMaxFlow(source, sink);
    }

    public MaxFlowMinCut.MinCutResult isolateCriticalBottlenecks(int n, double[][] capacityMatrix, int source, int sink) {
        return MaxFlowMinCut.findMinCut(n, capacityMatrix, source, sink);
    }

    public BipartiteMatching.MatchingResult matchCrewsToIncidents(MyArrayList<ServiceCrew> crews, MyArrayList<CitizenReport> urgentReports) {
        int cCount = crews.size();
        int rCount = urgentReports.size();
        BipartiteMatching bm = new BipartiteMatching(cCount, rCount);

        for (int i = 0; i < cCount; i++) {
            ServiceCrew crew = crews.get(i);
            for (int j = 0; j < rCount; j++) {
                CitizenReport rep = urgentReports.get(j);
                if (isCrewEligible(crew, rep)) {
                    bm.addEdge(i, j);
                }
            }
        }
        return bm.solve();
    }

    private boolean isCrewEligible(ServiceCrew crew, CitizenReport rep) {
        String spec = crew.getSpecialty();
        String cat = rep.getCategory();

        if (spec.equals("FIRE_RESCUE") && (cat.contains("FIRE") || cat.contains("EXPLOSION") || cat.contains("COLLISION"))) return true;
        if (spec.equals("ELECTRICAL_GRID") && (cat.contains("POWER") || cat.contains("GRID") || cat.contains("ELECTRICAL"))) return true;
        if (spec.equals("WATER_MAINS") && (cat.contains("WATER") || cat.contains("FLOOD") || cat.contains("DRAIN"))) return true;
        if (spec.equals("HAZMAT") && (cat.contains("GAS") || cat.contains("HAZMAT") || cat.contains("CHEMICAL"))) return true;
        if (spec.equals("ROAD_WORKS") && (cat.contains("POTHOLE") || cat.contains("SINKHOLE") || cat.contains("ROAD"))) return true;
        return spec.equals("TRAFFIC_POLICE");
    }
}
