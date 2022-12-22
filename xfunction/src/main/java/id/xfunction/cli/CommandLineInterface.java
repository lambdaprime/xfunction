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
package id.xfunction.cli;

import id.xfunction.concurrent.DaemonThreadFactory;
import id.xfunction.io.CompositeOutputStream;
import id.xfunction.lang.XExec;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

/** Set of functions to work with command line interface */
public class CommandLineInterface {

    private PrintStream out;
    private InputStream in;
    private PrintStream err;
    private Scanner scanner;
    private Thread keyPressWatchdog;

    /** Default ctor which binds new CLI object to System.in, System.out */
    public CommandLineInterface() {
        this(System.in, System.out, System.err);
    }

    public CommandLineInterface(InputStream in, OutputStream out, OutputStream err) {
        this.in = in;
        this.out = new PrintStream(out);
        this.err = new PrintStream(err);
        this.scanner = new Scanner(in);
    }

    /** Bounded to System.in, System.out */
    public static CommandLineInterface cli = new CommandLineInterface();

    /** Wait user to press enter */
    public void waitPressEnter() {
        try {
            in.read();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Print user message "Press Enter to continue..." and wait */
    public void askPressEnter() {
        out.print("Press Enter to continue...");
        waitPressEnter();
    }

    /**
     * When this method is called for the first time it returns false. All consecutive calls will
     * return false as well except when user press Enter key since the time when this method was
     * called last time.
     *
     * <p>It allows you to execute some action repeatedly without blocking it to wait for user to
     * press Enter key:
     *
     * <pre>{@code
     * while (!wasEnterKeyPressed()) {
     *     action();
     * }
     * }</pre>
     *
     * <p>Here action() will be executed indefinitely until user press any key.
     */
    public boolean wasEnterKeyPressed() {
        if (keyPressWatchdog == null) {
            keyPressWatchdog =
                    new DaemonThreadFactory()
                            .newThread(
                                    () -> {
                                        try {
                                            in.read();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
            keyPressWatchdog.setName("EnterKeyPressWatchdog");
            keyPressWatchdog.start();
        }
        if (!keyPressWatchdog.isAlive()) {
            keyPressWatchdog = null;
            return true;
        }
        return false;
    }

    /** Show message to the user and return what he enters */
    public String read(String msg) {
        out.print(msg);
        return read();
    }

    /** Read a line until user enters and return it */
    public String read() {
        return scanner.next();
    }

    /** Read an integer entered by the user and return it */
    public int readInt() {
        return scanner.nextInt();
    }

    /** Read password entered safely from the user */
    public String readPassword(String fmt, Object... args) {
        return new String(System.console().readPassword(fmt, args));
    }

    /**
     * Print user message and ask to confirm it entering either "yes" or "no"
     *
     * @return whether user confirmed or not
     */
    public boolean askConfirm(String message) {
        out.println(message);
        while (true) {
            switch (read(String.format("Please confirm [yes/no]: "))) {
                case "yes":
                    return true;
                case "no":
                    return false;
                default:
                    continue;
            }
        }
    }

    /** Print error to stderr and terminate application with error code 1 */
    public void fail(String fmt, Object... args) {
        printerr(fmt, args);
        System.exit(1);
    }

    /** Print formatted message to stdout adding new line at the end */
    public void print(String fmt, Object... args) {
        out.println(String.format(fmt, args));
    }

    /** Print message to stdout adding new line at the end */
    public void print(String message) {
        out.println(message);
    }

    /** Print object to stdout adding new line at the end */
    public void print(Object obj) {
        print(obj.toString());
    }

    /** Print formatted message to stderr adding new line at the end */
    public void printerr(String fmt, Object... args) {
        err.println(String.format(fmt, args));
    }

    /** Print formatted message to stderr adding new line at the end */
    public void printerr(String message) {
        err.println(message);
    }

    /** Print object to stderr adding new line at the end */
    public void printerr(Object obj) {
        printerr(obj.toString());
    }

    /** Attach T pipe for all output streams (out, err) and forward it to the file */
    public void teeToFile(Path path) {
        try {
            CompositeOutputStream stream =
                    new CompositeOutputStream(out, new FileOutputStream(path.toFile()));
            out = new PrintStream(stream);
            err = new PrintStream(stream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enable or disable echo to stdout for any key user press on keyboard.
     *
     * <p>This operation works only in systems with "stty" installed. If "stty" is not found or if
     * it returns error code the {@link Exception} will be thrown.
     */
    public static void echo(boolean enabled) throws Exception {
        var exec = new XExec("stty " + (enabled ? "" : "-") + "echo");
        exec.getProcessBuilder().redirectInput(ProcessBuilder.Redirect.INHERIT);
        exec.start().stderrThrow();
    }

    /**
     * Usually any read operation on {@link System#in} blocks until user press Enter (new line).
     * This operation allows to disable such behavior so that any key which user press on keyboard
     * will be immediately available.
     *
     * <p>This operation works only in systems with "stty" installed. If "stty" is not found or if
     * it returns error code the {@link Exception} will be thrown.
     */
    public static void nonBlockingSystemInput() throws Exception {
        var exec = new XExec("stty -icanon min 1");
        exec.getProcessBuilder().redirectInput(ProcessBuilder.Redirect.INHERIT);
        exec.start().stderrThrow();
    }
}
