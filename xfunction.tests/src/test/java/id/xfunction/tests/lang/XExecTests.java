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

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.xfunction.lang.XExec;
import id.xfunction.lang.XProcess;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class XExecTests {

    @Test
    public void test_run() throws Exception {
        XProcess result = new XExec("echo", "hello").start();
        List<String> out = result.stdoutAsStream().collect(toList());
        List<String> err = result.stderrAsStream().collect(toList());
        assertEquals(1, out.size());
        assertEquals(0, err.size());
        assertEquals(0, result.code().get().intValue());
        assertEquals("hello", out.get(0));
    }

    @Test
    public void test_run_with_error() throws Exception {
        XProcess result = new XExec("ls /asdff").start();
        List<String> out = result.stdoutAsStream().collect(toList());
        List<String> err = result.stderrAsStream().collect(toList());
        assertEquals(0, out.size());
        assertEquals(1, err.size());
        assertEquals(2, result.code().get().intValue());
        assertTrue(err.get(0).toUpperCase().contains("NO SUCH FILE"));
    }

    @Test
    public void test_stdoutAsString() {
        XProcess result = new XExec("echo", "hello").start();
        String out = result.stdout();
        assertEquals("hello", out);
    }

    @Test
    public void test_mask_secrets() throws Exception {
        XProcess result = new XExec("echo", "hello").withMaskedSecrets("ll").start();
        List<String> out = result.stdoutAsStream().collect(toList());
        List<String> err = result.stderrAsStream().collect(toList());
        assertEquals(1, out.size());
        assertEquals(0, err.size());
        assertEquals(0, result.code().get().intValue());
        assertEquals("hexxxxxxxo", out.get(0));
    }

    // test only that it compiles
    @Disabled
    public void test_sample() throws Exception {
        new XExec("kubectl get pods")
                .start()
                // consume stderr by printing it to terminal
                .forwardStderrAsync(false)
                // consume stdout and process it as a Java lazy Stream
                // this helps to support very big outputs which does not fit in memory
                .stdoutAsStream()
                // grep my-pod-
                .filter(l -> l.contains("my-pod-"))
                .filter(l -> l.contains("Complete"))
                // awk '{print $1}'
                .map(l -> l.split("\\s")[0])
                // xargs kubectl delete pod
                .forEach(
                        pod ->
                                new XExec("kubectl delete pod " + pod)
                                        .start()
                                        .forwardOutputAsync(true)
                                        .await());
    }
}
