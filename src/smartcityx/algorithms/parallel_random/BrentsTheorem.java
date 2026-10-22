package smartcityx.algorithms.parallel_random;

/**
 * Brent's Theorem Evaluation Engine.
 * Formulates the theoretical execution time and speedup bounds for parallel algorithms:
 *
 *     T_p <= (W - D) / p + D <= W / p + D
 *
 * Where:
 *   W = Total Work (Sequential operations count)
 *   D = Depth / Span (Longest dependency chain / critical path)
 *   p = Number of processing cores / threads
 *
 * Speedup S_p = T_1 / T_p = W / T_p
 * Parallel Efficiency E_p = S_p / p
 */
public class BrentsTheorem {

    public static class Evaluation {
        public final long work;
        public final long depth;
        public final int processors;
        public final double theoreticalTimeTp;
        public final double speedup;
        public final double efficiency;

        public Evaluation(long work, long depth, int processors, double theoreticalTimeTp, double speedup, double efficiency) {
            this.work = work;
            this.depth = depth;
            this.processors = processors;
            this.theoreticalTimeTp = theoreticalTimeTp;
            this.speedup = speedup;
            this.efficiency = efficiency;
        }

        @Override
        public String toString() {
            return String.format("P=%d: T_p=%.2f ops, Speedup=%.2fx, Efficiency=%.1f%%",
                    processors, theoreticalTimeTp, speedup, efficiency * 100);
        }
    }

    public static Evaluation evaluate(long work, long depth, int p) {
        if (p <= 0) p = 1;
        // Brent's upper bound: Tp <= (W - D)/p + D
        double tp = (double) (work - depth) / p + depth;
        if (tp < depth) tp = depth;

        double speedup = (double) work / tp;
        double efficiency = speedup / p;

        return new Evaluation(work, depth, p, tp, speedup, efficiency);
    }
}
