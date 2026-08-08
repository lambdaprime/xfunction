/*
 * Copyright 2026 lambdaprime
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
package id.xfunction.text;

import id.xfunction.lang.XExec;
import id.xfunction.logging.XLogger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class TextUtils {
    private static final XLogger LOGGER = XLogger.getLogger(TextUtils.class);

    private static Pattern ENV_PATTERN = Pattern.compile("\\$\\{env\\:.*\\}");
    private static Pattern COMMAND_PATTERN = Pattern.compile("\\$\\(.*\\)");

    /**
     * Expand variables or commands
     *
     * <p>Example:
     *
     * <pre>{@code
     * Map.of("key1", "hi-${env:USER}",
     *     "key2", "path-$(echo test)");
     * }</pre>
     *
     * Will return new Map where:
     *
     * <ul>
     *   <li>"key1" = "hi-ubuntu"
     *   <li>"key2" = "path-test"
     * </ul>
     */
    public Map<String, String> expand(
            Map<String, String> data, boolean expandVariables, boolean expandCommands) {
        if (expandVariables) data = resolveEnvVariables(data);
        if (expandCommands) data = resolveCommandsVariables(data);
        return data;
    }

    private static Map<String, String> resolveCommandsVariables(Map<String, String> data) {
        var resolved = new HashMap<String, String>();
        for (var entry : data.entrySet()) {
            var newVal =
                    COMMAND_PATTERN
                            .matcher(entry.getValue())
                            .replaceAll(
                                    res -> {
                                        var cmd = res.group().replace("$(", "").replace(")", "");
                                        var exec = new XExec(cmd);
                                        LOGGER.fine(
                                                "resolve command: %s",
                                                Arrays.toString(exec.getCommand()));
                                        var output =
                                                exec.start()
                                                        .stdoutAsync(false)
                                                        .stderrThrow()
                                                        .stdout();
                                        return output;
                                    });
            resolved.put(entry.getKey(), newVal);
        }
        return resolved;
    }

    private static Map<String, String> resolveEnvVariables(Map<String, String> data) {
        var resolved = new HashMap<String, String>();
        for (var entry : data.entrySet()) {
            var newVal =
                    ENV_PATTERN
                            .matcher(entry.getValue())
                            .replaceAll(
                                    res -> {
                                        var varName =
                                                res.group().replace("${env:", "").replace("}", "");
                                        var varVal = System.getenv(varName);
                                        if (varVal == null)
                                            LOGGER.fine(
                                                    "Environment variable {} is empty, setting it"
                                                            + " to 'null'",
                                                    varName);
                                        return "" + varVal;
                                    });
            resolved.put(entry.getKey(), newVal);
        }
        return resolved;
    }
}
