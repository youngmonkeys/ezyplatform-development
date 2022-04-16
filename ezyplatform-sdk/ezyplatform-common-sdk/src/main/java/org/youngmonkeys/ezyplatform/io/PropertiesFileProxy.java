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

package org.youngmonkeys.ezyplatform.io;

import com.tvd12.properties.file.reader.BaseFileReader;
import org.youngmonkeys.ezyplatform.exception.FilePermissionException;
import org.youngmonkeys.ezyplatform.util.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.youngmonkeys.ezyplatform.io.FolderProxy.createNewFile;
import static org.youngmonkeys.ezyplatform.util.Strings.startsWithIgnoreSpaces;

public final class PropertiesFileProxy {

    private PropertiesFileProxy() {}

    public static String readToMultiLinesString(
        File propertiesFile
    ) {
        return String.join(
            "\n",
            readAndSortToLines(propertiesFile)
        );
    }

    public static List<String> readAndSortToLines(
        File propertiesFile
    ) {
        return readAndSort(propertiesFile)
            .stream()
            .map(Strings::entryToString)
            .collect(Collectors.toList());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<Map.Entry<String, String>> readAndSort(
        File propertiesFile
    ) {
        return new BaseFileReader().read(propertiesFile)
            .entrySet()
            .stream()
            .map(it -> (Map.Entry<String, String>) (Map.Entry) it)
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toList());
    }

    public static void sortAndWrite(
        Map<?, ?> properties,
        File propertiesFile
    ) {
        createNewFile(propertiesFile);
        List<String> commentLines;
        try {
            commentLines = Files.lines(
                    propertiesFile.toPath()
                )
                .filter(line -> startsWithIgnoreSpaces(line, "#"))
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw FilePermissionException.fromFileAction(
                propertiesFile,
                "read"
            );
        }
        List<String> lines = new ArrayList<>(commentLines);
        lines.addAll(
            properties.entrySet()
                .stream()
                .map(Strings::entryToString)
                .sorted()
                .collect(Collectors.toList())
        );
        try {
            Files.write(propertiesFile.toPath(), lines);
        } catch (IOException e) {
            throw FilePermissionException.fromFileAction(
                propertiesFile,
                "write"
            );
        }
    }
}
