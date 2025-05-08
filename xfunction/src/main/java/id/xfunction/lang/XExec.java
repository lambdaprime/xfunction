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
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Additions to standard {@link java.lang.ProcessBuilder}
 *
 * <p>For example following Linux command:
 *
 * <pre>{@code
 * kubectl get pods | grep my-pod- | grep Complete | awk '{print $1}' | xargs kubectl delete pod
 * }</pre>
 *
 * <p>Can be called with {@link XExec} as follows:
 *
 * <pre>{@code
 * new XExec("kubectl get pods")
 *   .start()
 *   // consume stderr by printing it to terminal
 *   .forwardStderrAsync(false)
 *   // consume stdout and process it as a Java lazy Stream
 *   // this helps to support very big outputs which does not fit in memory
 *   .stdoutAsStream()
 *   // grep my-pod-
 *   .filter(l -> l.contains("my-pod-"))
 *   .filter(l -> l.contains("Complete"))
 *   // awk '{print $1}'
 *   .map(l -> l.split("\\s")[0])
 *   // xargs kubectl delete pod
 *   .forEach(pod -> new XExec("kubectl delete pod " + pod)
 *     .start()
 *     .forwardOutputAsync(true)
 *     .await());
 * }</pre>
 *
 * @author lambdaprime intid@protonmail.com
 */
public class XExec {

    private String[] cmd;
    private Stream<String> input;
    private ProcessBuilder processBuilder;
    private List<String> secrets = List.of();

    /**
     * Constructor which accepts the command to run. First item of the array should be the command
     * itself and the rest items are arguments of it.
     */
    public XExec(String... cmd) {
        this.cmd = cmd;
        processBuilder = new ProcessBuilder(cmd);
    }

    /**
     * Constructor which accepts the command to run. First item of the list should be the command
     * itself and the rest items are arguments of it.
     */
    public XExec(List<String> cmd) {
        this.cmd = cmd.toArray(new String[0]);
        processBuilder = new ProcessBuilder(cmd);
    }

    /**
     * Constructor which accepts the full command line to run.
     *
     * <p>It supports quotes so command like "ls \"/tmp/Program Files\"" will be processed like "ls"
     * "/tmp/Program Files" instead of "ls" "/tmp/Program" "Files"
     */
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
        processBuilder.directory(new File(workingDirectory));
        return this;
    }

    /** Sets the working directory for the process */
    public XExec withDirectory(Path workingDirectory) {
        processBuilder.directory(workingDirectory.toAbsolutePath().toFile());
        return this;
    }

    /** Adds following variables into environment */
    public XExec withEnvironmentVariables(Map<String, String> vars) {
        processBuilder.environment().putAll(vars);
        return this;
    }

    /** List of secrets which will be masked in the command output (both stdout, stderr) */
    public XExec withMaskedSecrets(String... secrets) {
        this.secrets = Arrays.asList(secrets);
        return this;
    }

    public String[] getCommand() {
        return cmd;
    }

    public ProcessBuilder getProcessBuilder() {
        return processBuilder;
    }

    /** Run the command with given input if any */
    public XProcess start() {
        try {
            Process p = processBuilder.start();
            if (input != null) {
                PrintStream ps = new PrintStream(p.getOutputStream(), true);
                input.forEach(ps::println);
                ps.close();
            }
            XProcess result = new XProcess(p, secrets);
            return result;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Encountered error executing command: " + Arrays.toString(cmd), e);
        }
    }
}
