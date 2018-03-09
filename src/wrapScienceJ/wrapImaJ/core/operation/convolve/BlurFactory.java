/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  BlurFactory.java                                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.convolve;

import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.wrapImaJ.core.VoxelDouble;


/**
 * @author remy
 *
 * Allows to retrieve Convolution Masks for Image Blur, such as Gaussian and Binomial Blur.
 * The class extends ConvolutionFactoryBase which provides features to add margins to the
 * input image, or embed the output buffer to allow for integer only calculations on voxel
 * values without incurring overflows.
 * 
 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed
 */
public interface BlurFactory extends ConvolutionFactoryBase {
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase#enlargeInImage(int, int, int, wrapScienceJ.resource.BufferEnlargementPolicy)
	 */
	@Override
	public BlurFactory enlargeInImage(int xMargin, int yMargin, int zMargin,
									  BufferEnlargementPolicy enlargementPolicy);


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase#embedOutput(boolean, int)
	 */
	@Override
	public BlurFactory embedOutput(boolean autoAllowSignedValues, int bitDepth);


	/**
	 * Allows to get a convolution mask which has no effect on an image (Identity mask
	 * with mass one concentrated at 0).
	 * The image must be embedded so as to allow for addition of voxel values without any
	 * overflow. Use the method
	 * {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * if necessary to embed the image in a 16 bits images, and to divide values by 256
	 * if the input image is already 16 bits.
	 * 
	 * @return The identity mask, the application of which has no effect.
	 */
	public ConvolutionBase getIdentityMask();


	/**
	 * Constructs a Convolution Mask for Binomial Blur on the image with a mask characterized
	 * by a given support cardinality and a given skipping step (distance between to elements
	 * of the mask support) in each direction.
	 * 
	 * @param nPointsX cardinality of the support of the smoothing mask; first coordinate
	 * @param nPointsY cardinality of the support of the smoothing mask; second coordinate
	 * @param nPointsZ cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStepX Skipping step in X
	 * @param skippingStepY Skipping step in Y
	 * @param skippingStepZ Skipping step in Z
	 * @return An instance of a binomial convolution mask
	 */
	public ConvolutionBase getBinomialBlur(int nPointsX, int nPointsY, int nPointsZ,
										   int skippingStepX, int skippingStepY, int skippingStepZ);


	/**
	 * Constructs a Convolution Mask for Binomial Blur on the image with a mask characterized
	 * by a given support cardinality and a skipping step (distance between to elements
	 * of the mask support) in each direction equal to 1.
	 * 
	 * @param nPointsX cardinality of the support of the smoothing mask; first coordinate
	 * @param nPointsY cardinality of the support of the smoothing mask; second coordinate
	 * @param nPointsZ cardinality of the support of the smoothing mask; second coordinate
	 * @return The convolution mask ready to perform a binomial mask on the image
	 * 		   underlying this instance.
	 */
	public ConvolutionBase getBinomialBlur(int nPointsX, int nPointsY, int nPointsZ);


	/**
	 * Constructs a Convolution Mask for Binomial Blur on the image with a mask characterized
	 * by a given support cardinality and a given skipping step (distance between to elements
	 * of the mask support) in each direction.
	 * 
	 * This constructor does the same as {@link #getBinomialBlur(int, int, int, int, int, int)} except
	 * that the skipping steps are given in (approximately) multiples of the calibration data
	 * (Voxels edges lengths in a given length unit) instead of number of voxels.
	 * 
	 * @param nPointsX cardinality of the support of the smoothing mask; first coordinate
	 * @param nPointsY cardinality of the support of the smoothing mask; second coordinate
	 * @param nPointsZ cardinality of the support of the smoothing mask; second coordinate
	 * @param realSkippingStepX Skipping step in X
	 * @param realSkippingStepY Skipping step in Y
	 * @param realSkippingStepZ Skipping step in Z
	 * @param voxelEdgesLength The voxel's scale (edges' length) used as a factor to estimate
	 * 						   actual skipping steps in each direction.
	 * 
	 * @return The convolution mask ready to perform a binomial mask on the image
	 * 		   underlying this instance.
	 */	
	public ConvolutionBase getBinomialBlurCalibrated(int nPointsX, int nPointsY, int nPointsZ,
										   double realSkippingStepX, double realSkippingStepY, double realSkippingStepZ,
										   VoxelDouble voxelEdgesLength);


	/**
	 * THE MARGINS DUE TO ENLARGEMENT ARE EQUAL TO ZERO
	 *
	 * @param sigmaX standard deviation of the Gaussian on the x coordinate
	 * @param sigmaY standard deviation of the Gaussian on the y coordinate
	 * @param sigmaZ standard deviation of the Gaussian on the z coordinate
	 * @return The mask allowing to convolve with a gaussian kernel
	 */
	public ConvolutionBase getGaussianBlur(double sigmaX, double sigmaY, double sigmaZ);


	/**
	 *
	 * @param sigmaX standard deviation of the Gaussian on the x coordinate
	 * @param sigmaY standard deviation of the Gaussian on the y coordinate
	 * @param sigmaZ standard deviation of the Gaussian on the z coordinate
	 * @param voxelEdgesLength The voxel's scale (edges' length) used as a factor to estimate
	 * 						   actual skipping steps in each direction.
	 * @return The mask allowing to convolve with a gaussian kernel
	 */
	public ConvolutionBase getGaussianBlurCalibrated(double sigmaX, double sigmaY, double sigmaZ,
												 	 VoxelDouble voxelEdgesLength
													);

}
