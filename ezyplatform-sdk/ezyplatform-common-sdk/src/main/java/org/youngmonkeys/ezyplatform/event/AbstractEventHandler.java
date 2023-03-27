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

import com.tvd12.ezyfox.reflect.EzyGenerics;
import com.tvd12.ezyfox.util.EzyLoggable;

public abstract class AbstractEventHandler<D, R>
    extends EzyLoggable
    implements EventHandler<D, R> {

    @Override
    public final R handleEventData(D data) {
        try {
            return doHandleEventData(data);
        } catch (Exception e) {
            logger.info("handle event error", e);
            return handleException(e);
        }
    }

    protected R doHandleEventData(D data) {
        processEventData(data);
        return null;
    }

    protected void processEventData(D data) {}

    protected R handleException(Exception e) {
        return null;
    }

    @Override
    public String getEventName() {
        try {
            return EzyGenerics.getGenericClassArguments(
                getClass().getGenericSuperclass(),
                2
            )[0].getName();
        } catch (Exception e) {
            return getClass().getName();
        }
    }
}
