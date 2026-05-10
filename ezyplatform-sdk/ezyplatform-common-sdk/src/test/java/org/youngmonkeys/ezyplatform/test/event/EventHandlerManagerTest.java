/*
 * Copyright 2026 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.test.event;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.event.EventHandler;
import org.youngmonkeys.ezyplatform.event.EventHandlerManager;
import org.youngmonkeys.ezyplatform.event.EventSchema;
import org.youngmonkeys.ezyplatform.event.EventSchemaFetcher;
import org.youngmonkeys.ezyplatform.event.VoidEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class EventHandlerManagerTest {

    @Test
    public void handleEventByEventNameReturnFirstNotNullResult() {
        // given
        String eventName = "test_event";
        List<String> calls = new ArrayList<>();
        RecordingEventHandler lastHandler = new RecordingEventHandler(
            eventName,
            100,
            "last",
            "last_result",
            calls
        );
        RecordingEventHandler firstHandler = new RecordingEventHandler(
            eventName,
            -100,
            "first",
            null,
            calls
        );
        RecordingEventHandler secondHandler = new RecordingEventHandler(
            eventName,
            0,
            "second",
            "second_result",
            calls
        );
        EventHandlerManager sut = newEventHandlerManager(
            Arrays.asList(lastHandler, firstHandler, secondHandler),
            Collections.emptyList()
        );

        // when
        String actual = sut.handleEvent(eventName, "data");

        // then
        Asserts.assertEquals(actual, "second_result");
        Asserts.assertEquals(
            calls,
            Arrays.asList("first:data", "second:data"),
            false
        );
    }

    @Test
    public void handleEventByDataClassNameReturnResult() {
        // given
        TestEvent data = new TestEvent("hello");
        List<String> calls = new ArrayList<>();
        RecordingEventHandler handler = new RecordingEventHandler(
            TestEvent.class.getName(),
            0,
            "handler",
            "result",
            calls
        );
        EventHandlerManager sut = newEventHandlerManager(
            Collections.singletonList(handler),
            Collections.emptyList()
        );

        // when
        String actual = sut.handleEvent(data);

        // then
        Asserts.assertEquals(actual, "result");
        Asserts.assertEquals(
            calls,
            Collections.singletonList("handler:" + data),
            false
        );
    }

    @Test
    public void handleVoidEventCallAllHandlersAndReturnNull() {
        // given
        TestVoidEvent data = new TestVoidEvent();
        List<String> calls = new ArrayList<>();
        RecordingEventHandler firstHandler = new RecordingEventHandler(
            TestVoidEvent.class.getName(),
            0,
            "first",
            "first_result",
            calls
        );
        RecordingEventHandler secondHandler = new RecordingEventHandler(
            TestVoidEvent.class.getName(),
            1,
            "second",
            "second_result",
            calls
        );
        EventHandlerManager sut = newEventHandlerManager(
            Arrays.asList(secondHandler, firstHandler),
            Collections.emptyList()
        );

        // when
        Object actual = sut.handleEvent(data);

        // then
        Asserts.assertNull(actual);
        Asserts.assertEquals(
            calls,
            Arrays.asList("first:" + data, "second:" + data),
            false
        );
    }

    @Test
    public void handleEventWithNoHandlerReturnNull() {
        // given
        EventHandlerManager sut = newEventHandlerManager(
            Collections.emptyList(),
            Collections.emptyList()
        );

        // when
        Object actual = sut.handleEvent("missing_event", "data");

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void getEventSchemaFetcherByEventNameReturnHighestPriorityFetcher() {
        // given
        TestEventSchemaFetcher lowPriorityFetcher = new TestEventSchemaFetcher(
            "test_event",
            -1
        );
        TestEventSchemaFetcher highPriorityFetcher = new TestEventSchemaFetcher(
            "test_event",
            1
        );
        TestEventSchemaFetcher otherFetcher = new TestEventSchemaFetcher(
            "other_event",
            0
        );
        EventHandlerManager sut = newEventHandlerManager(
            Collections.emptyList(),
            Arrays.asList(highPriorityFetcher, otherFetcher, lowPriorityFetcher)
        );

        // when
        EventSchemaFetcher actual = sut.getEventSchemaFetcherByEventName(
            "test_event"
        );

        // then
        Asserts.assertEquals(actual, highPriorityFetcher);
    }

    @Test
    public void getEventSchemaFetcherByEventNameWithMissingEventReturnNull() {
        // given
        EventHandlerManager sut = newEventHandlerManager(
            Collections.emptyList(),
            Collections.emptyList()
        );

        // when
        EventSchemaFetcher actual = sut.getEventSchemaFetcherByEventName(
            "missing_event"
        );

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void getEventSchemaFetcherMapReturnCopy() {
        // given
        String eventName = "test_event";
        TestEventSchemaFetcher fetcher = new TestEventSchemaFetcher(
            eventName,
            0
        );
        EventHandlerManager sut = newEventHandlerManager(
            Collections.emptyList(),
            Collections.singletonList(fetcher)
        );

        // when
        Map<String, EventSchemaFetcher> actual = sut.getEventSchemaFetcherMap();
        actual.clear();

        // then
        Asserts.assertEquals(actual.size(), 0);
        Asserts.assertEquals(
            sut.getEventSchemaFetcherByEventName(eventName),
            fetcher
        );
    }

    @Test
    public void lazyInitializeSingletonsOnce() {
        // given
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(EventHandler.class))
            .thenReturn(Collections.emptyList());
        when(singletonFactory.getSingletonsOf(EventSchemaFetcher.class))
            .thenReturn(Collections.emptyList());
        EventHandlerManager sut = new EventHandlerManager(singletonFactory);

        // when
        sut.handleEvent("event", "data");
        sut.handleEvent("event", "data");
        sut.getEventSchemaFetcherMap();
        sut.getEventSchemaFetcherByEventName("event");

        // then
        verify(singletonFactory).getSingletonsOf(EventHandler.class);
        verify(singletonFactory).getSingletonsOf(EventSchemaFetcher.class);
        verifyNoMoreInteractions(singletonFactory);
    }

    private EventHandlerManager newEventHandlerManager(
        List<EventHandler> eventHandlers,
        List<EventSchemaFetcher> eventSchemaFetchers
    ) {
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(EventHandler.class))
            .thenReturn(eventHandlers);
        when(singletonFactory.getSingletonsOf(EventSchemaFetcher.class))
            .thenReturn(eventSchemaFetchers);
        return new EventHandlerManager(singletonFactory);
    }

    private static class RecordingEventHandler
        implements EventHandler<Object, Object> {
        private final String eventName;
        private final int priority;
        private final String name;
        private final Object result;
        private final List<String> calls;

        private RecordingEventHandler(
            String eventName,
            int priority,
            String name,
            Object result,
            List<String> calls
        ) {
            this.eventName = eventName;
            this.priority = priority;
            this.name = name;
            this.result = result;
            this.calls = calls;
        }

        @Override
        public Object handleEventData(Object data) {
            calls.add(name + ":" + data);
            return result;
        }

        @Override
        public String getEventName() {
            return eventName;
        }

        @Override
        public int getPriority() {
            return priority;
        }
    }

    private static class TestEventSchemaFetcher implements EventSchemaFetcher {
        private final String eventName;
        private final int priority;
        private final EventSchema schema;

        private TestEventSchemaFetcher(String eventName, int priority) {
            this.eventName = eventName;
            this.priority = priority;
            this.schema = EventSchema
                .builder()
                .description(eventName)
                .build();
        }

        @Override
        public EventSchema getSchema() {
            return schema;
        }

        @Override
        public String getEventName() {
            return eventName;
        }

        @Override
        public int getPriority() {
            return priority;
        }
    }

    private static class TestEvent {
        private final String value;

        private TestEvent(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private static class TestVoidEvent implements VoidEvent {}
}
