/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2014 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package imagej.legacy.patches;

import ij.io.PluginClassLoader;
import imagej.legacy.ImageJ2Bridge;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Augments {@link PluginClassLoader} methods.
 * 
 * Make sure that the jars/ directory's .jar files are added to the classpath.
 * 
 * @author Johannes Schindelin
 */
@SuppressWarnings("javadoc")
public final class PluginClassLoaderMethods {

	private PluginClassLoaderMethods() {
		// prevent instantiation of utility class
	}

	private static Method addURLMethod;

	private static void addURL(final ImageJ2Bridge bridge, final PluginClassLoader loader, final URL url) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		synchronized(PluginClassLoaderMethods.class) {
			if (addURLMethod == null) {
				try {
					addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
				} catch (SecurityException e) {
					bridge.error(e);
				} catch (NoSuchMethodException e) {
					bridge.error(e);
				}
				addURLMethod.setAccessible(true);
			}
		}
		addURLMethod.invoke(loader, url);
	}

	/** Appends {@link PluginClassLoader#init(String)}. */
	public static void init(final ImageJ2Bridge bridge, final PluginClassLoader obj, final String path) {
		final File pluginsDirectory = new File(path);
		if (!pluginsDirectory.getName().equals("plugins")) return;
		final File ij1Directory = pluginsDirectory.getParentFile();
		if (ij1Directory != null) addJars(bridge, obj, new File(ij1Directory, "jars"));
	}

	protected static void addJars(final ImageJ2Bridge bridge, final PluginClassLoader obj, final File directory) {
		final File[] list = directory.listFiles();
		if (list == null) return;
		for (final File file : list) {
			if (file.isDirectory()) addJars(bridge, obj, file);
			else if (file.getName().endsWith(".jar")) addJar(bridge, obj, file);
		}
	}

	protected static void addJar(final ImageJ2Bridge bridge, final PluginClassLoader obj, final File jar) {
		try {
			addURL(bridge, obj, jar.toURI().toURL());
		} catch (IllegalArgumentException e) {
			bridge.error(e);
		} catch (MalformedURLException e) {
			bridge.error(e);
		} catch (IllegalAccessException e) {
			bridge.error(e);
		} catch (InvocationTargetException e) {
			bridge.error(e);
			bridge.error(e.getCause());
		}
	}
}
