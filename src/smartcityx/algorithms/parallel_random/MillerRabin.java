package smartcityx.algorithms.parallel_random;

import java.util.Random;

/**
 * Miller-Rabin Randomized Primality Test for municipal cryptography & secure IoT token generation.
 * Time Complexity: O(k * log^3 N) where k is the number of test rounds.
 * Error probability is bounded by (1/4)^k.
 */
public class MillerRabin {

    private static final Random RNG = new Random();

    public static boolean isPrime(long n, int iterations) {
        if (n <= 1 || n == 4) return false;
        if (n <= 3) return true;
        if (n % 2 == 0) return false;

        // Write n - 1 as 2^r * d
        long d = n - 1;
        int r = 0;
        while (d % 2 == 0) {
            d /= 2;
            r++;
        }

        for (int i = 0; i < iterations; i++) {
            long a = 2 + (long) (RNG.nextDouble() * (n - 4));
            if (!millerTest(d, r, n, a)) {
                return false; // Composite with certainty
            }
        }
        return true; // Probably prime
    }

    private static boolean millerTest(long d, int r, long n, long a) {
        long x = modularExponentiation(a, d, n);
        if (x == 1 || x == n - 1) return true;

        for (int i = 0; i < r - 1; i++) {
            x = modularMultiply(x, x, n);
            if (x == n - 1) return true;
            if (x == 1) return false;
        }
        return false;
    }

    private static long modularExponentiation(long base, long exp, long mod) {
        long res = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) res = modularMultiply(res, base, mod);
            base = modularMultiply(base, base, mod);
            exp >>= 1;
        }
        return res;
    }

    private static long modularMultiply(long a, long b, long mod) {
        return (long) ((java.math.BigInteger.valueOf(a)
                .multiply(java.math.BigInteger.valueOf(b))
                .mod(java.math.BigInteger.valueOf(mod))).longValue());
    }
}
