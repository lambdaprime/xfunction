/*
 * Copyright 2019 lambdaprime
 * 
 * Website: https://github.com/lambdaprime/xfunction
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.xfunction.logging;

import id.xfunction.util.PrefixTrieSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Log filter for {@link java.util.logging} based on logger name prefixes.
 *
 * <p>It will ignore all log records which are not satisfying its filtering list of prefixes.
 *
 * <p>By default this list is empty so all logger names will satisfying this list and will be
 * logged.
 *
 * <p>To change this filter you need to enable it in logging.properties and then specify list of
 * logger full names or their name prefixes:
 *
 * <pre>{@code
 * java.util.logging.ConsoleHandler.filter = id.xfunction.logging.LoggerNameFilter
 * id.xfunction.logging.filter = id, sun.net
 * }</pre>
 *
 * <p>To exclude specific logger you need to provide its full name or name prefix:
 *
 * <pre>{@code
 * id.xfunction.logging.excludedLoggers = id.HelloWorld
 * }</pre>
 *
 * <p>This may be useful in case you don't want to ignore logs from all classes with specific prefix
 * (ex. id), except for the one class (id.HelloWorld).
 *
 * @author lambdaprime intid@protonmail.com
 */
public class LoggerNameFilter implements Filter {

    private PrefixTrieSet namePrefixes;
    private PrefixTrieSet excludedLoggers;

    public LoggerNameFilter() {
        namePrefixes =
                getPropertyList("id.xfunction.logging.filter").stream()
                        .collect(Collectors.toCollection(PrefixTrieSet::new));
        excludedLoggers =
                getPropertyList("id.xfunction.logging.excludedLoggers").stream()
                        .collect(Collectors.toCollection(PrefixTrieSet::new));
    }

    private static List<String> getPropertyList(String propertyName) {
        return Pattern.compile(",")
                .splitAsStream(
                        Optional.ofNullable(LogManager.getLogManager().getProperty(propertyName))
                                .orElse(""))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        String name = record.getLoggerName();
        if (record.getLevel().intValue() >= Level.WARNING.intValue()) return true;
        if (name == null) return false;
        if (excludedLoggers.prefixMatches(name) != 0) return false;
        if (namePrefixes.isEmpty()) return true;
        return namePrefixes.prefixMatches(name) != 0;
    }
}
