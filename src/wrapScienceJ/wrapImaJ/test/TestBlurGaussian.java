/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  TestBlurGaussian.java                                             * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.test;

import java.io.IOException;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.core.ImageCalibration;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy;

/**
 * @author remy
 * 
 * This class allows to test features involving Gaussian Blur.
 *
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.BlurFactoryGeneric
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionFactoryBaseGeneric
 * 
 */
public class TestBlurGaussian {
	
	/**
	 * Validates image blurring with Gaussian kernel
	 * @param inputImageFile Path to the source file for the input image
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getGaussianBlur(double, double, double)
	 */
	public static void testBlurGaussian(String inputImageFile) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile, 
														 true, // Convert to 8 bits
														 true, // Maximize values range
														 RetrievalPolicy.Unspecified
											);
		
		GlobalOptions.getDefaultRenderTool()
					 .display(image.duplicate()
								   .getImageBlur()
								   .getGaussianBlur(5.0, 5.0, 5.0)
								   .getImageConvolved(ConvolutionNormalizationPolicy
										   			  .Gray16_QuantitativeNormalization)
									.setTitle("Gaussian Blurred image")
								   );

		GlobalOptions.getDefaultRenderTool().display(image);
			
	}
	
	
	/**
	 * Validates image blurring with Gaussian kernel
	 * @param inputImageFile Path to the source file for the input image
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getGaussianBlur(double, double, double)
	 */
	public static void testBlurGaussianApplyMask(String inputImageFile) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile, 
														 true, // Convert to 8 bits
														 true, // Maximize values range
														 RetrievalPolicy.Unspecified
											);
		
		ImageCore copy = image.duplicate();
		
		copy.getImageBlur()
		   .getGaussianBlur(5.0, 5.0, 5.0)
		   .applyMask();
		
		GlobalOptions.getDefaultRenderTool().display(copy.setTitle("Gaussian Blurred image"));

		GlobalOptions.getDefaultRenderTool().display(image);
		
		System.out.println(ResourcesMonitor.getRessourceInfo());
			
	}
	
	
	/**
	 * Validates image blurring with Gaussian kernel after adding margins
	 * @param inputImageFile Path to the source file for the input image
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getGaussianBlur(double, double, double)
	 */
	public static void testBlurGaussianAddMargins(String inputImageFile) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile, 
														 true, // Convert to 8 bits
														 true, // Maximize values range
														 RetrievalPolicy.Unspecified
											);

		ImageCore copy = image.duplicate();
		
		copy.getImageBlur()
			.enlargeInImage(50, 50, 10, BufferEnlargementPolicy.Mirror)
			.getGaussianBlur(5.0, 5.0, 5.0)
			.applyMask();
		
		GlobalOptions.getDefaultRenderTool().display(copy.setTitle("Gaussian Blurred image"));

		GlobalOptions.getDefaultRenderTool().display(image);
		
	}
	


	/**
	 * Validates Gaussian blur
	 * @param inputImageFile Path to the source file for the input image
	 * @param outputFile output image file where to save the modified image after contrast operation.
	 * @param sigmaX standard deviation of the Gaussian on the x coordinate
	 * @param sigmaY standard deviation of the Gaussian on the y coordinate
	 * @param sigmaZ standard deviation of the Gaussian on the z coordinate
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getGaussianBlur(double, double, double)
	 * @throws IOException 
	 */	
	public static void testGaussianBlurWriteToFile(String inputImageFile, String outputFile,
											double sigmaX, double sigmaY, double sigmaZ) throws IOException {

		ImageCore image = ImageCoreFactoryIJ.getInstance().getImageCore(inputImageFile);
		
		ImageCore copy = image.duplicate();
		copy.setTitle("Original Image");
		image.setTitle("Blurred Image");
		GlobalOptions.getDefaultRenderTool().display(copy);
		
		image.getImageBlur().getGaussianBlur(sigmaX, sigmaY, sigmaZ).applyMask();
		image.writeToFile(outputFile);

	}
	
	
	/**
	 * Validates image blurring with Gaussian kernel with standard deviation relative to voxel's
	 * calibration
	 * @param inputImageFile Path to the source file for the input image
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getGaussianBlurCalibrated(double, double, double, VoxelDouble)
	 */
	public static void testBlurGaussianCalibrated(String inputImageFile) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile, 
														 true, // Convert to 8 bits
														 true, // Maximize values range
														 RetrievalPolicy.Unspecified
											);
		
		image.retrieveMetaData(); // Make sure that calibration data is there
		ImageCalibration cal = image.getImageCalibration();
		
		GlobalOptions.getDefaultRenderTool().display(image);

		image.getImageContrast()
			 .maximizeValuesRange()
			 .setTitle("Original Image");
		
		ImageCore result = image.duplicate()
								.setTitle("Blurred and Scaled")
								.getImageBlur()
								.getGaussianBlurCalibrated(
											  0.8, 0.8, 0.8, // Same standard deviation on all axis
											  cal.getVoxelLength()
											)
								.getImageConvolved(ConvolutionNormalizationPolicy
												   .Gray8_Scale_MaximizeContrast
												  );
		
		GlobalOptions.getDefaultRenderTool().display(result);
		GlobalOptions.getDefaultRenderTool().display(image);
	}
	
	
	/**
	 * Validates image blurring by Gaussian convolution in the case when the image
	 * has a single slice.
	 * @param inputImageFile Path to the source file for the input image
	 * @param zCoord The dpeth of the slice to blur and display.
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getGaussianBlur(double, double, double)
	 */
	public static void testBlurGaussianSingleSlice(String inputImageFile, int zCoord) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
				.getImageCore(inputImageFile,
							  true, // Convert to 8 bits
							  true, // Maximize values range
							  RetrievalPolicy.Unspecified
				)
				.getImageDomainOperation()
				.extractSlice(zCoord)
				.setTitle("Raw Extracted Slice");
		
		GlobalOptions.getDefaultRenderTool()
					 .display(image.getImageContrast()
							 	   .maximizeValuesRange()
							 );
		
		ImageCore copy = image.duplicate();
		copy.setTitle("blurred and Scaled");
		
		ImageCore result = copy.getImageBlur()
							   .embedOutput(false, 16)
							   .getGaussianBlur(3.2d, 3.2d, 3.2d)
							   .getImageConvolved(ConvolutionNormalizationPolicy
												  .Gray8_Scale_MaximizeContrast)
							   .setTitle("Blurred Image");
		
		GlobalOptions.getDefaultRenderTool().display(result);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			//testBlurGaussian(TestImageThresholding.getSampleImage(0));
			
			//testBlurGaussianApplyMask(TestImageThresholding.getSampleImage(0));
			
			//testBlurGaussianAddMargins(TestImageThresholding.getSampleImage(0));
			
			//testBlurGaussianCalibrated(TestImageThresholding.getSampleImage(0));
			
			testBlurGaussianSingleSlice(TestImageThresholding.getSampleImage(0), 20);

			
			System.out.println(ResourcesMonitor.getRessourceInfo());
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
