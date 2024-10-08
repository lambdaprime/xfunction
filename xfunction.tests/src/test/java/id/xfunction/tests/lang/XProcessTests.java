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
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

@EnabledOnOs({OS.LINUX})
public class XProcessTests {

    @Test
    public void test_await() {
        assertEquals(0, new XExec("ls").start().await());
    }

    @Test
    public void test_stdoutAsString() {
        assertEquals("test", new XExec("echo", "test").start().stdout());
    }

    @Test
    public void test_flushStdout() {
        XProcess proc = new XExec("echo", "test").start();
        proc.stdoutAsync(false).await();
        proc.stdout();
        proc.stdout();
        assertEquals("test", proc.stdout());

        proc = new XExec("echo", "test").start();
        proc.stdoutAsync(false);
        proc.stdout();
        proc.stdout();
        assertEquals("test", proc.stdout());

        proc = new XExec("echo", "test").start();
        var out = new StringBuilder();
        proc.stdoutAsync(out::append).await();
        assertEquals("test", out.toString());
    }

    @Test
    public void test_flushStderr() {
        XProcess proc = new XExec("pwd", "-z").start();
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
        XProcess proc = xe.start();
        System.out.println(proc.outputAsync(true).await());
    }

    @Test
    public void test_forward() {
        XProcess proc = new XExec("echo", "test").start();
        proc.forwardOutputAsync(true).await();
    }

    @Test
    public void test_forward_and_flush_together() {
        XProcess proc = new XExec("echo", "test").start();
        proc.outputAsync(false);
        Assertions.assertThrows(IllegalStateException.class, () -> proc.forwardOutputAsync(true));
    }

    @Test
    public void test_stderrThrow_no_hang_big_output() {
        XProcess proc = new XExec("seq -w 999999").start().stdoutAsync(false).stderrThrow();
        Assertions.assertEquals(999999, proc.stdout().lines().count());
    }
}
