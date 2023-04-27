/*
 * Copyright 2023 youngmonkeys.org
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

import static org.youngmonkeys.ezyplatform.util.Htmls.*;

public class HtmlsTest {

    @Test
    public void containsScriptTagTest() {
        Asserts.assertTrue(
            containsScriptTag(
                "<script>hello"
            )
        );
        Asserts.assertTrue(
            containsScriptTag(
                "<  script>hello"
            )
        );
        Asserts.assertTrue(
            containsScriptTag(
                "<\tscript>hello"
            )
        );
        Asserts.assertTrue(
            containsScriptTag(
                "<\nscript>hello"
            )
        );
        Asserts.assertTrue(
            containsScriptTag(
                "<<<script>hello"
            )
        );
        Asserts.assertTrue(
            containsScriptTag(
                "<script>hello"
            )
        );
        Asserts.assertTrue(
            containsScriptTag(
                "<script    src=''>hello"
            )
        );
        Asserts.assertTrue(
            containsScriptTag(
                "<script\nsrc=''>hello"
            )
        );
        Asserts.assertFalse(containsScriptTag(""));

        Asserts.assertFalse(containsScriptTag("<sc hello"));
        Asserts.assertFalse(containsScriptTag("< scrip hello"));
    }

    @Test
    public void escapeScriptTagTest() {
        Asserts.assertEquals(
            escapeScriptTag(
                "<script>hello"
            ),
            "&ltscript&gthello"
        );
        Asserts.assertEquals(
            escapeScriptTag(
                "<  script>hello"
            ),
            "&lt  script&gthello"
        );
        Asserts.assertEquals(
            escapeScriptTag(
                "<\tscript>hello"
            ),
            "&lt\tscript&gthello"
        );
        Asserts.assertEquals(
            escapeScriptTag(
                "<\nscript>hello"
            ),
            "&lt\nscript&gthello"
        );
        Asserts.assertEquals(
            escapeScriptTag(
                "<<<script>hello"
            ),
            "<<&ltscript&gthello"
        );
        Asserts.assertEquals(
            escapeScriptTag(
                "<script>hello"
            ),
            "&ltscript&gthello"
        );
        Asserts.assertEquals(
            escapeScriptTag(
                "<script    src=''>hello"
            ),
            "&ltscript    src=''&gthello"
        );
        Asserts.assertEquals(
            escapeScriptTag(
                "<script\nsrc=''>hello"
            ),
            "&ltscript\nsrc=''&gthello"
        );
        Asserts.assertEquals(escapeScriptTag(""), "");

        Asserts.assertEquals(escapeScriptTag("<sc hello"), "<sc hello");
        Asserts.assertEquals(escapeScriptTag("< scrip hello"), "< scrip hello");
        Asserts.assertEquals(escapeScriptTag("<"), "<");
        Asserts.assertEquals(escapeScriptTag("<script />"), "&ltscript /&gt");
    }

    @Test
    public void validateHtmlContentTest() throws Exception {
        validateHtmlContent("<p>hello world</p>");
        validateHtmlContent(
            "<a value = \"hello world\"></a>"
        );
        validateHtmlContent(
            "<a value =\"\\\"\\\"hello''world\"></a>"
        );
        validateHtmlContent(
            "1 < 2 and 2 > 1"
        );
        validateHtmlContent(
            "<link><a></a></link>"
        );
        validateHtmlContent(
            "<link>hello</link>"
        );
        validateHtmlContent(
            "<br/>"
        );
        validateHtmlContent(
            "<!--hello-->"
        );
        validateHtmlContent(
            "<!-- hello -->"
        );
        validateHtmlContent(
            "<p>hello</p>"
        );
        validateHtmlContent(
            "<p class=\"text-primary\">hello</p>"
        );
        validateHtmlContent(
            "<p><span class=\"text-primary\">hello</span></p>"
        );
        validateHtmlContent(
            "<img src=\"img.png\"><a>hello</a>"
        );
        validateHtmlContent(
            "<img src=\"img.png\"><br><br></br><a>hello</a><p />"
        );
        validateHtmlContent(
            "<img src=\"img.png\"><br><br></br><a>hello</a><p />"
        );
        validateHtmlContent(
            "for b in bytes_:\n" +
                "        for i in range(8):\n" +
                "            bit = ((b >> (7 - i) & 1) == 1)\n" +
                "            c15 = ((crc >> 15 & 1) == 1)\n" +
                "            crc <<= 1\n" +
                "            if c15 ^ bit:\n" +
                "                crc ^= polynomial\n" +
                "    return format(crc & 0xFFFF, \"x\")"
        );
    }
}
