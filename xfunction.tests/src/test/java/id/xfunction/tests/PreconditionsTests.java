/*
 * Copyright 2019 lambdaprime
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

import id.xfunction.PreconditionException;
import id.xfunction.Preconditions;
import id.xfunction.logging.TracingToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PreconditionsTests {

    @Test
    public void test_assertTrue() {
        Assertions.assertThrows(PreconditionException.class, () -> Preconditions.isTrue(false));
        var e =
                Assertions.assertThrows(
                        PreconditionException.class,
                        () -> Preconditions.isTrue(false, "message test"));
        e.printStackTrace();
        Assertions.assertEquals("message test", e.getMessage());

        e =
                Assertions.assertThrows(
                        PreconditionException.class,
                        () -> Preconditions.isTrue(false, "message test %s", "value"));
        e.printStackTrace();
        Assertions.assertEquals("message test value", e.getMessage());
    }

    @Test
    public void test_assertTrue_happy() {
        Preconditions.isTrue(true);

        Preconditions.isTrue(true, "Already subscribed");
    }

    @Test
    public void test_assertNotNull() {
        Assertions.assertThrows(PreconditionException.class, () -> Preconditions.notNull(null));
    }

    @Test
    public void test_assertNotNull_happy() {
        Preconditions.notNull("test");
    }

    @Test
    public void test_assertEquals() {
        Preconditions.equals(null, null);
        Assertions.assertThrows(
                PreconditionException.class, () -> Preconditions.equals(null, "test"));

        var e =
                Assertions.assertThrows(
                        PreconditionException.class,
                        () -> Preconditions.equals(5, 6, "message test"));
        e.printStackTrace();
        Assertions.assertEquals(
                "message test: expected value <5>, actual value <6>", e.getMessage());

        e =
                Assertions.assertThrows(
                        PreconditionException.class,
                        () -> Preconditions.equals(5, 6, "message test %s", "val"));
        e.printStackTrace();
        Assertions.assertEquals(
                "message test val: expected value <5>, actual value <6>", e.getMessage());

        e =
                Assertions.assertThrows(
                        PreconditionException.class,
                        () ->
                                Preconditions.equals(
                                        5.,
                                        6.,
                                        new TracingToken("token1"),
                                        "message test %s",
                                        "val"));
        e.printStackTrace();
        Assertions.assertEquals(
                "token1: message test val: expected value <5.0>, actual value <6.0>",
                e.getMessage());
    }
}
