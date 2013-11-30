/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2013 Board of Regents of the University of
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package imagej.legacy;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.lang.reflect.Method;

public class Headless {
	public static void main(String... args) throws Exception {
		ClassLoader loader = new LegacyClassLoader();
		LegacyInjector injector = new LegacyInjector();
		injector.injectHooks(loader, true, null);
		Class<?> ij = loader.loadClass("ij.IJ");
		Method runMethod = ij.getMethod("runPlugIn", new Class[] { String.class, String.class });
		ImageJ1Bridge bridge = (ImageJ1Bridge)runMethod.invoke(null, new Object[] { ImageJ1Bridge.class.getName(), null });
		bridge.evalMacro("print(\"Hello, world!\");", null);
		ImageProcessor ip = new ByteProcessor(256, 256);
		Object ip2 = bridge.map(ip);
		System.err.println("ip: " + ip + "; loader: " + ip.getClass().getClassLoader());
		System.err.println("ip2: " + ip2 + "; loader: " + ip2.getClass().getClassLoader());
	}
}
