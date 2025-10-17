/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.devtools.test.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyBodyRequest {
    private String key;
    private String value;
    private List<Data1> data1List;
    private List<List<String>> lists;

    @Getter
    @Setter
    private static class Data1 {
        private String hello;
        private String world;
        private Data1 data1;
        private Data2 data2;
        private List<Data2> data2List;
    }

    @Getter
    @Setter
    private static class Data2 {
        private String foo;
        private String bar;
        private Data1 data1;
        private Data3 data3;
        private List<Data3> data3List;
    }

    @Getter
    @Setter
    private static class Data3 {
        private String cat;
        private String dog;
        private Data1 data1;
        private List<Data1> data1List;
    }
}
