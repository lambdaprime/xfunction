/*
 * Copyright 2022 lambdaprime
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
package id.xfunction.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import id.xfunction.function.Curry;
import id.xfunction.function.ThrowingFunction;
import id.xfunction.function.Unchecked;
import org.junit.jupiter.api.Test;

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
        run(Unchecked.wrapRun(Curry.curryAccept(this::accept, "12")));
        assertTrue(isOk);

        ThrowingFunction<String, String, RuntimeException> curry2nd =
                Curry.curryApply2nd(this::m1, 12);
        curry2nd = Curry.curryApply2nd(this::m2, 12);

        ThrowingFunction<String, Integer, RuntimeException> curry2ndFail =
                Curry.curryApply2nd(Integer::parseInt, 12);
    }
}
