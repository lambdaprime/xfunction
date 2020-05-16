/*
 * Copyright 2019 lambdaprime
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

import java.util.logging.Filter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import id.xfunction.PrefixTrieSet;

/**
 * <p>Log filter based on logger name prefixes.</p>
 * 
 * <p>It will ignore all log records which are coming from the non white listed
 * loggers.</p>
 * 
 * <p>To use this filter you just need to enable it in logging.properties and
 * then white list prefixes of loggers which you are interested in:</p>
 * 
 * <pre>{@code
 * java.util.logging.ConsoleHandler.filter = id.xfunction.logging.LoggerNameFilter
 * id.xfunction.logging.filter = id, sun.net
 * }</pre>
 * 
 */
public class LoggerNameFilter implements Filter {

    private PrefixTrieSet namePrefixes;

    public LoggerNameFilter() {
        String prop = LogManager.getLogManager().getProperty("id.xfunction.logging.filter");
        if (prop == null) {
            namePrefixes = new PrefixTrieSet();
        } else {
            namePrefixes = Pattern.compile(",").splitAsStream(prop)
                    .map(String::trim)
                    .collect(Collectors.toCollection(PrefixTrieSet::new));
        }
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        if (namePrefixes.isEmpty()) return false;
        String name = record.getLoggerName();
        if (name == null) return false;
        return namePrefixes.prefixMatches(name) != 0;
    }

}
