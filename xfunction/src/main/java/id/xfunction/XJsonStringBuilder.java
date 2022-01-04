/*
 * Copyright 2022 lambdaprime
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
 * <p>To get proper JSON all toString methods of each value to be added (appended)
 * should produce proper JSON representation for itself.
 * 
 * <p>This class can be used within Eclipse as toString method generator.
 * Generated toString will produce JSON representation of an object. To set it up
 * open Source - Generate toString - Code style - Custom toString builder and select
 * this class.
 */
public class XJsonStringBuilder {
    
    private List<Object> pairs = new ArrayList<>();

    public XJsonStringBuilder(Object obj) {

    }
    
    public XJsonStringBuilder append(final String fieldName, final Object fieldValue) {
        pairs.add(fieldName);
        pairs.add(fieldValue);
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
