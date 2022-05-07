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

package com.tvd12.ezyfox.boot.autoconfig;

import com.tvd12.ezyfox.bean.annotation.EzyConfigurationAfter;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import com.tvd12.ezyhttp.server.thymeleaf.ThymeleafViewContext;
import org.youngmonkeys.devtools.constant.PackageManagerType;
import org.youngmonkeys.devtools.constant.ScopeType;
import org.youngmonkeys.devtools.filewatch.FileWatcher;
import org.youngmonkeys.devtools.util.OS;
import org.youngmonkeys.devtools.util.OSType;
import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@EzyConfigurationAfter
public class DevToolsConfig extends EzyLoggable {

    public DevToolsConfig() {
        this(getPackageManagerType());
    }

    public DevToolsConfig(PackageManagerType packageManagerType) {
        watchResources(ScopeType.MAIN, packageManagerType);
        watchResources(ScopeType.TEST, packageManagerType);
    }

    private static PackageManagerType getPackageManagerType() {
        return new File("pom.xml").exists()
               ? PackageManagerType.MAVEN
               : PackageManagerType.GRADLE;
    }

    private void watchResources(
        ScopeType scopeType,
        PackageManagerType packageManagerType
    ) {
        Path srcFolder = getSourceFolder(scopeType);
        if (!Files.exists(srcFolder)) {
            return;
        }
        Path desFolder = getDestinationFolder(scopeType, packageManagerType);
        logger.debug("Start watching folder {}", srcFolder);

        FileWatcher watcher = new FileWatcher(srcFolder, file -> {
            logger.debug("FileListener.onFileModified {}", file);
            try {
                clearThymeleafCache();
                copyFileToFolder(srcFolder, file, desFolder);
            } catch (Exception e) {
                logger.error("FileListener.onFileModified error", e);
            }
        });
        watcher.watch();
    }

    private void refreshBrowser() throws IOException {
        logger.debug("Refresh Chrome browser");
        if (OS.currentType() == OSType.LINUX) {
            Runtime.getRuntime()
                .exec("xdotool search --onlyvisible --class Chrome windowfocus key ctrl+r");
        } else if (OS.currentType() == OSType.MAC) {
            String[] commands = new String[]{
                "osascript",
                "-e",
                "tell application \"Google Chrome\"\n"
                    + "activate\n"
                    + "tell application \"System Events\"\n"
                    + "tell process \"Google Chrome\"\n"
                    + "keystroke \"r\" using {command down, shift down}\n"
                    + "end tell\n"
                    + "end tell\n"
                    + "end tell"
            };
            Runtime.getRuntime().exec(commands);
        } else if (OS.currentType() == OSType.WINDOWS) {
            logger.warn("Auto-reload is not yet supported on Windows");
        } else {
            logger.warn("Auto-reload is not yet supported on {}", OS.currentType());
        }
    }

    private void copyFileToFolder(Path srcFolder, Path from, Path to) throws IOException {
        Files.walk(from).forEach(filePath -> {
            String targetFilePath = filePath
                .toString()
                .replace(srcFolder.toString(), to.toString());
            if (Files.isRegularFile(filePath)) {
                copyFile(filePath, Paths.get(targetFilePath));
            } else {
                createFolder(Paths.get(targetFilePath));
            }
        });
    }

    private void copyFile(Path from, Path to) {
        try {
            if (!Files.exists(to.getParent())) {
                Files.createDirectories(to.getParent());
            }
            Files.copy(from, to, REPLACE_EXISTING);
        } catch (Exception e) {
            logger.info("can not copy file from: {}, to: {}", from, to, e);
        }
    }

    private void createFolder(Path folder) {
        if (folder != null && !Files.exists(folder)) {
            try {
                Files.createDirectories(folder);
            } catch (IOException e) {
                logger.error("can not create folder: {}", folder, e);
            }
        }
    }

    private void clearThymeleafCache() throws NoSuchFieldException, IllegalAccessException {
        ViewContext viewContext = ComponentManager.getInstance().getViewContext();
        if (!(viewContext instanceof ThymeleafViewContext)) {
            return;
        }
        logger.debug("Clear cache of thymeleaf");
        Field field = viewContext.getClass().getDeclaredField("templateEngine");
        field.setAccessible(true);
        TemplateEngine templateEngine = (TemplateEngine) field.get(viewContext);
        templateEngine.getConfiguration().getTemplateManager().clearCaches();
    }

    private Path getDestinationFolder(
        ScopeType scopeType,
        PackageManagerType packageManagerType
    ) {
        if (packageManagerType == PackageManagerType.MAVEN) {
            return scopeType == ScopeType.MAIN
                   ? Paths.get("target/classes")
                   : Paths.get("target/test-classes");
        } else if (packageManagerType == PackageManagerType.GRADLE) {
            return scopeType == ScopeType.MAIN
                   ? Paths.get("build/resources/main")
                   : Paths.get("build/resources/test");
        }
        throw new IllegalArgumentException();
    }

    private Path getSourceFolder(ScopeType scopeType) {
        return scopeType == ScopeType.MAIN
               ? Paths.get("src/main/resources")
               : Paths.get("src/test/resources");
    }
}
