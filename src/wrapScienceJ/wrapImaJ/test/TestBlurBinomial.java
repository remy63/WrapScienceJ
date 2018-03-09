/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: TestBlurBinomial.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.test;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.core.*;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy;
import java.io.IOException;


/**
 * @author remy
 * 
 * Allows to test integer only implementation of Binomial Blur.
 * 
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.BlurFactoryGeneric
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionFactoryBaseGeneric
 */
public class TestBlurBinomial {
	
	/**
	 * Validates image blurring by binomial convolutions with skipping steps.
	 * @param inputImageFile Path to the source file for the input image
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getBinomialBlur(int, int, int, int, int, int)
	 */
	public static void testBlurBinomial(String inputImageFile) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile,
														  true, // Convert to 8 bits
														  false, // Maximize values range
														  RetrievalPolicy.Unspecified
											);
		
		GlobalOptions.getDefaultRenderTool().display(image);
		ImageCore copy = image.getImageSignPolicyEmbed()
							  .getImageEmbedding(false, 16)
							  .getImageRaw();
		
		copy.setTitle("Blurred and Scaled");
		image.getImageContrast().maximizeValuesRange();
		
		copy = copy.getImageBlur()
				   .getBinomialBlur(6, 6, 6, 5, 5, 2)
				   .getImageConvolved(ConvolutionNormalizationPolicy
									  .Gray16_QuantitativeNormalization);

		copy.getImageContrast().maximizeValuesRange();
		GlobalOptions.getDefaultRenderTool().display(copy);

	}
	
	
	/**
	 * Validates image blurring by binomial convolutions with skipping steps.
	 * @param inputImageFile Path to the source file for the input image
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getBinomialBlur(int, int, int, int, int, int)
	 */
	public static void testBlurBinomialApplyMask(String inputImageFile) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile,
														  true, // Convert to 8 bits
														  false, // Maximize values range
														  RetrievalPolicy.Unspecified
											);
		
		GlobalOptions.getDefaultRenderTool().display(image);
		ImageCore copy = image.getImageSignPolicyEmbed()
							  .getImageEmbedding(false, 16)
							  .getImageRaw();
		
		copy.setTitle("Blurred and Scaled");
		image.getImageContrast().maximizeValuesRange();
		
		copy.getImageBlur()
				   .getBinomialBlur(6, 6, 6, 5, 5, 2)
				   .applyMask();
		copy.getImageContrast().maximizeValuesRange();
		
		GlobalOptions.getDefaultRenderTool().display(copy);

	}
	
	
	/**
	 * Validates image blurring by binomial convolutions with skipping steps
	 * after adding margins.
	 * @param inputImageFile Path to the source file for the input image
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getBinomialBlur(int, int, int, int, int, int)
	 */
	public static void testBlurBinomialAddMargins(String inputImageFile) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile,
														  true, // Convert to 8 bits
														  false, // Maximize values range
														  RetrievalPolicy.Unspecified
											);
		
		GlobalOptions.getDefaultRenderTool().display(image.getImageContrast()
														  .maximizeValuesRange()
														  .setTitle("Original Image")
													);
		
		ImageCore blurredWithoutEnlarge = image.duplicate()
											   .getImageBlur()
											   .embedOutput(false, 16)
											   .getBinomialBlur(6, 6, 6, 5, 5, 2)
											   .getImageConvolved(ConvolutionNormalizationPolicy
																  .Gray8_Scale_MaximizeContrast)
											   .setTitle("Blurred and Scaled");
		System.gc();
		ImageCore blurredAfterEnlarge = image.getImageBlur()
											 .embedOutput(false, 16)
											 .enlargeInImage(100,  100,  20,
															 BufferEnlargementPolicy.Mirror
														    )
											 .embedOutput(false, 16)
											 .getBinomialBlur(6, 6, 6, 5, 5, 2)
											 .getImageConvolved(ConvolutionNormalizationPolicy
															    .Gray8_Scale_MaximizeContrast)
											 .setTitle("Blurred after Enlarge and Scaled");

		GlobalOptions.getDefaultRenderTool().display(blurredWithoutEnlarge);
		GlobalOptions.getDefaultRenderTool().display(blurredAfterEnlarge);
		
		System.out.println(ResourcesMonitor.getRessourceInfo());

	}
	

	/**
	 * Validates binomial blur
	 * @param inputImageFile Path to the source file for the input image
	 * @param outputFile output image file where to save the modified image after contrast operation.
	 * @param nPointsX cardinality of the support of the smoothing mask; first coordinate
	 * @param nPointsY cardinality of the support of the smoothing mask; second coordinate
	 * @param nPointsZ cardinality of the support of the smoothing mask; second coordinate
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getBinomialBlur(int, int, int)
	 * @throws IOException 
	 */	
	public static void testBinomialBlurWriteToFile(String inputImageFile, 
											String outputFile,
											int nPointsX, int nPointsY, int nPointsZ) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile);
		
		image = image.getImageBlur()
					 .getBinomialBlur(nPointsX, nPointsY, nPointsZ)
					 .getImageConvolved(ConvolutionNormalizationPolicy
									    .Gray8_QuantitativeNormalization_Clamp);
		
		image.writeToFile(outputFile);
		
	}
	
	
	/**
	 * Validates image blurring by binomial convolutions with skipping steps.
	 * @param inputImageFile Path to the source file for the input image
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getBinomialBlurCalibrated(int, int, int, double, double, double, VoxelDouble)
	 */
	public static void testBlurBinomialCalibrated(String inputImageFile) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile,
														  false, // Convert to 8 bits
														  false, // Maximize values range
														  RetrievalPolicy.Unspecified
											);
		
		image.retrieveMetaData(); // Make sure that calibration data is there
		
		GlobalOptions.getDefaultRenderTool().display(image);
		ImageCore copy = image.duplicate();
		copy.setTitle("Blurred and Scaled");
		image.getImageContrast().maximizeValuesRange();

		copy.getImageBlur()
			.embedOutput(false, 16)
			.getBinomialBlurCalibrated(
				  6, 6, 6,
				  0.6, 0.6, 0.6, // Same skipping steps on all axis
				  copy.getImageCalibration().getVoxelLength()
			)
			.applyMask();

		copy.getImageContrast().maximizeValuesRange();

		GlobalOptions.getDefaultRenderTool().display(copy);

	}
	
	
	/**
	 * Validates image blurring by binomial convolution with skipping steps
	 * in the case when the image has a single slice.
	 * @param inputImageFile Path to the source file for the input image
	 * @param zCoord The dpeth of the slice to blur and display.
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getBinomialBlur(int, int, int, int, int, int)
	 */
	public static void testBlurBinomialSingleSlice(String inputImageFile, int zCoord) throws IOException{
	
		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile,
														  true, // Convert to 8 bits
														  true, // Maximize values range
														  RetrievalPolicy.Unspecified
											)
											.getImageDomainOperation()
											.extractSlice(zCoord);
		
		image.setTitle("Raw Extracted Slice");
		
		GlobalOptions.getDefaultRenderTool()
					 .display(image.getImageContrast()
								   .maximizeValuesRange()
							 );
		
		ImageCore copy = image.duplicate();
		copy.setTitle("blurred and Scaled");
		
		ImageCore result = copy.getImageBlur()
							   .embedOutput(false, 16)
							   .getBinomialBlur(6, 6, 1, 3, 3, 1)
							   .getImageConvolved(ConvolutionNormalizationPolicy
												  .Gray8_QuantitativeNormalization_Clamp)
							   .setTitle("Blurred Image");
		
		GlobalOptions.getDefaultRenderTool().display(result);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			//testBlurBinomial(TestImageThresholding.getSampleImage(0));
			
			//testBlurBinomialApplyMask(TestImageThresholding.getSampleImage(0));
			
			//testBlurBinomialAddMargins(TestImageThresholding.getSampleImage(0));
			
			//testBlurBinomialCalibrated(TestImageThresholding.getSampleImage(0));
			
			testBlurBinomialSingleSlice(TestImageThresholding.getSampleImage(0), 20);			
			
			System.out.println(ResourcesMonitor.getRessourceInfo());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
