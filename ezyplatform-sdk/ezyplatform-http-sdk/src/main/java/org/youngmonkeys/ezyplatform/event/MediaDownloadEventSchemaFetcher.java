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

import java.util.Arrays;

@EzySingleton
public class MediaDownloadEventSchemaFetcher
    implements EventSchemaFetcher {

    @Override
    public String getEventName() {
        return MediaDownloadEvent.class.getName();
    }

    @Override
    public EventSchema getSchema() {
        return EventSchema.builder()
            .description("Notify handlers before or during a media download.")
            .argumentSchema(
                EventSchema.DataSchema.builder()
                    .dataType(MediaDownloadEvent.class)
                    .name("argument")
                    .required(true)
                    .fields(
                        Arrays.asList(
                            EventSchema.DataSchema.builder()
                                .dataType(Long.class)
                                .name("userId")
                                .description("The user downloading the media.")
                                .example("1")
                                .build(),
                            EventSchema.DataSchema.builder()
                                .dataType(MediaModel.class)
                                .name("media")
                                .required(true)
                                .description("The media item being downloaded.")
                                .build()
                        )
                    )
                    .build()
            )
            .build();
    }
}
