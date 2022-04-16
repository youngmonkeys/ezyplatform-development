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

package org.youngmonkeys.ezyplatform.manager;

import com.tvd12.properties.file.reader.BaseFileReader;
import com.tvd12.properties.file.util.PropertiesUtil;
import org.youngmonkeys.ezyplatform.entity.MediaType;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static com.tvd12.ezyfox.io.EzyLists.filter;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static java.nio.file.Files.exists;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.PROPERTY_NAME_EZYPLATFORM_HOME;
import static org.youngmonkeys.ezyplatform.io.FolderProxy.listFolders;

public interface FileSystemManager {

    String FOLDER_UPLOAD = "upload";
    String FOLDER_RUNTIME = ".runtime";
    String FOLDER_SETTINGS = "settings";
    String FILE_CONFIG_PROPERTIES = "config.properties";
    String FILE_MODULE_PROPERTIES = "module.properties";
    String FILE_ENCRYPTION_KEYS = "encryption-keys.txt";

    String getEzyHomePathString();

    default File getEzyHomePath() {
        return new File(
            isBlank(getEzyHomePathString())
                ? "."
                : getEzyHomePathString()
        );
    }

    static File concatWithEzyHome(
        String ezyplatformHome,
        String childPath
    ) {
        return (
            isBlank(ezyplatformHome)
                ? Paths.get(childPath)
                : Paths.get(ezyplatformHome, childPath)
        ).toFile();
    }

    default String concatWithEzyHome(String path) {
        return concatWithEzyHome(
            getEzyHomePathString(),
            path
        ).toString();
    }

    default File concatWithEzyHome(Path path) {
        return new File(concatWithEzyHome(path.toString()));
    }

    default File getUploadFolder() {
        return new File(concatWithEzyHome(FOLDER_UPLOAD));
    }

    default File getMediaFilePath(
        MediaType mediaType,
        String fileName
    ) {
        return getMediaFilePath(
            mediaType.getFolder(),
            fileName
        );
    }

    default File getMediaFilePath(
        String containerFolder,
        String fileName
    ) {
        return Paths.get(
            getMediaFolderPath(containerFolder).toString(),
            fileName
        ).toFile();
    }

    default File getMediaFolderPath(MediaType mediaType) {
        return getMediaFolderPath(
            mediaType.getFolder()
        );
    }

    default File getMediaFolderPath(String containerFolder) {
        return Paths.get(
            getUploadFolder().toString(),
            containerFolder
        ).toFile();
    }

    static String readConfigAndGetEzyHomePathString() {
        Properties configProperties = new BaseFileReader()
            .read(FILE_CONFIG_PROPERTIES);
        PropertiesUtil.setVariableValues(configProperties);
        return configProperties.getProperty(PROPERTY_NAME_EZYPLATFORM_HOME);
    }

    static List<File> getModuleFolders(
        String ezyplatformHome,
        String containerFolder
    ) {
        return filter(
            listFolders(concatWithEzyHome(ezyplatformHome, containerFolder)),
            it -> exists(Paths.get(it.toString(), FILE_MODULE_PROPERTIES))
        );
    }
}
