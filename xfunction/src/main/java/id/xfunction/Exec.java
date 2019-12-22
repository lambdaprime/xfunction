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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * Run shell command and obtain its output as a stream of lines.
 */
public class Exec {

    private String[] cmd;
    private boolean singleLine;
    private Stream<String> input;

    /**
     * Holds result of command execution
     */
    public static class Result {
        public Stream<String> stdout;
        public Stream<String> stderr;
        public Future<Integer> code;
        Result(Stream<String> stdout, Stream<String> stderr) { this.stdout = stdout; this.stderr = stderr; }
    }

    /**
     * Constructor which accepts the command to run.
     * First item of the array should be the command itself and the
     * rest items are arguments of it.  
     */
    public Exec(String... cmd) {
        this.cmd = cmd;
    }

    /**
     * Constructor which accepts the full command line to run
     */
    public Exec(String cmd) {
        this(new String[] {cmd});
        singleLine = true;
    }

    /**
     * Given input will be sent to command's stdin.
     */
    public Exec withInput(Stream<String> input) {
        this.input = input;
        return this;
    }

    /**
     * Run the command and send it the input if any
     */
    public Result run() {
        try {
            Process p = runProcess();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            if (input != null) {
                PrintStream ps = new PrintStream(p.getOutputStream(), true);
                input.forEach(ps::println);
                ps.close();
            }
            BufferedReader ein = new BufferedReader(
                new InputStreamReader(p.getErrorStream()));
            Result result = new Result(in.lines(), ein.lines());
            result.code = CompletableFuture.supplyAsync(Unchecked.wrapGet(p::waitFor));
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Encountered error executing command: " + Arrays.toString(cmd), e);
        }
    }

    private Process runProcess() throws IOException {
        if (singleLine) {
            return Runtime.getRuntime().exec(cmd[0]);
        }
        return new ProcessBuilder(cmd).start();
    }

}