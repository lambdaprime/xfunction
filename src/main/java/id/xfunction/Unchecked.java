/* 
 * This source file is a part of xfunction module.
 * Description for this  project/command/program can be found in README.org
 *
 * xfunction is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * xfunction is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with xfunction. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package id.xfunction;

/**
 * This class allows you to execute functions which throw checked exceptions in a way if they were
 * throwing unchecked RuntimeException.<br/>
 * 
 * For example given method which throws checked Exception:<br/>
 * 
 * <pre>
 * int m() throws Exception {
 *     return 0;
 * }
 * 
 * </pre>
 * 
 * Instead of writing:<br/>
 * 
 * <pre>
 * try { m(); } catch (Exception e) { throw new RuntimeException(e); }
 * 
 * </pre>
 * 
 * You can use this class:<br/>
 * 
 * <pre>
 * Unchecked.run(this::m);
 * 
 * </pre>
 * 
 */
public class Unchecked {

    public static <R, E extends Exception> R run(ThrowingSupplier<R, E> s) {
        try {
            return s.run();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <E extends Exception> void run(ThrowingRunnable<E> s) {
        try {
            s.run();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

