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

package org.youngmonkeys.devtools.filewatch;

import com.tvd12.ezyfox.util.EzyLoggable;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileWatcher extends EzyLoggable implements Runnable {

    protected volatile  boolean active;
    protected final Path folder;
    protected final Map<Path, Long> fileStates;
    protected final FileListener fileListener;

    protected static final long SLEEP_TIME = 250;

    public FileWatcher(Path folder, FileListener fileListener) {
        this.folder = folder;
        this.fileStates = new HashMap<>();
        this.fileStates.putAll(walkFiles());
        this.fileListener = fileListener;
    }

    public void watch() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("devtools-file-watcher");
        thread.start();
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        this.active = true;
        while (active) {
            try {
                Thread.sleep(SLEEP_TIME);
                List<Path> modifiedFiles = new ArrayList<>();
                Map<Path, Long> newStates = walkFiles();

                for (Path file : newStates.keySet()) {
                    Long newState = newStates.get(file);
                    Long oldState = fileStates.get(file);
                    if (!newState.equals(oldState)) {
                        modifiedFiles.add(file);
                    }
                }
                fileStates.clear();
                fileStates.putAll(newStates);

                modifiedFiles.forEach(fileListener::onFileModified);
            } catch (Exception e) {
                logger.warn("watch folder: {} error", folder, e);
            }
        }
    }

    public void stop() {
        this.active = false;
    }

    private Map<Path, Long> walkFiles() {
        try {
            return Files.walk(folder, Integer.MAX_VALUE)
                .filter(Files::isRegularFile)
                .filter(it -> !it.getFileName().toString().endsWith("~"))
                .collect(
                    Collectors.toMap(
                        it -> it,
                        it -> it.toFile().lastModified()
                    )
                );
        } catch (Exception e) {
            if (e instanceof NoSuchFileException) {
                logger.info("walk folder: {} stopped due to: {}", folder, e.getMessage());
            } else {
                logger.warn("walk folder: {} error", folder, e);
            }
            return Collections.emptyMap();
        }
    }
}
