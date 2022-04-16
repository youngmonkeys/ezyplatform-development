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

package org.youngmonkeys.devtools.test;

import org.youngmonkeys.devtools.filewatch.FileWatcher;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileWatcherTest {

    @Test
    public void test() throws InterruptedException, IOException {
        // given
        Path folder = Paths.get("src/test/resources/templates");
        final Map<String, String> map = new HashMap<>();
        FileWatcher watcher = new FileWatcher(
            folder,
            file -> map.put("file.modified", file.getFileName().toString())
        );

        // when
        watcher.watch();

        // then
        Thread.sleep(250);
        File file = new File(folder + "/test.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(RandomUtil.randomShortAlphabetString());
        }
        Thread.sleep(300);
        Asserts.assertEquals(file.getName(), map.get("file.modified"));
        watcher.stop();
    }
}
