package smartcityx.service;

import smartcityx.algorithms.parallel_random.*;
import smartcityx.core.MyArrayList;
import smartcityx.model.IoTSensorReading;

/**
 * Service orchestrating Randomized & Parallel Algorithms (M6 - CO6) for continuous smart-city sensor analytics.
 */
public class IoTStreamAnalyticsService {

    public MyArrayList<IoTSensorReading> sampleTelemetryStream(MyArrayList<IoTSensorReading> fullStream, int sampleSize) {
        return ReservoirSampling.sample(fullStream, sampleSize);
    }

    public boolean authenticateSensorToken(long securityToken, int rounds) {
        return MillerRabin.isPrime(securityToken, rounds);
    }

    public long[] computeCumulativeEnergyConsumption(long[] hourlyMegawattReadings) {
        return BlellochScan.exclusiveScan(hourlyMegawattReadings);
    }

    public double aggregateCityMetrics(double[] sensorValues, boolean computeMax) {
        if (computeMax) {
            return ParallelReduce.parallelMax(sensorValues);
        } else {
            return ParallelReduce.parallelSum(sensorValues);
        }
    }

    public BrentsTheorem.Evaluation evaluateParallelSpeedup(long work, long depth, int cores) {
        return BrentsTheorem.evaluate(work, depth, cores);
    }
}
