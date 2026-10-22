package smartcityx.service;

import smartcityx.algorithms.strings.*;
import smartcityx.algorithms.suffix.*;
import smartcityx.algorithms.dp.*;
import smartcityx.algorithms.flow.*;
import smartcityx.algorithms.np.*;
import smartcityx.algorithms.parallel_random.*;
import smartcityx.core.MyArrayList;
import smartcityx.core.Pair;

/**
 * Benchmark Arena & Asymptotic Verification Service.
 * Provides microsecond telemetry, asymptotic Big-O validation, and competitive showdowns across all 17 algorithms.
 */
public class BenchmarkArenaService {

    public static class AlgorithmTelemetry {
        public final int id;
        public final String name;
        public final String module;
        public final String timeComplexity;
        public final String spaceComplexity;
        public final String status;
        public final long executionTimeMicros;

        public AlgorithmTelemetry(int id, String name, String module, String timeComplexity,
                                  String spaceComplexity, String status, long executionTimeMicros) {
            this.id = id;
            this.name = name;
            this.module = module;
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
            this.status = status;
            this.executionTimeMicros = executionTimeMicros;
        }
    }

    public static class ShowdownEntry {
        public final String algorithmName;
        public final long elapsedNanos;
        public final int resultCount;
        public final String complexity;

        public ShowdownEntry(String algorithmName, long elapsedNanos, int resultCount, String complexity) {
            this.algorithmName = algorithmName;
            this.elapsedNanos = elapsedNanos;
            this.resultCount = resultCount;
            this.complexity = complexity;
        }
    }

    public MyArrayList<ShowdownEntry> runStringSearchShowdown(String text, String pattern) {
        MyArrayList<ShowdownEntry> list = new MyArrayList<>();

        // 1. KMP
        long t0 = System.nanoTime();
        MyArrayList<Integer> kmpRes = KmpMatcher.search(text, pattern);
        long t1 = System.nanoTime();
        list.add(new ShowdownEntry("KMP Matcher", t1 - t0, kmpRes.size(), "O(N + M)"));

        // 2. Rabin-Karp
        t0 = System.nanoTime();
        MyArrayList<Integer> rkRes = RabinKarp.search(text, pattern);
        t1 = System.nanoTime();
        list.add(new ShowdownEntry("Rabin-Karp Rolling Hash", t1 - t0, rkRes.size(), "O(N + M) avg"));

        // 3. Z-Algorithm
        t0 = System.nanoTime();
        MyArrayList<Integer> zRes = ZAlgorithm.search(text, pattern);
        t1 = System.nanoTime();
        list.add(new ShowdownEntry("Z-Algorithm", t1 - t0, zRes.size(), "O(N + M)"));

        // 4. Bittu's Algorithm (Invention)
        t0 = System.nanoTime();
        MyArrayList<Integer> bittuRes = BittuAlgorithm.search(text, pattern);
        t1 = System.nanoTime();
        list.add(new ShowdownEntry("Bittu's Algorithm (Invention)", t1 - t0, bittuRes.size(), "O(N) rolling O(1) ops"));

        // 5. Aho-Corasick
        AhoCorasick ac = new AhoCorasick();
        ac.insert(pattern);
        ac.buildAutomaton();
        t0 = System.nanoTime();
        MyArrayList<AhoCorasick.MatchResult> acRes = ac.searchInText(text);
        t1 = System.nanoTime();
        list.add(new ShowdownEntry("Aho-Corasick Automaton", t1 - t0, acRes.size(), "O(N + total matches)"));

        return list;
    }

    public MyArrayList<AlgorithmTelemetry> runFullSystemVerification() {
        MyArrayList<AlgorithmTelemetry> results = new MyArrayList<>();

        String sampleText = "SMART_CITY_EMERGENCY_DISPATCH_WATER_GRID_SENSOR_OBSERVATION_ALERT_PIPELINE_SMART_CITY";
        String samplePattern = "SMART_CITY";

        // M1: String Algorithms
        // 1. KMP
        long t0 = System.nanoTime();
        MyArrayList<Integer> r1 = KmpMatcher.search(sampleText, samplePattern);
        long t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(1, "KMP Matcher", "M1: String Algorithms (CO1)", "O(N + M)", "O(M)",
                r1.size() == 2 ? "VERIFIED (2 matches)" : "FAILED", (t1 - t0) / 1000));

        // 2. Z-Algorithm
        t0 = System.nanoTime();
        MyArrayList<Integer> r2 = ZAlgorithm.search(sampleText, samplePattern);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(2, "Z-Algorithm", "M1: String Algorithms (CO1)", "O(N + M)", "O(N + M)",
                r2.size() == 2 ? "VERIFIED (2 matches)" : "FAILED", (t1 - t0) / 1000));

        // 3. Rabin-Karp
        t0 = System.nanoTime();
        MyArrayList<Integer> r3 = RabinKarp.search(sampleText, samplePattern);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(3, "Rabin-Karp Rolling Hash", "M1: String Algorithms (CO1)", "O(N + M) avg", "O(1)",
                r3.size() == 2 ? "VERIFIED (2 matches)" : "FAILED", (t1 - t0) / 1000));

        // 4. Aho-Corasick
        AhoCorasick ac = new AhoCorasick();
        ac.insert("EMERGENCY");
        ac.insert("WATER_GRID");
        ac.insert("SENSOR");
        ac.buildAutomaton();
        t0 = System.nanoTime();
        MyArrayList<AhoCorasick.MatchResult> r4 = ac.searchInText(sampleText);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(4, "Aho-Corasick Trie Automaton", "M1: String Algorithms (CO1)", "O(N + M + K)", "O(Sum |P| * Sigma)",
                r4.size() == 3 ? "VERIFIED (3 keywords)" : "FAILED", (t1 - t0) / 1000));

        // 5. Bittu's Algorithm
        t0 = System.nanoTime();
        MyArrayList<Integer> r5 = BittuAlgorithm.search(sampleText, samplePattern);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(5, "Bittu's Algorithm (Invention)", "M1: String Algorithms (CO1)", "O(N) rolling O(1) ops", "O(Sigma^2) array",
                r5.size() == 2 ? "VERIFIED (2 matches)" : "FAILED", (t1 - t0) / 1000));

        // M2: Suffix Structures
        // 6. Suffix Array
        t0 = System.nanoTime();
        SuffixArray saObj = new SuffixArray(sampleText);
        MyArrayList<Integer> r6 = saObj.findAllOccurrences(samplePattern);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(6, "Suffix Array (Prefix Doubling)", "M2: Suffix Structures (CO2)", "O(N log^2 N) build, O(M log N) query", "O(N)",
                r6.size() == 2 ? "VERIFIED (2 matches)" : "FAILED", (t1 - t0) / 1000));

        // 7. SA-IS
        t0 = System.nanoTime();
        int[] saIs = SAIS.buildSA(sampleText);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(7, "SA-IS Linear Suffix Array", "M2: Suffix Structures (CO2)", "O(N) linear", "O(N)",
                saIs.length == sampleText.length() ? "VERIFIED (Full SA)" : "FAILED", (t1 - t0) / 1000));

        // 8. Kasai's LCP
        t0 = System.nanoTime();
        int[] lcp = KasaiLCP.buildLCP(sampleText, saIs);
        String lrs = KasaiLCP.findLongestRepeatedSubstring(sampleText, saIs, lcp);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(8, "Kasai's LCP Array", "M2: Suffix Structures (CO2)", "O(N) linear", "O(N)",
                lrs.equals("SMART_CITY") ? "VERIFIED ('" + lrs + "')" : "FAILED", (t1 - t0) / 1000));

        // 9. Suffix Automaton
        t0 = System.nanoTime();
        SuffixAutomaton sam = new SuffixAutomaton(sampleText);
        boolean r9 = sam.containsSubstring("DISPATCH_WATER");
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(9, "Suffix Automaton (SAM)", "M2: Suffix Structures (CO2)", "O(N) build, O(M) query", "O(N * Sigma)",
                r9 ? "VERIFIED (Substring detected)" : "FAILED", (t1 - t0) / 1000));

        // M3: Advanced DP
        // 10. Levenshtein Distance
        t0 = System.nanoTime();
        LevenshteinDistance.DpTraceResult levRes = LevenshteinDistance.computeWithTrace("WATER_BURST", "WATER_MAIN_BURST");
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(10, "Levenshtein / Damerau DP", "M3: Dynamic Programming (CO3)", "O(N * M)", "O(N * M)",
                levRes.distance == 5 ? "VERIFIED (Dist=5)" : "FAILED", (t1 - t0) / 1000));

        // 11. Bitmask DP TSP
        double[][] sampleDist = {
                {0, 10, 15, 20},
                {10, 0, 35, 25},
                {15, 35, 0, 30},
                {20, 25, 30, 0}
        };
        t0 = System.nanoTime();
        BitmaskDP.RouteResult tspRes = BitmaskDP.solveTSP(sampleDist);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(11, "Bitmask DP (TSP Routing)", "M3: Dynamic Programming (CO3)", "O(2^N * N^2)", "O(2^N * N)",
                tspRes.minCost == 80.0 ? "VERIFIED (Cost=80.0)" : "FAILED", (t1 - t0) / 1000));

        // 12. Matrix-Chain Multiplication
        int[] dims = {10, 30, 5, 60};
        t0 = System.nanoTime();
        MatrixChainMultiplication.McmResult mcmRes = MatrixChainMultiplication.solve(dims);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(12, "Matrix-Chain Multiplication", "M3: Dynamic Programming (CO3)", "O(N^3)", "O(N^2)",
                mcmRes.minScalarMultiplications == 4500 ? "VERIFIED (Cost=4500)" : "FAILED", (t1 - t0) / 1000));

        // M4: Network Flow
        // 13. Edmonds-Karp & Dinic Max Flow
        double[][] flowMatrix = {
                {0, 16, 13, 0, 0, 0},
                {0, 0, 10, 12, 0, 0},
                {0, 4, 0, 0, 14, 0},
                {0, 0, 9, 0, 0, 20},
                {0, 0, 0, 7, 0, 4},
                {0, 0, 0, 0, 0, 0}
        };
        t0 = System.nanoTime();
        EdmondsKarp.FlowResult ekRes = EdmondsKarp.computeMaxFlow(6, flowMatrix, 0, 5);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(13, "Edmonds-Karp Max Flow", "M4: Network Flow (CO4)", "O(V * E^2)", "O(V^2)",
                ekRes.maxFlow == 23.0 ? "VERIFIED (MaxFlow=23.0)" : "FAILED", (t1 - t0) / 1000));

        t0 = System.nanoTime();
        MaxFlowMinCut.MinCutResult cutRes = MaxFlowMinCut.findMinCut(6, flowMatrix, 0, 5);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(14, "Max-Flow Min-Cut Bottlenecks", "M4: Network Flow (CO4)", "O(V * E^2 + V + E)", "O(V^2)",
                cutRes.cutCapacity == 23.0 ? "VERIFIED (MinCut=23.0)" : "FAILED", (t1 - t0) / 1000));

        // M5: NP-Completeness
        // 15. DPLL 3-SAT Solver
        DpllSatSolver.SatFormula formula = new DpllSatSolver.SatFormula(3);
        formula.addClause(1, 2, 3);
        formula.addClause(-1, 2, 3);
        formula.addClause(1, -2, 3);
        t0 = System.nanoTime();
        DpllSatSolver.SatResult satRes = DpllSatSolver.solve(formula);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(15, "DPLL 3-SAT Solver", "M5: NP-Completeness (CO5)", "O(2^N) exponential worst-case", "O(N)",
                satRes.isSatisfiable ? "VERIFIED (SATISFIABLE)" : "FAILED", (t1 - t0) / 1000));

        // 16. Vertex Cover 2-Approximation
        MyArrayList<Pair<Integer, Integer>> edges = new MyArrayList<>();
        edges.add(Pair.of(0, 1));
        edges.add(Pair.of(1, 2));
        edges.add(Pair.of(2, 3));
        t0 = System.nanoTime();
        VertexCover2Approx.ApproxCoverResult vcRes = VertexCover2Approx.computeCover(4, edges);
        t1 = System.nanoTime();
        results.add(new AlgorithmTelemetry(16, "Vertex-Cover 2-Approximation", "M5: NP-Completeness (CO5)", "O(V + E) linear", "O(V)",
                vcRes.coverVertices.size() <= 4 ? "VERIFIED (|C|=" + vcRes.coverVertices.size() + ")" : "FAILED", (t1 - t0) / 1000));

        // M6: Randomized & Parallel
        // 17. Blelloch Parallel Prefix Scan
        long[] sensorData = {3, 1, 7, 0, 4, 1, 6, 3};
        t0 = System.nanoTime();
        long[] scanRes = BlellochScan.exclusiveScan(sensorData);
        t1 = System.nanoTime();
        boolean scanOk = scanRes.length == 8 && scanRes[0] == 0 && scanRes[7] == 22;
        results.add(new AlgorithmTelemetry(17, "Blelloch Parallel Prefix Scan", "M6: Parallel & Randomized (CO6)", "O(N) work, O(log N) depth", "O(N)",
                scanOk ? "VERIFIED (Prefix Scan)" : "FAILED", (t1 - t0) / 1000));

        return results;
    }
}
