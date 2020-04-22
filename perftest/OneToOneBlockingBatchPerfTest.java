package test;

class OneToOneBlockingBatchPerfTest extends OneToOneBlockingPerfTest {
    public static void main(String[] args) {
        new OneToOneBlockingBatchPerfTest().runTest();
    }

    @Override
    public long run() {
        Writer.runAsync(NUM_ITERATIONS, RING_BUFFER);
        return BatchReader.runAsync(NUM_ITERATIONS, READ_BUFFER_SIZE, RING_BUFFER);
    }
}