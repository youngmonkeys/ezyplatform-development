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
import org.youngmonkeys.ezyplatform.data.MediaFileSizeReductionResult;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import java.io.File;

import java.util.Arrays;

@EzySingleton
public class MediaFileSizeReductionEventSchemaFetcher
    implements EventSchemaFetcher {

    @Override
    public String getEventName() {
        return MediaFileSizeReductionEvent.class.getName();
    }

    @Override
    public EventSchema getSchema() {
        return EventSchema.builder()
            .description("Request handlers to reduce media file size.")
            .argumentSchema(
                EventSchema.DataSchema.builder()
                    .dataType(MediaFileSizeReductionEvent.class)
                    .name("argument")
                    .required(true)
                    .fields(
                        Arrays.asList(
                            EventSchema.DataSchema.builder()
                                .dataType(MediaType.class)
                                .name("mediaType")
                                .required(true)
                                .description("The media type of the file.")
                                .example("IMAGE")
                                .build(),
                            EventSchema.DataSchema.builder()
                                .dataType(File.class)
                                .name("mediaFilePath")
                                .required(true)
                                .description("The media file path to reduce.")
                                .example("/data/media/example.jpg")
                                .build(),
                            EventSchema.DataSchema.builder()
                                .dataType(long.class)
                                .name("expectedFileSize")
                                .required(true)
                                .description("The target maximum file size in bytes.")
                                .example("1048576")
                                .build()
                        )
                    )
                    .build()
            )
            .resultSchema(
                EventSchema.DataSchema.builder()
                    .dataType(MediaFileSizeReductionResult.class)
                    .name("result")
                    .description(
                        "The reduced file information, or null if no handler reduces it."
                    )
                    .build()
            )
            .build();
    }
}
