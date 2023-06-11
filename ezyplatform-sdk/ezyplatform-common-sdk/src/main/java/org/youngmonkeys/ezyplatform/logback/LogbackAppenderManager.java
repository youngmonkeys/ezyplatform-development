/*
 * Copyright 2023 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.logback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class LogbackAppenderManager {

    private final Map<Class<?>, LogbackAppender> appenderByType;

    public static final LogbackAppenderManager INSTANCE =
        new LogbackAppenderManager();

    public static LogbackAppenderManager getInstance() {
        return INSTANCE;
    }

    private LogbackAppenderManager() {
        this.appenderByType = new ConcurrentHashMap<>();
    }

    public void addAppender(LogbackAppender appender) {
        appenderByType.putIfAbsent(
            appender.getClass(),
            appender
        );
    }

    public void forEach(Consumer<LogbackAppender> consumer) {
        appenderByType.values().forEach(consumer);
    }
}
