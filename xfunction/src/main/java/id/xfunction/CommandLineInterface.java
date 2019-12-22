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

import java.util.Scanner;

/**
 * Set of functions to work with command line interface
 */
public class CommandLineInterface {

    /**
     * Wait user to press enter
     */
    public static void waitPressEnter() {
        try {
            System.in.read();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Print user message to press Enter and wait
     */
    public static void askPressEnter() {
        System.out.print("Press Enter to continue...");
        waitPressEnter();
    }

    /**
     * Show message to the user and return what he enters
     */
    public static String read(String msg) {
        System.out.print(msg);
        return new Scanner(System.in).next();
    }

    /**
     * Read password safely from the user
     */
    public static String readPassword(String msg) {
        return new String(System.console().readPassword(msg));
    }
}
