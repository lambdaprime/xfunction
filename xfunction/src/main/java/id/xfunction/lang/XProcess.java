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
package id.xfunction.lang;

import static java.util.stream.Collectors.joining;

import id.xfunction.function.Unchecked;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Wraps standard java.lang.Process class with convenient methods.
 *
 * <p>Some commands may block until you start reading their stdout or stderr. This may be problem
 * when you just want to run a command ignoring its output. Use flush methods in that case.
 */
public class XProcess {
    private Process process;
    private Stream<String> stdout;
    private Stream<String> stderr;
    private Optional<String> stdoutAsString = Optional.empty();
    private Optional<String> stderrAsString = Optional.empty();
    private Optional<Future<Integer>> code = Optional.empty();
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    private boolean isStderrConsumed;
    private boolean isStdoutConsumed;

    public XProcess(Process process) {
        this.process = process;
        this.stdout = new BufferedReader(new InputStreamReader(process.getInputStream())).lines();
        this.stderr = new BufferedReader(new InputStreamReader(process.getErrorStream())).lines();
    }

    /** This ctor supposed to be used in tests when you want to mock results of XExec */
    public XProcess(Process process, Stream<String> stdout, Stream<String> stderr, int code) {
        this.process = process;
        this.stdout = stdout;
        this.stderr = stderr;
        this.code = Optional.of(CompletableFuture.completedFuture(code));
    }

    /**
     * Returns standard output as a string. This call will consume stdout stream meaning that you
     * can call it only once. If you want to call it multiple times make sure to flush stdout first
     * and wait until process will finish.
     *
     * @see flushStdout await
     * @throws IllegalStateException if called more than once
     */
    public synchronized String stdoutAsString() {
        if (isStdoutConsumed)
            try {
                // wait for flushStdout to finish if it is
                // running now
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        return stdoutAsString.orElseGet(() -> stdout.collect(joining("\n")));
    }

    /** Consumes stdout stream into internal buffer or ignores it. This call is async. */
    public XProcess flushStdout(boolean ignore) {
        consumeStdout();
        if (!process.isAlive()) return this;
        executor.execute(
                () -> {
                    if (ignore) {
                        stdout.forEach(l -> {});
                    } else {
                        stdoutAsString = Optional.of(stdout.collect(joining("\n")));
                    }
                });
        return this;
    }

    /** Consumes stdout stream and forwards it to System.out This call is async. */
    public XProcess forwardStdout() {
        return forwardStdout(System.out::println);
    }

    /** Consumes stdout stream and forwards it to consumer. This call is async. */
    public XProcess forwardStdout(Consumer<String> consumer) {
        consumeStdout();
        if (!process.isAlive()) return this;
        executor.execute(
                () -> {
                    stdout.forEach(consumer);
                });
        return this;
    }

    /**
     * Returns standard error output as a string. This call will consume stderr stream meaning that
     * you can call it only once. If you want to call it multiple times make sure to flush stderr
     * first.
     *
     * @see flushStderr
     * @throws IllegalStateException if called more than once
     */
    public String stderrAsString() {
        return stderrAsString.orElseGet(() -> stderr.collect(joining("\n")));
    }

    /** Consumes stderr stream into internal buffer asynchronously. This call is async. */
    public XProcess flushStderr(boolean ignore) {
        consumeStderr();
        if (!process.isAlive()) return this;
        executor.execute(
                () -> {
                    if (ignore) {
                        stderr.forEach(l -> {});
                    } else {
                        stderrAsString = Optional.of(stderr.collect(joining("\n")));
                    }
                });
        return this;
    }

    /** Consumes stderr stream and forwards it to System.err This call is async. */
    public XProcess forwardStderr() {
        consumeStderr();
        if (!process.isAlive()) return this;
        executor.execute(
                () -> {
                    stderr.forEach(System.err::println);
                });
        return this;
    }

    /**
     * Flushes stdout and stderr.
     *
     * @see flushStderr
     * @see flushStdout
     */
    public XProcess flush(boolean ignore) {
        flushStderr(ignore);
        flushStdout(ignore);
        return this;
    }

    /**
     * Forwards stdout and stderr to System.out and System.err respectively.
     *
     * @see forwardStderr
     * @see forwardStdout
     */
    public XProcess forward() {
        forwardStderr();
        forwardStdout();
        return this;
    }

    public Process process() {
        return process;
    }

    /**
     * After you consume this stream it will not be longer valid.
     *
     * @see flushStdout
     */
    public Stream<String> stdout() {
        return stdout;
    }

    /**
     * After you consume this stream it will not be longer valid.
     *
     * @see flushStderr
     */
    public Stream<String> stderr() {
        return stderr;
    }

    /** @return process return code */
    public Future<Integer> code() {
        if (!code.isPresent()) {
            code = Optional.of(CompletableFuture.supplyAsync(Unchecked.wrapGet(process::waitFor)));
        }
        return code.get();
    }

    /**
     * Waits for process to complete and returns code safely wrapping all checked exceptions to
     * RuntimeException.
     *
     * <p>Make sure to use flush methods if you ignore output/stderr.
     */
    public int await() {
        executor.shutdown();
        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Unchecked.getInt(code()::get);
    }

    /**
     * Java original method {@link Process#destroyForcibly()} does not destroy child processes. This
     * method destroys all children processes including parent one.
     */
    public void destroyAllForcibly() {
        process.descendants().forEach(ProcessHandle::destroyForcibly);
        process.destroyForcibly();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void consumeStdout() {
        if (isStdoutConsumed) throw new IllegalStateException("Stdout has consumer already");
        isStdoutConsumed = true;
    }

    private void consumeStderr() {
        if (isStderrConsumed) throw new IllegalStateException("Stderr has consumer already");
        isStderrConsumed = true;
    }
}
