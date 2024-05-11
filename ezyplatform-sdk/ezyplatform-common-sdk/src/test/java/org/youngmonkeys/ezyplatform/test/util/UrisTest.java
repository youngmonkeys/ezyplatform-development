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

package org.youngmonkeys.ezyplatform.test.util;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.util.Uris;

public class UrisTest {

    @Test
    public void resolveUrlTest() {
        Asserts.assertEquals(
            Uris.resolveUrl("x/", "a/b/c/"),
            "x/a/b/c/"
        );
        Asserts.assertEquals(
            Uris.resolveUrl("x", "a/b/c/"),
            "x/a/b/c/"
        );
        Asserts.assertEquals(
            Uris.resolveUrl("x/", "/a/b/c/"),
            "x/a/b/c/"
        );
        Asserts.assertEquals(
            Uris.resolveUrl("x", "/a/b/c/"),
            "x/a/b/c/"
        );
    }

    @Test
    public void getSiteName() {
        Asserts.assertEquals(
            Uris.getSiteName("", "Admin"),
            "Admin"
        );
        Asserts.assertEquals(
            Uris.getSiteName("http://localhost:8080", ""),
            "Localhost"
        );
        Asserts.assertEquals(
            Uris.getSiteName("http://youngmonkeys.org:8080", ""),
            "Youngmonkeys"
        );
        Asserts.assertEquals(
            Uris.getSiteName("http://youngmonkeys.org", ""),
            "Youngmonkeys"
        );
        Asserts.assertEquals(
            Uris.getSiteName("http://admin.youngmonkeys.org", ""),
            "Youngmonkeys"
        );
        Asserts.assertEquals(
            Uris.getSiteName("youngmonkeys.org:8080", ""),
            "Youngmonkeys"
        );
        Asserts.assertEquals(
            Uris.getSiteName("admin.youngmonkeys.org:8080", ""),
            "Youngmonkeys"
        );
    }

    @Test
    public void getFileExtensionInUrlTest() {
        Asserts.assertEquals(
            Uris.getFileExtensionInUrl("https://hello.world/abc.jpg"),
            "jpg"
        );
        Asserts.assertNull(
            Uris.getFileExtensionInUrl("https://hello.world/abc.")
        );
        Asserts.assertNull(
            Uris.getFileExtensionInUrl("https://hello.world/abc")
        );
        Asserts.assertNull(
            Uris.getFileExtensionInUrl(null)
        );
        Asserts.assertNull(Uris.getFileExtensionInUrl("abc"));
    }
}
