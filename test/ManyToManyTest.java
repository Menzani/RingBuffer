package test;

import eu.menzani.ringbuffer.RingBuffer;

public class ManyToManyTest implements RingBufferTest {
    public static final RingBuffer<Event> RING_BUFFER = RingBuffer.<Event>empty(NOT_ONE_TO_ONE_SIZE)
            .manyReaders()
            .manyWriters()
            .build();

    public static void main(String[] args) {
        new ManyToManyTest().runTest();
    }

    @Override
    public int getBenchmarkRepeatTimes() {
        return 12;
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