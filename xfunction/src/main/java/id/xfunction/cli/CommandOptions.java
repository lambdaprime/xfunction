/*
 * Copyright 2020 lambdaprime
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
package id.xfunction.cli;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;

/**
 * Provides access to command line options. Options have key-value structure.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class CommandOptions {

    private Properties options;

    private CommandOptions(Properties options) {
        this.options = options;
    }

    /**
     * Collect command options from list of arguments.
     *
     * <p>Each option may have zero or one argument.
     *
     * <p>If option has no argument then it is added with an empty string as a value.
     *
     * <p>Each option inside args should start from "-", or "--".
     *
     * <p>Options may accept arguments in one of the following ways: -option arg, -option=arg
     *
     * <p>When options are collected then leading "-" is removed.
     */
    public static CommandOptions collectOptions(String[] args) throws ArgumentParsingException {
        Properties props = new Properties();
        String curOption = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (curOption != null) {
                String option = curOption;
                curOption = null;
                if (!arg.startsWith("-")) {
                    props.put(option, arg);
                    continue;
                }
                props.put(option, "");
            }
            if (!arg.startsWith("-")) {
                throw new ArgumentParsingException(
                        "Option \'"
                                + arg
                                + "\" is not recognized. Options should be specified as a key-value"
                                + " pair: \"-option value\" or \"-option=value\"");
            }
            arg = arg.replaceAll("^-+", "");
            int pos = arg.indexOf('=');
            if (pos == -1) {
                curOption = arg;
                continue;
            }
            props.put(arg.subSequence(0, pos), arg.substring(pos + 1));
        }
        if (curOption != null) {
            props.put(curOption, "");
        }
        return new CommandOptions(props);
    }

    /**
     * If given option is not available it throws {@link ArgumentParsingException} with missing
     * option name.
     *
     * <p>It is recommended to catch {@link ArgumentParsingException} in the main() function itself
     * and show user usage information.
     */
    public String getRequiredOption(String optionName) throws ArgumentParsingException {
        var val = options.getProperty(optionName);
        if (val == null)
            throw new ArgumentParsingException(
                    "Command-line option \"-" + optionName + "\" is missing");
        return val;
    }

    /** Some options may be optional. Use this method to obtain them. */
    public Optional<String> getOption(String optionName) {
        return Optional.ofNullable(options.getProperty(optionName));
    }

    /** Check if optionName is set to "true": -optionName=true */
    public boolean isOptionTrue(String optionName) {
        return getOption(optionName)
                .map(String::toLowerCase)
                .filter(Predicate.isEqual("true"))
                .isPresent();
    }

    @Override
    public String toString() {
        return options.toString();
    }
}
