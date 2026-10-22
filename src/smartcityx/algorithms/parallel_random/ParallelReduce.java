package smartcityx.algorithms.parallel_random;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Parallel Reduce using Java ForkJoin framework for city-wide IoT telemetry aggregation.
 * Work: O(N), Span (Depth): O(log N).
 */
public class ParallelReduce {

    private static final int THRESHOLD = 1000;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    private static class SumTask extends RecursiveTask<Double> {
        private final double[] array;
        private final int start;
        private final int end;

        public SumTask(double[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Double compute() {
            if (end - start <= THRESHOLD) {
                double sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            } else {
                int mid = (start + end) / 2;
                SumTask left = new SumTask(array, start, mid);
                SumTask right = new SumTask(array, mid, end);
                left.fork();
                double rightAns = right.compute();
                double leftAns = left.join();
                return leftAns + rightAns;
            }
        }
    }

    private static class MaxTask extends RecursiveTask<Double> {
        private final double[] array;
        private final int start;
        private final int end;

        public MaxTask(double[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Double compute() {
            if (end - start <= THRESHOLD) {
                double max = Double.NEGATIVE_INFINITY;
                for (int i = start; i < end; i++) {
                    if (array[i] > max) max = array[i];
                }
                return max;
            } else {
                int mid = (start + end) / 2;
                MaxTask left = new MaxTask(array, start, mid);
                MaxTask right = new MaxTask(array, mid, end);
                left.fork();
                double rightAns = right.compute();
                double leftAns = left.join();
                return Math.max(leftAns, rightAns);
            }
        }
    }

    public static double parallelSum(double[] array) {
        if (array == null || array.length == 0) return 0;
        return POOL.invoke(new SumTask(array, 0, array.length));
    }

    public static double parallelMax(double[] array) {
        if (array == null || array.length == 0) return 0;
        return POOL.invoke(new MaxTask(array, 0, array.length));
    }
}
