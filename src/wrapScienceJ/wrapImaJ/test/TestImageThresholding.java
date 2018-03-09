/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: TestImageThresholding.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.test;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.core.*;
import wrapScienceJ.wrapImaJ.core.operation.ImageContrast;
import wrapScienceJ.wrapImaJ.core.operation.ImageThresholding;
import wrapScienceJ.wrapImaJ.core.operation.ThresholdingOption;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy;

import java.io.File;
import java.io.IOException;

/**
 * @author Remy Malgouyres
 * 
 * Allows to test images' thresholding methods.
 * 
 * @see wrapScienceJ.wrapImaJ.core.operation.ImageThresholding
 */
public class TestImageThresholding {
	
	/**
	 * @return The directory where the sample data coming with the release and documentation
	 * should be placed.
	 */
	public static String getSampleDataDir(){
		return GlobalOptions.getDefaultInputDir()+"wrapSampleData"+File.separator;
	}

	/**
	 * @return The directory where the output of the tests on sample data within
	 * this package should be placed.
	 */
	public static String getTestOutputDir(){	
		return getSampleDataDir()+"outputFiles"+File.separator;
	}
	
	/**
	 * Allows to retrieve the path to a sample file with type image, gray levels, 8bits per voxel
	 * @param index The index of the sample image (as the might be a few of them...)
	 * @return A paths to a Gray Levels (8bits) sample image for tests.
	 */
	public static String getSampleImageGray8(int index){	
		return getSampleDataDir() + "outputFiles" + File.separator + m_testImages_8bits[index];
	}
	
	/**
	 * Allows to retrieve the path to a sample file with type image
	 * @param index The index of the sample image (as the might be a few of them...)
	 * @return A path to a sample image for tests.
	 */
	public static String getSampleImage(int index){	
		return getSampleDataDir()+m_testImages[index];
	}
	
	
	/**
	 * Collection of source image files.
	 */
	public static String[] m_testImages = {"crop_531_743_48_Exp_5_DAPI.tif",
										  "crop_733_525_45_Exp_5_DAPI.tif",
										  "crop_531_743_48_Exp6_DAPI.tif",
										  "crop_733_525_45_Exp6_DAPI.tif",
										  "Exp_5_DAPI.TIF", 
		                                  "Exp6_DAPI.TIF"};

	/**
	 * Collection of image files with type GRAY8.
	 */
	public static String[] m_testImages_8bits = {"crop_531_743_48_Exp_5_DAPI_8bits.tif",
												 "crop_733_525_45_Exp_5_DAPI_8bits.tif",
												 "crop_531_743_48_Exp6_DAPI_8bits.tif",
												 "crop_733_525_45_Exp6_DAPI_8bits.tif",
												 "Exp_5_DAPI_8bits.tif",
		                                         "Exp6_DAPI_8bits.tif"};
	
	/**
	 * Collection of image files with type GRAY8 and gray levels having maximal range for that type.
	 */
	public static String[] m_testImages_8bits_scaled = {
												"crop_531_743_48_Exp_5_DAPI_8bits_scaled.tif",
												"crop_733_525_45_Exp_5_DAPI_8bits_scaled.tif",
												"crop_531_743_48_Exp6_DAPI_8bits_scaled.tif",
												"crop_733_525_45_Exp6_DAPI_8bits_scaled.tif",
												"Exp_5_DAPI_8bits_scaled.tif",
		                                        "Exp6_DAPI_8bits_scaled.tif"};


	
	/**
	 * Validates image thresholding by applying a thresholding method after a blurring pass
	 * @param inputImageFile The input image path
	 * @param thresholdingMethod The predefined threshold calculation method
	 * @throws IOException 
	 * @see ThresholdingOption
	 * @see ImageThresholding#thresholdImageAndBinarize(ThresholdingOption, boolean)
	 */
	public static void testImageThresholdAndBinarize(String inputImageFile,
											  ThresholdingOption thresholdingMethod) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile, 
														  false, // Convert to 8 bits
														  true, // Maximize values range
														  RetrievalPolicy.Unspecified);
		
		ImageCore copy = image.duplicate();
		copy.setTitle("Thresholded Image (" + thresholdingMethod + " Method)");
		
		copy.getImageBlur()
			.embedOutput(false, 16)
			.getBinomialBlur(6, 6, 6, 10, 10, 3)
			.getImageConvolved(ConvolutionNormalizationPolicy
							   .Gray8_QuantitativeNormalization_Clamp)
			.getImageThresholding()
			.thresholdImageAndBinarize(thresholdingMethod, true);
		
		GlobalOptions.getDefaultRenderTool().display(image);
		GlobalOptions.getDefaultRenderTool().display(copy);
			
	}
	
	
	/**
	 * Validates image histogram construction and saving to a text file
	 * @param inputImageFile Image file path
	 * @param outputFile output text file for the histogram.
	 * @throws IOException 
	 * @see ImageThresholding#getHistogram()
	 */
	public static void testHistogram(String inputImageFile, String outputFile) throws IOException{

		ImageThresholding image = ImageCoreFactoryIJ.getInstance()
													.getImageCore(inputImageFile,
																  false, // Convert to 8 bits
																  true, // Maximize values range
																  RetrievalPolicy.Unspecified
																 )
						  							.getImageThresholding();
		GlobalOptions.getDefaultRenderTool()
					 .display(image);
		image.getHistogram().writeToFile(outputFile);

	}
	

	/**
	 * Validates image colors Thresholding and sets to zero the voxels of which
	 * are not between a set minimum and maximum value.
	 * 
	 * @param inputImageFile the source file for image data.
	 * @param outputFile1 output text file for the histogram before contrast operation.
	 * @param outputFile2 output text file for the histogram after contrast operation.
	 * @param min The minimum value of the display range that remains, between 0.0 and 1.0. 
	 * @param max The maximum value of the display range that remains, between 0.0 and 1.0. 
	 * @throws IOException 
	 * @see ImageContrast#adjustValuesRange(double, double)
	 */
	public static void testApplyThresholdMinAndMax(String inputImageFile,
												String outputFile1,
												String outputFile2,
												double min, 
												double max) throws IOException{
	
		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile,
														  false, // Convert to 8 bits
														  false, // Maximize values range
														  RetrievalPolicy.Unspecified
											);
		
		image.getImageThresholding().getHistogram().writeToFile(outputFile1);	
		
		ImageCore copy = image.duplicate();
		copy.setTitle("Adjusted image (min : "+ min +
					  ", max: " + max);
		copy.getImageThresholding().applyThresholdMinAndMax(min, max);
		copy.getImageThresholding().getHistogram().writeToFile(outputFile2);
		
		GlobalOptions.getDefaultRenderTool().display(image);
		GlobalOptions.getDefaultRenderTool().display(copy);

	}


	
	/**
	 * Main function of the package's tests.
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			testImageThresholdAndBinarize(getSampleImage(1), ThresholdingOption.Otsu);
			
			
			//testApplyThresholdMinAndMax(getSampleImageGray8(0), 
			//		getTestOutputDir()+"histogram_Exp5.dat", 
			//		getTestOutputDir()+"histogram_Exp5_contrast.dat",
			//		0.25d, 0.7d);

		} catch(IOException e){
			e.printStackTrace();
		}
		
		System.out.println(ResourcesMonitor.getRessourceInfo());
		System.out.println("The program ended normally.");
	}
}
