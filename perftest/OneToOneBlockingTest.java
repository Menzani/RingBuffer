package perftest;

import eu.menzani.ringbuffer.RingBuffer;

public class OneToOneBlockingTest implements RingBufferTest {
    public static final RingBuffer<Event> RING_BUFFER =
            RingBuffer.<Event>empty(BLOCKING_SIZE)
                    .oneReader()
                    .oneWriter()
                    .blocking()
                    .withGC()
                    .build();

    public static void main(String[] args) {
        new OneToOneBlockingTest().runTest();
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
        Reader reader = Reader.runAsync(NUM_ITERATIONS, RING_BUFFER);
        Writer writer = Writer.runAsync(NUM_ITERATIONS, RING_BUFFER);
        reader.reportPerformance();
        writer.reportPerformance();
        return reader.getSum();
    }
}
