package imagej.legacy;

public interface ImageJ1Bridge {

	/**
	 * Maps certain ImageJ 1.x data structure instances between different class
	 * loaders.
	 * <p>
	 * Due to ImageJ 1.x' design, there is no context. the {@link ij.IJ} class
	 * *is* the context. Since we do not want to limit ourselves to a singleton
	 * with ImageJ2, we use a trick: the ImageJ 1.x is encapsulated in its own
	 * class loader.
	 * </p>
	 * <p>
	 * That poses a problem when interacting with ImageJ 1.x, though: the data
	 * structures inside that custom class loader use classes that are
	 * incompatible with classes loaded in any other class loader.
	 * </p>
	 * <p>
	 * This method can be used to map a limited set of ImageJ 1.x data structures
	 * into data compatible with the current ClassLoader.
	 * </p>
	 * 
	 * @param data the data to map
	 * @return the mapped data
	 */
	Object map(Object data);

	void run(Object /* ImagePlus */ image, String commandName, String optionsString);

	void evalMacro(String macroCode, String argument);
}