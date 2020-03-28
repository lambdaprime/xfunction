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

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Persistent store for Serializable objects.
 * All objects are stored in a file you specify.
 * Useful in case you need some simple persistent
 * and don't have time to deal with DB (JPA) etc.
 */
public class ObjectsStore<T extends Serializable> {

	private Optional<Path> store;
	private Set<T> entities;

	private ObjectsStore(Optional<Path> store, Set<T> entities) {
		this.store = store;
		this.entities = entities;
	}

	/**
	 * Add new object to a store
	 */
	public void add(T entity) {
		entities.add(entity);
		save();
	}

    /**
     * Update existing object
     */
	public void update(T entity) {
		entities.remove(entity);
		entities.add(entity);
		save();
	}

	/**
     * Persist the store content to a file in which it is located
     */
	public void save() {
	    store.ifPresent(path -> {
    		try (FileOutputStream f = new FileOutputStream(path.toFile());
    				ObjectOutputStream o = new ObjectOutputStream(f);) {
    			o.writeObject(entities);
    		} catch (Exception e) {
    			throw new RuntimeException(e);
    		}
	    });
	}

	/**
	 * Retrieve all objects from a store
	 */
	public List<T> findAll() {
		return entities.stream()
				.collect(toList());
	}

	/**
	 * Loads an ObjectsStore from a given file
	 */
	@SuppressWarnings("unchecked")
    public static <T extends Serializable> ObjectsStore<T> load(Path path) {
	    Optional<Path> store = Optional.of(path);
		File file = path.toFile();
		if (!file.exists())
			return new ObjectsStore<T>(store, new HashSet<>());
		try (FileInputStream fi = new FileInputStream(file);
				ObjectInputStream oi = new ObjectInputStream(fi);) {
			Set<T> entities = (Set<T>) oi.readObject();
			return new ObjectsStore<>(store, entities);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
     * Creates in memory ObjectsStore. Useful in tests for mocking file version.
     */
    public static <T extends Serializable> ObjectsStore<T> create(Set<T> entities) {
        return new ObjectsStore<>(Optional.empty(), entities);
    }
}