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

package org.youngmonkeys.ezyplatform.socket.event;

import com.tvd12.ezyfox.entity.EzyObject;
import com.tvd12.ezyfoxserver.event.EzyUserLoginEvent;
import org.youngmonkeys.ezyplatform.socket.data.SocketLoginData;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;

public final class EzyUserLoginEvents {

    private EzyUserLoginEvents() {}

    @SuppressWarnings("unchecked")
    public static  <T> T getProperty(
        EzyUserLoginEvent event,
        String key,
        T defaultValue
    ) {
        return (T) event
            .getUserProperties()
            .getOrDefault(key, defaultValue);
    }

    public static boolean isAuthenticated(EzyUserLoginEvent event) {
        return getProperty(event, KEY_AUTHENTICATED, false);
    }

    public static SocketLoginData getLoginData(EzyUserLoginEvent event) {
        Object data = event.getData();
        if (!(data instanceof EzyObject)) {
            return new SocketLoginData();
        }
        EzyObject object = (EzyObject) data;
        return new SocketLoginData(
            object.get(COOKIE_NAME_ACCESS_TOKEN, String.class),
            object.get(COOKIE_NAME_ADMIN_ACCESS_TOKEN, String.class)
        );
    }
}
