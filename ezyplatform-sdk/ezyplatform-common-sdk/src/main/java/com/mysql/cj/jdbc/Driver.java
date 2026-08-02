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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Backward-compatibility shim so settings files still referencing the legacy
 * "com.mysql.cj.jdbc.Driver" class name (from the previously bundled,
 * GPL-licensed mysql-connector-j) keep working after the driver dependency
 * was replaced with LGPL-licensed MariaDB Connector/J. It contains no code
 * from and is not affiliated with, endorsed by, or derived from Oracle's
 * mysql-connector-j; it only reuses the class name for compatibility and
 * delegates every call to {@link org.mariadb.jdbc.Driver}.
 */
public class Driver implements java.sql.Driver {

    private static final String LEGACY_PREFIX = "jdbc:mysql:";
    private static final String MARIADB_PREFIX = "jdbc:mariadb:";

    private static final org.mariadb.jdbc.Driver DELEGATE =
        new org.mariadb.jdbc.Driver();

    static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        Properties resolvedInfo = info == null ? new Properties() : info;
        return DELEGATE.connect(toMariaDbUrl(url, resolvedInfo), resolvedInfo);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url != null
            && url.startsWith(LEGACY_PREFIX)
            && DELEGATE.acceptsURL(toMariaDbUrl(url, new Properties()));
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(
        String url,
        Properties info
    ) throws SQLException {
        Properties resolvedInfo = info == null ? new Properties() : info;
        return DELEGATE.getPropertyInfo(
            toMariaDbUrl(url, resolvedInfo),
            resolvedInfo
        );
    }

    @Override
    public int getMajorVersion() {
        return DELEGATE.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return DELEGATE.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return DELEGATE.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return DELEGATE.getParentLogger();
    }

    /**
     * MariaDB Connector/J's HostAddress parser only understands
     * "host[:port]" and has no notion of an embedded "user:password@"
     * prefix (it just does {@code str.split(":")} and treats index 1 as
     * the port), so a legacy mysql-connector-j style URL such as
     * "jdbc:mysql://root:12345678@localhost:3306/db" would fail there
     * with "Incorrect port value : 12345678@localhost". This method
     * strips any embedded credentials out of the authority section,
     * moves them into the connection properties (without overriding
     * ones already set there), and rewrites the scheme to "jdbc:mariadb:".
     */
    private static String toMariaDbUrl(String url, Properties info) {
        if (url == null) {
            return null;
        }
        String rest = url.substring(LEGACY_PREFIX.length());
        if (!rest.startsWith("//")) {
            return MARIADB_PREFIX + rest;
        }
        int authorityStart = 2;
        int authorityEnd = authorityStart;
        while (authorityEnd < rest.length()
            && rest.charAt(authorityEnd) != '/'
            && rest.charAt(authorityEnd) != '?') {
            authorityEnd++;
        }
        String authority = rest.substring(authorityStart, authorityEnd);
        int at = authority.lastIndexOf('@');
        if (at < 0) {
            return MARIADB_PREFIX + rest;
        }
        String credentials = authority.substring(0, at);
        String hostPort = authority.substring(at + 1);
        int colon = credentials.indexOf(':');
        String user = colon < 0 ? credentials : credentials.substring(0, colon);
        String password = colon < 0 ? null : credentials.substring(colon + 1);
        if (!user.isEmpty() && info.getProperty("user") == null) {
            info.setProperty("user", user);
        }
        if (password != null && info.getProperty("password") == null) {
            info.setProperty("password", password);
        }
        return MARIADB_PREFIX
            + "//" + hostPort
            + rest.substring(authorityEnd);
    }
}
