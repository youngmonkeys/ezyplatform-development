/*
 * Copyright 2022 youngmonkeys.org
 * 
 * Licensed under the ezyplatform, Version 1.0.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://youngmonkeys.org/licenses/ezyplatform-1.0.0.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.youngmonkeys.ezyplatform.concurrent;

import com.tvd12.ezyfox.util.EzyThreads;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class Waiter {

    private final Supplier<Boolean> condition;
    private final int maxWaitingTime;

    private static final int DEFAULT_MAX_WAITING_TIME = 15000;

    public Waiter(Supplier<Boolean> condition) {
        this(condition, DEFAULT_MAX_WAITING_TIME);
    }

    public void await() {
        long startTime = System.currentTimeMillis();
        while (true) {
            if (condition.get()) {
                break;
            }
            long offset = System.currentTimeMillis() - startTime;
            if (offset > maxWaitingTime) {
                break;
            }
            EzyThreads.sleep(5);
        }
    }
}
