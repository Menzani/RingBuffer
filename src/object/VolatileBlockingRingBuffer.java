package eu.menzani.ringbuffer.object;

import eu.menzani.ringbuffer.builder.EmptyRingBufferBuilder;
import eu.menzani.ringbuffer.builder.Proxy;
import eu.menzani.ringbuffer.memory.Integer;
import eu.menzani.ringbuffer.wait.BusyWaitStrategy;

import java.util.function.Consumer;

import static eu.menzani.ringbuffer.builder.Proxy.*;

class VolatileBlockingRingBuffer<T> implements EmptyRingBuffer<T> {
    private final int capacity;
    private final int capacityMinusOne;
    private final T[] buffer;
    private final BusyWaitStrategy readBusyWaitStrategy;
    private final BusyWaitStrategy writeBusyWaitStrategy;

    private final Integer readPosition;
    private final Integer writePosition;

    VolatileBlockingRingBuffer(EmptyRingBufferBuilder<T> builder) {
        capacity = Proxy.getCapacity(builder);
        capacityMinusOne = getCapacityMinusOne(builder);
        buffer = getBuffer(builder);
        readBusyWaitStrategy = getReadBusyWaitStrategy(builder);
        writeBusyWaitStrategy = getWriteBusyWaitStrategy(builder);
        readPosition = newCursor(builder);
        writePosition = newCursor(builder);
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void put(T element) {
        int writePosition = this.writePosition.getPlain();
        int newWritePosition;
        if (writePosition == 0) {
            newWritePosition = capacityMinusOne;
        } else {
            newWritePosition = writePosition - 1;
        }
        writeBusyWaitStrategy.reset();
        while (readPosition.get() == newWritePosition) {
            writeBusyWaitStrategy.tick();
        }
        buffer[writePosition] = element;
        this.writePosition.set(newWritePosition);
    }

    @Override
    public T take() {
        int readPosition = this.readPosition.getPlain();
        readBusyWaitStrategy.reset();
        while (writePosition.get() == readPosition) {
            readBusyWaitStrategy.tick();
        }
        if (readPosition == 0) {
            this.readPosition.set(capacityMinusOne);
        } else {
            this.readPosition.set(readPosition - 1);
        }
        return buffer[readPosition];
    }

    @Override
    public void advance() {}

    @Override
    public void takeBatch(int size) {
        int readPosition = this.readPosition.getPlain();
        readBusyWaitStrategy.reset();
        while (size(readPosition) < size) {
            readBusyWaitStrategy.tick();
        }
    }

    @Override
    public T takePlain() {
        int readPosition = this.readPosition.getPlain();
        if (readPosition == 0) {
            this.readPosition.set(capacityMinusOne);
        } else {
            this.readPosition.set(readPosition - 1);
        }
        return buffer[readPosition];
    }

    @Override
    public void advanceBatch() {}

    @Override
    public void forEach(Consumer<T> action) {
        int readPosition = this.readPosition.get();
        int writePosition = this.writePosition.get();
        if (writePosition <= readPosition) {
            for (; readPosition > writePosition; readPosition--) {
                action.accept(buffer[readPosition]);
            }
        } else {
            forEachSplit(action, readPosition, writePosition);
        }
    }

    private void forEachSplit(Consumer<T> action, int readPosition, int writePosition) {
        for (; readPosition >= 0; readPosition--) {
            action.accept(buffer[readPosition]);
        }
        for (readPosition = capacityMinusOne; readPosition > writePosition; readPosition--) {
            action.accept(buffer[readPosition]);
        }
    }

    @Override
    public boolean contains(T element) {
        int readPosition = this.readPosition.get();
        int writePosition = this.writePosition.get();
        if (writePosition <= readPosition) {
            for (; readPosition > writePosition; readPosition--) {
                if (buffer[readPosition].equals(element)) {
                    return true;
                }
            }
            return false;
        }
        return containsSplit(element, readPosition, writePosition);
    }

    private boolean containsSplit(T element, int readPosition, int writePosition) {
        for (; readPosition >= 0; readPosition--) {
            if (buffer[readPosition].equals(element)) {
                return true;
            }
        }
        for (readPosition = capacityMinusOne; readPosition > writePosition; readPosition--) {
            if (buffer[readPosition].equals(element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size(readPosition.get());
    }

    private int size(int readPosition) {
        int writePosition = this.writePosition.get();
        if (writePosition <= readPosition) {
            return readPosition - writePosition;
        }
        return capacity - (writePosition - readPosition);
    }

    @Override
    public boolean isEmpty() {
        return isEmpty(readPosition.get(), writePosition.get());
    }

    private boolean isEmpty(int readPosition, int writePosition) {
        return writePosition == readPosition;
    }

    @Override
    public String toString() {
        int readPosition = this.readPosition.get();
        int writePosition = this.writePosition.get();
        if (isEmpty(readPosition, writePosition)) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder(16);
        builder.append('[');
        if (writePosition < readPosition) {
            for (; readPosition > writePosition; readPosition--) {
                builder.append(buffer[readPosition].toString());
                builder.append(", ");
            }
        } else {
            toStringSplit(builder, readPosition, writePosition);
        }
        builder.setLength(builder.length() - 2);
        builder.append(']');
        return builder.toString();
    }

    private void toStringSplit(StringBuilder builder, int readPosition, int writePosition) {
        for (; readPosition >= 0; readPosition--) {
            builder.append(buffer[readPosition].toString());
            builder.append(", ");
        }
        for (readPosition = capacityMinusOne; readPosition > writePosition; readPosition--) {
            builder.append(buffer[readPosition].toString());
            builder.append(", ");
        }
    }
}
