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

import com.tvd12.ezyfox.util.EzyArrayUtil;
import com.tvd12.ezyfox.util.EzyDirectories;
import com.tvd12.ezyfox.util.EzyFileUtil;
import org.youngmonkeys.ezyplatform.data.FileItem;
import org.youngmonkeys.ezyplatform.data.FileItem.FileItemMutable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.youngmonkeys.ezyplatform.exception.FilePermissionException.fromFileAction;
import static org.youngmonkeys.ezyplatform.exception.FilePermissionException.fromFolderAction;

public final class FolderProxy {

    private FolderProxy() {}

    public static void writeDistinctLines(
        File file,
        String newLine
    ) {
        writeDistinctLines(file, singletonList(newLine));
    }

    public static void writeDistinctLines(
        File file,
        Collection<String> newLines
    ) {
        Path filePath = file.toPath();
        createNewFile(file);
        List<String> lines;
        try {
            lines = Stream.concat(
                    newLines.stream(),
                    Files.lines(filePath)
                )
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw fromFileAction(file, "read");
        }
        try {
            Files.write(filePath, lines);
        } catch (Exception e) {
            throw fromFileAction(file, "write");
        }
    }

    public static List<File> listFolders(File folder) {
        File[] files = null;
        if (folder.exists()) {
            files = folder.listFiles();
        }
        File[] tmp = files != null ? files : new File[0];
        List<File> answer = new ArrayList<>();
        for (File f : tmp) {
            if (f.isDirectory()) {
                answer.add(f);
            }
        }
        return answer;
    }

    public static void mkdir(File folder) {
        if (folder.exists()) {
            return;
        }
        if (!folder.mkdir()) {
            throw fromFolderAction(folder, "create");
        }
    }

    public static void mkdirs(File folder) {
        if (folder.exists()) {
            return;
        }
        if (!folder.mkdirs()) {
            throw fromFolderAction(folder, "create");
        }
    }

    public static void createNewFile(File file) {
        if (file.exists()) {
            return;
        }
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            mkdirs(parent);
        }
        boolean result;
        try {
            result = file.createNewFile();
        } catch (IOException e) {
            throw fromFileAction(file, "create", e);
        }
        if (!result) {
            throw fromFileAction(file, "create");
        }
    }

    public static void copyFile(File from, File to) {
        try {
            EzyFileUtil.copyFile(from, to);
        } catch (Exception e) {
            throw fromFolderAction(to, "copy to", e);
        }
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (!file.delete()) {
                throw fromFileAction(file, "delete");
            }
        }
    }

    public static void deleteFolder(File folder) {
        try {
            if (folder.exists()) {
                EzyDirectories.deleteFolder(folder);
            }
        } catch (IOException e) {
            throw fromFolderAction(folder, "delete", e);
        }
    }

    public static void deleteFileOrFolder(File fileOrFolder) {
        if (fileOrFolder.isFile()) {
            deleteFile(fileOrFolder);
        } else {
            deleteFolder(fileOrFolder);
        }
    }

    public static void clearFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            deleteFileOrFolder(file);
        }
    }

    public static void copyFolder(File from, File to) {
        try {
            EzyDirectories.copyFolder(from, to);
        } catch (IOException e) {
            throw fromFolderAction(to, "copy to", e);
        }
    }

    public static void copyAndDeleteFolder(File from, File to) {
        copyFolder(from, to);
        deleteFolder(from);
    }

    public static void moveFile(File file, File toFolder) {
        File newLocationFile = Paths.get(
            toFolder.toString(),
            file.getName()
        ).toFile();
        createNewFile(newLocationFile);
        try {
            Files.move(
                file.toPath(),
                newLocationFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw fromFolderAction(newLocationFile, "move to", e);
        }
    }

    public static File subpath(File file, File parent) {
        return EzyDirectories.subpath(file, parent);
    }

    public static String getFileExtension(String fileName) {
        return EzyFileUtil.getFileExtension(fileName);
    }

    public static List<FileItem> listFileItems(
        File rootFolder,
        int maxLevel,
        Predicate<File> filter
    ) {
        FileItemMutable root = new FileItemMutable()
            .setFile(rootFolder);
        Queue<FileItemMutable> queue = new LinkedList<>();
        queue.offer(root);
        while (queue.size() > 0) {
            FileItemMutable parent = queue.poll();
            if (parent.getLevel() >= maxLevel) {
                continue;
            }
            File[] files = parent.getFile().listFiles();
            if (EzyArrayUtil.isEmpty(files)) {
                continue;
            }
            for (File file : files) {
                if (!filter.test(file)) {
                    continue;
                }
                FileItemMutable fileItem = new FileItemMutable()
                    .setFile(file)
                    .setLevel(parent.getLevel() + 1)
                    .setParent(parent)
                    .setRelativePath(
                        file.getAbsolutePath()
                            .substring(rootFolder.getAbsolutePath().length() + 1)
                    );
                parent.addChild(fileItem);
                if (file.isDirectory()) {
                    queue.offer(fileItem);
                }
            }
        }
        return root.flatChildren();
    }

    public static Predicate<File> isNotHiddenFilePredicate() {
        return f -> !f.isHidden();
    }

    public static Predicate<File> fileHasNotExtensionPredicate(
        String extension
    ) {
        return f -> f.isDirectory() || !f.getPath().endsWith(extension);
    }
}
