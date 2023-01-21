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

package org.youngmonkeys.ezyplatform.config;

import com.tvd12.ezyhttp.server.core.view.MessageProvider;
import com.tvd12.ezyhttp.server.core.view.MessageReader;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.entity.ModuleType;
import org.youngmonkeys.ezyplatform.entity.TargetType;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@AllArgsConstructor
public abstract class AbstractMessageProvider implements MessageProvider {

    private final FileSystemManager fileSystemManager;
    private static final String MESSAGES_FOLDER = "resources/messages";

    @Override
    public Map<String, Properties> provide() {
        String ezyplatformHomePath = fileSystemManager.getEzyHomePathString();
        Map<String, Properties> answer = new HashMap<>();
        TargetType targetType = getInclusiveTargetType();
        readAndAppendMessages(
            answer,
            Paths.get(
                ezyplatformHomePath,
                targetType.getName(),
                MESSAGES_FOLDER
            )
        );
        ModuleType[] moduleTypes = getInclusiveModuleTypes();
        Map<ModuleType, List<String>> modulesMap = getInclusiveModulesMapByModuleTypes(
            moduleTypes
        );
        for (ModuleType moduleType : moduleTypes) {
            List<String> modules = modulesMap.getOrDefault(
                moduleType,
                Collections.emptyList()
            );
            for (String module : modules) {
                readAndAppendMessages(
                    answer,
                    Paths.get(
                        ezyplatformHomePath,
                        moduleType.getTargetFolder(),
                        module,
                        MESSAGES_FOLDER
                    )
                );
            }
        }
        return answer;
    }

    private void readAndAppendMessages(
        Map<String, Properties> messages,
        Path messageFolder
    ) {
        if (!Files.exists(messageFolder)) {
            return;
        }
        MessageReader messageReader = MessageReader.getDefault();
        Map<String, Properties> map = messageReader.read(
            messageFolder.toString()
        );
        for (Map.Entry<String, Properties> e : map.entrySet()) {
            String lang = e.getKey();
            messages
                .computeIfAbsent(lang, k -> new Properties())
                .putAll(e.getValue());
        }
    }

    protected abstract TargetType getInclusiveTargetType();

    protected abstract ModuleType[] getInclusiveModuleTypes();

    protected abstract Map<ModuleType, List<String>> getInclusiveModulesMapByModuleTypes(
        ModuleType[] moduleTypes
    );
}
