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

package org.youngmonkeys.ezyplatform.event;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.concurrent.EzyLazyInitializer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EventHandlerManager {

    private final EzyLazyInitializer<Map<String, List<EventHandler>>>
        eventHandlersByName;

    public EventHandlerManager(EzySingletonFactory singletonFactory) {
        this.eventHandlersByName = new EzyLazyInitializer<>(() ->
            ((List<EventHandler>) singletonFactory
                .getSingletonsOf(EventHandler.class)
            )
                .stream()
                .sorted(Comparator.comparingInt(EventHandler::getPriority))
                .collect(
                    Collectors.groupingBy(
                        EventHandler::getEventName,
                        Collectors.toList()
                    )
                )
        );
    }

    public <R> R handleEvent(Object data) {
        return handleEvent(
            data.getClass().getName(),
            data
        );
    }

    public <R> R handleEvent(String eventName, Object data) {
        List<EventHandler> handlers = eventHandlersByName
            .get()
            .getOrDefault(eventName, Collections.emptyList());
        for (EventHandler handler : handlers) {
            Object result = handler.handleEventData(data);
            if (result != null) {
                return (R) result;
            }
        }
        return null;
    }
}
