/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDomainProjection.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation;

import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * @author remy
 *
 * Implements the Projection of the voxels of an image onto a coordinate plane
 * (projection perpendicular to one of the axis), and computes voxels according to two policies:
 * <ul>
 * <li>Tomography, in which an average of the values of all the voxels projected on a given pixel is used;</li>
 * <li>Volume Rendering, in which the maximum of the values of all the voxels projected on a given pixel is used;</li>
 * </ul>
 */
public interface ImageDomainProjection {
	
	/**
	 * Creates a 2D image with each pixel color equal to the average of gray level
	 * along a line parallel to a coordinate axis which projects onto that pixel.
	 * @param axis The axis along which to project
	 * @param maximizeContrast If true, the gray scales in the output image should be scaled
	 * 						   so as to maximize contrast among possible colors.
	 * 						   A Blurring filter is also applied before scaling.
	 * @return The 2D image with pixel's gray levels averaging the image along one direction.
	 */
	public ImageCore projectionTomography(CoordinateAxis axis, boolean maximizeContrast);
	
	/**
	 * Creates a 2D image with each pixel color equal to the maximum of all gray levels
	 * along a line parallel to a coordinate axis which projects onto that pixel.
	 * @param axis The axis along which to project
	 * @param maximizeContrast If true, the gray scales in the output image should be scaled
	 * 						   so as to maximize contrast among possible colors.
	 * 						   A Blurring filter is also applied before scaling.
	 * @return The 2D image with pixel's gray levels averaging the image along one direction.
	 */
	public ImageCore projectionVolumeRendering(CoordinateAxis axis, boolean maximizeContrast);
	
}
