/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  TestConvolutionBase.java                                          * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.test;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy;
import wrapScienceJ.wrapImaJ.core.operation.convolve.generic.BinomialBlurGeneric;

/**
 * @author remy
 *
 * Allows to test basic routines about convolution masks.
 * 
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory
 * 
 */
public class TestConvolutionBase {

	
	static ImageCore getEllipsoid(int bitDepth, int radius){
		ImageCore image = ImageCoreFactoryIJ.getInstance()
				.getEmptyImageCore(989, 1011, 101, bitDepth);
		
		image.setTitle("Ellipsoid");
		for (int z=0 ; z<image.getDepth() ; z++){
			for (int y=0 ; y<image.getHeight() ; y++){
				for (int x=0 ; x<image.getWidth() ; x++){
					if (Math.sqrt((x-500)*(x-500)+(y-500)*(y-500)+(10*(z-50)*10*(z-50)))<radius){
						image.setVoxel(x, y, z, image.getWhiteValue());
					}else{
						image.setVoxel(x, y, z, 0);
					}
				}
			}
		}
		return image;
	}
	
	
	/**
	 * Validates image blurring by binomial convolutions with skipping steps.
	 * @param normalizationPolicy The integer only mask's normalization policy
	 * @param bitDepth The bit depth for output embedding
	 * @param multiplyFactor Factor by which the output values are multiplied
	 * @param divideFactor  Factor by which the output values are divided
	 * 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getBinomialBlur(int, int, int, int, int, int)
	 * @see ConvolutionNormalizationPolicy
	 * @see ConvolutionFactoryBase#embedOutput(boolean, int)
	 */
	public static void testBinomialMask(ConvolutionNormalizationPolicy normalizationPolicy,
								 int bitDepth, int multiplyFactor, int divideFactor){
		
		ImageCore image = getEllipsoid(8, 400);
		
		GlobalOptions.getDefaultRenderTool().display(image.duplicate()
														  .setTitle("Original Image")
													);
		
		ConvolutionBase maskBlur = image.getImageBlur()
										.embedOutput(false, bitDepth) // Change Bit Depth
										.enlargeInImage(30, 30, 30,
											 			BufferEnlargementPolicy.Mirror
											 		   )
										.getBinomialBlur(3, 3, 3, // Mask support cardinality
											 			 50, 25, 25 // Skipping Steps
											 		 	);
		
		ImageCore resultImage = maskBlur.getImageConvolved(normalizationPolicy)  // Apply Blur and get Image
										.getImageSignPolicyEmbed()  // Get Linear Operations
										.multiplyValues(multiplyFactor) // Scale values
										.divideValues(divideFactor) // Scale values inverse
										.getImageRaw() // Get Image
										.getImageContrast()
										.maximizeValuesRange()
										;
		
		GlobalOptions.getDefaultRenderTool().display(resultImage.setTitle("Blurred Image"));
		
	}
	
	/**
	 * Validates image blurring by binomial convolutions with skipping steps.
	 * @param normalizationPolicy The integer only mask's normalization policy
	 * @param inputBitDepth bitDepth The bit depth for input image
	 * @param bitDepth The bit depth for output embedding
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getBinomialBlur(int, int, int, int, int, int)
	 * @see ConvolutionNormalizationPolicy
	 */
	public static void testMaskComposition(ConvolutionNormalizationPolicy normalizationPolicy,
									int inputBitDepth,
									int bitDepth){
		
		ImageCore image = getEllipsoid(inputBitDepth, 400);
		
		if (inputBitDepth == 8){
			GlobalOptions.getDefaultRenderTool().display(image.duplicate()
														  .setTitle("Original Image"));
		}
		
		VoxelInt margins = new VoxelInt(50, 50, 25);
		
		ConvolutionBase mask = image.getImageBlur()
									.embedOutput(false, bitDepth)
									.enlargeInImage(margins.getX(), margins.getY(), margins.getZ(),
													BufferEnlargementPolicy.Mirror)
									.getIdentityMask();
		if (inputBitDepth == 16){
			image = null;
			System.gc();
		}
		
		ImageCore resultImage = 
				mask.composeWith(
						new BinomialBlurGeneric(mask.getInImageSignPolicyEmbed(),
												mask.getOutImageSignPolicyEmbed(),
												CoordinateAxis.X, 6, 25,
												margins
												)
								)
				.composeWith(
						new BinomialBlurGeneric(mask.getInImageSignPolicyEmbed(),
												mask.getOutImageSignPolicyEmbed(),
												CoordinateAxis.Y, 6, 25,
												margins
												)				
							)
				.composeWith(
						new BinomialBlurGeneric(mask.getInImageSignPolicyEmbed(),
												mask.getOutImageSignPolicyEmbed(),
												CoordinateAxis.Z, 6, 12,
												margins
												)
							)
				.getImageConvolved(normalizationPolicy);
				//.getImageContrast()
				//.maximizeValuesRange();

		GlobalOptions.getDefaultRenderTool().display(resultImage);
	}
	
	
	/**
	 * Validates image blurring by binomial convolutions with skipping steps,
	 * and composition of masks with lazy computation.
	 * 
	 * @param normalizationPolicy The integer only mask's normalization policy
	 * @param nPointsUnit Skipping steps in the binomial mask's application
	 * @param bitDepth The bit depth for output embedding
	 * 
	 * @see ConvolutionBase#composeWith(ConvolutionBase)
	 * @see ConvolutionNormalizationPolicy
	 */
	public static void testMaskCompositionRepeated(ConvolutionNormalizationPolicy normalizationPolicy,
								 			int nPointsUnit, int bitDepth){
		
		ImageCore image = getEllipsoid(8, 400);
		
		GlobalOptions.getDefaultRenderTool().display(image);
		
		VoxelInt margins = new VoxelInt(50, 50, 25);
		
		ConvolutionBase mask = image.getImageBlur()
									.embedOutput(false, bitDepth)
									.enlargeInImage(margins.getX(), margins.getY(), margins.getZ(),
													BufferEnlargementPolicy.Mirror)
									.getIdentityMask();
							
		ImageCore resultImage = mask.composeWith(
						new BinomialBlurGeneric(mask.getInImageSignPolicyEmbed(),
												mask.getOutImageSignPolicyEmbed(),
												  CoordinateAxis.X, nPointsUnit, 25,
												  margins
												 )
								)
				.composeWith(
						new BinomialBlurGeneric(mask.getInImageSignPolicyEmbed(),
								mask.getOutImageSignPolicyEmbed(),
										 CoordinateAxis.Y, nPointsUnit, 25,
										 margins
										)				
							)
				.composeWith(
						new BinomialBlurGeneric(mask.getInImageSignPolicyEmbed(),
												mask.getOutImageSignPolicyEmbed(),
												CoordinateAxis.Z, nPointsUnit, 12,
												margins
										)				
							)
				.composeWith(new BinomialBlurGeneric(mask.getInImageSignPolicyEmbed(),
													 mask.getOutImageSignPolicyEmbed(),
													 CoordinateAxis.X, nPointsUnit, 25,
													 margins
													)
								)
				.composeWith(
						new BinomialBlurGeneric(mask.getInImageSignPolicyEmbed(),
												mask.getOutImageSignPolicyEmbed(),
												CoordinateAxis.Y, nPointsUnit, 25,
												margins
												)				
							)
				.composeWith(
						new BinomialBlurGeneric(mask.getInImageSignPolicyEmbed(),
												mask.getOutImageSignPolicyEmbed(),
												CoordinateAxis.Z, nPointsUnit, 12,
												margins
												)				
							)			
				.getImageConvolved(normalizationPolicy);
				//.getImageContrast()
				//.maximizeValuesRange();
				
		GlobalOptions.getDefaultRenderTool().display(resultImage);
	}

	/**
	 * @param normalizationPolicy The integer only mask's normalization policy
	 * @param bitDepth The bit depth for output embedding
	 * 
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory
	 * @see ConvolutionNormalizationPolicy
	 */
	public static void testImageBlurFactory(ConvolutionNormalizationPolicy normalizationPolicy,
											int bitDepth){
		
		ImageCore image = getEllipsoid(8, 400);
		
		GlobalOptions.getDefaultRenderTool().display(image);
		
		ImageCore embeddedCopy = image.getImageSignPolicyEmbed()
									  .getImageEmbedding(false, bitDepth)
									  .getImageRaw();
									
		ImageCore resultImage = embeddedCopy
									.getImageBlur()
									.getIdentityMask()
									.composeWith(
										embeddedCopy
											.getImageBlur()
											.getBinomialBlur(6, 6, 6, 25, 25, 12)
									 )
									.getImageConvolved(normalizationPolicy);
									//.getImageContrast()
									//.maximizeValuesRange();
		
		GlobalOptions.getDefaultRenderTool().display(resultImage);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//testBinomialMask(ConvolutionNormalizationPolicy.No_Normalization,
		//		 		 16, 1, 1
		//		 		);

		//testMaskComposition(ConvolutionNormalizationPolicy
		//					.Gray8_Scale_MaximizeContrast, 
		//					16, 8);

		testMaskCompositionRepeated(ConvolutionNormalizationPolicy.Gray8_Scale_MaximizeContrast,
									5, 16);

		//testImageBlurFactory(ConvolutionNormalizationPolicy
		//		.Gray8_QuantitativeNormalization_Clamp, 
		//		false);

	}
}
