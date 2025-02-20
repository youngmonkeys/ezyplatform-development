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

package org.youngmonkeys.ezyplatform.test.validator;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.validator.DefaultValidator;

public class DefaultValidatorTest {

    @Test
    public void isValidEmailTest() {
        Asserts.assertTrue(
            DefaultValidator.isValidEmail("dung@youngmonkey.org")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidEmail("dung@youngmonkey.academy")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidEmail("dung@youngmonkey.aaacademy")
        );
        Asserts.assertFalse(
            DefaultValidator.isValidEmail("dung@youngmonkey.aaaacademy")
        );
    }

    @Test
    public void isValidPhoneTest() {
        Asserts.assertTrue(
            DefaultValidator.isValidUIntNumber("123")
        );
        Asserts.assertFalse(
            DefaultValidator.isValidUIntNumber("a123")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidPhone("+841234567890")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidPhone("1234567890")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidPhone("123-456-7890")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidPhone("(123) 456-7890")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidPhone("123 456 7890")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidPhone("+841234567890")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidPhone("123.456.7890")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidPhone("+91 (123) 456-7890")
        );
        Asserts.assertFalse(
            DefaultValidator.isValidPhone("12321")
        );
    }

    @Test
    public void isValidUrlTest() {
        Asserts.assertTrue(
            DefaultValidator.isValidHttpUrl("https://google.com")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidHttpUrl("https://tvd12.com?u=tvd12")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidHttpUrl("https://vu.tvd12.com?u=tvd12")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidHttpUrl("http://google.com")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidHttpUrl("http://google.com:9090")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidHttpUrl("http://localhost:9090")
        );
        Asserts.assertFalse(
            DefaultValidator.isValidHttpUrl("wss://google.com")
        );
    }

    @Test
    public void isValidWebsocketUrlTest() {
        Asserts.assertTrue(
            DefaultValidator.isValidWebsocketUrl("ws://google.com")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidWebsocketUrl("ws://google.com:9090")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidWebsocketUrl("ws://localhost:9090")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidWebsocketUrl("wss://tvd12.com?u=tvd12")
        );
        Asserts.assertFalse(
            DefaultValidator.isValidWebsocketUrl("https://vu.tvd12.com?u=tvd12")
        );
        Asserts.assertFalse(
            DefaultValidator.isValidWebsocketUrl("http://google.com")
        );
        Asserts.assertFalse(
            DefaultValidator.isValidWebsocketUrl("google.com")
        );
    }

    @Test
    public void containVariableTest() {
        Asserts.assertTrue(
            DefaultValidator.containsVariable("hello ${world}")
        );
        Asserts.assertFalse(
            DefaultValidator.containsVariable("hello ${world")
        );
    }

    @Test
    public void isSha256StringTest() {
        Asserts.assertTrue(
            DefaultValidator.isSha256String("deea9efe778fd3ed248427997afb7d2c06fda7be2457f5188ace85d6dcd15945")
        );
        Asserts.assertFalse(
            DefaultValidator.containsVariable("deea9efe778fd3ed248427997afb7d2c06fda7be2457f5188ace85d6dcd159451")
        );
        Asserts.assertFalse(
            DefaultValidator.containsVariable("deea9efe778fd3ed248427997afb7d2c06fda7be2457f5188ace85d6dcd1594z")
        );
    }

    @Test
    public void isValidMediaNameTest() {
        Asserts.assertTrue(
            DefaultValidator.isValidMediaName("example.txt")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidMediaName("file-name.txt")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidMediaName("file_name.doc")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidMediaName("_file.txt")
        );
        Asserts.assertFalse(
            DefaultValidator.isValidMediaName("folder/file.doc")
        );
        Asserts.assertFalse(
            DefaultValidator.isValidMediaName(".hidden-file")
        );
        Asserts.assertFalse(
            DefaultValidator.isValidMediaName("invalid..file")
        );
        Asserts.assertTrue(
            DefaultValidator.isValidMediaName("5fcbfdc3c0b00331df26b45e3745df99a18ca94ce8dd736cc6f6bc6d89ae8ff0")
        );
    }
}
