package bench.wait;

import org.ringbuffer.wait.BusyWaitStrategy;

public class TwoStepManualMultiStepBenchmark extends MultiStepBusyWaitStrategyBenchmark {
    public static void main(String[] args) {
        new TwoStepManualMultiStepBenchmark().runBenchmark();
    }

    public TwoStepManualMultiStepBenchmark() {
        this(true);
    }

    public TwoStepManualMultiStepBenchmark(boolean isBenchmark) {
        super(isBenchmark);
    }

    @Override
    BusyWaitStrategy getStrategy() {
        return new TwoStepManualMultiStepBusyWaitStrategy();
    }

    @Override
    public int getNumSteps() {
        return 2;
    }

    private class TwoStepManualMultiStepBusyWaitStrategy implements BusyWaitStrategy {
        private int counter;

        @Override
        public void reset() {
            counter = STEP_TICKS + 1;
        }

        @Override
        public void tick() {
            switch (counter) {
                case 0:
                    second.tick();
                    return;
                case 1:
                    second.reset();
                    second.tick();
                    break;
                case STEP_TICKS + 1:
                    first.reset();
                default:
                    first.tick();
            }
            counter--;
        }
    }
}
