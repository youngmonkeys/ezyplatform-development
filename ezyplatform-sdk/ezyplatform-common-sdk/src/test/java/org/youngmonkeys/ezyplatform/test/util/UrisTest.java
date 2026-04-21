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

import static org.youngmonkeys.ezyplatform.util.Uris.isSslDomain;
import static org.youngmonkeys.ezyplatform.util.Uris.uriStartsWith;

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
        Asserts.assertEquals(
            Uris.getFileExtensionInUrl("https://hello.world/abc.01234567890123456789"),
            "0123456789012345"
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
        Asserts.assertEquals(
            Uris.getFileExtensionInUrl("https://scontent.fhan2-3.fna.fbcdn.net/v/t1.6435-9/130865829_3624731430883679_313484451003519932_n.jpg?_nc_cat=108&ccb=1-7&_nc_sid=1d70fc&_nc_eui2=AeH_FXQlED9X5uNzBLW8xfSzX8q6x63I8kpfyrrHrcjyStgx92yZYSOS1pP37zNu8Kl5xk5S7FwSVCoE62bKxHt3&_nc_ohc=3v6deKSRaZIQ7kNvwG2_jH8&_nc_oc=Adqf6X08eMZy3LnNbGB9JSZHTU_tU_cefChY3mOigUHbHIwA7fHn8_HmlWKhUoVF4gQ&_nc_zt=23&_nc_ht=scontent.fhan2-3.fna&_nc_gid=g49LZ-O-2_iqg4MD-hhyGQ&_nc_ss=7a3a8&oh=00_Af3hdykopSaMkztdAd9DB1Sq6RG1h2o4fXAwHD5NeHTQ8Q&oe=6A0BE50C"),
            "jpg"
        );
    }

    @Test
    public void uriStartsWithTest() {
        // given
        // when
        // then
        Asserts.assertTrue(uriStartsWith("/hello", "/hello"));
        Asserts.assertTrue(uriStartsWith("/hello", "hello"));
        Asserts.assertTrue(uriStartsWith("/hello/world", "hello"));
        Asserts.assertTrue(uriStartsWith("/hello/world", "/hello"));
        Asserts.assertFalse(uriStartsWith("/hellos", "hello"));
        Asserts.assertFalse(uriStartsWith("/hellos", "/hello"));
    }

    @Test
    public void isSslDomainTest() {
        Asserts.assertTrue(isSslDomain("https://youngmonkeys.org"));
        Asserts.assertTrue(isSslDomain("https://admin.youngmonkeys.org/path"));
        Asserts.assertFalse(isSslDomain("http://youngmonkeys.org"));
        Asserts.assertFalse(isSslDomain("https://127.0.0.1"));
        Asserts.assertFalse(isSslDomain("https://[2001:db8::1]"));
        Asserts.assertFalse(isSslDomain(null));
    }
}
