/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDifferentialOperator.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation;

import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy;

/**
 * @author remy
 *
 */
public interface ImageDifferentialOperator {

	/**
	 * @return The convolution mask underlying this differential operator
	 */
	public ConvolutionBase getConvolutionMask();
	
	
	/**
	 * Allows to create a mask by composing two masks one after another on an image.
	 * @param rightHandSide The mask to compose with this mask.
	 * @return this instance to allow for the Cascade Design Pattern
	 */
	public ConvolutionBase composeWith(ConvolutionBase rightHandSide);
	
	
	/**
	 * @param normalizationPolicy The policy for normalizing the results.
	 * @return The resulting image with the convolution applied, as well as
	 * 		   a normalization policy.
	 */
	public ImageCore getImageConvolved(ConvolutionNormalizationPolicy normalizationPolicy);
	
	
	/**
	 * Composes the mask with a Partial Differential of arbitrary order in each
	 * direction by (iterated) finite differences with skipping steps.
	 * A centered symmetric difference is computed between point distant from 2*skippingStep.
	 * This process is iterated order times for each direction.
	 * 
	 * The results can be normalized according to different policies.
	 * 
	 * @param orderX cardinality of the support of the smoothing mask; first coordinate
	 * @param orderY cardinality of the support of the smoothing mask; second coordinate
	 * @param orderZ cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStepX Skipping step in X
	 * @param skippingStepY Skipping step in Y
	 * @param skippingStepZ Skipping step in Z
	 * @return this instance to allow for the Cascade Design Pattern
	 */
	public ConvolutionBase finiteDifference(
							int orderX, int orderY, int orderZ,
							int skippingStepX, int skippingStepY, int skippingStepZ);
	
	
	/**
	 * Composes the current mask with a Partial Differential of arbitrary order in
	 * each direction by (iterated) finite differences with skipping steps.
	 * A centered symmetric difference is computed between point distant from 2*skippingStep.
	 * This process is iterated order times for each direction.
	 * 
	 * The results can be normalized according to different policies.
	 * 
	 * The skipping steps are given in (approximately) multiples of the calibration data
	 * (Voxels edges lengths in a given length unit) instead of number of voxels.
	 * 
	 * @param orderX cardinality of the support of the smoothing mask; first coordinate
	 * @param orderY cardinality of the support of the smoothing mask; second coordinate
	 * @param orderZ cardinality of the support of the smoothing mask; second coordinate
	 * @param realSkippingStepX Skipping step in X
	 * @param realSkippingStepY Skipping step in Y
	 * @param realSkippingStepZ Skipping step in Z
	 * @return The convolution mask implementing the finite difference
	 */
	public ConvolutionBase finiteDifferenceCalibrated(int orderX, int orderY, int orderZ,
										double realSkippingStepX, 
										double realSkippingStepY, 
										double realSkippingStepZ);
	
	/**
	 * Constructs a convolution mask to evaluate a finite difference along a given
	 * axis coordinate.
	 * @param axis The coordinate axis along which to evaluate a partial derivative
	 * @param order cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStep The skipping step used for finite differealues to be added AND subtracted without overflow.nce evaluation
	 * @return The convolution mask implementing the finite difference
	 */
	public ConvolutionBase partialFiniteDifference(CoordinateAxis axis,
												   int order, int skippingStep);
	
	/**
	 * Constructs a convolution mask to evaluate a (calibrated) finite difference
	 * along a given axis coordinate.
	 * @param axis The coordinate axis along which to evaluate a partial derivative
	 * @param order cardinality of the support of the smoothing mask; second coordinate
	 * @param realSkippingStep The skipping step used for finite difference evaluation,
	 * 						   considered as a multiple of the calibration's voxel edge length.
	 * @return The convolution mask implementing the finite difference
	 */
	public ConvolutionBase partialFiniteDifferenceCalibrated(
									CoordinateAxis axis,
									int order, double realSkippingStep);
	
	
	/**
	 * Computes an image with unsigned values representing the gradient norm of the input
	 * image.
	 * 
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
	public ImageCore getGradientNorm(int skippingStepX, int skippingStepY, int skippingStepZ,
			 							   int outputBitDepth, int scaleDenominatorIntermediateValues);	
	
	
	/**
	 * Computes an image with unsigned values representing the gradient norm of the input
	 * image.
	 * 
	 * @param skippingStepX Skipping step in X
	 * @param skippingStepY Skipping step in Y
	 * @param skippingStepZ Skipping step in Z
	 * @param outputImageBuffer 
	 * @param scaleDenominatorIntermediateValues A value by which to divide the squared partial
	 * 											 differentials to ensure the result fits into the
	 * 											 bit depth of outputLinearCombination.
	 * @return The norm of the gradient of the image as an unsigned image.
	 */
	public ImageCore getGradientNorm(int skippingStepX, int skippingStepY, int skippingStepZ,
			 							   ImageCore outputImageBuffer, int scaleDenominatorIntermediateValues
			 							  );	
	
	
	/**
	 * Computes an image with unsigned values representing the gradient norm of the input
	 * image.
	 * 
	 * @param realSkippingStepX Skipping step in X
	 * @param realSkippingStepY Skipping step in Y
	 * @param realSkippingStepZ Skipping step in Z
	 * @param outputBitDepth The bit depth for the output image containing the gradient norm
	 * 						(should be able to contain the squared norm for intermediate calculations
	 * 						 to be carried out without overflow)
	 * @param scaleDenominatorIntermediateValues A value by which to divide the squared partial
	 * 											 differentials to ensure the result fits into the
	 * 											 bit depth of outputLinearCombination.
	 * @return The norm of the gradient of the image as an unsigned image.
	 */
	public ImageCore getGradientNormCalibrated(
							double realSkippingStepX, double realSkippingStepY, double realSkippingStepZ,
 							int outputBitDepth, int scaleDenominatorIntermediateValues
 							);
	
	
	/**
	 * Computes an image with unsigned values representing the gradient norm of the input
	 * image.
	 * 
	 * @param realSkippingStepX Skipping step in X
	 * @param realSkippingStepY Skipping step in Y
	 * @param realSkippingStepZ Skipping step in Z
	 * @param outputImageBuffer 
	 * @param scaleDenominatorIntermediateValues A value by which to divide the squared partial
	 * 											 differentials to ensure the result fits into the
	 * 											 bit depth of outputLinearCombination.
	 * @return The norm of the gradient of the image as an unsigned image.
	 */
	public ImageCore getGradientNormCalibrated(
										double realSkippingStepX, double realSkippingStepY, double realSkippingStepZ,
									   ImageCore outputImageBuffer, int scaleDenominatorIntermediateValues
									  );
	
	
	
}
