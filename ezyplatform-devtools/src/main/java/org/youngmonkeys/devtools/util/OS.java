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

package org.youngmonkeys.devtools.util;

@SuppressWarnings("AbbreviationAsWordInName")
public final class OS {

    private static OSType currentType;

    private OS() {
    }

    public static OSType currentType() {
        if (currentType == null) {
            String operSys = System.getProperty("os.name").toLowerCase();
            if (operSys.contains("win")) {
                currentType = OSType.WINDOWS;
            } else if (operSys.contains("nix")
                    || operSys.contains("nux")
                    || operSys.contains("aix")) {
                currentType = OSType.LINUX;
            } else if (operSys.contains("mac")) {
                currentType = OSType.MAC;
            } else if (operSys.contains("sunos")) {
                currentType = OSType.SOLARIS;
            }
        }
        return currentType;
    }
}
