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

package org.ringbuffer.concurrent;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class Atomic<T> {
    private static final VarHandle VALUE;

    static {
        try {
            VALUE = MethodHandles.lookup().findVarHandle(Atomic.class, "value", Object.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private T value;

    public Atomic() {}

    public Atomic(T value) {
        this.value = value;
    }

    public void setPlain(T value) {
        this.value = value;
    }

    public void setOpaque(T value) {
        VALUE.setOpaque(this, value);
    }

    public void setRelease(T value) {
        VALUE.setRelease(this, value);
    }

    public void setVolatile(T value) {
        VALUE.setVolatile(this, value);
    }

    public T getPlain() {
        return value;
    }

    @SuppressWarnings("unchecked")
    public T getOpaque() {
        return (T) VALUE.getOpaque(this);
    }

    @SuppressWarnings("unchecked")
    public T getAcquire() {
        return (T) VALUE.getAcquire(this);
    }

    @SuppressWarnings("unchecked")
    public T getVolatile() {
        return (T) VALUE.getVolatile(this);
    }

    public boolean compareAndSetVolatile(T oldValue, T newValue) {
        return VALUE.compareAndSet(this, oldValue, newValue);
    }

    public boolean weakComparePlainAndSetPlain(T oldValue, T newValue) {
        return VALUE.weakCompareAndSetPlain(this, oldValue, newValue);
    }

    public boolean weakComparePlainAndSetRelease(T oldValue, T newValue) {
        return VALUE.weakCompareAndSetRelease(this, oldValue, newValue);
    }

    public boolean weakCompareAcquireAndSetPlain(T oldValue, T newValue) {
        return VALUE.weakCompareAndSetAcquire(this, oldValue, newValue);
    }

    public boolean weakCompareAndSetVolatile(T oldValue, T newValue) {
        return VALUE.weakCompareAndSet(this, oldValue, newValue);
    }

    @SuppressWarnings("unchecked")
    public T getPlainAndSetRelease(T value) {
        return (T) VALUE.getAndSetRelease(this, value);
    }

    @SuppressWarnings("unchecked")
    public T getAcquireAndSetPlain(T value) {
        return (T) VALUE.getAndSetAcquire(this, value);
    }

    @SuppressWarnings("unchecked")
    public T getAndSetVolatile(T value) {
        return (T) VALUE.getAndSet(this, value);
    }

    @SuppressWarnings("unchecked")
    public T comparePlainAndExchangeRelease(T oldValue, T newValue) {
        return (T) VALUE.compareAndExchangeRelease(this, oldValue, newValue);
    }

    @SuppressWarnings("unchecked")
    public T compareAcquireAndExchangePlain(T oldValue, T newValue) {
        return (T) VALUE.compareAndExchangeAcquire(this, oldValue, newValue);
    }

    @SuppressWarnings("unchecked")
    public T compareAndExchangeVolatile(T oldValue, T newValue) {
        return (T) VALUE.compareAndExchange(this, oldValue, newValue);
    }

    public T getAndUpdate(UnaryOperator<T> updateFunction) {
        T prev = getVolatile(), next = null;
        for (boolean haveNext = false; ; ) {
            if (!haveNext)
                next = updateFunction.apply(prev);
            if (weakCompareAndSetVolatile(prev, next))
                return prev;
            haveNext = (prev == (prev = getVolatile()));
        }
    }

    public T updateAndGet(UnaryOperator<T> updateFunction) {
        T prev = getVolatile(), next = null;
        for (boolean haveNext = false; ; ) {
            if (!haveNext)
                next = updateFunction.apply(prev);
            if (weakCompareAndSetVolatile(prev, next))
                return next;
            haveNext = (prev == (prev = getVolatile()));
        }
    }

    public T getAndAccumulate(T constant, BinaryOperator<T> accumulatorFunction) {
        T prev = getVolatile(), next = null;
        for (boolean haveNext = false; ; ) {
            if (!haveNext)
                next = accumulatorFunction.apply(prev, constant);
            if (weakCompareAndSetVolatile(prev, next))
                return prev;
            haveNext = (prev == (prev = getVolatile()));
        }
    }

    public T accumulateAndGet(T constant, BinaryOperator<T> accumulatorFunction) {
        T prev = getVolatile(), next = null;
        for (boolean haveNext = false; ; ) {
            if (!haveNext)
                next = accumulatorFunction.apply(prev, constant);
            if (weakCompareAndSetVolatile(prev, next))
                return next;
            haveNext = (prev == (prev = getVolatile()));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getVolatile());
    }
}