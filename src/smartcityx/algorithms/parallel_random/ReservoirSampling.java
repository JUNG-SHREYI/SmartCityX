package smartcityx.algorithms.parallel_random;

import smartcityx.core.MyArrayList;
import java.util.Random;

/**
 * Reservoir Sampling (Algorithm R) for unbounded continuous IoT sensor telemetry streams.
 * Maintains a uniform random sample of size k from a stream of unknown total length N.
 * Invariant: Every item observed so far has exact probability k / n of being in the reservoir.
 * Space Complexity: O(k). Time Complexity: O(N).
 */
public class ReservoirSampling {

    private static final Random RNG = new Random();

    public static <T> MyArrayList<T> sample(MyArrayList<T> stream, int k) {
        MyArrayList<T> reservoir = new MyArrayList<>(k);
        if (stream == null || k <= 0) return reservoir;

        int n = stream.size();
        for (int i = 0; i < Math.min(k, n); i++) {
            reservoir.add(stream.get(i));
        }

        for (int i = k; i < n; i++) {
            int j = RNG.nextInt(i + 1); // 0 <= j <= i
            if (j < k) {
                reservoir.set(j, stream.get(i));
            }
        }

        return reservoir;
    }
}
