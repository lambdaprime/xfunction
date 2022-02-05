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

/**
 * <p>Exception which is thrown by {@link XAsserts} methods.
 */
public class XAssertException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String EQUALS_MESSAGE_FORMAT = "%s: expected value <%s>, actual value <%s>";
    
    public XAssertException(String message) {
        super(message);
    }

    public XAssertException(Throwable t) {
        super(t);
    }

    public XAssertException(String fmt, Object...objs) {
        super(String.format(fmt, objs));
    }
    
    public XAssertException(double expected, double actual) {
        super(String.format(EQUALS_MESSAGE_FORMAT, "Assertion error", expected, actual));
    }
    
    public XAssertException(String message, double expected, double actual) {
        super(String.format(EQUALS_MESSAGE_FORMAT, message, expected, actual));
    }

    public <T> XAssertException(String message, T expected, T actual) {
        super(String.format(EQUALS_MESSAGE_FORMAT, message, expected, actual));
    }
}
