package perftest;

class PrefilledManyReadersBlockingBatchTest extends PrefilledManyReadersBlockingTest {
    public static void main(String[] args) {
        new PrefilledManyReadersBlockingBatchTest().runTest();
    }

    @Override
    public long run() {
        TestThreadGroup readerGroup = SynchronizedAdvancingBatchReader.runGroupAsync(READ_BUFFER_BLOCKING_SIZE, RING_BUFFER);
        PrefilledWriter writer = PrefilledWriter.runAsync(TOTAL_ELEMENTS, RING_BUFFER);
        readerGroup.reportPerformance();
        writer.reportPerformance();
        return readerGroup.getReaderSum();
    }
}