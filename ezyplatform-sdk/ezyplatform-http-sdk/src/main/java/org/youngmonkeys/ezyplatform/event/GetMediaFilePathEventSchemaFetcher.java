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
import java.io.File;

import java.util.Arrays;
import java.util.Collections;

@EzySingleton
public class GetMediaFilePathEventSchemaFetcher
    implements EventSchemaFetcher {

    @Override
    public String getEventName() {
        return GetMediaFilePathEvent.class.getName();
    }

    @Override
    public EventSchema getSchema() {
        return EventSchema.builder()
            .description("Fetch the physical file path for a media item.")
            .argumentSchema(
                EventSchema.DataSchema.builder()
                    .dataType(GetMediaFilePathEvent.class)
                    .name("argument")
                    .required(true)
                    .fields(
                        Collections.singletonList(
                            EventSchema.DataSchema.builder()
                                .dataType(MediaModel.class)
                                .name("media")
                                .required(true)
                                .description("The media item to resolve.")
                                .build()
                        )
                    )
                    .build()
            )
            .resultSchema(
                EventSchema.DataSchema.builder()
                    .dataType(File.class)
                    .name("result")
                    .description(
                        "The physical media file path, or null if no handler provides it."
                    )
                    .build()
            )
            .build();
    }
}
