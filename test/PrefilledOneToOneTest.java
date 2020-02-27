package eu.menzani.ringbuffer;

public class PrefilledOneToOneTest extends RingBufferTest {
    public PrefilledOneToOneTest() {
        super(VolatileRingBuffer.class, 499999500000L, RingBuffer.prefilled(NUM_ITERATIONS + 1, Event.RING_BUFFER_FILLER)
                .oneReader()
                .oneWriter());
    }

    @Override
    int getBenchmarkRepeatTimes() {
        return 130;
    }

    long run() {
        Reader reader = Reader.newReader(NUM_ITERATIONS, ringBuffer);
        PrefilledWriter writer = new PrefilledWriter(NUM_ITERATIONS, ringBuffer);
        reader.reportPerformance();
        writer.reportPerformance();
        return reader.getSum();
    }
}
