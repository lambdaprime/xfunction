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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Set of functions to work with command line interface
 */
public class CommandLineInterface {

    private PrintStream out;
    private InputStream in;
    private Scanner scanner;

    /**
     * Default ctor which binds to System.in, System.out
     */
    public CommandLineInterface() {
        this(System.in, System.out);
    }

    public CommandLineInterface(InputStream in, OutputStream out) {
        this.in = in;
        this.out = new PrintStream(out);
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
     * Show message to the user and return what he enters
     */
    @SuppressWarnings("resource")
    public String read(String msg) {
        out.print(msg);
        return read();
    }

    /**
     * Reads a line and returns it
     */
    public String read() {
        return scanner.next();
    }

    /**
     * Reads an integer and returns it
     */
    public int readInt() {
        return scanner.nextInt();
    }

    /**
     * Read password safely from the user
     */
    public String readPassword(String msg) {
        return new String(System.console().readPassword(msg));
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
     * Terminate application with error code
     */
    public static void error(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}
