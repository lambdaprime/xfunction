/*
 * Copyright 2022 lambdaprime
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
package id.xfunction.tests.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import id.xfunction.cli.SmartArgs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class SmartArgsTest {

    private List<String> buf = new ArrayList<>();
    private Map<String, Consumer<String>> handlers =
            new HashMap<String, Consumer<String>>() {
                {
                    put("-k", val -> buf.add("Dir: " + val));
                    put("-e", expr -> buf.add("Expression: " + expr));
                    put("-d", dir -> buf.add("Dir: " + dir));
                }
            };
    private Function<String, Boolean> defaultHandler =
            arg -> {
                switch (arg) {
                    case "-v":
                        buf.add("Verbose");
                        return true;
                    case "--no-wait":
                        buf.add("No wait");
                        return true;
                }
                throw new RuntimeException();
            };

    @Test
    public void test_run() {
        SmartArgs args = new SmartArgs(handlers, defaultHandler);
        args.parse(new String[] {"-k", "value"});
        assertEquals("[Dir: value]", buf.toString());

        buf.clear();
        args.parse(new String[] {"-k", "value", "-v"});
        assertEquals("[Dir: value, Verbose]", buf.toString());

        buf.clear();
        args.parse(new String[] {"-k", "value", "-v", "-e", "expr"});
        assertEquals("[Dir: value, Verbose, Expression: expr]", buf.toString());

        buf.clear();
        args.parse(new String[] {"-k", "value", "-v", "-e", "expr", "--no-wait"});
        assertEquals("[Dir: value, Verbose, Expression: expr, No wait]", buf.toString());
    }

    @Test
    public void test_unknown_arg() {
        SmartArgs args = new SmartArgs(handlers, defaultHandler);
        assertThrows(RuntimeException.class, () -> args.parse(new String[] {"-g", "ggg"}));
    }

    @Test
    public void test_positional_args() {
        List<String> args = new ArrayList<>();
        new SmartArgs(handlers, arg -> args.add(arg))
                .parse(new String[] {"-k", "value", "arg1", "arg2", "arg3"});
        assertEquals("[Dir: value]", buf.toString());
        assertEquals("[arg1, arg2, arg3]", args.toString());
    }
}
