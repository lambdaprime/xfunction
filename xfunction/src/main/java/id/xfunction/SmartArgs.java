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
package id.xfunction;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Class for parsing command line arguments.
 * 
 * Key-value arguments (like "-k val") are sent to HANDLERS.
 * 
 * Key only arguments (like "-l") are sent to DEFAULT_HANDLER.
 * 
 * If default handler returns false we stop processing further arguments.
 * 
 * Example:
 * 
 * <pre>{@code
 * private Map<String, Consumer<String>> handlers = new HashMap<String, Consumer<String>>() {{
 *     put("-k", val -> {...});
 *     put("-e", expr -> {...});
 *     put("-d", dir -> {...});
 * }};
 * private Function<String, Boolean> defaultHandler = arg -> {
 *     switch (arg) {
 *     case "-v": {
 *         ...
 *         return true;
 *     }
 *     case "--no-wait": {
 *         ...
 *         return true;
 *     }}
 *     throw new RuntimeException();
 * };
 * SmartArgs args = new SmartArgs(handlers, defaultHandler);
 * args.parse(new String[] {"-k" , "value", "-v", "-e", "expr"});
 * }</pre>
 * 
 */
public class SmartArgs {

    private Map<String, Consumer<String>> handlers;
    private Function<String, Boolean> defaultHandler;

    public SmartArgs(Map<String, Consumer<String>> handlers, Function<String, Boolean> defaultHandler) {
        this.handlers = handlers;
        this.defaultHandler = defaultHandler;
    }

    public void parse(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            boolean expectValue = handlers.containsKey(args[i]);
            if (expectValue && i + 1 == args.length)
                throw new Exception();
            if (!expectValue && !defaultHandler.apply(args[i]))
                return;
            if (!expectValue)
                continue;
            handlers.get(args[i]).accept(args[i + 1]);
            i++;
        }
    }
}
