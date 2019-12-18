package id.xfunction.tests;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import id.xfunction.Exec;
import id.xfunction.Exec.Result;

public class ExecTests {

    @Test
    public void test_run() throws Exception {
        Result result = new Exec("echo", "hello").run();
        List<String> out = result.stdout.collect(toList());
        List<String> err = result.stderr.collect(toList());
        assertEquals(1, out.size());
        assertEquals(0, err.size());
        assertEquals(0, result.code.get().intValue());
        assertEquals("hello", out.get(0));
    }

    @Test
    public void test_run_with_error() throws Exception {
        Result result = new Exec("ls /asdff").run();
        List<String> out = result.stdout.collect(toList());
        List<String> err = result.stderr.collect(toList());
        assertEquals(0, out.size());
        assertEquals(1, err.size());
        assertEquals(2, result.code.get().intValue());
        assertTrue(err.get(0).toUpperCase().contains("NO SUCH FILE"));
    }

}