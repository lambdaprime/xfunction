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

import id.xfunction.Preconditions;
import id.xfunction.function.Unchecked;
import id.xfunction.text.Substitutor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Wraps standard java.lang.Process class with convenient methods.
 *
 * <p>Some commands may block until you start reading their stdout or stderr. This may be problem
 * when you just want to run a command ignoring its output. Use flush methods in that case.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class XProcess {
    private Process process;
    private Stream<String> stdout;
    private Stream<String> stderr;
    private Optional<String> stdoutAsString = Optional.empty();
    private Optional<String> stderrAsString = Optional.empty();
    private Optional<CompletableFuture<Integer>> code = Optional.empty();
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    private boolean isStderrConsumed;
    private boolean isStdoutConsumed;
    private Semaphore stdoutSemaphore = new Semaphore(1);
    private Semaphore stderrSemaphore = new Semaphore(1);

    public XProcess(Process process, List<String> secrets) {
        this.process = process;
        var maskSecretsFunc = new Substitutor().maskSecretsFunc(secrets);
        this.stdout =
                new BufferedReader(new InputStreamReader(process.getInputStream()))
                        .lines()
                        .map(maskSecretsFunc);
        this.stderr =
                new BufferedReader(new InputStreamReader(process.getErrorStream()))
                        .lines()
                        .map(maskSecretsFunc);
    }

    /** This ctor supposed to be used in tests when you want to mock results of XExec */
    public XProcess(Process process, Stream<String> stdout, Stream<String> stderr, int code) {}

    /** This ctor supposed to be used in tests when you want to mock results of XExec */
    public XProcess(
            Process process,
            Stream<String> stdout,
            Stream<String> stderr,
            List<String> secrets,
            int code) {
        this.process = process;
        var maskSecretsFunc = new Substitutor().maskSecretsFunc(secrets);
        this.stdout = stdout.map(maskSecretsFunc);
        this.stderr = stderr.map(maskSecretsFunc);
        this.code = Optional.of(CompletableFuture.completedFuture(code));
    }

    /**
     * Returns standard output as a string. This call will consume stdout stream meaning that you
     * can call it only once. If you want to call it multiple times make sure to call {@link
     * #stdoutAsync(boolean)} or {@link #forwardStdoutAsync(boolean)} first.
     *
     * @see #stdoutAsync(boolean)
     * @throws IllegalStateException if called more than once
     */
    public String stdout() {
        try {
            stdoutSemaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            return stdoutAsString.orElseGet(() -> stdout.collect(joining("\n")));
        } finally {
            stdoutSemaphore.release();
        }
    }

    /**
     * Consumes stdout stream into internal buffer which later can be obtained through {@link
     * XProcess#stdout()} or ignores it. This call is async.
     *
     * <p>When process writes to stdout it may get blocked until somebody starts reading it. This
     * methods starts reading immediately in background (so that process will not block) and
     * preserves the output.
     */
    public XProcess stdoutAsync(boolean ignore) {
        return stdoutAsync(l -> {}, ignore);
    }

    /**
     * Consumes stdout stream by forwarding it to System.out. This call is async.
     *
     * @param ignore save forwarded output into internal buffer which later can be obtained through
     *     {@link XProcess#stdout()} or ignore it
     */
    public XProcess forwardStdoutAsync(boolean ignore) {
        return stdoutAsync(System.out::println, ignore);
    }

    /** Consumes stdout stream by forwarding it to consumer. This call is async. */
    public XProcess stdoutAsync(Consumer<String> consumer) {
        return stdoutAsync(consumer, true);
    }

    private XProcess stdoutAsync(Consumer<String> consumer, boolean ignore) {
        consumeStdout();
        executor.execute(
                () -> {
                    if (ignore) {
                        stdout.forEach(consumer);
                    } else {
                        stdoutAsString = Optional.of(stdout.peek(consumer).collect(joining("\n")));
                    }
                    stdoutSemaphore.release();
                });
        return this;
    }

    /**
     * Returns standard error output as a string. This call will consume stderr stream meaning that
     * you can call it only once. If you want to call it multiple times make sure to call {@link
     * XProcess#stderrAsync(boolean)} or {@link #forwardStderrAsync(boolean)} first.
     *
     * @see #stderrAsync(boolean)
     * @throws IllegalStateException if called more than once
     */
    public String stderr() {
        try {
            stderrSemaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            return stderrAsString.orElseGet(() -> stderr.collect(joining("\n")));
        } finally {
            stderrSemaphore.release();
        }
    }

    /**
     * Block until process finishes and then check its return code. If it is non 0 then throw an
     * exception with data from {@link #stderr()}.
     */
    public XProcess stderrThrow() {
        Preconditions.isTrue(
                isStdoutConsumed,
                "stdout needs to be consumed first, otherwise method may block forever");
        var err = stderr();
        var code = await();
        if (code == 0) return this;
        throw new RuntimeException(err);
    }

    /**
     * Consumes stderr stream into internal buffer which later can be obtained through {@link
     * XProcess#stderr()} or ignores it. This call is async.
     *
     * <p>When process writes to stderr it may get blocked until somebody starts reading it. This
     * methods starts reading immediately in background (so that process will not block) and
     * preserves the output.
     */
    public XProcess stderrAsync(boolean ignore) {
        return stderrAsync(l -> {}, ignore);
    }

    /** Consumes stderr stream by forwarding it to consumer. This call is async. */
    public XProcess stderrAsync(Consumer<String> consumer) {
        return stderrAsync(consumer, true);
    }

    private XProcess stderrAsync(Consumer<String> consumer, boolean ignore) {
        consumeStderr();
        executor.execute(
                () -> {
                    if (ignore) {
                        stderr.forEach(consumer);
                    } else {
                        stderrAsString = Optional.of(stderr.peek(consumer).collect(joining("\n")));
                    }
                    stderrSemaphore.release();
                });
        return this;
    }

    /**
     * Consumes stderr stream by forwarding it to System.err This call is async.
     *
     * @param ignore save forwarded stderr into internal buffer which later can be obtained through
     *     {@link XProcess#stderr()} or ignore it
     */
    public XProcess forwardStderrAsync(boolean ignore) {
        return stderrAsync(System.err::println, ignore);
    }

    /**
     * Combines {@link #stdoutAsync(boolean)} and {@link #stderrAsync(boolean)} into one method
     *
     * <p>When process writes to stdout/stderr it may get blocked until somebody starts reading it.
     * This methods starts reading both immediately in background (so that process will not block)
     * and preserves the output.
     *
     * @see #stdoutAsync
     * @see #stderrAsync
     */
    public XProcess outputAsync(boolean ignore) {
        stderrAsync(ignore);
        stdoutAsync(ignore);
        return this;
    }

    /**
     * Consumes stdout and stderr by forwarding them to System.out and System.err respectively.
     *
     * @param ignore save forwarded outputs into internal buffers which later can be obtained
     *     through {@link #stdout()} and {@link #stderr()} or ignore it
     * @see #forwardStderrAsync(boolean)
     * @see #forwardOutputAsync(boolean)
     */
    public XProcess forwardOutputAsync(boolean ignore) {
        forwardStderrAsync(ignore);
        forwardStdoutAsync(ignore);
        return this;
    }

    public Process process() {
        return process;
    }

    /** After you consume this stream it will not be longer valid. */
    public Stream<String> stdoutAsStream() {
        return stdout;
    }

    /** After you consume this stream it will not be longer valid. */
    public Stream<String> stderrAsStream() {
        return stderr;
    }

    /**
     * @return process return code
     */
    public CompletableFuture<Integer> code() {
        if (!code.isPresent()) {
            code = Optional.of(CompletableFuture.supplyAsync(Unchecked.wrapGet(process::waitFor)));
        }
        return code.get();
    }

    /**
     * Waits for process to complete and returns code safely wrapping all checked exceptions to
     * RuntimeException.
     *
     * <p>Make sure to use {@link XProcess#outputAsync(boolean)} method to ignore both
     * output/stderr.
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
        if (isStdoutConsumed || !stdoutSemaphore.tryAcquire())
            throw new IllegalStateException("Stdout has a consumer already");
        isStdoutConsumed = true;
    }

    private void consumeStderr() {
        if (isStderrConsumed || !stderrSemaphore.tryAcquire())
            throw new IllegalStateException("Stderr has a consumer already");
        isStderrConsumed = true;
    }
}
