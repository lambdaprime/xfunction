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
package id.xfunction.tests.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import id.xfunction.cli.ArgumentParsingException;
import id.xfunction.cli.CommandOptions;
import id.xfunction.cli.CommandOptions.Config;
import org.junit.jupiter.api.Test;

public class CommandOptionsTest {

    @Test
    public void test_collectOptions() {
        assertThrows(
                ArgumentParsingException.class,
                () ->
                        CommandOptions.collectOptions(
                                new String[] {"-k", "value", "arg1", "arg2", "arg3"}));
        assertEquals(
                "{arg3=, k=value}",
                CommandOptions.collectOptions(
                                new Config().withIgnoreParsingExceptions(),
                                new String[] {"-k", "value", "arg1", "arg2", "-arg3"})
                        .toString());

        CommandOptions props =
                CommandOptions.collectOptions(new String[] {"-k1", "value", "--k2", "arg2", "-k3"});
        assertEquals("{k1=value, k2=arg2, k3=}", props.toString());

        props =
                CommandOptions.collectOptions(
                        new String[] {"-k1=value", "--k2==arg2", "-k3=", "-b1", "true", "-b2"});
        assertEquals("{b2=, k1=value, k2==arg2, k3=, b1=true}", props.toString());
        assertEquals(true, props.isOptionTrue("b1"));
        assertEquals(true, props.isOptionTrue("b2"));
    }
}
