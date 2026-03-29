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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    /** List can be stored inside the file. In such case the option should contain */
    public List<String> getOptionList(String optionName, boolean isRequired) {
        var option = getOption(optionName).orElse(null);
        if (isRequired && option == null) {
            throw new ArgumentParsingException(
                    "Command-line option \"-" + optionName + "\" is missing");
        }
        if (option == null) return List.of();
        if (option.startsWith("@")) {
            var documentFileListPath = Path.of(option.substring(1));
            try {
                return Files.readAllLines(documentFileListPath).stream()
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read " + documentFileListPath, e);
            }
        } else {
            return Arrays.stream(option.split(",")).collect(Collectors.toList());
        }
    }

    /**
     * Check if optionName is set to "true"
     *
     * <p>Two forms are allowed:
     *
     * <ol>
     *   <li>-optionName
     *   <li>-optionName=true
     * </ol>
     */
    public boolean isOptionTrue(String optionName) {
        return getOption(optionName)
                .map(String::toLowerCase)
                .filter(Predicate.isEqual("true").or(Predicate.isEqual("")))
                .isPresent();
    }

    /** Adds new option */
    public void addOption(String name, boolean value) {
        addOption(name, "" + value);
    }

    public int size() {
        return options.size();
    }

    /** Adds new option */
    private void addOption(String name, String value) {
        options.put(name, value);
    }

    @Override
    public String toString() {
        return options.toString();
    }

    /**
     * Load additional properties from a file whose name is supplied by the {@code
     * propertyFileOption} option and merges them into the current {@link CommandOptions} instance.
     *
     * <p>The option named {@code propertyFileOption} must be present in the command‑line arguments
     * and its value should be a path to a Java {@code .properties} file. If the option is missing
     * or the file cannot be read, a {@link RuntimeException} is thrown.
     *
     * <p>When the file is successfully loaded, every key/value pair found in it is added to the
     * internal {@code Properties} object of this {@code CommandOptions}. Existing properties are
     * overwritten by the new values.
     *
     * @param propertyFileOption the name of the option whose value is the path to a {@code
     *     .properties} file to be loaded
     * @throws IllegalArgumentException if {@code propertyFileOption} is {@code null} or empty
     * @throws RuntimeException if the specified file cannot be opened or an I/O error occurs while
     *     loading it
     */
    public void populateFromFile(String propertyFileOption) {
        getOption(propertyFileOption)
                .ifPresent(
                        propertyFile -> {
                            var props = new Properties();
                            try (var in = new FileInputStream(propertyFile)) {
                                props.load(in);
                            } catch (IOException e) {
                                throw new RuntimeException(
                                        "Failed to load property file: " + propertyFile, e);
                            }
                            options.putAll(props);
                        });
    }
}
