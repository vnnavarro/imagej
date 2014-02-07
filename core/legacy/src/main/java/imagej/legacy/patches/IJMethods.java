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

import ij.IJ;
import imagej.legacy.ImageJ2Bridge;

import java.io.BufferedWriter;
import java.util.Date;

/**
 * Overrides {@link IJ} methods.
 * 
 * @author Curtis Rueden
 */
public class IJMethods {

	/** Resolution to use when converting double progress to int ratio. */
	private static final int PROGRESS_GRANULARITY = 1000;

	/** Appends {@link IJ#showProgress(double)}. */
	public static void showProgress(final ImageJ2Bridge bridge, final double progress) {
		if (bridge.isLegacyMode()) return;
		// approximate progress as int ratio
		final int currentIndex = (int) (PROGRESS_GRANULARITY * progress);
		final int finalIndex = PROGRESS_GRANULARITY;
		showProgress(bridge, currentIndex, finalIndex);
	}

	/** Appends {@link IJ#showProgress(int, int)}. */
	public static void
		showProgress(final ImageJ2Bridge bridge, final int currentIndex, final int finalIndex)
	{
		if (bridge.isLegacyMode()) return;
		bridge.debug("showProgress: " + currentIndex + "/" + finalIndex);
		// report progress through global event mechanism
		bridge.showProgress(currentIndex, finalIndex);
	}

	/** Appends {@link IJ#showStatus(String)}. */
	public static void showStatus(final ImageJ2Bridge bridge, final String s) {
		if (bridge.isLegacyMode()) return;
		bridge.debug("showStatus: " + s);
		if (!bridge.isInitialized()) {
			// suppress ImageJ1 bootup messages
			return;
		}
		// report status through global event mechanism
		bridge.showStatus(s);
	}

	// if the ij.log.file property is set, log every message to the file pointed to
	private static BufferedWriter logFileWriter;

	public static void log(@SuppressWarnings("unused") final ImageJ2Bridge bridge, String message) {
		if (message != null) {
			String logFilePath = System.getProperty("ij.log.file");
			if (logFilePath != null) {
				try {
					if (logFileWriter == null) {
						java.io.OutputStream out = new java.io.FileOutputStream(
								logFilePath, true);
						java.io.Writer writer = new java.io.OutputStreamWriter(
								out, "UTF-8");
						logFileWriter = new java.io.BufferedWriter(writer);
						logFileWriter.write("Started new log on " + new Date() + "\n");
					}
					logFileWriter.write(message);
					if (!message.endsWith("\n"))
						logFileWriter.newLine();
					logFileWriter.flush();
				} catch (Throwable t) {
					t.printStackTrace();
					System.getProperties().remove("ij.log.file");
					logFileWriter = null;
				}
			}
		}
	}

}
