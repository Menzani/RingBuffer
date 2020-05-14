package test;

import eu.menzani.ringbuffer.EmptyRingBuffer;
import eu.menzani.ringbuffer.OverwritingPrefilledRingBuffer;
import eu.menzani.ringbuffer.PrefilledRingBuffer;
import eu.menzani.ringbuffer.wait.YieldBusyWaitStrategy;

public class ProducersToProcessorToConsumersTest extends RingBufferTest {
    public static final EmptyRingBuffer<Event> PRODUCERS_RING_BUFFER =
            EmptyRingBuffer.<Event>withCapacity(BLOCKING_SIZE)
                    .manyWriters()
                    .oneReader()
                    .blocking()
                    .withGC()
                    .build();
    public static final OverwritingPrefilledRingBuffer<Event> CONSUMERS_RING_BUFFER =
            PrefilledRingBuffer.withCapacityAndFiller(NOT_ONE_TO_ONE_SIZE, FILLER)
                    .oneWriter()
                    .manyReaders()
                    .waitingWith(YieldBusyWaitStrategy.getDefault())
                    .build();

    public static void main(String[] args) {
        new ProducersToProcessorToConsumersTest().run();
    }

    @Override
    protected int getRepeatTimes() {
        return 10;
    }

    @Override
    long getSum() {
        return MANY_WRITERS_SUM;
    }

    @Override
    long testSum() {
        TestThreadGroup group = Writer.startGroupAsync(PRODUCERS_RING_BUFFER);
        Processor processor = Processor.startAsync(TOTAL_ELEMENTS);
        long sum = BatchReader.runGroupAsync(BATCH_SIZE, CONSUMERS_RING_BUFFER);
        processor.reportPerformance();
        group.reportPerformance();
        return sum;
    }

    static class Processor extends TestThread {
        private static Processor startAsync(int numIterations) {
            Processor processor = new Processor(numIterations);
            processor.start();
            return processor;
        }

        static void runAsync(int numIterations) {
            startAsync(numIterations).reportPerformance();
        }

        private Processor(int numIterations) {
            super(numIterations, null);
        }

        @Override
        void loop() {
            int numIterations = getNumIterations();
            for (; numIterations > 0; numIterations--) {
                int eventData = PRODUCERS_RING_BUFFER.take().getData();
                int key = CONSUMERS_RING_BUFFER.nextKey();
                CONSUMERS_RING_BUFFER.next(key).setData(eventData);
                CONSUMERS_RING_BUFFER.put(key);
            }
        }
    }
}
