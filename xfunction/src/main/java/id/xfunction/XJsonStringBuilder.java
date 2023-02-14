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
package id.xfunction;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON string builder for key-value pairs.
 *
 * <p>To get proper JSON all toString methods of each value to be added (appended) should produce
 * proper JSON representation for itself.
 *
 * <p>This class can be used within Eclipse as toString method generator. Generated toString will
 * produce JSON representation of an object. To set it up open Source - Generate toString - Code
 * style - Custom toString builder and select this class.
 */
public class XJsonStringBuilder {

    private List<Object> pairs = new ArrayList<>();

    public XJsonStringBuilder() {}

    /** Required by Eclipse toString method generator. This method does nothing. */
    public XJsonStringBuilder(Object obj) {}

    public XJsonStringBuilder append(String fieldName, Object fieldValue) {
        pairs.add(fieldName);
        pairs.add(fieldValue);
        return this;
    }

    public XJsonStringBuilder append(String fieldName, long fieldValue) {
        pairs.add(fieldName);
        pairs.add(fieldValue);
        return this;
    }

    public XJsonStringBuilder append(String fieldName, double fieldValue) {
        pairs.add(fieldName);
        pairs.add(fieldValue);
        return this;
    }

    public XJsonStringBuilder append(Object... pairs) {
        Preconditions.isTrue(pairs.length % 2 == 0, "Key-value missmatch");
        for (int i = 0; i < pairs.length; i += 2) {
            if (pairs[i] == null) continue;
            append(pairs[i].toString(), pairs[i + 1]);
        }
        return this;
    }

    public String build() {
        return XJson.asString(pairs.toArray());
    }

    @Override
    public String toString() {
        return build();
    }
}
