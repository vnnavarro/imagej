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

import ij.ImagePlus;

/**
 * The {@link ImageJ2Bridge} encapsulating an active {@link LegacyService} for use within the patched ImageJ 1.x.
 * 
 * @author Johannes Schindelin
 */
public class LegacyImageJ2Bridge implements ImageJ2Bridge {

	private LegacyService legacyService;

	public LegacyImageJ2Bridge(LegacyService legacyService) {
		this.legacyService = legacyService;
	}

	@Override
	public boolean isLegacyMode() {
		return legacyService.isLegacyMode();
	}

	@Override
	public boolean isInitialized() {
		return legacyService.isInitialized();
	}

	@Override
	public void dispose() {
		legacyService.getContext().dispose();
	}

	@Override
	public void showProgress(int currentIndex, int finalIndex) {
		legacyService.status().showProgress(currentIndex, finalIndex);
	}

	@Override
	public void showStatus(String s) {
		legacyService.status().showStatus(s);
	}

	@Override
	public void registerLegacyImage(Object image) {
		// TODO: avoid using ImagePlus here altogether; use ImgLib2-ij's wrap() instead
		legacyService.getImageMap().registerLegacyImage((ImagePlus)image);
	}

	@Override
	public void unregisterLegacyImage(Object image) {
		// TODO: avoid using ImagePlus here altogether; use ImgLib2-ij's wrap() instead
		legacyService.getImageMap().unregisterLegacyImage((ImagePlus)image);
	}

	@Override
	public void debug(String string) {
		legacyService.log().debug(string);
	}

	@Override
	public void error(Throwable t) {
		legacyService.log().error(t);
	}

}
