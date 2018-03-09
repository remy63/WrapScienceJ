/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageSignPolicyEmbed.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation;

import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelInt;

/**
 * @author remy
 *
 * Provides operations to create and manage images with signed values.
 * This allows to represent differences of images, and signed differential operators
 * as images.
 * 
 * There is a polymorphic representation of signed images and unsigned images.
 * Linear operations (such as values scale, set constant value for voxels, subtraction/addition),
 * absolute values, positive and negative parts, etc. are also provided.
 * 
 * The interfaces also provide an operation to map an unsigned image onto a signed image,
 * scaling the values if necessary, even handling the case when the bit depths are different
 * for the signed and unsigned images (in which case some information may be lost).
 */
public interface ImageSignPolicyEmbed {
	
	/**
	 * Allows to retrieve the underlying image data without any normalization or
	 * conversion.
	 * @return The raw image data representing either signed or unsigned values.
	 */
	public ImageCore getImageRaw();
	
	
	/**
	 * @return true if the voxel's values are signed, false otherwise
	 */
	public boolean isSigned();
	
	
	/**
	 * The zero value, as obtained using an unsigned int, depends on the
	 * type of image (signed or unsigned), and the bits per voxel.
	 * 
	 * If the image is unsigned, then the unsigned value of zero is realy zero. 
	 * If the image is signed, then it can be obtained by a shift with the relevant integer value.
	 * 
	 * @return The value representing zero in this image representation
	 */
	public int getZero();
	
	
	/**
	 * @return The maximal representable value in this image representation
	 */
	public int getMaxValue();
	
	
	/**
	 * @return The maximal representable value in this image representation
	 */
	public int getMinValue();	
	
	/**
	 * Produces an image with a given bit depth that can hold greater values than the original image.
	 * In case the bit depth of the input image is equal to the target bit depth, the input
	 * image is replaced by the new one. Use {@link ImageCore#duplicate()} to override
	 * this behaviour.
	 * 
	 * The purpose is to allow operations such as difference or linear combination of images without overflows.
	 * 
	 * If the original image is GRAY8 and the target bit depth is 8, then the values
	 * are divided by 32, allowing squares of gray levels to fit into a voxel value.
	 * If the original image is GRAY16 and the target bit depth is 16, then the values
	 * are divided by 512, allowing squares of gray levels to fit into a voxel value.
	 * If the original image is GRAY8 and the target bit depth is 16, then the values
	 * copied as is into the new image, allowing squares of gray levels to fit into a voxel value.
	 * If the original image is GRAY16 and the target bit depth is 8, then the values
	 * are divided by 512*16 = 8192, allowing squares of gray levels to fit into a voxel value.
	 * 
	 * @param allowSignedValues If true, the gray levels are shifted to start in the middle of
	 * 								the range of a short value.
	 * @param bitDepth The target bit depth
	 * @return This instance to allow for use of the Cascade Design Pattern.
	 * 		   that can contain greater values. The reference is that of the original image
	 * 		   in case that image has type GRAY16;
	 */
	public ImageSignPolicyEmbed getImageEmbedding(boolean allowSignedValues, int bitDepth);	
	
		
	/**
	 * Allows to retrieve a signed representation of the underlying image data
	 * without changing the number of bits per pixels. Consequently, this might
	 * result in a loss of data.
	 * 
	 * Loss of data can be avoided for GRAY8 images by using {@link #getImageEmbedding(boolean, int)}
	 * or can be minimized by dividing all values by 2.
	 *
	 * @return This instance to allow for use of the Cascade Design Pattern.
	 */
	public ImageSignPolicyEmbed getImageSignedClamp();
	
	
	/**
	 * Allows to retrieve positive part of the underlying image data as an unsigned
	 * values image with negative valued voxels set to zero.
	 * 
	 * @return This instance to allow for use of the Cascade Design Pattern.
	 */
	public ImageSignPolicyEmbed getImagePositivePart();
	
	
	/**
	 * Allows to retrieve positive part of the underlying image data as an unsigned
	 * values image with negative valued voxels set to their absolute values, and
	 * positive valued voxels set to zero.
	 * 
	 * @return This instance to allow for use of the Cascade Design Pattern.
	 */
	public ImageSignPolicyEmbed getImageNegativePart();
	
	
	/**
	 * Allows to retrieve the absolute value of the underlying image data as an unsigned
	 * values image with negative valued voxels set to their absolute values, and
	 * positive valued voxels left unchanged.
	 * 
	 * @return This instance to allow for use of the Cascade Design Pattern.
	 */
	public ImageSignPolicyEmbed getImageAbsoluteValue();
	
	
	/**
	 * Allows to set a given gray level value to all voxels, making the gray
	 * levels constant across the image.
	 * @param value The gray level to use for all voxels.
	 * @throws IllegalArgumentException if the value is not between 0 and white.
	 * @see ImageCore#getWhiteValue()
	 * 
	 * @return This instance to allow for use of the Cascade Design Pattern.
	 */
	public ImageSignPolicyEmbed setConstantValue(int value);
	
	
	/**
	 * Allows to set  all voxels to the black color, which is the minimal possible value.
	 * 
	 * @return This instance to allow for use of the Cascade Design Pattern.
	 */
	public ImageSignPolicyEmbed setAllBlack();
	
	
	/**
	 * Allows to set  all voxels to the white color, which is the largest possible value.
	 * @see ImageCore#getWhiteValue()
	 * 
	 * @return This instance to allow for use of the Cascade Design Pattern.
	 */
	public ImageSignPolicyEmbed setAllWhite();
	
	
	/**
	 * Allows to set  all voxels to the zero value.
	 * @see #getZero()
	 * 
	 * @return This instance to allow for use of the Cascade Design Pattern.
	 */
	public ImageSignPolicyEmbed setAllZero();
	
	

	/**
	 * Multiplies the values of the image by a common factor.
	 * @param factor The factor by which to multiply.
	 * 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageSignPolicyEmbed multiplyValues(double factor);
	
	
	/**
	 * Multiplies the values of the image by a common factor.
	 * @param factor The factor by which to multiply.
	 * 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageSignPolicyEmbed multiplyValues(int factor);
	
	
	/**
	 * Multiplies the values of the image by a common factor.
	 * @param denominator The common denominator by which to divide.
	 * 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageSignPolicyEmbed divideValues(int denominator);
	
	
	/**
	 * Multiplies the values of the image by a common factor.
	 * @param denominator The common denominator by which to divide.
	 * 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageSignPolicyEmbed divideValues(double denominator);
	
	
	/**
	 * Adds the values of a signed/unsigned image to the signed/unsigned image underlying
	 * this instance.
	 * 
	 * @param imageToAdd The signed/unsigned image to add to this signed/unsigned image
	 * 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageSignPolicyEmbed addValues(ImageSignPolicyEmbed imageToAdd);
	
	
	/**
	 * Adds the values of a signed/unsigned image to the signed/unsigned image underlying
	 * this instance.
	 * 
	 * @param imageToSubtract The signed/unsigned image to add to this signed/unsigned image
	 * 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageSignPolicyEmbed subtractValues(ImageSignPolicyEmbed imageToSubtract);

	
	/**
	 * Changes a signed image into its opposite. If the image is unsigned, the result is zero.
	 * 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageSignPolicyEmbed getOpposite();
	
	
	/**
	 * Changes a signed image into a reversed image. In the case of a signed image,
	 * we get the same as {@link #getOpposite()} MINUS ONE (because the range is not symmetrical).
	 * 
	 * In case of an unsigned image, we compute newValues = (white-values).
	 * 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageSignPolicyEmbed getReversedValues();
	
	
	/**
	 * Copies this image into another image, which must have been previously allocated.
	 * The voxel's coordinates of the input image are translated (shifted) before
	 * being copied into the destination image.
	 * 
	 * If the original image is GRAY8 and the target bit depth is 8, then the values
	 * are divided by 16, allowing squares of gray levels to fit into a voxel value.
	 * If the original image is GRAY16 and the target bit depth is 16, then the values
	 * are divided by 256, allowing squares of gray levels to fit into a voxel value.
	 * If the original image is GRAY8 and the target bit depth is 16, then the values
	 * copied as is into the new image, allowing squares of gray levels to fit into a voxel value.
	 * If the original image is GRAY16 and the target bit depth is 8, then the values
	 * are divided by 256*16 = 4096, allowing squares of gray levels to fit into a voxel value.
	 * 
	 * The destination image's calibration and other metadata is merged with that
	 * of the original image (and replace for parameters that already exist in
	 * the destination image's metadata).
	 * The implementation might require that both source and destination images
	 * should be defined on the same Image Processing Framework (i.e. Created by the same
	 * ImageCore factory).
	 * @see ImageCore#getPreferedFactory()
	 * 
	 * @param destImageSignPolicyEmbed The destination image into which the voxels gray levels
	 * 						   should be copies, swapping two coordinates in the voxels'
	 * 						   domain.
	 * @param shiftMargin The destination voxel Vd has coordinates (Vs + shiftMargin),
	 * 					  where Vs is the source voxel's coordinate vector.
	 * @param scaleForBitDepth If true and the two images involved have different bit depth,
	 * 						   the colors is multiplied or divided by 256 to adjust gray scales.
	 * 
	 * @return The reference to destinationImage for use in the cascade pattern
	 */
	public ImageSignPolicyEmbed copyInto(ImageSignPolicyEmbed destImageSignPolicyEmbed, VoxelInt shiftMargin, boolean scaleForBitDepth);
	
	
	/**
	 * Copies an image, which must have been previously allocated, into this image.
	 * The voxel's coordinates of the input image are translated (shifted) before
	 * being copied into the destination image.
	 * 
	 * If the original image is GRAY8 and the target bit depth is 8, then the values
	 * are divided by 16, allowing squares of gray levels to fit into a voxel value.
	 * If the original image is GRAY16 and the target bit depth is 16, then the values
	 * are divided by 256, allowing squares of gray levels to fit into a voxel value.
	 * If the original image is GRAY8 and the target bit depth is 16, then the values
	 * copied as is into the new image, allowing squares of gray levels to fit into a voxel value.
	 * If the original image is GRAY16 and the target bit depth is 8, then the values
	 * are divided by 256*16 = 4096, allowing squares of gray levels to fit into a voxel value.
	 *
	 * The destination image's calibration and other metadata is merged with that
	 * of the original image (and replace for parameters that already exist in
	 * the destination image's metadata).
	 * The implementation might require that both source and destination images
	 * should be defined on the same Image Processing Framework (i.e. Created by the same
	 * ImageCore factory).
	 * @see ImageCore#getPreferedFactory()
	 * 
	 * @param destImageSignPolicyEmbed The destination image into which the voxels gray levels
	 * 						   should be copies, swapping two coordinates in the voxels'
	 * 						   domain.
	 * @param shiftMargin The destination voxel Vd has coordinates (Vs + shiftMargin),
	 * 					  where Vs is the source voxel's coordinate vector.
	 * @param scaleForBitDepth If true and the two images involved have different bit depth,
	 * 						   the colors is multiplied or divided by 256 to adjust gray scales.
	 * 
	 * @return The reference to the image underlying this instance for use in the cascade pattern
	 */
	public ImageSignPolicyEmbed copyFrom(ImageSignPolicyEmbed destImageSignPolicyEmbed, VoxelInt shiftMargin,
										 boolean scaleForBitDepth);

	
}
