/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ringbuffer;

import org.ringbuffer.classcopy.CopiedClass;
import org.ringbuffer.memory.MemoryOrder;
import org.ringbuffer.wait.BusyWaitStrategy;
import org.ringbuffer.wait.HintBusyWaitStrategy;

import java.lang.invoke.MethodHandles;

public abstract class AbstractRingBufferBuilder<T> {
    private Boolean oneWriter;
    private Boolean oneReader;
    protected RingBufferType type = RingBufferType.CLEARING;
    private BusyWaitStrategy writeBusyWaitStrategy;
    private BusyWaitStrategy readBusyWaitStrategy = HintBusyWaitStrategy.getDefault();
    protected MemoryOrder memoryOrder = MemoryOrder.LAZY;
    protected boolean copyClass;
    // All fields are copied in <init>(AbstractRingBufferBuilder<T>)

    protected AbstractRingBufferBuilder() {}

    protected AbstractRingBufferBuilder(AbstractRingBufferBuilder<?> builder) {
        oneWriter = builder.oneWriter;
        oneReader = builder.oneReader;
        type = builder.type;
        writeBusyWaitStrategy = builder.writeBusyWaitStrategy;
        readBusyWaitStrategy = builder.readBusyWaitStrategy;
        memoryOrder = builder.memoryOrder;
        copyClass = builder.copyClass;
    }

    public abstract AbstractRingBufferBuilder<T> oneWriter();

    protected void oneWriter0() {
        oneWriter = true;
    }

    public abstract AbstractRingBufferBuilder<T> manyWriters();

    protected void manyWriters0() {
        oneWriter = false;
    }

    public abstract AbstractRingBufferBuilder<T> oneReader();

    protected void oneReader0() {
        oneReader = true;
    }

    public abstract AbstractRingBufferBuilder<T> manyReaders();

    protected void manyReaders0() {
        oneReader = false;
    }

    protected abstract AbstractRingBufferBuilder<?> blocking();

    protected void blocking0() {
        blocking0(HintBusyWaitStrategy.getDefault());
    }

    protected abstract AbstractRingBufferBuilder<?> blocking(BusyWaitStrategy busyWaitStrategy);

    protected void blocking0(BusyWaitStrategy busyWaitStrategy) {
        type = RingBufferType.BLOCKING;
        writeBusyWaitStrategy = busyWaitStrategy;
    }

    public abstract AbstractRingBufferBuilder<T> waitingWith(BusyWaitStrategy busyWaitStrategy);

    protected void waitingWith0(BusyWaitStrategy busyWaitStrategy) {
        readBusyWaitStrategy = busyWaitStrategy;
    }

    public abstract AbstractRingBufferBuilder<T> withMemoryOrder(MemoryOrder memoryOrder);

    protected void withMemoryOrder0(MemoryOrder memoryOrder) {
        this.memoryOrder = memoryOrder;
    }

    /**
     * A separate implementation will be created to allow inlining of polymorphic calls.
     *
     * @see CopiedClass
     */
    public abstract AbstractRingBufferBuilder<T> copyClass();

    protected void copyClass0() {
        copyClass = true;
    }

    public T build() {
        validate();

        RingBufferConcurrency concurrency;
        if (oneReader) {
            if (oneWriter) {
                concurrency = RingBufferConcurrency.VOLATILE;
            } else {
                concurrency = RingBufferConcurrency.ATOMIC_WRITE;
            }
        } else if (oneWriter) {
            concurrency = RingBufferConcurrency.ATOMIC_READ;
        } else {
            concurrency = RingBufferConcurrency.CONCURRENT;
        }
        return create(concurrency, type);
    }

    protected void validate() {
        if (oneReader == null && oneWriter == null) {
            throw new IllegalStateException("You must call either oneReader() or manyReaders(), and oneWriter() or manyWriters().");
        }
        if (oneReader == null) {
            throw new IllegalStateException("You must call either oneReader() or manyReaders().");
        }
        if (oneWriter == null) {
            throw new IllegalStateException("You must call either oneWriter() or manyWriters().");
        }
    }

    protected abstract T create(RingBufferConcurrency concurrency, RingBufferType type);

    protected T instantiateCopy(Class<?> ringBufferClass) {
        return CopiedClass.<T>of(ringBufferClass, getImplLookup())
                .getConstructor(getClass())
                .call(this);
    }

    protected abstract MethodHandles.Lookup getImplLookup();

    protected BusyWaitStrategy getWriteBusyWaitStrategy() {
        return writeBusyWaitStrategy;
    }

    protected BusyWaitStrategy getReadBusyWaitStrategy() {
        return readBusyWaitStrategy;
    }

    protected enum RingBufferConcurrency {
        VOLATILE,
        ATOMIC_READ,
        ATOMIC_WRITE,
        CONCURRENT
    }

    protected enum RingBufferType {
        CLEARING,
        BLOCKING,
        DISCARDING
    }
}