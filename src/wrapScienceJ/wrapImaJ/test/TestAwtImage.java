/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: TestAwtImage.java                                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.io.stream.FileHelper;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.core.ImageCore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * Allows to test image conversions from ImageCore slices to regular AWT images in Java.
 * This is especially useful to load or export image slices ad regular image files (e.g. PNG)
 * or to use AWT features such as drawing into an image.
 * 
 * @see wrapScienceJ.factory.image.ImageCoreFactory#createFromAwtImages
 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert
 * @see TestImageDraw
 * 
 */
public class TestAwtImage {
	
	static String[] regularImageDriectory = {
			"regularImageFormat" + File.separator + "gray8"  + File.separator,
			"regularImageFormat" + File.separator + "gray16" + File.separator		
									 };
	
	/**
	 * Allows to load an AWT image from a path to an image file.
	 * @param imageFile Abstract pathname to an image
	 * @return The image loaded from the file.
	 * @throws IOException In case of failure to read the image.
	 */
	static BufferedImage loadAwtImage(File imageFile) throws IOException{
		
		BufferedImage img = ImageIO.read(imageFile);
		
		return img;
	}
	
	/**
	 * Allows to save an AWT image to an image file.
	 * @param img The input AWT image to save.
	 * @param imageFile Abstract pathname to an image file where to save the file
	 * @throws IOException In case of failure to read the image.
	 */
	public static void saveAwtImage(BufferedImage img, File imageFile) throws IOException{
		
		ImageIO.write(img, "png", imageFile);
	}
	
	
	/**
	 * Allows to load an array with all the PNG images contained in a given directory
	 * The search for images inside the directory is not recursive.
	 * 
	 * @param sourceImageDirectory Path to a directory containing (hopefully) PNG images
	 * @return An array with all the images loaded from the PNG file directly inside the directory.
	 * @throws IOException In case the path does not lead to a directory or
	 * 					   failure to read an image.
	 */
	public static BufferedImage[] loadPngImagesInDirectory(String sourceImageDirectory) throws IOException {
		ArrayList<BufferedImage> imagesList = new ArrayList<BufferedImage>();
		File dir = new File(sourceImageDirectory);
		File[] files = dir.listFiles();
		ArrayList<String> fileNames = new ArrayList<String>();
		if (files != null) {
			for (File child : files) {
				if (child.getName().toLowerCase().endsWith(".png")){
					fileNames.add(child.getName());
				}
			}
		}else{
			throw new IOException("The input path " + sourceImageDirectory + " is not a directory");
		}
		
		fileNames.sort(new Comparator<String>() {
			/**
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(String string0, String string1) {
				return string0.compareToIgnoreCase(string1);
			}
		});
		
		for (String fileName: fileNames) {
			imagesList.add(loadAwtImage(new File(sourceImageDirectory + 
												 File.separator + 
												 fileName
												)
										)
						  );
		}
		
		System.err.println("directory : " + sourceImageDirectory);
		BufferedImage[] result = imagesList.toArray(new BufferedImage[imagesList.size()]);
		
		System.err.println("Number of PNG Images : " + result.length);
		
		return result;
	}
	
	/**
	 * Validates the construction of an ImageCore from AWT images.
	 * Constructs an ImageCore and displays it
	 * 
	 * @param sourceImageDirectory Path to a directory containing (hopefully) PNG images
	 */
	public static void testLoadFromAwtImages(String sourceImageDirectory){
		try{
			ImageCore image = ImageCoreFactoryIJ.getInstance()
												.createFromAwtImages(
														loadPngImagesInDirectory(sourceImageDirectory)
														);

			GlobalOptions.getDefaultRenderTool().display(image);

		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Validates the construction of an ImageCore from AWT images.
	 * Constructs an ImageCore and displays it
	 * 
	 * @param inputImageFile A sample image to load.
	 */
	public static void testGetSliceAsAwtImage(String inputImageFile){
		try{

			ImageCore image = ImageCoreFactoryIJ.getInstance()
												.getImageCore(
													inputImageFile,
													false,
													true
												);

			BufferedImage buffImg = image.getImageConvert().getSliceAsAwtImage(16);
			saveAwtImage(buffImg, new File(FileHelper.changeDirectoryPostfixAndExtension(
										   TestImageThresholding.getSampleImage(0),
										   TestImageThresholding.getTestOutputDir(),
										   "_slice16", "png")
										  )
						);

			System.err.println("PNG file written in the directory " +
							   TestImageThresholding.getTestOutputDir());
			GlobalOptions.getDefaultRenderTool().display(image);

		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Validates the construction of an ImageCore from AWT images.
	 * Constructs an ImageCore and displays it
	 * 
	 * @param inputImageFile A sample image to load.
	 */
	public static void testSetSliceFromAwtImage(String inputImageFile){
		try{
			ImageCore image = ImageCoreFactoryIJ.getInstance()
												.getImageCore(
													inputImageFile,
													false,
													true
												);
			BufferedImage buffImg = image.getImageConvert().getSliceAsAwtImage(16);

			
			for (int z=0 ; z<image.getDepth() ; z++){
				image.getImageConvert().setSliceFromAwtImage(buffImg, z);
			}
			
			System.err.println("PNG file written in the directory " +
							   TestImageThresholding.getTestOutputDir());
			GlobalOptions.getDefaultRenderTool().display(image);

		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Validates the initialization of an ImageCore from AWT images.
	 * Constructs an ImageCore and displays it
	 * 
	 * @param inputImageFile A sample image to load.
	 */
	public static void testInitializeFromAwtImages(String inputImageFile){
		try{
			ImageCore image = ImageCoreFactoryIJ.getInstance()
												.getImageCore(
													inputImageFile,
													false,
													true
												);
			BufferedImage buffImg = image.getImageConvert().getSliceAsAwtImage(16);

			
			BufferedImage[] arrayBuffImages = new BufferedImage[image.getDepth()];
			
			for (int z=0 ; z<image.getDepth() ; z++){
				arrayBuffImages[z] = buffImg;
			}
			
			image.getImageConvert().initializeFromAwtImages(arrayBuffImages);
			
			GlobalOptions.getDefaultRenderTool().display(image);

		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Gray8 images
		//testLoadFromAwtImages(TestCoreMethods.getSampleDataDir()+regularImageDriectory[0]);
		
		// Gray16 images
		testLoadFromAwtImages(TestImageThresholding.getSampleDataDir()+regularImageDriectory[1]);
		
		//testGetSliceAsAwtImage(TestCoreMethods.getSampleImage(0));
		
		//testSetSliceFromAwtImage(TestCoreMethods.getSampleImage(0));
		
		//testInitializeFromAwtImages(TestCoreMethods.getSampleImage(0));
		
		System.out.println(ResourcesMonitor.getRessourceInfo());

		System.out.println("The program ended normally.");
	}

}
