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

import com.tvd12.ezyfox.function.EzyExceptionSupplier;
import com.tvd12.ezyfox.function.EzyExceptionVoid;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.exception.MaxRetryCountException;

import java.util.function.BiConsumer;

@AllArgsConstructor
public class Retry<T> {
    private final EzyExceptionSupplier<T> func;
    private final BiConsumer<Integer, Exception> exceptionConsumer;
    private final int maxRetryTime;
    private static final int DEFAULT_MAX_RETRY_TIME = -1;
    private static final int DEFAULT_SLEEP_TIME = 1000;
    private static final int MAX_SLEEP_TIME = 3000;

    public Retry(
        EzyExceptionSupplier<T> func,
        BiConsumer<Integer, Exception> exceptionConsumer
    ) {
        this(func, exceptionConsumer, DEFAULT_MAX_RETRY_TIME);
    }

    @SuppressWarnings("unchecked")
    public Retry(
        EzyExceptionVoid func,
        BiConsumer<Integer, Exception> exceptionConsumer
    ) {
        this(
            () -> {
                func.apply();
                return (T) Boolean.TRUE;
            },
            exceptionConsumer
        );
    }

    public void run() throws InterruptedException {
        get();
    }

    @SuppressWarnings("BusyWait")
    public T get() throws InterruptedException {
        int retryCount = 0;
        while (true) {
            try {
                return func.get();
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                if (maxRetryTime > 0 && retryCount >= maxRetryTime) {
                    throw new MaxRetryCountException(maxRetryTime);
                }
                exceptionConsumer.accept(retryCount++, e);
                int sleepTime = Math.min(
                    DEFAULT_SLEEP_TIME * retryCount,
                    MAX_SLEEP_TIME
                );
                Thread.sleep(sleepTime);
            }
        }
    }

    public T getOrNull() {
        return getOrDefault(null);
    }

    public T getOrDefault(T defaultValue) {
        try {
            return get();
        } catch (InterruptedException e) {
            return defaultValue;
        }
    }
}
