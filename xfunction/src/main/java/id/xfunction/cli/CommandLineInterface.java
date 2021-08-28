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
package id.xfunction.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import id.xfunction.concurrent.DaemonThreadFactory;

/**
 * Set of functions to work with command line interface
 */
public class CommandLineInterface {

    private PrintStream out;
    private InputStream in;
    private PrintStream err;
    private Scanner scanner;
    private Thread keyPressWatchdog;

    /**
     * Default ctor which binds new CLI object to System.in, System.out
     */
    public CommandLineInterface() {
        this(System.in, System.out, System.err);
    }

    public CommandLineInterface(InputStream in, OutputStream out, OutputStream err) {
        this.in = in;
        this.out = new PrintStream(out);
        this.err = new PrintStream(err);
        this.scanner = new Scanner(in);
    }

    /**
     * Bounded to System.in, System.out
     */
    public static CommandLineInterface cli = new CommandLineInterface();

    /**
     * Wait user to press enter
     */
    public void waitPressEnter() {
        try {
            in.read();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Print user message to press Enter and wait
     */
    public void askPressEnter() {
        out.print("Press Enter to continue...");
        waitPressEnter();
    }

    /**
     * <p>When this method is called for the first time it returns false.
     * All consecutive calls will return false as well except when user press
     * any key since the time when this method was called last time.</p>
     * 
     * <p>It allows you to execute some action repeatedly without blocking
     * it to wait for user to press any key:</p>
     * 
     * <pre>{@code
     * while (!wasKeyPressed()) {
     *     action();
     * }
     * }</pre>
     * 
     * <p>Here action() will be executed indefinitely until user press any key.</p>
     * 
     */
    public boolean wasKeyPressed() {
        if (keyPressWatchdog == null) {
            keyPressWatchdog = new DaemonThreadFactory().newThread(() -> {
                try {
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            keyPressWatchdog.setName("keyPressWatchdog");
            keyPressWatchdog.start();
        }
        if (!keyPressWatchdog.isAlive()) {
            keyPressWatchdog = null;
            return true;
        }
        return false;
    }
    
    /**
     * Show message to the user and return what he enters
     */
    @SuppressWarnings("resource")
    public String read(String msg) {
        out.print(msg);
        return read();
    }

    /**
     * Read a line and returns it
     */
    public String read() {
        return scanner.next();
    }

    /**
     * Read an integer and returns it
     */
    public int readInt() {
        return scanner.nextInt();
    }

    /**
     * Read password safely from the user
     */
    public String readPassword(String fmt, Object...args) {
        return new String(System.console().readPassword(fmt, args));
    }

    /**
     * Print user message and ask to confirm it entering either "yes" or "no"
     * @return whether user confirmed or not
     */
    public boolean askConfirm(String message) {
        out.println(message);
        while (true) {
            switch (read(String.format("Please confirm [yes/no]: "))) {
            case "yes": return true;
            case "no": return false;
            default: continue;
            }
        }
    }

    /**
     * Print error to stderr and terminate application with
     * error code 1
     */
    public void fail(String fmt, Object...args) {
        printerr(fmt, args);
        System.exit(1);
    }

    /**
     * Print formatted message to stdout adding new line at the end
     */
    public void print(String fmt, Object...args) {
        out.println(String.format(fmt, args));
    }

    /**
     * Print message to stdout adding new line at the end
     */
    public void print(String message) {
        out.println(message);
    }

    /**
     * Print object to stdout adding new line at the end
     */
    public void print(Object obj) {
        print(obj.toString());
    }
    
    /**
     * Print formatted message to stderr adding new line at the end
     */
    public void printerr(String fmt, Object...args) {
        err.println(String.format(fmt, args));
    }
    
    /**
     * Print formatted message to stderr adding new line at the end
     */
    public void printerr(String message) {
        err.println(message);
    }
    
    /**
     * Print object to stderr adding new line at the end
     */
    public void printerr(Object obj) {
        printerr(obj.toString());
    }
}
