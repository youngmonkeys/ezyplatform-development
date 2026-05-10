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

package org.youngmonkeys.ezyplatform.event;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import org.youngmonkeys.ezyplatform.model.MediaModel;

import java.util.Collections;

@EzySingleton
public class MediaAddedEventSchemaFetcher
    implements EventSchemaFetcher {

    @Override
    public String getEventName() {
        return MediaAddedEvent.class.getName();
    }

    @Override
    public EventSchema getSchema() {
        return EventSchema.builder()
            .description("Notify handlers after a media record has been added.")
            .argumentSchema(
                EventSchema.DataSchema.builder()
                    .dataType(MediaAddedEvent.class)
                    .name("argument")
                    .required(true)
                    .fields(
                        Collections.singletonList(
                            EventSchema.DataSchema.builder()
                                .dataType(MediaModel.class)
                                .name("media")
                                .required(true)
                                .description("The added media item.")
                                .build()
                        )
                    )
                    .build()
            )
            .build();
    }
}
