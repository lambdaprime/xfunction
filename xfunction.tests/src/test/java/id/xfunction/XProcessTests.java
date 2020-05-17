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

import org.junit.jupiter.api.Test;

public class XProcessTests {

    @Test
    public void test_getCode() {
        assertEquals(0, new XExec("ls").run().getCode());
    }

    @Test
    public void test_stdoutAsString() {
        assertEquals("test", new XExec("echo", "test").run().stdoutAsString());
    }

    @Test
    public void test_flushStdout() {
        XProcess proc = new XExec("echo", "test").run();
        proc.flushStdout();
        proc.stdoutAsString();
        proc.stdoutAsString();
        assertEquals("test", proc.stdoutAsString());
    }

    @Test
    public void test_flushStderr() {
        XProcess proc = new XExec("pwd", "-z").run();
        proc.flushStderr();
        proc.stderrAsString();
        proc.stderrAsString();
        assertEquals("pwd: invalid option -- 'z'\nTry 'pwd --help' for more information.",
            proc.stderrAsString());
    }
}
