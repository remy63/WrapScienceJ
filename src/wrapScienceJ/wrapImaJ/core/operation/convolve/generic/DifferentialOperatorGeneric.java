/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  DifferentialOperatorGeneric.java                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.convolve.generic;

import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric;

/**
 * @author remy
 *
 * TODO THIS PACKAGE IS CURRENTLY BROKEN AND HAS TO BE FIXED BEFORE USE.
 * 
 */
public abstract class DifferentialOperatorGeneric extends ConvolutionDifferentialGeneric {
	
	
	/**
	 * Computes an image with unsigned values representing the gradient norm of the input
	 * image.
	 * 
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param skippingStepX Skipping step in X
	 * @param skippingStepY Skipping step in Y
	 * @param skippingStepZ Skipping step in Z
	 * @param outputBitDepth The bit depth for the output image containing the gradient norm
	 * 						(should be able to contain the squared norm for intermediate calculations
	 * 						 to be carried out without overflow)
	 * @param scaleDenominatorIntermediateValues A value by which to divide the squared partial
	 * 											 differentials to ensure the result fits into the
	 * 											 bit depth of outputLinearCombination.
	 * @return The norm of the gradient of the image as an unsigned image.
	 */	
	public static  ImageCore getGradientNorm(ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed,
			 final int skippingStepX, final int skippingStepY, final int skippingStepZ,
			 int outputBitDepth, final int scaleDenominatorIntermediateValues){
		inImageSignPolicyEmbed.getImageRaw();
		return  getGradientNorm(inImageSignPolicyEmbed,
								skippingStepX, skippingStepY, skippingStepZ,
								inImageSignPolicyEmbed.getImageRaw().getPreferedFactory()
										.getEmptyImageCore(inImageSignPolicyEmbed.getImageRaw().getWidth(),
														   inImageSignPolicyEmbed.getImageRaw().getHeight(),
														   inImageSignPolicyEmbed.getImageRaw().getDepth(), 
														   outputBitDepth)
										.getImageSignPolicyEmbed(), 
								scaleDenominatorIntermediateValues
				);
	}
	
	
	/**
	 * Computes an image with unsigned values representing the gradient norm of the input
	 * image.
	 * 
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param skippingStepX Skipping step in X
	 * @param skippingStepY Skipping step in Y
	 * @param skippingStepZ Skipping step in Z
	 * @param outImageSignPolicyEmbed An instance of an unsigned image allowing for linear
	 * 								  operations without incurring overflows.
	 * @param scaleDenominatorIntermediateValues A value by which to divide the squared partial
	 * 											 differentials to ensure the result fits into the
	 * 											 bit depth of outputLinearCombination.
	 * @return The norm of the gradient of the image as an unsigned image.
	 */
	public static  ImageCore getGradientNorm(ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed,
											 int skippingStepX, int skippingStepY, int skippingStepZ,
											 ImageSignPolicyEmbedGeneric outImageSignPolicyEmbed,
											 int scaleDenominatorIntermediateValues
											){
		
		if (inImageSignPolicyEmbed.getImageRaw() != outImageSignPolicyEmbed.getImageRaw()){
			int zeroValueOut = outImageSignPolicyEmbed.getZero();
			outImageSignPolicyEmbed.setConstantValue(zeroValueOut);
		}
		
		partialFiniteDifferenceZ(inImageSignPolicyEmbed, 1, skippingStepZ, 
								 outImageSignPolicyEmbed, true, true, true,
								 scaleDenominatorIntermediateValues)
								 .applyMask();
						
		partialFiniteDifferenceY(inImageSignPolicyEmbed, 1, skippingStepY, 
								 outImageSignPolicyEmbed, true, true, true, 
				 				 scaleDenominatorIntermediateValues)
				 				 .applyMask();
		
		partialFiniteDifferenceX(inImageSignPolicyEmbed, 1, skippingStepX, 
								 outImageSignPolicyEmbed , true, true, true, 
				 				 scaleDenominatorIntermediateValues)
				 				 .applyMask();
		
		ImageCore imageResult = outImageSignPolicyEmbed.getImageRaw();	
			
		for (int z=0 ; z<imageResult.getDepth() ; z++){
			for (int y=0 ; y<imageResult.getHeight() ; y++){
				for (int x=0 ; x<imageResult.getWidth() ; x++){
					imageResult.setVoxel(x, y, z, 
										 (int)(Math.sqrt(imageResult.getVoxel(x, y, z)
												 		 *scaleDenominatorIntermediateValues
												 		)
											  )
					);
				}
			}
		}
		return imageResult;
	}

}
