/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ConvolutionFactoryBase.java                                       * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.convolve;

import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed;

/**
 * @author remy
 *
 * This class is a base class for convolution masks Factories, and provides utilities
 * to enable signed representations for voxel values, embed output buffers so that
 * the values can be (to some extent) added, possibly subtracted, or multiplied by a limitted factor
 * without incurring overflows.
 *  
 */
public interface ConvolutionFactoryBase {
	/**
	 * @return A mask that does nothing with the same input and output buffer as this one.
	 */
	public ConvolutionBase getIdentityMask();
	
	/**
	 * Produces a GRAY16 image that can hold greater values than the original output image.
	 * The output image underlying this instance is replace by the new one.
	 * This allows operations such as difference or linear combination of images without overflows.
	 * If the original image is GRAY8, this is done by creating an image with 16 bits per pixel
	 * by copying the values without scaling (hence values are less than 256).
	 * If the original image is GRAY16, then the values are divided by 256 (hence values also
	 * are less than 256).
	 * If the resulting image is intended to allow for negative values (e.g. to compute differences),
	 * the gray levels are shifted by adding a value (Short.MAX_VALUE-Short.MIN_VALUE)/2 = 32767.
	 *
	 * @param autoAllowSignedValues If true, the gray levels are shifted to start in the middle of
	 * 								the range of a short value.
	 * @param bitDepth The target bit depth
	 * @return The reference to this instance to allow for use of the cascade pattern.
	 */
	public ConvolutionFactoryBase embedOutput(boolean autoAllowSignedValues, int bitDepth);
	
	
	/**
	 * Allows to enlarge the input image to prevent masks to reach outside the input image.
	 * The enlargement policy allows to choose which values should be used when accessing
	 * the added margins.
	 * 
	 * @param xMargin Width of the margin to add on both sides
	 * @param yMargin Height of the margin to add on both sides
	 * @param zMargin Depth of the margin to add on both sides
	 * @param enlargementPolicy Specifies the policy to initialize the new voxel values.
	 * @return The reference to this instance to allow for use of the cascade pattern.
	 */
	public ConvolutionFactoryBase enlargeInImage(int xMargin, int yMargin, int zMargin,
												 		BufferEnlargementPolicy enlargementPolicy);
	
	
	/**
	 * Allows to retrieve the input image without any normalization.
	 * @return The embedded image in this current state.
	 */
	public ImageSignPolicyEmbed getInImageSignPolicyEmbed();
	
	
	/**
	 * Allows to retrieve the input image without any normalization.
	 * @return The embedded image in this current state.
	 */
	public ImageCore getCroppedInImage();
	
	
	/**
	 * Allows to retrieve the output image without any normalization.
	 * @return The embedded image in this current state.
	 */
	public ImageSignPolicyEmbed getOutImageSignPolicyEmbed();
}
