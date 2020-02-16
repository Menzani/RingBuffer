package eu.menzani.ringbuffer;

public interface RingBuffer<T> {
    int getCapacity();

    void put(T element);

    T take();

    int size();

    boolean isEmpty();

    boolean isNotEmpty();
}
