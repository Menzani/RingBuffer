package test.wait;

import eu.menzani.ringbuffer.wait.BusyWaitStrategy;
import eu.menzani.ringbuffer.wait.LinkedMultiStepBusyWaitStrategy;

public class TwoStepLinkedMultiStepTest extends MultiStepBusyWaitStrategyTest {
    public static void main(String[] args) {
        new TwoStepLinkedMultiStepTest(true).runBenchmark();
    }

    public TwoStepLinkedMultiStepTest(boolean isPerfTest) {
        super(isPerfTest);
    }

    @Override
    String getProfilerName() {
        return "TwoStepLinkedMultiStep";
    }

    @Override
    BusyWaitStrategy getStrategy() {
        return LinkedMultiStepBusyWaitStrategy.endWith(SECOND)
                .after(FIRST, STEP_TICKS)
                .build();
    }

    @Override
    public int getNumSteps() {
        return 2;
    }
}