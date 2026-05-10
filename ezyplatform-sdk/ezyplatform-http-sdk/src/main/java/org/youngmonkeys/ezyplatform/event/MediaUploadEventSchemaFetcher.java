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

import org.youngmonkeys.ezyplatform.data.FileMetadata;
import org.youngmonkeys.ezyplatform.model.MediaModel;

import java.util.Arrays;

public class MediaUploadEventSchemaFetcher
    implements EventSchemaFetcher {

    @Override
    public String getEventName() {
        return MediaUploadEvent.class.getName();
    }

    @Override
    public EventSchema getSchema() {
        return EventSchema.builder()
            .description("Notify handlers while a media upload is being processed.")
            .argumentSchema(
                EventSchema.DataSchema.builder()
                    .dataType(MediaUploadEvent.class)
                    .name("argument")
                    .required(true)
                    .fields(
                        Arrays.asList(
                            EventSchema.DataSchema.builder()
                                .dataType(String.class)
                                .name("uploadFrom")
                                .description("The source that initiated the upload.")
                                .example("admin")
                                .build(),
                            EventSchema.DataSchema.builder()
                                .dataType(long.class)
                                .name("ownerAdminId")
                                .required(true)
                                .description("The admin owner ID.")
                                .example("1")
                                .build(),
                            EventSchema.DataSchema.builder()
                                .dataType(long.class)
                                .name("ownerUserId")
                                .required(true)
                                .description("The user owner ID.")
                                .example("1")
                                .build(),
                            EventSchema.DataSchema.builder()
                                .dataType(MediaModel.class)
                                .name("media")
                                .required(true)
                                .description("The media item being uploaded.")
                                .build(),
                            EventSchema.DataSchema.builder()
                                .dataType(FileMetadata.class)
                                .name("fileMetadata")
                                .required(true)
                                .description("Metadata of the uploaded file.")
                                .build()
                        )
                    )
                    .build()
            )
            .build();
    }
}
