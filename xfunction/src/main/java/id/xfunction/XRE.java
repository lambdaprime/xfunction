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
 * <p>An alias for XRuntimeException which is very useful in Java scripting.</p>
 * 
 * <pre>{@code
 * throw new XRE("Message");
 * }</pre>
 * 
 * Instead super long:
 * 
 * <pre>{@code
 * throw new XRuntimeException("Message");
 * }</pre>
 *
 */
public class XRE extends XRuntimeException {

    private static final long serialVersionUID = 1L;

    public XRE() {
        super();
    }
    
    public XRE(String message) {
        super(message);
    }

    public XRE(String fmt, Object...objs) {
        super(fmt, objs);
    }
}
