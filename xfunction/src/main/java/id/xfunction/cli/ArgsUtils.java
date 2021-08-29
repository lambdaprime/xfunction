/*
 * Copyright 2020 lambdaprime
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

import java.util.Properties;

/**
 * <p>Set of methods for parsing command line arguments.</p>
 */
public class ArgsUtils {

    /**
     * <p>Collects options with their arguments into Properties object.</p>
     * <p>Each option may have zero or one argument.</p>
     * <p>If option has no argument then it is added to Properties with an empty string as a value.</p>
     * <p>Each option should start from -, or --.</p>
     * <p>Options may accept arguments in one of the following ways: -option arg, -option=arg</p> 
     */
    public Properties collectOptions(String[] args) throws ArgumentParsingException {
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
                throw new ArgumentParsingException();
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
        return props;
    }
}
