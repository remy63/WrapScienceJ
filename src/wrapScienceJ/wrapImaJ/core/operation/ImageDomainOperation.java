/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDomainOperation.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation;

import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * @author remy
 * 
 *	Provides operations to modify the domain of an image, such as cropping,
 *  enlarging, concatenating two images with compatible sizes, enlarging images
 *  by adding margins (with different policies to initialize the new voxels' values),
 *  creating an image sandwich by inserting slices into another image, etc.
 */
public interface ImageDomainOperation {
	
    /**
     * Crops the Region of Interest (ROI) in the image. 
     * The coordinates bounding boxe's minimal coordinates and edges length (in number of pixels)
     * are given as parameters. 
     * 
     * The bounds of the ROI are checked against the input image's own boundaries,
     * and clamped if necessary to avoid out of bounds access during the cropping operation.
     * 
     * The metadata of the image is used to initialize the metadata
     * of the newly created image.
     * @see wrapScienceJ.resource.ResourceCore#mergeMetaData(ResourceCore)
     * 
     * @param xmin minimal X coordinate of the ROI
     * @param ymin minimal Y coordinate of the ROI
     * @param zmin minimal Z coordinate of the ROI
     * @param xmax maximal X coordinate of the ROI plus 1
     * @param ymax maximal Y coordinate of the ROI plus 1
     * @param zmax maximal Z coordinate of the ROI plus 1
     * @return : ImageCore instance with the of the cropped image.
     */
	public ImageCore crop(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax);

	
	/**
	 * Enlarges the domain of an image, using a completion policy.
	 * 
     * The metadata of the image is used to initialize the metadata
     * of the newly created image.
     * @see wrapScienceJ.resource.ResourceCore#mergeMetaData(ResourceCore)
     * 
	 * @param xMargin Width of the margin to add on both sides
	 * @param yMargin Height of the margin to add on both sides
	 * @param zMargin Depth of the margin to add on both sides
	 * @param enlargementPolicy Specifies the policy to initialize the new voxel values.
	 * 
	 * @return An enlarged (or shrunk if margins are negative) version of the image underlying this instance.
	 */
	public ImageCore getEnlargedImage(int xMargin, int yMargin, int zMargin,
									  BufferEnlargementPolicy enlargementPolicy);
	
	/**
	 * Retrieves a slice of the image underlying this instance, and returns it as
	 * an ImageCore with a single slice.
	 * 
	 * The new image, which has a single slice, contains a reference to the pixels
	 * buffer of the original slice in this image.
	 * The data of the slice is NOT copied into a newly allocated low level buffer.
	 * Use {@link ImageCore#duplicate()} on the output to work around this
	 * memory policy and get a full copy of the slice.

     * The metadata of the image is used to initialize the metadata
     * of the newly created image.
     * @see wrapScienceJ.resource.ResourceCore#mergeMetaData(ResourceCore)
	 * 
	 * @param zCoord The depth coordinate from which to extract a slice
	 * @return The ImageCore having the slice at depth zCoord as a single slice
	 */
	public ImageCore extractSlice(int zCoord);
	
	/**
	 * Retrieves a contiguous set of slices of this instance and return them
	 * as the slices of an ImageCore.
	 * 
	 * The new image contains references to the pixels buffers of the original
	 * slices extracted from this image.
	 * The data of the slice is NOT copied into newly allocated low level buffers.
	 * Use {@link ImageCore#duplicate()} on the output to work around this
	 * memory policy and get a full copy of the extracted slices.
	 * 
	 * The number of extracted slices (difference between the arguments) must
	 * be positive (non zero).
	 * 
	 * The metadata of the image is used to initialize the metadata
     * of the newly created image.
     * @see wrapScienceJ.resource.ResourceCore#mergeMetaData(ResourceCore)
	 *
	 * @param zCoordMin The lowest depth coordinate of the extracted slices.
	 * 					non negative.
	 * @param zCoordMaxPlusOne The highest depth coordinate of the extracted
	 * 						   slices PLUS ONE.
	 * 						   (less than or equal to this image's depth.)
	 * @return The ImageCore having the slice at depth zCoord as a single slice
	 */
	public ImageCore extractSlices(int zCoordMin, int zCoordMaxPlusOne);

	/**
	 * Allows to unite the domain of the image underlying this instance with the
	 * domain of another image, by placing one domain after the other
	 * along a given axis direction. The respective sizes of the images in the other
	 * directions must agree, as well as their bit depths.
	 * 
	 * The memory management policy is that the images are copied to create a merged
	 * image. The images memory must be released after use if no longer useful.
	 * 
	 * The metadata associated with the newly created image is copied from the
	 * image underlying this instance. Metadata associated with the other image
	 * should be manually added if necessary.
	 * 
	 * @see ImageCore#mergeMetaData(wrapScienceJ.resource.ResourceCore)
	 * @see ImageCore#addMetaData(wrapScienceJ.metaData.container.MetaDataRetriever)
	 * 
	 * @param image The image to concatenate after this instance's underlying image.
	 * @param axis The axis along which to place one image domain after the other.
	 * @return The resulting concatenation image.
	 */
	public ImageCore domainConcatenation(ImageCore image, CoordinateAxis axis);
	
	
	/**
	 * Allows to unite the domain of the image underlying this instance with the
	 * domain of another image, by inserting the domain of the argument image
	 * between two planes orthogonal to a given axis.
	 * The respective sizes of the images in the other directions must agree, as
	 * well as their bit depths.
	 * 
	 * The memory management policy is that the images are copied to create a merged
	 * image. The images memory must be released after use if no longer useful.
	 * 
	 * The metadata associated with the newly created image is copied from the
	 * image underlying this instance. Metadata associated with the other image
	 * should be manually added if necessary.
	 * 
	 * @see ImageCore#mergeMetaData(wrapScienceJ.resource.ResourceCore)
	 * @see ImageCore#addMetaData(wrapScienceJ.metaData.container.MetaDataRetriever)
	 * 
	 * @param image The image to concatenate after this instance's underlying image.
	 * @param axis The axis along which to place one image domain after the other.
	 * @param coordMinInsert The minimal coordinate of the planes of argument image domain within the
	 * 					resulting image. The first slice (with coordinate Z=0) of image
	 * 					is mapped onto the slice with coordinate Z=zCoordMin.
	 * @return The resulting concatenation image.
	 */
	public ImageCore insertImageSandwich(ImageCore image, CoordinateAxis axis, int coordMinInsert);


	/**
	 * Allows to add all the slices of an image to the image underlying this instance.
	 * This image must have at least one slice. Otherwise, use a mere reference affectation.
	 * The input image must have same width and height as this image, and the bit depth
	 * must also be the same. Both images must be non empty.
	 * 
	 * The slices of the input image are added trough the reference to their data buffer.
	 * The data-buffer is then shared by both this image and the input image.
	 * To work around this memory policy, use {@link ImageCore#duplicate()} on the input
	 * image before invocation, or use {@link #domainConcatenation(ImageCore, CoordinateAxis)}
	 * if both images should remain unchanged.
	 * 
	 * Strange things can happen of the two images already share common slices as references,
	 * and in particular id the two images have the same reference. In such a case,
	 * using {@link ImageCore#duplicate()} before invocation may be in order.
	 * 
	 * The metadata associated with both images remains unchanged.
	 * 
	 * @param image The input image the slices of which should be added to this image.
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore mergeSlices(ImageCore image);
	
	
	/**
	 * Allows to add all the slices of an argument image to the image underlying this instance.
	 * This image must have at least one slice. Otherwise, use a mere reference affectation.
	 * The input image must have same width and height as this image, and the bit depth
	 * must also be the same. The depth coordinate zCoordMin must be non negative
	 * and less than or equal to the depth of the image underlying this instance,
	 * as returned by {@link ImageCore#getDepth()}.
	 * 
	 * The slices of the input image are added trough the reference to their data buffer.
	 * The data-buffer is then shared by both this image and the input image.
	 * To work around this memory policy, use {@link ImageCore#duplicate()} on the input
	 * image before invocation, or use {@link #domainConcatenation(ImageCore, CoordinateAxis)}
	 * if both images should remain unchanged.
	 * 
	 * Strange things can happen of the two images already share common slices as references,
	 * and in particular id the two images have the same reference. In such a case,
	 * using {@link ImageCore#duplicate()} before invocation may be in order.
	 * 
	 * The metadata associated with both images remains unchanged.
	 * 
	 * @param image The input image the slices of which should be added to this image.
	 * @param zCoordMin The minimal depth coordinate of the slices of argument image within
	 * 					the resulting image. The first slice (with coordinate Z=0) of image
	 * 					is mapped onto the slice with coordinate Z=zCoordMin.
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore insertSlices(ImageCore image, int zCoordMin);
	
}
