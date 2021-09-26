package osmo.tester.generator.testsuite;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class TestStatistics {
    private long total = 0;
    private long count = 0;
    private double powerSum1 = 0;
    private double powerSum2 = 0;
    private double stdev = 0;
    private List<Long> raw = new ArrayList<>();

    public void addValue(long value) {
        count++;
        total += value;
        powerSum1 += value;
        powerSum2 += Math.pow(value, 2);
        stdev = Math.sqrt(count*powerSum2 - Math.pow(powerSum1, 2))/count;
        raw.add(value);
    }

    public double getMean() {
        double dTotal = total;
        return dTotal / count;
    }

    public double getStandardDeviation() {
        return stdev;
    }
}
