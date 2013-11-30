package imagej.legacy.bridge;

import ij.IJ;
import imagej.legacy.ImageJ1Bridge;

/**
 * The default implementation of the {@link ImageJ1Bridge}.
 *
 * @author Johannes Schindelin
 */
public class DefaultImageJ1Bridge implements ImageJ1Bridge {

	@Override
	public Object map(Object data) {
		return Mapping.map(data);
	}

	@Override
	public void run(Object image, String commandName, String optionsString) {
		System.err.println("Hello, " + image + " " + commandName);
		/* IJ.run(image, commandName, optionsString); */
	}

	@Override
	public void evalMacro(String macroCode, String argument) {
		IJ.runMacro(macroCode, argument);
	}

}
