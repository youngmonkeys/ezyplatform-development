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

import org.youngmonkeys.ezyplatform.model.MediaModel;

import java.io.File;
import java.util.Arrays;

public class MediaUploadedEventSchemaFetcher
    implements EventSchemaFetcher {

    @Override
    public String getEventName() {
        return MediaUploadedEvent.class.getName();
    }

    @Override
    public EventSchema getSchema() {
        return EventSchema.builder()
            .description("Notify handlers after a media file has been uploaded.")
            .argumentSchema(
                EventSchema.DataSchema.builder()
                    .dataType(MediaUploadedEvent.class)
                    .name("argument")
                    .required(true)
                    .fields(
                        Arrays.asList(
                            EventSchema.DataSchema.builder()
                                .dataType(MediaModel.class)
                                .name("media")
                                .required(true)
                                .description("The uploaded media item.")
                                .build(),
                            EventSchema.DataSchema.builder()
                                .dataType(File.class)
                                .name("mediaFilePath")
                                .required(true)
                                .description("The uploaded media file path.")
                                .example("/data/media/example.jpg")
                                .build()
                        )
                    )
                    .build()
            )
            .build();
    }
}
