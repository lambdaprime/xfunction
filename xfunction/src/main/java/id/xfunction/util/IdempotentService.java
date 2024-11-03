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
 * Simple implementation of service lifecycle.
 *
 * <p>Lifecycle phases:
 *
 * <ul>
 *   <li>not started
 *   <li>started
 *   <li>stopped
 * </ul>
 *
 * <p>As it name implies {@link #start()} and {@link #close()} operations are idempotent.
 *
 * @author lambdaprime intid@protonmail.com
 */
public abstract class IdempotentService implements AutoCloseable {

    private static enum Status {
        NOT_STARTED,
        STARTED,
        STOPPED
    }

    private Status status = Status.NOT_STARTED;

    /** Starts the service if it is not yet started (idempotent operation). */
    public void start() {
        if (status == Status.NOT_STARTED) {
            onStart();
            status = Status.STARTED;
        } else if (status == Status.STOPPED) {
            throw new IllegalStateException("Already stopped");
        }
    }

    /**
     * Called only once during service lifetime when no exception is thrown. If it throws an
     * exception then service will not be started and can be retried later again until it finishes
     * without exceptions.
     */
    protected abstract void onStart();

    /**
     * Called only once during service lifetime. Service will be marked as closed even if exception
     * is thrown
     */
    protected abstract void onClose();

    /** Stops the service if it is not yet stopped (idempotent operation). */
    @Override
    public void close() {
        if (status != Status.STARTED) return;
        onClose();
        status = Status.STOPPED;
    }
}
