package bench.object;

import org.ringbuffer.object.RingBuffer;

public class ManyToManyBlockingContentionPerfBenchmark extends ManyToManyBlockingContentionBenchmark {
    public static final RingBuffer<Event> RING_BUFFER =
            RingBuffer.<Event>withCapacity(NOT_ONE_TO_ONE_SIZE)
                    .manyReaders()
                    .manyWriters()
                    .blocking()
                    .build();

    public static void main(String[] args) {
        new ManyToManyBlockingContentionPerfBenchmark().runBenchmark();
    }

    RingBuffer<Event> getRingBuffer() {
        return RING_BUFFER;
    }
}
