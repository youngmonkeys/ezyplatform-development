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

package org.youngmonkeys.ezyplatform.exception;

import lombok.Getter;

import java.io.File;

@Getter
public class FilePermissionException extends IllegalStateException {
    private static final long serialVersionUID = -5070978867972106894L;
    
    private final File file;

    public FilePermissionException(File file, String msg) {
        super(msg);
        this.file = file;
    }

    public FilePermissionException(File file, String msg, Exception e) {
        super(msg, e);
        this.file = file;
    }

    public static FilePermissionException fromFileAction(
        File file,
        String action
    ) {
        return new FilePermissionException(
            file,
            "Can not " + action + " file: " + file + ", please check your permission"
        );
    }

    public static FilePermissionException fromFileAction(
        File file,
        String action,
        Exception e
    ) {
        return new FilePermissionException(
            file,
            "Can not " + action + " file: " + file + ", please check your permission",
            e
        );
    }

    public static FilePermissionException fromFolderAction(
        File folder,
        String action
    ) {
        return new FilePermissionException(
            folder,
            "Can not " + action + " folder: " + folder + ", please check your permission"
        );
    }

    public static FilePermissionException fromFolderAction(
        File folder,
        String action,
        Exception e
    ) {
        return new FilePermissionException(
            folder,
            "Can not " + action + " folder: " + folder + ", please check your permission",
            e
        );
    }
}
