package eu.menzani.ringbuffer.memory;

class LazyMemoryOrder implements MemoryOrder {
    @Override
    public Integer newInteger() {
        return new LazyInteger();
    }
}
