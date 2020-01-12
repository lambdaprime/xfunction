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
import java.util.Set;

/**
 * Persistent store for Serializable objects.
 * All objects are stored in a file you specify.
 * Useful in case you need some simple persistent
 * and don't have time to deal with DB (JPA) etc.
 */
public class ObjectsStore<T extends Serializable> {

	private Path store;
	private Set<T> entities;

	private ObjectsStore(Path store, Set<T> entities) {
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
		try (FileOutputStream f = new FileOutputStream(store.toFile());
				ObjectOutputStream o = new ObjectOutputStream(f);) {
			o.writeObject(entities);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
	public static <T extends Serializable> ObjectsStore<T> load(Path store) {
		File file = store.toFile();
		if (!file.exists())
			return new ObjectsStore<T>(store, new HashSet<>());
		try (FileInputStream fi = new FileInputStream(store.toFile());
				ObjectInputStream oi = new ObjectInputStream(fi);) {
			Set<T> entities = (Set<T>) oi.readObject();
			return new ObjectsStore(store, entities);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}