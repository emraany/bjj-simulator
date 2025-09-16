package bjj.engine;

import java.util.Random;

public final class DurationSampler {
    public static long triangular(Random r, long min, long typ, long max) {
        double f = (double) (typ - min) / (double) (max - min);
        double u = r.nextDouble();
        if (u < f) {
            return (long) (min + Math.sqrt(u * (max - min) * (typ - min)));
        } else {
            return (long) (max - Math.sqrt((1 - u) * (max - min) * (max - typ)));
        }
    }
}
