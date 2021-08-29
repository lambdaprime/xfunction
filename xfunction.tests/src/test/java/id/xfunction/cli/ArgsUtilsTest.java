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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;

import org.junit.jupiter.api.Test;

public class ArgsUtilsTest {
    
    private ArgsUtils utils = new ArgsUtils();

    @Test
    public void test_collectOptions() {
        assertThrows(ArgumentParsingException.class, () ->
        utils.collectOptions(new String[] {"-k" , "value", "arg1", "arg2", "arg3"}));
        
        Properties props = utils.collectOptions(new String[] {"-k1" , "value", "--k2", "arg2", "-k3"});
        assertEquals("{k1=value, k2=arg2, k3=}", props.toString());
        
        props = utils.collectOptions(new String[] {"-k1=value", "--k2==arg2", "-k3="});
        assertEquals("{k1=value, k2==arg2, k3=}", props.toString());
    }
}
