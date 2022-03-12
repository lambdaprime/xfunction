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
import java.util.Optional;
import java.util.Set;
import java.util.logging.Filter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Log filter for {@link java.util.logging} based on logger name prefixes.
 *
 * <p>It will ignore all log records which are not satisfying its filtering list of prefixes.
 *
 * <p>To use this filter you need to enable it in logging.properties and then specify list of logger
 * name prefixes which you are interested in:
 *
 * <pre>{@code
 * java.util.logging.ConsoleHandler.filter = id.xfunction.logging.LoggerNameFilter
 * id.xfunction.logging.filter = id, sun.net
 * }</pre>
 *
 * <p>It allows to exclude specific logger names by providing their complete names:
 *
 * <pre>{@code
 * id.xfunction.logging.excludedLoggers = id.HelloWorld
 * }</pre>
 *
 * <p>This may be useful in case you don't want to ignore logs from all classes with specific prefix
 * (ex. id), except for the one class (id.HelloWorld).
 */
public class LoggerNameFilter implements Filter {

    private PrefixTrieSet namePrefixes;
    private Set<String> excludedLoggers;

    public LoggerNameFilter() {
        namePrefixes =
                Pattern.compile(",")
                        .splitAsStream(
                                Optional.ofNullable(
                                                LogManager.getLogManager()
                                                        .getProperty("id.xfunction.logging.filter"))
                                        .orElse(""))
                        .map(String::trim)
                        .collect(Collectors.toCollection(PrefixTrieSet::new));
        excludedLoggers =
                Pattern.compile(",")
                        .splitAsStream(
                                Optional.ofNullable(
                                                LogManager.getLogManager()
                                                        .getProperty(
                                                                "id.xfunction.logging.excludedLoggers"))
                                        .orElse(""))
                        .map(String::trim)
                        .collect(Collectors.toSet());
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        String name = record.getLoggerName();
        if (name == null) return false;
        if (namePrefixes.isEmpty()) return false;
        if (excludedLoggers.contains(name)) return false;
        return namePrefixes.prefixMatches(name) != 0;
    }
}
