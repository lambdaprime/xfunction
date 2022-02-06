/*
 * Copyright 2021 lambdaprime
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import id.xfunction.AssertRunCommand;

public class AssertRunCommandTests {

    @Test
    public void test_vars() {
        Map<String, String> vars = new HashMap<>();
        vars.put("xvar", "xval");
        new AssertRunCommand("/bin/sh", "-c", "set")
            .withOutputConsumer(env -> {
                System.out.println(env);
                assertEquals(true, env.contains("xvar='xval'"));
            })
            .withEnvironmentVariables(vars )
            .run();
    }

}
