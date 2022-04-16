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

package org.youngmonkeys.ezyplatform.config;

import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationBefore;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.io.EzyStrings;
import lombok.Setter;

import java.time.Clock;
import java.time.ZoneId;

@Setter
@EzyConfigurationBefore
public class ClockConfig {

    @EzyProperty("server.time_zone_id")
    private String zoneId;

    @EzySingleton
    public ZoneId zoneId() {
        return EzyStrings.isBlank(zoneId)
            ? ZoneId.systemDefault()
            : ZoneId.of(zoneId);
    }

    @EzySingleton
    public Clock clock(ZoneId zoneId) {
        return Clock.system(zoneId);
    }
}
