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
package id.xfunction.lang;

/**
 * <p>RuntimeException which allows to specify formatted messages.</p>
 * 
 * <p>Example:</p>
 * 
 * <pre>{@code
 * throw new XRuntimeException("Command %s failed: %s", cmd, error);
 * }</pre>
 *
 */
public class XRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public XRuntimeException() {
        super();
    }
    
    public XRuntimeException(String message) {
        super(message);
    }

    public XRuntimeException(String fmt, Object...objs) {
        super(String.format(fmt, objs));
    }
    
    public XRuntimeException(Throwable t) {
        super(t);
    }
}
