package smartcityx.service;

import smartcityx.algorithms.np.*;
import smartcityx.core.MyArrayList;
import smartcityx.core.Pair;

/**
 * Service orchestrating NP-Completeness & Approximation (M5 - CO5) for municipal constraint checking & sensor placement.
 */
public class InfrastructureSurveillanceService {

    public DpllSatSolver.SatResult verifyPlanningConstraints(DpllSatSolver.SatFormula formula) {
        return DpllSatSolver.solve(formula);
    }

    public SatToCliqueReduction.ReducedCliqueGraph reduceSatToClique(DpllSatSolver.SatFormula formula) {
        return SatToCliqueReduction.reduce(formula);
    }

    public CliqueToISReduction.ComplementGraph reduceCliqueToIS(int n, boolean[][] cliqueAdj) {
        return CliqueToISReduction.reduce(n, cliqueAdj);
    }

    public ISToVertexCover.VertexCoverReductionResult reduceISToVertexCover(int n, int k) {
        return ISToVertexCover.reduce(n, k);
    }

    public VertexCover2Approx.ApproxCoverResult placeOptimalSensors(int numIntersections, MyArrayList<Pair<Integer, Integer>> roadSegments) {
        return VertexCover2Approx.computeCover(numIntersections, roadSegments);
    }
}
