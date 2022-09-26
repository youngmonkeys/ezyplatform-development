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

package org.youngmonkeys.ezyplatform.entity;

import com.tvd12.ezyfox.util.EzyEnums;
import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public enum ModuleType {

    THEME("theme", "Theme", "web", "themes"),
    WEB_PLUGIN("web-plugin", "Web Plugin", "web", "plugins"),
    SOCKET_APP("socket-app", "Socket App", "socket", "apps"),
    SOCKET_PLUGIN("socket-plugin", "Socket Plugin", "socket", "plugins"),
    ADMIN_PLUGIN("admin-plugin", "Admin Plugin", "admin", "plugins");

    @Getter
    private final String name;

    @Getter
    private final String displayName;

    @Getter
    private final String parentFolder;

    @Getter
    private final String containerFolder;

    private static final Map<String, ModuleType> MAP =
            EzyEnums.enumMap(ModuleType.class, it -> it.name);

    ModuleType(
        String name,
        String displayName,
        String parentFolder,
        String containerFolder
    ) {
        this.name = name;
        this.displayName = displayName;
        this.parentFolder = parentFolder;
        this.containerFolder = containerFolder;
    }

    public String getTargetFolder() {
        return getTargetFolderPath().toString();
    }

    public Path getTargetFolderPath() {
        return Paths.get(parentFolder, containerFolder);
    }

    public static ModuleType of(String name) {
        return name == null ? null : MAP.get(name);
    }
}
