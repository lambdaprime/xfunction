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
package id.xfunction;

/**
 * Exception which is thrown by {@link Preconditions} methods.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class PreconditionException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String EQUALS_MESSAGE_FORMAT =
            "%s: expected value <%s>, actual value <%s>";

    public PreconditionException(String message) {
        super(message);
    }

    public PreconditionException(Throwable t) {
        super(t);
    }

    public PreconditionException(String fmt, Object... objs) {
        super(String.format(fmt, objs));
    }

    public PreconditionException(long expected, long actual) {
        super(String.format(EQUALS_MESSAGE_FORMAT, "Predondition error", expected, actual));
    }

    public PreconditionException(String message, long expected, long actual) {
        super(String.format(EQUALS_MESSAGE_FORMAT, message, expected, actual));
    }

    public PreconditionException(double expected, double actual) {
        super(String.format(EQUALS_MESSAGE_FORMAT, "Predondition error", expected, actual));
    }

    public PreconditionException(String message, double expected, double actual) {
        super(String.format(EQUALS_MESSAGE_FORMAT, message, expected, actual));
    }

    public <T> PreconditionException(String message, T expected, T actual) {
        super(String.format(EQUALS_MESSAGE_FORMAT, message, expected, actual));
    }
}
