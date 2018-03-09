/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  TestImageContrast.java                                            * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.test;

import java.io.IOException;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.ImageThresholding;

/**
 * @author remy
 * 
 * Allows to test classical operations on contrast and brightness.
 *
 * @see wrapScienceJ.wrapImaJ.core.operation.ImageContrast
 */
public class TestImageContrast {

	
	/**
	 * Validates image contrast and brightness changes through Histogram generation and
	 * visualization. The histogram of the scaled image is smoothed to avoid ragged
	 * variations.
	 * @param inputImageFile the source file for image data.
	 * @param outputFile1 output text file for the histogram before contrast operation.
	 * @param outputFile2 output text file for the histogram after contrast operation.
	 * @param constrastFactor parameter (between 0 and 1) to enhance contrast
	 * @param brightnessFactor parameter (between 0 and 1) to enhance brightness
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageContrast#adjustContrastBrightness(double, double)
	 */
	public static void testContrastBrightnessHistogram(String inputImageFile,
												String outputFile1,
												String outputFile2,
												double constrastFactor, 
												double brightnessFactor) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile,
														  false, // Convert to 8 bits
														  false, // Maximize values range
														  RetrievalPolicy.Unspecified
											);
		
		image.getImageThresholding().getHistogram().writeToFile(outputFile1);	
		
		ImageCore copy = image.duplicate();
		copy.setTitle("Adjusted image (contrast : "+ constrastFactor +
					  ", Brightness: " + brightnessFactor);
		copy.getImageContrast()
			.adjustContrastBrightness(constrastFactor, brightnessFactor);
		
		GlobalOptions.getDefaultRenderTool().display(image);
		GlobalOptions.getDefaultRenderTool().display(copy);

	}	
	
	
	/**
	 * Validates image colors scaling through Histogram generation and visualization.
	 * The histogram of the scaled image is smoothed to avoid ragged variations.
	 * 
	 * @param inputImageFile the source file for image data.
	 * @param outputFile1 output text file for the histogram before contrast operation.
	 * @param outputFile2 output text file for the histogram after contrast operation.
	 * @param min The minimum value of the display range that remains, between 0.0 and 1.0. 
	 * @param max The maximum value of the display range that remains, between 0.0 and 1.0. 
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageContrast#adjustValuesRange(double, double)
	 */
	public static void testAdjustValuesRangeHistogram(String inputImageFile,
												String outputFile1,
												String outputFile2,
												double min, 
												double max) throws IOException{

		ImageThresholding image = ImageCoreFactoryIJ.getInstance()
													.getImageCore(inputImageFile,
																  false, // Convert to 8 bits
																  false, // Maximize values range
																  RetrievalPolicy.Unspecified
													).getImageThresholding();
		
		image.getHistogram().writeToFile(outputFile1);	
		
		ImageCore copy = image.duplicate();
		copy.setTitle("Adjusted image (min : "+ min +
					  ", max: " + max);
		copy.getImageContrast().adjustValuesRange(min, max);
		copy.getImageThresholding().getHistogram().writeToFile(outputFile2);
		
		GlobalOptions.getDefaultRenderTool().display(image);
		GlobalOptions.getDefaultRenderTool().display(copy);

	}
	
	
	/**
	 * Validates image contrast and brightness changes
	 * @param inputImageFile
	 * @param outputFile output image file where to save the modified image after contrast operation.
	 * @param constrastFactor parameter (between 0 and 1) to enhance contrast
	 * @param brightnessFactor parameter (between 0 and 1) to enhance brightness
	 * @throws IOException 
	 */	 
	public static void testContrastBrightnessWriteToFile(String inputImageFile, 
												  String outputFile,
												  double constrastFactor, 
												  double brightnessFactor) throws IOException{

		ImageThresholding image = ImageCoreFactoryIJ.getInstance()
													.getImageCore( inputImageFile)
													.getImageThresholding();		
		image.getImageContrast().adjustContrastBrightness(constrastFactor, brightnessFactor);
		image.writeToFile(outputFile);

	}
	
	
	/**
	 * Validates image thresholding 
	 * 
	 * @param inputImageFile
	 * @param min : the min threshold for display range
	 * @param max : the max threshold for display range
	 * @throws IOException 
	 */
	public static void testDisplayRange(String inputImageFile, int min, int max) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance().getImageCore(inputImageFile);
		
		image.getImageBlur().getBinomialBlur(8, 8, 2);

		GlobalOptions.getDefaultRenderTool().display(image);

	}
	
	
	/**
	 * Validates image thresholding 
	 * @param inputImageFile
     * @param contrastFactor ranges between 0.0 and 1.0, Increases or decreases image contrast by varying the width of the display range. The narrower the display range, the higher the contrast
     * @param brightnessFactor  ranges between 0.0 and 1.0, Increases or decreases image brightness by moving the display range
	 * @throws IOException 
	 */
	public static void testContrastBrightness(String inputImageFile, 
			double contrastFactor, 
			double brightnessFactor) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance().getImageCore(inputImageFile);
		image.getImageBlur().getBinomialBlur(8, 8, 2);
		
		GlobalOptions.getDefaultRenderTool().display(image);
		
		System.out.println("Press any key to continue...");
		System.in.read();
		
		image.getImageContrast().adjustContrastBrightness(contrastFactor, brightnessFactor);
		
		GlobalOptions.getDefaultRenderTool().display(image);

	}
	
	
	/**
	 * Main function to run the tests in this class.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		//testDisplayRange(getSampleImageGray8(0), 0, 255);
		//testContrastBrightness(getSampleImageGray8(0), 0.7d, 0.75d);
		//testContrastBrightnessHistogram(getSampleImageGray8(0), 
		//								getTestOutputDir()+"histogram_Exp5.dat", 
		//								getTestOutputDir()+"histogram_Exp5_contrast.dat",
		//								0.7d, 0.25d);
		
		//testAdjustValuesRangeHistogram(getSampleImageGray8(0), 
		//		getTestOutputDir()+"histogram_Exp5.dat", 
		//		getTestOutputDir()+"histogram_Exp5_contrast.dat",
		//		0.25d, 0.7d);

	}
}
