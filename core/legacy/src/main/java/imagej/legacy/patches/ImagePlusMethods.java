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

import ij.ImagePlus;
import ij.WindowManager;
import imagej.legacy.ImageJ2Bridge;
import imagej.legacy.LegacyOutputTracker;
import imagej.legacy.Utils;

/**
 * Overrides {@link ImagePlus} methods.
 * 
 * @author Curtis Rueden
 * @author Barry DeZonia
 */
public final class ImagePlusMethods {

	private ImagePlusMethods() {
		// prevent instantiation of utility class
	}

	/** Appends {@link ImagePlus#updateAndDraw()}. */
	public static void updateAndDraw(final ImageJ2Bridge bridge, final ImagePlus obj) {
		if (obj == null) return;
		if (!obj.isProcessor()) return;
		if (obj.getWindow() == null) return;
		if (!bridge.isLegacyMode()) {
			if (!Utils.isLegacyThread(Thread.currentThread())) return;
			bridge.debug("ImagePlus.updateAndDraw(): " + obj);
		}
		try {
			bridge.registerLegacyImage(obj);
		} catch (UnsupportedOperationException e) {
			// ignore: the dummy legacy service does not have an image map
		}
		// TODO - add here too?
		//WindowManager.setCurrentWindow(obj.getWindow());
	}

	/** Appends {@link ImagePlus#repaintWindow()}. */
	public static void repaintWindow(final ImageJ2Bridge bridge, final ImagePlus obj) {
		if (obj == null) return;
		if (obj.getWindow() == null) return;
		if (!bridge.isLegacyMode()) {
			if (!Utils.isLegacyThread(Thread.currentThread())) return;
			bridge.debug("ImagePlus.repaintWindow(): " + obj);
		}
		try {
			bridge.registerLegacyImage(obj);
		} catch (UnsupportedOperationException e) {
			// ignore: the dummy legacy service does not have an image map
		}
		// TODO - add here too?
		//WindowManager.setCurrentWindow(obj.getWindow());
	}

	/** Appends {@link ImagePlus#show(String message)}. */
	public static void show(final ImageJ2Bridge bridge, final ImagePlus obj,
		@SuppressWarnings("unused") final String message)
	{
		if (obj == null) return;
		if (!bridge.isLegacyMode()) {
			if (!Utils.isLegacyThread(Thread.currentThread())) return;
			bridge.debug("ImagePlus.show(): " + obj);
		}
		try {
			bridge.registerLegacyImage(obj);
		} catch (UnsupportedOperationException e) {
			// ignore: the dummy legacy service does not have an image map
		}
		WindowManager.setCurrentWindow(obj.getWindow());
	}

	/** Appends {@link ImagePlus#hide()}. */
	public static void hide(final ImageJ2Bridge bridge, final ImagePlus obj) {
		if (bridge.isLegacyMode()) return;
		if (obj == null) return;
		if (!bridge.isLegacyMode() && !Utils.isLegacyThread(Thread.currentThread())) return;
		bridge.debug("ImagePlus.hide(): " + obj);
		LegacyOutputTracker.removeOutput(obj);
		// Original method
		//LegacyOutputTracker.getClosedImps().add(obj);
		// Alternate method
		// begin alternate
		try {
				bridge.unregisterLegacyImage(obj);
		} catch (UnsupportedOperationException e) {
			// ignore: the dummy legacy service does not have an image map
		}
		// end alternate
	}

	/** Appends {@link ImagePlus#close()}. */
	// TODO: LegacyOutputTracker should not be a singleton
	public static void close(final ImageJ2Bridge bridge, final ImagePlus obj) {
		if (obj == null) return;
		if (!bridge.isLegacyMode() && !Utils.isLegacyThread(Thread.currentThread())) return;
		LegacyOutputTracker.addClosed(obj);
	}
}
