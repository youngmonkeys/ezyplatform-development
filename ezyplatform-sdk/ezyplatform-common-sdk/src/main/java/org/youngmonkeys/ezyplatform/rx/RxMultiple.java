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

package org.youngmonkeys.ezyplatform.rx;

import java.util.concurrent.TimeUnit;

public interface RxMultiple extends RxOperation {

    <R> RxMultiple mapBegin(RxFunction<RxValueMap, R> mapper);

    <T, R> RxMultiple map(RxFunction<T, R> mapper);

    void blockingConsume(RxConsumer<RxValueMap> consumer);

    <T> T blockingGet(int timeout, TimeUnit timeUnit);

    <T> T blockingGet(RxFunction<RxValueMap, T> mapper);

    <T> T blockingGet(
        RxFunction<RxValueMap, T> mapper,
        int timeout,
        TimeUnit timeUnit
    );
}
