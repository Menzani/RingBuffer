package test;

import eu.menzani.ringbuffer.RingBuffer;

public class ManyToManyBlockingTest implements RingBufferTest {
    public static final RingBuffer<Event> RING_BUFFER = RingBuffer.<Event>empty(BLOCKING_SIZE)
            .manyReaders()
            .manyWriters()
            .blocking()
            .withGC()
            .build();

    public static void main(String[] args) {
        new ManyToManyBlockingTest().runTest();
    }

    @Override
    public int getBenchmarkRepeatTimes() {
        return 10;
    }

    @Override
    public long getSum() {
        return MANY_WRITERS_SUM;
    }

    @Override
    public long run() {
        TestThreadGroup group = Writer.startGroupAsync(RING_BUFFER);
        long sum = Reader.runGroupAsync(RING_BUFFER);
        group.reportPerformance();
        return sum;
    }
}