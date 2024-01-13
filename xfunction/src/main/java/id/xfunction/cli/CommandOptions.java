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
 * <p>This class is only relying on {@link Properties} and so it is thread-safe.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class CommandOptions {

    public static class Config {
        private boolean ignoreParsingExceptions;

        /**
         * Allows to support positional arguments which otherwise would cause exception. For example
         * given args string "arg1 arg2 -option val", with {@link #withPositionalArguments()}
         * enabled "arg1", "arg2" will be ignored.
         *
         * <p>Positional arguments validation and processing are not covered by {@link
         * CommandOptions} and needs to be done manually.
         */
        public Config withPositionalArguments() {
            return withIgnoreParsingExceptions();
        }

        public Config withIgnoreParsingExceptions() {
            this.ignoreParsingExceptions = true;
            return this;
        }
    }

    private Properties options;

    /**
     * @see #collectOptions(String[]) for creating {@link CommandOptions} from main(String[] args)
     */
    public CommandOptions(Properties options) {
        this.options = options;
    }

    /**
     * Collect command options from list of arguments. Can be used with main(String[] args)
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
    public static CommandOptions collectOptions(Config config, String[] args)
            throws ArgumentParsingException {
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
                if (config.ignoreParsingExceptions) continue;
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

    /** Similar to {@link #collectOptions(String[])} except uses default {@link Config} settings */
    public static CommandOptions collectOptions(String[] args) throws ArgumentParsingException {
        return collectOptions(new Config(), args);
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

    /** Some options may be optional. Use this method to obtain them. */
    public Optional<Integer> getOptionInt(String optionName) {
        return Optional.ofNullable(options.getProperty(optionName)).map(Integer::parseInt);
    }

    /** Some options may be optional. Use this method to obtain them. */
    public Optional<Double> getOptionDouble(String optionName) {
        return Optional.ofNullable(options.getProperty(optionName)).map(Double::parseDouble);
    }

    /** Check if optionName is set to "true": -optionName=true */
    public boolean isOptionTrue(String optionName) {
        return getOption(optionName)
                .map(String::toLowerCase)
                .filter(Predicate.isEqual("true"))
                .isPresent();
    }

    /** Adds new option */
    public void addOption(String name, boolean value) {
        addOption(name, "" + value);
    }

    /** Adds new option */
    private void addOption(String name, String value) {
        options.put(name, value);
    }

    @Override
    public String toString() {
        return options.toString();
    }
}
