package imagej.legacy.bridge;

import ij.process.ImageProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapping<T> {
	private final static ClassLoader loader = DefaultImageJ1Bridge.class.getClassLoader();
	private static Map<Class<?>, Mapping<?>> mappings = new HashMap<Class<?>, Mapping<?>>();

	private Class<T> clazz;
	private Field[] fields;
	private Field bridgedField;
	private Map<Class<?>, Field[]> fieldsMap = new HashMap<Class<?>, Field[]>();

	public static<T> void register(Class<T> clazz) {
		mappings.put(clazz, new Mapping<T>(clazz));
	}

	public static Object map(Object data) {
		if (data == null) {
			return null;
		}
		return getMapping(data.getClass()).realMap(data);
	}

	private static synchronized Mapping<?> getMapping(Class<?> clazz) {
		Mapping<?> mapping = mappings.get(clazz);
		if (mapping != null) {
			return mapping;
		}
		try {
			Class<?> ourClazz = loader.loadClass(clazz.getName());
			mapping = mappings.get(ourClazz);
			if (mapping == null) {
				throw new RuntimeException("Cannot map data of class " + clazz.getName());
			}
			mappings.put(clazz, mapping);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return mapping;
	}

	private Mapping(Class<T> clazz) {
		this.clazz = clazz;
		List<Field> fields = new ArrayList<Field>();
		for (Field field : clazz.getDeclaredFields()) {
			if ((field.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) != 0) {
				continue; 
			}
			String name = field.getName();
			if (name.equals("_bridged")) {
				field.setAccessible(true);
				bridgedField = field;
				continue;
			}
			if (ImageProcessor.class.isAssignableFrom(clazz) && name.equals("snapshotPixels")) {
				continue;
			}
			field.setAccessible(true);
			fields.add(field);
		}
		this.fields = fields.toArray(new Field[fields.size()]);
	}

	private T realMap(Object o) {
		Field[] otherFields = getFields(o.getClass());
		try {
			Constructor<T> ctor = clazz.getConstructor();
			T result = ctor.newInstance();
			if (bridgedField != null) {
				bridgedField.set(result, o);
			}
			for (int i = 0; i < fields.length; i++) {
				fields[i].set(result, otherFields[i].get(o));
			}
			return result;
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		catch (SecurityException e) {
			throw new RuntimeException(e);
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private synchronized Field[] getFields(Class<?> clazz) {
		Field[] result = fieldsMap.get(clazz);
		if (result != null) {
			return result;
		}
		if (!clazz.getName().equals(this.clazz.getName())) {
			throw new RuntimeException("Cannot map " + clazz.getName() + " to " + this.clazz.getName());
		}
		result = new Field[fields.length];
		for (int i = 0; i < fields.length; i++) try {
			result[i] = clazz.getDeclaredField(fields[i].getName());
			result[i].setAccessible(true);
		}
		catch (SecurityException e) {
			throw new RuntimeException(e);
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		fieldsMap.put(clazz, result);
		return result;
	}
}
