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
package id.xfunction;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import id.xfunction.function.Unchecked;

/**
 * Wraps standard Process class with convenient methods
 */
public class XProcess {
    private Process process;
    private Stream<String> stdout;
    private Stream<String> stderr;
    private Optional<Future<Integer>> code = Optional.empty();

    public XProcess(Process process) {
        this.process = process;
        this.stdout = new BufferedReader(
            new InputStreamReader(process.getInputStream())).lines();
        this.stderr = new BufferedReader(
            new InputStreamReader(process.getErrorStream())).lines();
    }

    /**
     * @return Standard output as a string
     */
    public String stdoutAsString() {
        return stdout.collect(joining("\n"));
    }

    /**
     * @return Standard error output as a string
     */
    public String stderrAsString() {
        return stderr.collect(joining("\n"));
    }

    public Process process() {
        return process;
    }

    public Stream<String> stdout() {
        return stdout;
    }

    public Stream<String> stderr() {
        return stderr;
    }

    /**
     * @return process return code
     */
    public Future<Integer> code() {
        if (code.isEmpty()) {
            code = Optional.of(CompletableFuture.supplyAsync(Unchecked.wrapGet(process::waitFor)));
        }
        return code.get();
    }
    
}