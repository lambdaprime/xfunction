/*
 * Copyright 2020 lambdaprime
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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import id.xfunction.lang.XRE;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;

/** Set of functions to work with JSON format */
public class XJson {

    private static final int MAX_FRACTION_LEN = 10;
    private static NumberFormat format = (NumberFormat) NumberFormat.getInstance().clone();
    private static boolean isNegativeZeroDisabled;

    static {
        format.setMaximumFractionDigits(MAX_FRACTION_LEN);
        format.setGroupingUsed(false);
    }

    /**
     * Build a JSON from a given pair of objects (k1, v1, k2, v2, ...). String for each object is
     * obtained by calling toString on the object itself.
     *
     * <p>Example:
     *
     * <pre>{@code
     * XJson.asString(
     *     "k1", 123,
     *     "k2", "ggg",
     *     "k3", List.of("sg", "dfg", "dsfg")));
     * }</pre>
     *
     * <p>Produces a string like: { "k1": "123", "k2": "ggg", "k3": ["sg", "dfg", "dsfg"] }
     */
    public static String asString(Object... pairs) {
        Preconditions.isTrue(pairs.length % 2 == 0, "Key-value missmatch");
        StringJoiner buf = new StringJoiner(", ");
        for (int i = 0; i < pairs.length; i += 2) {
            Object k = pairs[i];
            Object v = pairs[i + 1];
            if (v == null) continue;
            String vstr = jsonToString(v);
            if (vstr == null) vstr = "";
            if (!(v instanceof Number)) vstr = quote(vstr);
            if (v instanceof Map) {
                vstr = asString((Map<?, ?>) v);
            } else if (v instanceof Collection) {
                vstr = "[" + asJsonArray((Collection<?>) v) + "]";
            } else if (v.getClass().isArray()) {
                Class<?> type = v.getClass().getComponentType();
                if (type == int.class) vstr = asJsonArray((int[]) v);
                else if (type == byte.class) vstr = asJsonArray((byte[]) v);
                else if (type == double.class) vstr = asJsonArray((double[]) v);
                else if (type == float.class) vstr = asJsonArray((float[]) v);
                else if (type == boolean.class) vstr = asJsonArray((boolean[]) v);
                else if (type == long.class) vstr = asJsonArray((long[]) v);
                else if (type == short.class) vstr = asJsonArray((short[]) v);
                else if (!type.isPrimitive()) vstr = asJsonArray((Object[]) v);
                else throw new XRE("Type %s is not supported", type);
                vstr = "[" + vstr + "]";
            }
            if (v instanceof Optional) {
                Optional opt = (Optional) v;
                if (opt.isPresent()) {
                    buf.add(unmap(asString(k, opt.get())));
                } else {
                    buf.add("\"" + k + "\": " + "\"Optional.empty\"");
                }
            } else {
                buf.add("\"" + k + "\": " + vstr);
            }
        }
        return "{ " + buf.toString() + " }";
    }

    private static String jsonToString(Object v) {
        if (v instanceof Number) {
            String r = format.format(v);
            if (isNegativeZeroDisabled && Objects.equals(r, "-0")) r = "0";
            return r;
        }
        return Objects.toString(v);
    }

    /**
     * Round all numbers in output JSON to N decimal places. By default it is equal to -1 so numbers
     * are not rounded and included into JSON as is.
     */
    public static void setLimitDecimalPlaces(int n) {
        format.setMaximumFractionDigits(n == -1 ? MAX_FRACTION_LEN : n);
    }

    /**
     * Java allows negative zero. With this method you may disable it in the final JSON so that it
     * will be replaced with positive zero instead.
     */
    public static void setNegativeZero(boolean isEnabled) {
        isNegativeZeroDisabled = !isEnabled;
    }

    private static String asJsonArray(byte[] a) {
        StringBuilder buf = new StringBuilder();
        for (byte i : a) {
            buf.append("\"" + jsonToString(i) + "\", ");
        }
        if (buf.length() != 0) {
            buf.setLength(buf.length() - 2);
        }
        return buf.toString();
    }

    private static String asJsonArray(Object[] a) {
        StringBuilder buf = new StringBuilder();
        for (Object i : a) {
            String s = jsonToString(i);
            if (!(i instanceof Number)) s = quote(s);
            buf.append(s + ", ");
        }
        if (buf.length() != 0) {
            buf.setLength(buf.length() - 2);
        }
        return buf.toString();
    }

    private static String asJsonArray(long[] a) {
        StringBuilder buf = new StringBuilder();
        for (long i : a) {
            buf.append(jsonToString(i) + ", ");
        }
        if (buf.length() != 0) {
            buf.setLength(buf.length() - 2);
        }
        return buf.toString();
    }

    private static String asJsonArray(boolean[] a) {
        StringBuilder buf = new StringBuilder();
        for (boolean i : a) {
            buf.append("\"" + jsonToString(i) + "\", ");
        }
        if (buf.length() != 0) {
            buf.setLength(buf.length() - 2);
        }
        return buf.toString();
    }

    private static String asJsonArray(double[] a) {
        StringBuilder buf = new StringBuilder();
        for (double i : a) {
            buf.append(jsonToString(i) + ", ");
        }
        if (buf.length() != 0) {
            buf.setLength(buf.length() - 2);
        }
        return buf.toString();
    }

    private static String asJsonArray(float[] a) {
        StringBuilder buf = new StringBuilder();
        for (double i : a) {
            buf.append(jsonToString(i) + ", ");
        }
        if (buf.length() != 0) {
            buf.setLength(buf.length() - 2);
        }
        return buf.toString();
    }

    private static String asJsonArray(int[] a) {
        StringBuilder buf = new StringBuilder();
        for (int i : a) {
            buf.append(jsonToString(i) + ", ");
        }
        if (buf.length() != 0) {
            buf.setLength(buf.length() - 2);
        }
        return buf.toString();
    }

    private static String asJsonArray(short[] a) {
        StringBuilder buf = new StringBuilder();
        for (int i : a) {
            buf.append(jsonToString(i) + ", ");
        }
        if (buf.length() != 0) {
            buf.setLength(buf.length() - 2);
        }
        return buf.toString();
    }

    private static String asJsonArray(Collection<?> collection) {
        StringJoiner buf = new StringJoiner(", ");
        for (Object item : collection) {
            String s = jsonToString(item);
            if (!(item instanceof Number)) s = quote(s);
            buf.add(s);
        }
        return buf.toString();
    }

    private static String quote(String value) {
        if (value.equals("null")) return value;
        if (value.isEmpty() || value.charAt(0) != '{') return XUtils.quote(value);
        return value;
    }

    /**
     * Build a JSON from a given pair of objects (k1, v1, k2, v2, ...). String for each object is
     * obtained by calling toString on the object itself.
     *
     * <p>All pairs will be processed in lexicographical order of their keys.
     */
    public static <V> String asString(Map<?, ?> m) {
        if (m instanceof LinkedHashMap) {
            Object[] pairs = new Object[m.size() * 2];
            int i = 0;
            for (Entry<?, ?> e : m.entrySet()) {
                pairs[i++] = jsonToString(e.getKey());
                pairs[i++] = e.getValue();
            }
            return asString(pairs);
        } else {
            return asString(
                    m.entrySet().stream()
                            .map(e -> e.getValue() == null ? Map.entry(e.getKey(), "null") : e)
                            .collect(toMap(e -> jsonToString(e.getKey()), Entry::getValue))
                            .entrySet()
                            .stream()
                            .sorted(Entry.comparingByKey())
                            .flatMap(e -> Stream.<Object>of(e.getKey(), e.getValue()))
                            .toArray());
        }
    }

    /**
     * Merges multiple JSONs into one.
     *
     * <p>Useful when you override toString on a subclass which returns JSON. In that case you can
     * use this method to merge it with JSON produced from its super class.
     *
     * <p>Example:
     *
     * <pre>{@code
     * return XJson.merge(
     *     super.toString(),
     *     XJson.asString(
     *         "k1", "v1",
     *         "k2", "v2"));
     * }</pre>
     */
    public static String merge(String... jsons) {
        String json = "";
        if (jsons.length == 0) return json;
        List<String> list = Arrays.stream(jsons).map(String::trim).collect(toList());
        if (list.size() == 1) return list.get(0);
        json = list.get(0);
        for (int i = 1; i < jsons.length; ++i) {
            json = json.substring(0, json.length() - 1);
            json = json.trim();
            json += ",";
            json += jsons[i].substring(1);
        }
        return json;
    }

    /** Strip "{" and "}" */
    private static String unmap(String json) {
        if (json.isEmpty()) return json;
        if (json.charAt(0) != '{') return json;
        return json.substring(1, json.length() - 1).strip();
    }
}
