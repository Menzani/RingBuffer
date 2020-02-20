package eu.menzani.ringbuffer;

class ManyReadersOneWriterRingBuffer<T> implements RingBuffer<T> {
    private final OneReaderOneWriterRingBuffer delegate;

    ManyReadersOneWriterRingBuffer(RingBufferBuilder options) {
        delegate = new OneReaderOneWriterRingBuffer<>(options);
    }

    @Override
    public int getCapacity() {
        return delegate.getCapacity();
    }

    @Override
    public T put() {
        return (T) delegate.put();
    }

    @Override
    public void commit() {
        delegate.commit();
    }

    @Override
    public void put(T element) {
        delegate.put(element);
    }

    @Override
    public synchronized T take() {
        return (T) delegate.take();
    }

    @Override
    public boolean contains(T element) {
        return delegate.contains(getReadPosition(), delegate.getWritePosition(), element);
    }

    @Override
    public int size() {
        return delegate.size(getReadPosition(), delegate.getWritePosition());
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty(getReadPosition(), delegate.getWritePosition());
    }

    @Override
    public String toString() {
        return delegate.toString(getReadPosition(), delegate.getWritePosition());
    }

    private synchronized int getReadPosition() {
        return delegate.getReadPosition();
    }
}
