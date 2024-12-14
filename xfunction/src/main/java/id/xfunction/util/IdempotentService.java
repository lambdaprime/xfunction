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
package id.xfunction.util;

/**
 * Idempotent version of {@link ServiceLifecycle}
 *
 * <p>As its name implies {@link #start()} and {@link #close()} operations are idempotent and can be
 * called multiple times.
 *
 * @author lambdaprime intid@protonmail.com
 */
public abstract class IdempotentService extends ServiceLifecycle {

    public IdempotentService() {
        super(true, true);
    }

    /**
     * Starts the service if it is not yet started (idempotent operation), otherwise do nothing.
     *
     * @throws IllegalStateException when service was already stopped
     */
    @Override
    public void start() {
        super.start();
    }

    /** Stops the service if it is not yet stopped (idempotent operation), otherwise do nothing. */
    @Override
    public void close() {
        super.close();
    }
}
