package eu.menzani.ringbuffer;

public class PrefilledOneToOneTest extends RingBufferTest {
    public PrefilledOneToOneTest() {
        super(VolatileRingBuffer.class, 499999500000L, RingBuffer.prefilled(NUM_ITERATIONS + 1, Event.RING_BUFFER_FILLER)
                .oneReader()
                .oneWriter());
    }

    long run() throws InterruptedException {
        Reader reader = new Reader(NUM_ITERATIONS, ringBuffer);
        PrefilledWriter writer = new PrefilledWriter(NUM_ITERATIONS, ringBuffer);
        reader.reportPerformance();
        writer.reportPerformance();
        return reader.getSum();
    }
}