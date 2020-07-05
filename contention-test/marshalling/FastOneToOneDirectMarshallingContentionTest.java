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

package test.marshalling;

import org.ringbuffer.marshalling.DirectMarshallingRingBuffer;
import test.Profiler;

public class FastOneToOneDirectMarshallingContentionTest extends RingBufferTest {
    public static final DirectMarshallingRingBuffer RING_BUFFER =
            DirectMarshallingRingBuffer.withCapacity(ONE_TO_ONE_SIZE)
                    .oneReader()
                    .oneWriter()
                    .fast()
                    .build();

    public static void main(String[] args) {
        new FastOneToOneDirectMarshallingContentionTest().runBenchmark();
    }

    @Override
    protected long getSum() {
        return ONE_TO_ONE_SUM;
    }

    @Override
    protected long testSum() {
        Profiler profiler = createThroughputProfiler(NUM_ITERATIONS);
        FastDirectWriter.startAsync(NUM_ITERATIONS, RING_BUFFER, profiler);
        return FastDirectReader.runAsync(NUM_ITERATIONS, RING_BUFFER, profiler);
    }
}
