package perftest;

import eu.menzani.ringbuffer.RingBuffer;

public class PrefilledLocalRingBufferTest implements RingBufferTest {
    public static final RingBuffer<Event> RING_BUFFER =
            RingBuffer.prefilled(ONE_TO_ONE_SIZE, FILLER)
                    .build();

    public static void main(String[] args) {
        new PrefilledLocalRingBufferTest().runTest();
    }

    @Override
    public int getBenchmarkRepeatTimes() {
        return 50;
    }

    @Override
    public long getSum() {
        return ONE_TO_ONE_SUM;
    }

    @Override
    public long run() {
        PrefilledWriter writer = PrefilledWriter.runSync(NUM_ITERATIONS, RING_BUFFER);
        Reader reader = Reader.runSync(NUM_ITERATIONS, RING_BUFFER);
        reader.reportPerformance();
        writer.reportPerformance();
        return reader.getSum();
    }
}
