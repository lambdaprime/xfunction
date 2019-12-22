package id.xfunction.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import id.xfunction.Curry;
import id.xfunction.Unchecked;

public class CurryTest {

    boolean isOk;

    void accept(String s) {
        isOk = true;
    }

    void run(Runnable r) {
        r.run();
    }

    @Test
    public void test_run() throws Exception {
        run(Unchecked.wrapRun(Curry.curry(this::accept, "12")));
        assertTrue(isOk);
    }

}
