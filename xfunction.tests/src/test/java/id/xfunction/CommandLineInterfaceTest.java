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
package id.xfunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import id.xfunction.CommandLineInterface;

public class CommandLineInterfaceTest {

    @Test
    public void test_askConfirm() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream("yes".getBytes());
        CommandLineInterface cli = new CommandLineInterface(bais, baos, baos);
        String msg = "test?";
        boolean actual = cli.askConfirm(msg);
        assertTrue(actual);
        assertEquals(msg + "\nPlease confirm [yes/no]: ", baos.toString());
    }

    @Test
    public void test_askConfirm_wrong_answer() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream("ye\nnoo\nno".getBytes());
        CommandLineInterface cli = new CommandLineInterface(bais, baos, baos);
        String msg = "test?";
        boolean actual = cli.askConfirm(msg);
        assertFalse(actual);
        assertEquals(msg + "\nPlease confirm [yes/no]: Please confirm [yes/no]: Please confirm [yes/no]: ", baos.toString());
    }
    
    @Test
    public void test_print() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CommandLineInterface cli = new CommandLineInterface(System.in, baos, baos);
        cli.print(12);
        assertEquals("12\n", baos.toString());
    }
    
    @Test
    public void test_print_string() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CommandLineInterface cli = new CommandLineInterface(System.in, baos, baos);
        cli.print("12");
        assertEquals("12\n", baos.toString());
    }
    
    @Test
    public void test_print_string_with_special_char() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CommandLineInterface cli = new CommandLineInterface(System.in, baos, baos);
        cli.print("12%d");
        assertEquals("12%d\n", baos.toString());
    }
}
