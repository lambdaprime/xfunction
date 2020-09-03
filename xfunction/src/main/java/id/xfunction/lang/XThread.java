/*
 * Copyright 2019 lambdaprime
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
package id.xfunction.lang;

import id.xfunction.function.Unchecked;

public class XThread {

    /**
     * <p>Standard way to sleep in Java is pretty verbose because it throws
     * checked exception which you need to handle:</p>
     * 
     * <pre>{@code
     * try {
     *     Thread.sleep(1000);
     * } catch (InterruptedException e) {
     *     throw new RuntimeException(e);
     * }
     * }</pre>
     * 
     * <p>When you write scripts in Java you probably want it to fit in one
     * line and wrap any thrown exception to unchecked.</p>
     * 
     * <pre>{@code
     * XThread.sleep(1000);
     * }</pre>
     * 
     */
    public static void sleep(long msec) {
        Unchecked.run(() -> Thread.sleep(msec));
    }
}
