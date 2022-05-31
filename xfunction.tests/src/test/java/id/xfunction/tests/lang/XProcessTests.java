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
package id.xfunction.tests.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.lang.XExec;
import id.xfunction.lang.XProcess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XProcessTests {

    @Test
    public void test_await() {
        assertEquals(0, new XExec("ls").run().await());
    }

    @Test
    public void test_stdoutAsString() {
        assertEquals("test", new XExec("echo", "test").run().stdout());
    }

    @Test
    public void test_flushStdout() {
        XProcess proc = new XExec("echo", "test").run();
        proc.stdoutAsync(false).await();
        proc.stdout();
        proc.stdout();
        assertEquals("test", proc.stdout());
    }

    @Test
    public void test_flushStderr() {
        XProcess proc = new XExec("pwd", "-z").run();
        proc.stderrAsync(false).await();
        proc.stderr();
        proc.stderr();
        assertEquals(
                "pwd: invalid option -- 'z'\nTry 'pwd --help' for more information.",
                proc.stderr());
    }

    @Test
    public void test_await_not_hangs() throws Exception {
        XExec xe = new XExec("printf \"%120000d\" 12");
        XProcess proc = xe.run();
        System.out.println(proc.outputAsync(true).await());
    }

    @Test
    public void test_forward() {
        XProcess proc = new XExec("echo", "test").run();
        proc.forwardOutputAsync().await();
    }

    @Test
    public void test_forward_and_flush_together() {
        XProcess proc = new XExec("echo", "test").run();
        proc.outputAsync(false);
        Assertions.assertThrows(IllegalStateException.class, () -> proc.forwardOutputAsync());
    }
}
