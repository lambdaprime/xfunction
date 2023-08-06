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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Persistent store for Serializable object. Object is stored in a file you specify. Later you can
 * restore it back. Useful in case you need some simple persistent and don't have time to deal with
 * DB (JPA), or adding dependency to some Json library etc. The drawback of this store is that you
 * would not be able manually to modify the object values in the file after it is stored.
 *
 * <p>If you want to store Collection object make sure to parameterize it with collection
 * implementation which implements Serializable and not with Collection interface. For example you
 * cannot store Set but you can store HashSet since Set does not implement Serializable and HashSet
 * does.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class ObjectStore<T extends Serializable> {

    private Path path;

    /** Create a store and use following file */
    public ObjectStore(Path file) {
        this.path = file;
    }

    /** Persist given object into current store */
    public void save(T obj) {
        try (FileOutputStream f = new FileOutputStream(path.toFile());
                ObjectOutputStream o = new ObjectOutputStream(f); ) {
            o.writeObject(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Loads object from a current store */
    @SuppressWarnings("unchecked")
    public Optional<T> load() {
        File file = path.toFile();
        if (!file.exists()) return Optional.empty();
        try (FileInputStream fi = new FileInputStream(file);
                ObjectInputStream oi = new ObjectInputStream(fi); ) {
            return Optional.of((T) oi.readObject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
