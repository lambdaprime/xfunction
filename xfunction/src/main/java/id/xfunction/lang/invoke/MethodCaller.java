/*
 * Copyright 2022 lambdaprime
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
package id.xfunction.lang.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows to call methods of an object by name dynamically at a runtime.
 *
 * <p>Calls are done thru MethodHandle and not reflection which supposed to be faster.
 *
 * <p>Overloaded methods are not supported.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class MethodCaller {

    private Object object;
    private Map<String, MethodHandle> methods = new HashMap<>();

    /**
     * @param lookup this is normally object returned by {@link MethodHandles#lookup()} which is
     *     called in user module space. This allows MethodCaller to call cross module methods
     *     (methods which declared outside of xfunction module)
     * @param object object which methods will be called
     */
    public MethodCaller(Lookup lookup, Object object) throws Exception {
        this.object = object;
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            if (!Modifier.isPublic(m.getModifiers())) continue;
            if (Modifier.isNative(m.getModifiers())) continue;
            if (Modifier.isStatic(m.getModifiers())) continue;
            MethodType mt = MethodType.methodType(m.getReturnType(), m.getParameterTypes());
            MethodHandle mh = lookup.findVirtual(clazz, m.getName(), mt);
            this.methods.put(m.getName(), mh);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object call(String methodName, List args) throws Throwable, NoSuchMethodException {
        MethodHandle mh = methods.get(methodName);
        if (mh == null) throw new NoSuchMethodException(methodName);
        List list = new ArrayList<>();
        list.add(object);
        list.addAll(args);
        return mh.invokeWithArguments(list);
    }

    public Object call(String methodName) throws Throwable, NoSuchMethodException {
        return call(methodName, Collections.emptyList());
    }
}
