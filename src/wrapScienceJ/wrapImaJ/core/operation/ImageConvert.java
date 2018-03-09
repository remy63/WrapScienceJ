/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageConvert.java                                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core.operation;

import java.awt.image.BufferedImage;

import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * General Interface to convert to and from other usual image formats or regular Java arrays.
 * Also provides features for slices extractions, addition, as well as tomography-style projection
 * or volume rendering along coordinate axis.
 */
public interface ImageConvert {
	
	/**
	 * Adds a slice to an image using an array of short.
	 * The image underlying this instance cannot be empty and must have at least
	 * one slice.
	 * 
	 * @param shortArray A 2D image as a one-dimensional buffer to add a slice to an image core. 
	 * @param bitDepth The number of bits per voxel of the new image
	 * 				   (values are truncated in case of overflow).
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */	
	public ImageCore addSliceFromArray(short[] shortArray, int bitDepth);
	
	/**
	 * Adds a slice to an image using an array of byte.
	 * The image underlying this instance cannot be empty and must have at least
	 * one slice.
	 * 
	 * @param byteArray  A 2D image as a one-dimensional buffer to add a slice to an image core.
	 * @param bitDepth The number of bits per voxel of the new image
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */	
	public ImageCore addSliceFromArray(byte[] byteArray, int bitDepth);
	
	/**
	 * Adds a slice to an image using an java.awt.Image.
	 * The image underlying this instance must be non empty (at least depth one).
	 * 
	 * The length of the input array must agree with the depth (number of slices) of this image.
	 * The width and height of each image in the input array must agree with the width and height
	 * of this image. The bit depth of the AWT image must agree with the bit depth of
	 * this image.
	 * 
	 * The memory policy is that the reference of the input image is used to recycle
	 * the memory for pixels of the input image in the new slice. Duplicate the input image
	 * if this behavior is not the right one.
	 * 
	 * @param bufferedImage An image as AWT Image instance to add a slice to an image core. 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */	
	public ImageCore addSLiceFromAwtImage(java.awt.image.BufferedImage bufferedImage);

	/**
	 * Initializes the values of the image underlying this instance using an array of java.awt.Image.
	 * The length of the input array must agree with the depth (number of slices) of this image.
	 * The width and height of each image in the input array must agree with the width and height
	 * of this image.
	 * At last, the bit depth of all input AWT images must agree with the bit depth of
	 * this image.
	 * @param bufferedImages An array of image as AWT Image instance to initialize slices of an image core. 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore initializeFromAwtImages(java.awt.image.BufferedImage[] bufferedImages);
	
	
	
	/**
	 * Copies an java.awt.Image to an ImageCore slice. The width and height of this instance and
	 * the AWT image must agree.
	 * @param bufferedImage An array of image as AWT Image instance to initialize slices of an image core. 
	 * @param zCoord The depth coordinate for which a slice should be initialized.
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore setSliceFromAwtImage(java.awt.image.BufferedImage bufferedImage, int zCoord);
	
	
	/**
	 * Creates a 2D image with each pixel color equal to the average of gray levels
	 * along a line parallel to a coordinate axis which projects onto that pixel.
	 * @param axis The axis along which to project
	 * @param maximizeContrast If true, the gray scales in the output image should be scaled
	 * 						   so as to maximize contrast among possible colors.
	 * 						   A Blurring filter is also applied before scaling.
	 * @return The 2D image with pixel's gray levels averaging the image along one direction.
	 */
	public BufferedImage projectionTomography(CoordinateAxis axis, boolean maximizeContrast);
	
	/**
	 * Creates a 2D image with each pixel color equal to the maximum of all gray levels
	 * along a line parallel to a coordinate axis which projects onto that pixel.
	 * @param axis The axis along which to project
	 * @param maximizeContrast If true, the gray scales in the output image should be scaled
	 * 						   so as to maximize contrast among possible colors.
	 * 						   A Blurring filter is also applied before scaling.
	 * @return The 2D image with pixel's gray levels averaging the image along one direction.
	 */
	public BufferedImage projectionVolumeRendering(CoordinateAxis axis, boolean maximizeContrast);

	
	/**
	 * Retrieves a slice of this instance as an AWT BufferedImage.
	 * The data of the slice is copied into a newly allocated low level buffer.
	 * @param zCoord The depth coordinate from which to extract a slice
	 * @return The BufferredImage representing the slice at depth zCoord
	 */
	public BufferedImage getSliceAsAwtImage(int zCoord);
	

	/**
	 * Retrieves a slice of this instance as an Array of gray level values.
	 * The array gives the reference of the slice data so that the values
	 * of the slice can be modified through the returned Array.
	 * Depending on the Bit Depth (8 or 16), the array can be an instance
	 * of byte[] or short[].
	 * The data of the slice is NOT copied into a newly allocated low level buffer.
	 * @param zCoord The depth coordinate from which to extract a slice
	 * @return An Object which is either instance of byte[] or short[] and provides
	 * 					 the reference to the slice's raw Array (buffer) of gray levels.
	 */
	public Object getSliceAsRawArray(int zCoord);	
	
	/**
	 * Converts the image to a regular array of short.
	 * The integer values of gray levels are unchanged.
	 * If the image has type GRAY16, the memory of this instance is recycled
	 * and the resulting array references the very slice pixels arrays of this image.
	 * Use {@link ImageCore#duplicate()} if this is not the right behavior. 
	 * 
	 * @return A regular array of short representing the voxel's gray levels.
	 */
	public short[][] getRegularShortArray();
	
	/**
	 * Converts the image to a regular array of bytes.
	 * The integer values of gray levels are unchanged if the input image has type GRAY8.
	 * The integer values of gray levels are left unchanged if the input image has type GRAY16,
	 * so that overflows might happen in general.
	 * If the image has type GRAY8, the memory of this instance is recycled
	 * and the resulting array references the very slice pixels arrays of this image.
	 * Use {@link ImageCore#duplicate()} if this is not the right behavior. 
	 * 
	 * @return A regular array of bytes representing the voxel's gray levels.
	 */
	public byte[][] getRegularByteArray();
	
	/**
	 * Converts the image to one Byte gray levels (colors from 0 to 255)
	 * The method is NOT assumed to preserve the original image and
	 * the returned reference could be this if the pixel's formats are already
	 * 8bits per voxel.
	 * @param scaleMaxContrast If true, all values are scaled so that the values range is maximal
	 * 					before reducing quantization resolution.
	 * 					otherwise, the values are divided by 256.
	 * @see ImageContrast#maximizeValuesRange()
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore convertToGray8(boolean scaleMaxContrast);	
	
	
	/**
	 * Converts the image to one Byte gray levels (colors from 0 to 255)
	 * The method is NOT assumed to preserve the original image and
	 * the returned reference could be this if  if the pixel's formats are already
	 * 8bits per voxel.
	 * The values of gray levels are clamped and set to 255 for the voxels
	 * with values greater than 255.
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore convertToGray8Clamp();	
	
	
	/**
	 * Copies the image to one Byte gray levels (colors from 0 to 255)
	 * The method is assumed to preserve the original image and
	 * a new image is created in all cases.
	 * @param scaleMaxContrast If true, all values are scaled so that the values range is maximal
	 * 					before reducing quantization resolution.
	 * 					otherwise, the values are divided by 256.
	 * @see ImageContrast#maximizeValuesRange()
	 * @return The new image with Gray8 values format
	 */
	public ImageCore getCopyAsGray8(boolean scaleMaxContrast);
	
	
	/**
	 * Copies the image to one Byte gray levels (colors from 0 to 255)
	 * The method is assumed to preserve the original image and
	 * a new image is created in all cases.
	 * The values of gray levels are clamped and set to 255 for the voxels
	 * with values greater than 255.
	 * @return The new image with Gray8 values format
	 */
	public ImageCore getCopyAsGray8Clamp();
	
	
}	