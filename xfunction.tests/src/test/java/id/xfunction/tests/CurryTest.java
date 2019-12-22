package id.xfunction.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import id.xfunction.function.Curry;
import id.xfunction.function.ThrowingFunction;
import id.xfunction.function.Unchecked;

public class CurryTest {

    boolean isOk;

    void accept(String s) {
        isOk = true;
    }

    void run(Runnable r) {
        r.run();
    }

    String m1(String s, Integer i) {
        return "";
    }

    String m2(String s, int i) {
        return "";
    }

    @Test
    public void test_run() throws Exception {
        run(Unchecked.wrapRun(Curry.curry(this::accept, "12")));
        assertTrue(isOk);

        ThrowingFunction<String, String, RuntimeException> curry2nd =
                Curry.curry2nd(this::m1, 12);
        curry2nd = Curry.curry2nd(this::m2, 12);

        ThrowingFunction<String, Integer, RuntimeException> curry2ndFail =
                Curry.curry2nd(Integer::parseInt, 12);
    }

}
