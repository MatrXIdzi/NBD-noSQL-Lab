package org.restaurant.benchmarks;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

@Disabled
public class BenchmarksRunnerTest {
    @Test
    public void runMongoBenchmark() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include("MongoBenchmark")
                .mode (Mode.AverageTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .warmupTime(TimeValue.milliseconds(5000))
                .warmupIterations(2)
                .measurementTime(TimeValue.milliseconds(5000))
                .measurementIterations(2)
                .threads(1)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(opt).run();
    }

    @Test
    public void runRedisBenchmark() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include("RedisBenchmark")
                .mode (Mode.AverageTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .warmupTime(TimeValue.milliseconds(5000))
                .warmupIterations(2)
                .measurementTime(TimeValue.milliseconds(5000))
                .measurementIterations(2)
                .threads(1)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(opt).run();
    }
}
