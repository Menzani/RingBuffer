package eu.menzani.ringbuffer;

public class LocalDiscardingRingBuffer<T> extends LocalRingBufferBase<T> {
    private final T dummyElement;

    public LocalDiscardingRingBuffer(RingBufferOptions<T> options) {
        super(options);
        dummyElement = options.getDummyElement();
    }

    @Override
    public T put() {
        int oldWritePosition = incrementWritePosition();
        if (readPosition == writePosition) {
            return dummyElement;
        }
        return (T) buffer[oldWritePosition];
    }

    @Override
    public void put(T element) {
        int oldWritePosition = incrementWritePosition();
        if (readPosition != writePosition) {
            buffer[oldWritePosition] = element;
        }
    }

    private int incrementWritePosition() {
        int oldWritePosition = writePosition;
        if (oldWritePosition == capacityMinusOne) {
            writePosition = 0;
        } else {
            writePosition++;
        }
        return oldWritePosition;
    }
}