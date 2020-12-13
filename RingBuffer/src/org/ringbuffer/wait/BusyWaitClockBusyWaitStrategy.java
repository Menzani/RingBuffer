/*
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

package org.ringbuffer.wait;

import org.ringbuffer.util.BusyWaitClock;

public class BusyWaitClockBusyWaitStrategy implements BusyWaitStrategy {
    public static final BusyWaitClockBusyWaitStrategy DEFAULT_INSTANCE = new BusyWaitClockBusyWaitStrategy(50_000);

    public static BusyWaitStrategy getDefault() {
        return WaitBusyWaitStrategy.createDefault(DEFAULT_INSTANCE);
    }

    public static BusyWaitStrategy getDefault(int nanoseconds) {
        return WaitBusyWaitStrategy.createDefault(new BusyWaitClockBusyWaitStrategy(nanoseconds));
    }

    private final int nanoseconds;

    public BusyWaitClockBusyWaitStrategy(int nanoseconds) {
        this.nanoseconds = nanoseconds;
    }

    @Override
    public void reset() {
    }

    @Override
    public void tick() {
        BusyWaitClock.sleepCurrentThread(nanoseconds);
    }
}
