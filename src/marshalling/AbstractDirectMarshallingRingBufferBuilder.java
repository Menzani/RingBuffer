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

package org.ringbuffer.marshalling;

import org.ringbuffer.java.Assume;
import org.ringbuffer.java.Number;
import org.ringbuffer.memory.Long;

abstract class AbstractDirectMarshallingRingBufferBuilder<T> extends AbstractBaseMarshallingRingBufferBuilder<T> {
    private final long capacity;
    private DirectByteArray.Factory byteArrayFactory = DirectByteArray.SAFE;
    // All fields are copied in <init>(AbstractDirectMarshallingRingBufferBuilder<T>)

    AbstractDirectMarshallingRingBufferBuilder(long capacity) {
        Assume.notLesser(capacity, 2L);
        if (!Number.isPowerOfTwo(capacity)) {
            throw new IllegalArgumentException("capacity must be a power of 2.");
        }
        this.capacity = capacity;
    }

    AbstractDirectMarshallingRingBufferBuilder(AbstractDirectMarshallingRingBufferBuilder<?> builder) {
        super(builder);
        capacity = builder.capacity;
        byteArrayFactory = builder.byteArrayFactory;
    }

    public abstract AbstractDirectMarshallingRingBufferBuilder<T> withByteArray(DirectByteArray.Factory factory);

    void withByteArray0(DirectByteArray.Factory factory) {
        byteArrayFactory = factory;
    }

    long getCapacity() {
        return capacity;
    }

    long getCapacityMinusOne() {
        return capacity - 1L;
    }

    DirectByteArray getBuffer() {
        return byteArrayFactory.newInstance(capacity);
    }

    Long newCursor() {
        return memoryOrder.newLong();
    }
}