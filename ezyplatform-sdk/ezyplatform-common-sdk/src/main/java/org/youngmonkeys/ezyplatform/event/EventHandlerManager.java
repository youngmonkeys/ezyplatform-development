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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EventHandlerManager {

    private final EzyLazyInitializer<Map<String, List<EventHandler>>>
        eventHandlersByName;
    private final EzyLazyInitializer<Map<String, EventSchemaFetcher>>
        eventSchemaFetcherByName;
    private final EzyLazyInitializer<List<EventHandlerProvider>>
        eventHandlerProviders;

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
        this.eventSchemaFetcherByName = new EzyLazyInitializer<>(() ->
            ((List<EventSchemaFetcher>) singletonFactory
                .getSingletonsOf(EventSchemaFetcher.class)
            )
                .stream()
                .sorted(Comparator.comparingInt(EventSchemaFetcher::getPriority))
                .collect(
                    Collectors.toMap(
                        EventSchemaFetcher::getEventName,
                        it -> it,
                        (o, n) -> n
                    )
                )
        );
        this.eventHandlerProviders = new EzyLazyInitializer<>(() ->
            singletonFactory.getSingletonsOf(EventHandlerProvider.class)
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
        boolean voidEvent = data instanceof VoidEvent;
        for (EventHandler handler : handlers) {
            Object result = handler.handleEventData(data);
            if (!voidEvent && result != null) {
                return (R) result;
            }
        }
        for (EventHandlerProvider provider : eventHandlerProviders.get()) {
            handlers = provider.provideEventHandlers(eventName);
            for (EventHandler handler : handlers) {
                Object result = handler.handleEventData(data);
                if (!voidEvent && result != null) {
                    return (R) result;
                }
            }
        }
        return null;
    }

    public Map<String, List<EventHandler>> getEventHandlersByEventName() {
        Map<String, List<EventHandler>> answer = eventHandlersByName
            .get()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    it -> new ArrayList<>(it.getValue())
                )
            );
        for (EventHandlerProvider provider : eventHandlerProviders.get()) {
            Map<String, List<EventHandler>> map = provider
                .getEventHandlersByEventName();
            for (Map.Entry<String, List<EventHandler>> e : map.entrySet()) {
                answer
                    .computeIfAbsent(e.getKey(), k -> new ArrayList<>())
                    .addAll(e.getValue());
            }
        }
        return answer;
    }

    public List<EventHandler> getEventHandlersByEventName(
        String eventName
    ) {
        List<EventHandler> answer = new ArrayList<>(
            eventHandlersByName
                .get()
                .getOrDefault(
                    eventName,
                    Collections.emptyList()
                )
        );
        for (EventHandlerProvider provider : eventHandlerProviders.get()) {
            List<EventHandler> list = provider
                .provideEventHandlers(eventName);
            if (list != null) {
                answer.addAll(list);
            }
        }
        return answer;
    }

    public EventSchemaFetcher getEventSchemaFetcherByEventName(
        String eventName
    ) {
        EventSchemaFetcher answer = eventSchemaFetcherByName
            .get()
            .get(eventName);
        if (answer == null) {
            for (EventHandlerProvider provider : eventHandlerProviders.get()) {
                answer = provider.provideEventSchemaFetcher(eventName);
                if (answer != null) {
                    break;
                }
            }
        }
        return answer;
    }

    public List<EventSchemaFetcher> getEventSchemaFetchers() {
        List<EventSchemaFetcher> answer = new ArrayList<>(
            eventSchemaFetcherByName.get().values()
        );
        for (EventHandlerProvider provider : eventHandlerProviders.get()) {
            answer.addAll(provider.provideEventSchemaFetchers());
        }
        return answer;
    }

    public Map<String, EventSchemaFetcher> getEventSchemaFetcherMap() {
        Map<String, EventSchemaFetcher> answer = new HashMap<>(
            eventSchemaFetcherByName.get()
        );
        for (EventHandlerProvider provider : eventHandlerProviders.get()) {
            answer.putAll(provider.getEventFetcherByEventName());
        }
        return answer;
    }
}
