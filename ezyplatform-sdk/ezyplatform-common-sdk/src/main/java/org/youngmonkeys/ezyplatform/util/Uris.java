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

package org.youngmonkeys.ezyplatform.util;

public final class Uris {

    private Uris() {}

    public static String resolveUrl(String root, String path) {
        if (root.endsWith("/")) {
            if (path.startsWith("/")) {
                if (path.length() == 1) {
                    return root;
                }
                return  root + path.substring(1);
            } else {
                return root + path;
            }
        } else {
            if (path.startsWith("/")) {
                return root + path;
            } else {
                return root + '/' + path;
            }
        }
    }

    public static boolean uriStartsWith(String uri, String prefix) {
        if (uri.startsWith(prefix)) {
            return true;
        }
        return uri.startsWith("/" + prefix);
    }
}
