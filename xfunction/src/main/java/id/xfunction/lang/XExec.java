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

import id.xfunction.text.QuotesTokenizer;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/** Additions to standard java.lang.ProcessBuilder */
public class XExec {

    private String[] cmd;
    private Stream<String> input;
    private Optional<File> workingDirectory = Optional.empty();
    private Map<String, String> vars = Collections.emptyMap();

    /**
     * Constructor which accepts the command to run. First item of the array should be the command
     * itself and the rest items are arguments of it.
     */
    public XExec(String... cmd) {
        this.cmd = cmd;
    }

    /**
     * Constructor which accepts the command to run. First item of the list should be the command
     * itself and the rest items are arguments of it.
     */
    public XExec(List<String> cmd) {
        this.cmd = cmd.toArray(new String[0]);
    }

    /** Constructor which accepts the full command line to run */
    public XExec(String cmd) {
        this(new QuotesTokenizer().tokenize(cmd));
    }

    /** Given input will be sent to command's stdin. */
    public XExec withInput(Stream<String> input) {
        this.input = input;
        return this;
    }

    /** Sets the working directory for the process */
    public XExec withDirectory(String workingDirectory) {
        this.workingDirectory = Optional.of(new File(workingDirectory));
        return this;
    }

    /** Sets the working directory for the process */
    public XExec withDirectory(Path workingDirectory) {
        this.workingDirectory = Optional.of(workingDirectory.toAbsolutePath().toFile());
        return this;
    }

    /** Adds following variables into environment */
    public XExec withEnvironmentVariables(Map<String, String> vars) {
        this.vars = vars;
        return this;
    }

    public String[] getCommand() {
        return cmd;
    }

    /** Run the command with given input if any */
    public XProcess run() {
        try {
            Process p = runProcess();
            if (input != null) {
                PrintStream ps = new PrintStream(p.getOutputStream(), true);
                input.forEach(ps::println);
                ps.close();
            }
            XProcess result = new XProcess(p);
            return result;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Encountered error executing command: " + Arrays.toString(cmd), e);
        }
    }

    private Process runProcess() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if (workingDirectory.isPresent()) pb.directory(workingDirectory.get());
        if (!vars.isEmpty()) pb.environment().putAll(vars);
        return pb.start();
    }
}
