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

package org.youngmonkeys.ezyplatform.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

@Getter
@EqualsAndHashCode(of = "file")
public class FileItem {
    protected File file;
    protected int level;
    protected FileItem parent;
    protected String relativePath;
    protected List<FileItem> children = new ArrayList<>();

    public int childrenCount() {
        return children.size();
    }

    public void walk(Consumer<FileItem> consumer) {
        consumer.accept(this);
        for (FileItem child : children) {
            child.walk(consumer);
        }
    }

    public void walkChildren(Consumer<FileItem> consumer) {
        for (FileItem child : children) {
            if (child.childrenCount() == 0) {
                consumer.accept(child);
            } else {
                child.walkChildren(consumer);
            }
        }
    }

    public List<FileItem> flatChildren() {
        return flatChildren(true);
    }

    public List<FileItem> flatChildren(boolean sort) {
        List<FileItem> answer = new ArrayList<>();
        walkChildren(answer::add);
        if (sort) {
            answer.sort(Comparator.comparing(a -> a.relativePath));
        }
        return answer;
    }

    public static class FileItemMutable extends FileItem {

        public FileItemMutable setFile(File file) {
            this.file = file;
            return this;
        }

        public FileItemMutable setLevel(int level) {
            this.level = level;
            return this;
        }

        public FileItemMutable setParent(FileItem parent) {
            this.parent = parent;
            return this;
        }

        public FileItemMutable setRelativePath(String relativePath) {
            this.relativePath = relativePath;
            return this;
        }

        public FileItemMutable addChild(FileItem child) {
            this.children.add(child);
            return this;
        }
    }
}
