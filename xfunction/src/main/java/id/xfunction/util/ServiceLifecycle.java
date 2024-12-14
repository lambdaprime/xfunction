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
 * Service lifecycle implementation.
 *
 * <p>Lifecycle phases:
 *
 * <ul>
 *   <li>not started
 *   <li>started
 *   <li>stopped
 * </ul>
 *
 * @see IdempotentService
 * @author lambdaprime intid@protonmail.com
 */
public abstract class ServiceLifecycle implements AutoCloseable {

    public static enum Status {
        NOT_STARTED,
        STARTED,
        STOPPED
    }

    private Status status = Status.NOT_STARTED;
    private boolean isStartIdempotent, isCloseIdempotent;

    /**
     * @param isStartIdempotent when false calling {@link #start()} more than once will result in
     *     exception
     * @param isCloseIdempotent when false calling {@link #stop()} more than once will result in
     *     exception
     */
    public ServiceLifecycle(boolean isStartIdempotent, boolean isCloseIdempotent) {
        this.isStartIdempotent = isStartIdempotent;
        this.isCloseIdempotent = isCloseIdempotent;
    }

    /**
     * Starts the service if it is not yet started
     *
     * @throws IllegalStateException when service was already stopped
     */
    public void start() {
        if (status == Status.NOT_STARTED) {
            onStart();
            status = Status.STARTED;
        } else if (status == Status.STOPPED) throw new IllegalStateException("Already stopped");
        else if (!isStartIdempotent && status == Status.STARTED)
            throw new IllegalStateException("Already started");
    }

    /**
     * Called only once during service lifetime. If it throws an exception then service will not be
     * started and can be retried later again until it eventually starts without exceptions.
     */
    protected abstract void onStart();

    /**
     * Called only once during service lifetime. Service will be marked as closed even if exception
     * is thrown
     */
    protected abstract void onClose();

    /** Stops the service if it is not yet stopped */
    @Override
    public void close() {
        if (!isCloseIdempotent && status == Status.STOPPED)
            throw new IllegalStateException("Already stopped");
        else if (status != Status.STARTED) return;
        onClose();
        status = Status.STOPPED;
    }

    public Status getServiceStatus() {
        return status;
    }
}
