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

package org.youngmonkeys.ezyplatform.test.event;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.event.EventHandler;

public class EventHandlerTest {

    @Test
    public void getEventNameTest() {
        // given
        TestEventHandler sut = new TestEventHandler();

        // when
        String actual = sut.getEventName();

        // then
        Asserts.assertEquals(actual, String.class.getName());
    }

    public static class TestEventHandler
        implements EventHandler<String, Void> {
        @Override
        public Void handleEventData(String data) {
            return null;
        }
    }
}
