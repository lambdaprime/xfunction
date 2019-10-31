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
 * Set of functions for implementing currying in Java
 */
public class Curry {

    public static <T, E extends Exception> ThrowingRunnable<E> curry(
            ThrowingConsumer<T, E> consumer, T v) {
        return () -> consumer.accept(v);
    }

}
