/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDomainTransform.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation;

import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelInt;


/**
 * @author remy
 * 
 * Allows to perform operations such as symmetry, axis swaps, 90 degrees rotations
 * around a coordinate axis. Also provides facilities to copy (part of) to (part of)
 * another image. 
 */
public interface ImageDomainTransform {

	/**
	 * Constructs a copy of this image with two axis swapped. This amounts to
	 * performing a symmetry over the domain of the image.
	 * 
	 * For example, if I1 is the input image with size (w, h, d), and if the
	 * input axis are Y and Z, we get an image I2 with size, (w, d, h), and such that
	 * I2(x, y, z) = I1(x, z, y).
	 * Most of the time and in general, this operation requires re-allocating,
	 * and this method systematically allocates a new image without modifying the
	 * input image.
	 * 
	 * @param axis1 The first axis to be swapped
	 * @param axis2 The second axis to be swapped
	 * @return The image with swapped axis
	 */
	public ImageCore getAxisSwapped(CoordinateAxis axis1, CoordinateAxis axis2);
	
	/**
	 * Copies this image into another image, with two axis swapped. This amounts to
	 * performing a symmetry over the domain of the image.
	 * 
	 * For example, if I1 is the input image with size (w, h, d), and if the
	 * input axis are Y and Z, we get an image I2 with size, (w, d, h), and such that
	 * I2(x, y, z) = I1(x, z, y).
	 * This method is intended to avoid a new image allocation in using
	 * {@link #getAxisSwapped(CoordinateAxis, CoordinateAxis)} when an image with
	 * the right dimensions has already been allocated and can be reused.
	 * 
	 * The size of the destination image must agree with the swapped size of
	 * the original image, and the two images must have same bitdepth.
	 * Furthermore, the destination image's calibration and other metadata is left unchanged.
	 * 
	 * @param destinationImage The destination image into which the voxels gray levels
	 * 						   should be copied, swapping two coordinates in the voxels'
	 * 						   domain.
	 * 
	 * Note that in case axis1 is equal to axis2, the method performs a mere copy, which
	 * might be less efficient than {@link #copyTo(ImageCore)}.
	 * 
	 * @param axis1 The first axis to be swapped
	 * @param axis2 The second axis to be swapped
	 * @return The reference to destinationImage for use in the cascade pattern
	 */
	public ImageCore copyAxisSwapped(ImageCore destinationImage, CoordinateAxis axis1, CoordinateAxis axis2);
	
	/**
	 * Copies this image into another image, which must have been previously allocated.
	 * The size of the destination image must agree in all dimensions with the size
	 * the original image, and the two images must have same bitdepth.
	 * The destination image's calibration and other metadata is merged with that
	 * of the original image (and replace for parameters that already exist in
	 * the destination image's metadata).
	 * The implementation might require that both source and destination images
	 * should be defined on the same Image Processing Framework (i.e. Created by the same
	 * ImageCore factory).
	 * @see ImageCore#getPreferedFactory()
	 * 
	 * @param destinationImage The destination image into which the voxels gray levels
	 * 						   should be copies, swapping two coordinates in the voxels'
	 * 						   domain.
	 * @return The reference to destinationImage for use in the cascade pattern
	 */
	public ImageCore copyTo(ImageCore destinationImage);
	
	
	/**
	 * Copies this image into another image, which must have been previously allocated.
	 * The voxel's coordinates of the input image are translated (shifted) before
	 * being copied into the destination image.
	 * 
	 * The two images must have same bit depth, but no requirement is made on the respective sizes
	 * of the two images. If the destination voxel does not exist, it is dropped.
	 * 
	 * The destination image's calibration and other metadata is merged with that
	 * of the original image (and replace for parameters that already exist in
	 * the destination image's metadata).
	 * The implementation might require that both source and destination images
	 * should be defined on the same Image Processing Framework (i.e. Created by the same
	 * ImageCore factory).
	 * @see ImageCore#getPreferedFactory()
	 * 
	 * @param destinationImage The destination image into which the voxels gray levels
	 * 						   should be copies, swapping two coordinates in the voxels'
	 * 						   domain.
	 * @param shiftMargin The destination voxel Vd has coordinates (Vs + shiftMargin),
	 * 					  where Vs is the source voxel's coordinate vector.
	 * @param scaleForBitDepth If true and the two images involved have different bit depth,
	 * 						   the colors is multiplied or divided by 256 to adjust gray scales.
	 * @return The reference to destinationImage for use in the cascade pattern
	 */
	public ImageCore copyInto(ImageCore destinationImage, VoxelInt shiftMargin, boolean scaleForBitDepth);
	
	

	
	/**
	 * Copies an image, which must have been previously allocated, into this image.
	 * The voxel's coordinates of the input image are translated (shifted) before
	 * being copied into the destination image.
	 * 
	 * The two images must have same bit depth, but no requirement is made on the respective sizes
	 * of the two images. If the destination voxel does not exist, it is dropped.

	 * The destination image's calibration and other metadata is merged with that
	 * of the original image (and replace for parameters that already exist in
	 * the destination image's metadata).
	 * The implementation might require that both source and destination images
	 * should be defined on the same Image Processing Framework (i.e. Created by the same
	 * ImageCore factory).
	 * @see ImageCore#getPreferedFactory()
	 * 
	 * @param destinationImage The destination image into which the voxels gray levels
	 * 						   should be copies, swapping two coordinates in the voxels'
	 * 						   domain.
	 * @param shiftMargin The destination voxel Vd has coordinates (Vs + shiftMargin),
	 * 					  where Vs is the source voxel's coordinate vector.
	 * @param scaleForBitDepth If true and the two images involved have different bit depth,
	 * 						   the colors is multiplied or divided by 256 to adjust gray scales.
	 * @return The reference to the image underlying this instance for use in the cascade pattern
	 */
	public ImageCore copyFrom(ImageCore destinationImage, VoxelInt shiftMargin, boolean scaleForBitDepth);
	
	
	/**
	 * Reverses the coordinates along axis, thus performing a symmetry for a given
	 * set of coordinates axis.
	 * The coordinates axis must be distinct.
	 * 
	 * @param axisCollection The list of (at most 3) distinct axis for which the
	 * 						 voxels' order must be reversed.
	 * @return The image underlying this instance to allow for use of the cascade pattern
	 */
	public ImageCore getAxisReversed(CoordinateAxis[] axisCollection);
	
	
	/**
	 * Copies this image into another image, with the planes generated by two axis rotated.
	 * This amounts to swapping the two axis and reversing the second, thus sending the
	 * first axis direction vector onto the second one, and sending the second axis direction
	 * onto the opposite of the first one.
	 * 
	 * For example, if I1 is the input image with size (w, h, d), and if the
	 * input axis are Y and Z, we get an image I2 with size, (w, d, h), and such that
	 * I2(x, y, z) = I1(x, d-1-z, y).
	 * This method is intended to avoid a new image allocation in using
	 * {@link #getPlaneRotated90(CoordinateAxis, CoordinateAxis)} when an image with
	 * the right dimensions has already been allocated and can be reused.
	 * 
	 * The size of the destination image must agree with the rotated size of
	 * the original image, and the two images must have same bit depth.
	 * Furthermore, the destination image's calibration and other metadata is left unchanged.
	 * 
	 * @param destinationImage The destination image into which the voxels gray levels
	 * 						   should be copied, swapping two coordinates in the voxels'
	 * 						   domain.
	 * 
	 * Note that in case axis1 is equal to axis2, the method performs a mere symmetry, which
	 * might be less efficient than {@link #getAxisReversed(CoordinateAxis[])} with a single axis.
	 * 
	 * @param axis1 The first axis to be rotated and sent onto the second
	 * @param axis2 The second axis to be rotated and sent onto the first on, but reversed.
	 * @return The reference to destinationImage for use in the cascade pattern
	 */
	public ImageCore copyPlaneRotated90(ImageCore destinationImage, CoordinateAxis axis1, CoordinateAxis axis2);
	
	/**
	 * Constructs a copy of this image, with the planes generated by two axis rotated.
	 * This amounts to swapping the two axis and reversing the second, thus sending the
	 * first axis direction vector onto the second one, and sending the second axis direction
	 * onto the opposite of the first one.
	 * 
	 * For example, if I1 is the input image with size (w, h, d), and if the
	 * input axis are Y and Z, we get an image I2 with size, (w, d, h), and such that
	 * I2(x, y, z) = I1(x, d-1-z, y).
	 * 
	 * Most of the time and in general, this operation requires re-allocating,
	 * and this method systematically allocates a new image without modifying the
	 * input image. 
	 * Use {@link #copyPlaneRotated90(ImageCore, CoordinateAxis, CoordinateAxis)} if no allocation
	 * is really required.
	 * 
	 * Note that in case axis1 is equal to axis2, the method performs a mere symmetry, which
	 * might be less efficient than {@link #getAxisReversed(CoordinateAxis[])} with a single axis.
	 * 
	 * @param axis1 The first axis to be rotated and sent onto the second
	 * @param axis2 The second axis to be rotated and sent onto the first on, but reversed.
	 * @return The image with planes generated by the two axis rotated.
	 */
	public ImageCore getPlaneRotated90(CoordinateAxis axis1, CoordinateAxis axis2);
	
	
}
