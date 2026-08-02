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

package com.mysql.cj.jdbc;

import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

import java.util.Properties;

public class DriverTest extends BaseTest {

    @Test
    public void toMariaDbUrlWithNullUrlTest() {
        Asserts.assertEquals(
            Driver.toMariaDbUrl(null, new Properties()),
            null
        );
    }

    @Test
    public void toMariaDbUrlWithUnknownSchemeIsUntouchedTest() {
        // given
        String url = "jdbc:postgresql://root:12345678@localhost:5432/ezyplatform";
        Properties info = new Properties();

        // when
        String actual = Driver.toMariaDbUrl(url, info);

        // then
        Asserts.assertEquals(actual, url);
        Asserts.assertEquals(info, new Properties());
    }

    @Test
    public void toMariaDbUrlMysqlWithoutCredentialsTest() {
        // given
        Properties info = new Properties();

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mysql://localhost:3306/ezyplatform",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost:3306/ezyplatform"
        );
        Asserts.assertEquals(info, new Properties());
    }

    @Test
    public void toMariaDbUrlMysqlWithoutCredentialsWithQueryTest() {
        // given
        Properties info = new Properties();

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mysql://localhost:3306/ezyplatform?useSSL=false",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost:3306/ezyplatform?useSSL=false"
        );
        Asserts.assertEquals(info, new Properties());
    }

    @Test
    public void toMariaDbUrlMysqlWithUserAndPasswordTest() {
        // given
        Properties info = new Properties();
        Properties expectedInfo = new Properties();
        expectedInfo.setProperty("user", "root");
        expectedInfo.setProperty("password", "12345678");

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mysql://root:12345678@localhost:3306/ezyplatform",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost:3306/ezyplatform"
        );
        Asserts.assertEquals(info, expectedInfo);
    }

    @Test
    public void toMariaDbUrlMysqlWithUserAndPasswordWithQueryTest() {
        // given
        Properties info = new Properties();
        Properties expectedInfo = new Properties();
        expectedInfo.setProperty("user", "root");
        expectedInfo.setProperty("password", "12345678");

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mysql://root:12345678@localhost:3306/ezyplatform?useSSL=false",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost:3306/ezyplatform?useSSL=false"
        );
        Asserts.assertEquals(info, expectedInfo);
    }

    @Test
    public void toMariaDbUrlMysqlWithUserOnlyTest() {
        // given
        Properties info = new Properties();
        Properties expectedInfo = new Properties();
        expectedInfo.setProperty("user", "root");

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mysql://root@localhost:3306/ezyplatform",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost:3306/ezyplatform"
        );
        Asserts.assertEquals(info, expectedInfo);
    }

    @Test
    public void toMariaDbUrlMysqlDoesNotOverrideExistingCredentialsTest() {
        // given
        Properties info = new Properties();
        info.setProperty("user", "admin");
        info.setProperty("password", "secret");
        Properties expectedInfo = new Properties();
        expectedInfo.setProperty("user", "admin");
        expectedInfo.setProperty("password", "secret");

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mysql://root:12345678@localhost:3306/ezyplatform",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost:3306/ezyplatform"
        );
        Asserts.assertEquals(info, expectedInfo);
    }

    @Test
    public void toMariaDbUrlMysqlWithoutPortAndCredentialsTest() {
        // given
        Properties info = new Properties();

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mysql://localhost/ezyplatform",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost/ezyplatform"
        );
        Asserts.assertEquals(info, new Properties());
    }

    @Test
    public void toMariaDbUrlMysqlWithoutPortWithUserAndPasswordTest() {
        // given
        Properties info = new Properties();
        Properties expectedInfo = new Properties();
        expectedInfo.setProperty("user", "root");
        expectedInfo.setProperty("password", "12345678");

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mysql://root:12345678@localhost/ezyplatform",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost/ezyplatform"
        );
        Asserts.assertEquals(info, expectedInfo);
    }

    @Test
    public void toMariaDbUrlMysqlWithoutDoubleSlashTest() {
        // given
        Properties info = new Properties();

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mysql:loopback:",
            info
        );

        // then
        Asserts.assertEquals(actual, "jdbc:mariadb:loopback:");
        Asserts.assertEquals(info, new Properties());
    }

    @Test
    public void toMariaDbUrlMariaDbWithoutCredentialsIsUnchangedTest() {
        // given
        Properties info = new Properties();

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mariadb://localhost:3306/ezyplatform",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost:3306/ezyplatform"
        );
        Asserts.assertEquals(info, new Properties());
    }

    @Test
    public void toMariaDbUrlMariaDbWithUserAndPasswordTest() {
        // given
        Properties info = new Properties();
        Properties expectedInfo = new Properties();
        expectedInfo.setProperty("user", "root");
        expectedInfo.setProperty("password", "12345678");

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mariadb://root:12345678@localhost:3306/ezyplatform",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost:3306/ezyplatform"
        );
        Asserts.assertEquals(info, expectedInfo);
    }

    @Test
    public void toMariaDbUrlMariaDbWithUserAndPasswordWithQueryTest() {
        // given
        Properties info = new Properties();
        Properties expectedInfo = new Properties();
        expectedInfo.setProperty("user", "root");
        expectedInfo.setProperty("password", "12345678");

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mariadb://root:12345678@localhost:3306/ezyplatform?permitMysqlScheme",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost:3306/ezyplatform?permitMysqlScheme"
        );
        Asserts.assertEquals(info, expectedInfo);
    }

    @Test
    public void toMariaDbUrlMariaDbWithoutPortAndCredentialsTest() {
        // given
        Properties info = new Properties();

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mariadb://localhost/ezyplatform",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost/ezyplatform"
        );
        Asserts.assertEquals(info, new Properties());
    }

    @Test
    public void toMariaDbUrlMariaDbWithoutPortWithUserAndPasswordTest() {
        // given
        Properties info = new Properties();
        Properties expectedInfo = new Properties();
        expectedInfo.setProperty("user", "root");
        expectedInfo.setProperty("password", "12345678");

        // when
        String actual = Driver.toMariaDbUrl(
            "jdbc:mariadb://root:12345678@localhost/ezyplatform",
            info
        );

        // then
        Asserts.assertEquals(
            actual,
            "jdbc:mariadb://localhost/ezyplatform"
        );
        Asserts.assertEquals(info, expectedInfo);
    }

    @Test
    public void acceptsUrlWithMysqlSchemeTest() throws Exception {
        Asserts.assertEquals(
            new Driver().acceptsURL(
                "jdbc:mysql://root:12345678@localhost:3306/ezyplatform"
            ),
            true
        );
    }

    @Test
    public void acceptsUrlWithMariaDbSchemeTest() throws Exception {
        Asserts.assertEquals(
            new Driver().acceptsURL(
                "jdbc:mariadb://localhost:3306/ezyplatform"
            ),
            false
        );
    }

    @Test
    public void acceptsUrlWithOtherSchemeTest() throws Exception {
        Asserts.assertEquals(
            new Driver().acceptsURL(
                "jdbc:postgresql://localhost:5432/ezyplatform"
            ),
            false
        );
    }

    @Test
    public void acceptsUrlWithNullTest() throws Exception {
        Asserts.assertEquals(
            new Driver().acceptsURL(null),
            false
        );
    }
}
