package imagej.legacy;

import ij.ImagePlus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.scijava.util.ClassUtils;

public class LegacyClassLoader extends URLClassLoader {
	private Map<String, Class<?>> knownClasses = new HashMap<String, Class<?>>();

	public LegacyClassLoader() {
		super(getImageJ1Jar(), determineParent());
		for (Class<?> clazz : new Class[] {
			ImageJ1Bridge.class, ImageJ2Bridge.class
		}) {
			knownClasses.put(clazz.getName(), clazz);
		}
	}

	@Override
	public Class<?> loadClass(final String className) throws ClassNotFoundException {
if (className.contains("imagej")) { System.err.println(className); }
		Class<?> result = knownClasses.get(className);
		if (result != null) {
			return result;
		}
		if (className.startsWith("imagej.legacy.")) {
System.err.println("defining class " + className);
			result = defineClass(className);
			knownClasses.put(className, result);
			return result;
		}
		}
		return super.loadClass(className);
	}

	private static ClassLoader determineParent() {
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		for (;;) try {
			if (loader.loadClass("ij.IJ") == null) {
				return loader;
			}
			loader = loader.getParent();
			if (loader == null) {
				throw new RuntimeException("Cannot find bootstrap class loader");
			}
		} catch (ClassNotFoundException e) {
			return loader;
		}
	}

	private Class<?> defineClass(String className) throws ClassNotFoundException {
		String path = "/" + className.replace('.', '/') + ".class";
		URL resource = getClass().getResource(path);
		if (resource == null) {
			throw new ClassNotFoundException(className);
		}
		try {
			InputStream in = resource.openStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[16384];
			for (;;) {
				int count = in.read(buffer);
				if (count < 0) {
					break;
				}
				out.write(buffer, 0, count);
			}
			in.close();
			out.close();
			byte[] array = out.toByteArray();
			return super.defineClass(className, array, 0, array.length);
		}
		catch (IOException e) {
			throw new ClassNotFoundException(className, e);
		}
	}

	private static URL[] getImageJ1Jar() {
		return new URL[] { ClassUtils.getLocation(ImagePlus.class) };
	}
}
