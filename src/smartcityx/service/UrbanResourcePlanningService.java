package smartcityx.service;

import smartcityx.algorithms.dp.*;
import smartcityx.core.MyArrayList;
import smartcityx.core.Pair;

/**
 * Service orchestrating Advanced Dynamic Programming (M3 - CO3) for urban resource optimization.
 */
public class UrbanResourcePlanningService {

    public LevenshteinDistance.DpTraceResult autoCorrectCitizenQuery(String userInput, String standardDictionaryWord) {
        return LevenshteinDistance.computeWithTrace(userInput, standardDictionaryWord);
    }

    public BitmaskDP.RouteResult planOptimalEmergencyRoute(double[][] incidentDistanceMatrix) {
        return BitmaskDP.solveTSP(incidentDistanceMatrix);
    }

    public MatrixChainMultiplication.McmResult optimizeAnalyticsPipeline(int[] tensorDimensions) {
        return MatrixChainMultiplication.solve(tensorDimensions);
    }

    public SubsetResourceDP.OptimizationResult selectOptimalProjects(MyArrayList<SubsetResourceDP.ProjectItem> projects, int budgetLimit) {
        return SubsetResourceDP.optimizeProjects(projects, budgetLimit);
    }
}
