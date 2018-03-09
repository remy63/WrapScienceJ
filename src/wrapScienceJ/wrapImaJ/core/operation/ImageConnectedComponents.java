/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageConnectedImponents.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation;

import wrapScienceJ.wrapImaJ.connectivity.ConnectedComponent;
import wrapScienceJ.wrapImaJ.connectivity.LabelingPolicy;
import wrapScienceJ.wrapImaJ.connectivity.filtering.ComponentRemovalPredicate;

/**
 * @author remy
 *
 *	Allows to retrieve Connected Components of an image.
 */
public interface ImageConnectedComponents {
	
	/**
	 * Constructs a ConnectedComponent derived class instance with relevant 
	 * dimension (2D or 3D) and labels the components.
	 * The input image must have type Gray8.
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 * </ul>
	 * @param labelingPolicy Can be 3D components labeling, or slice by slice against a given axis.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by filtering
	 * @param thresholdComponentVolume minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @param setRandomColors true if the colors of the original image should be set according to the components labels.
	 * @return an instance of a concrete derived class for ConnectedComponent
	 * @throws IllegalStateException in case the number of connected components exceeds the Short.MAX_VALUE (32767)
	 * 	 							 or if the image doesn't have type GRAY8.
	 */
	public ConnectedComponent getLabeledComponents(LabelingPolicy labelingPolicy,
													int foregroundColor,
													boolean removeBorderComponent,
													double thresholdComponentVolume,
													boolean setRandomColors
												  )throws IllegalStateException;
	
	
	/**
	 * Constructs a ConnectedComponent derived class instance with relevant dimension
	 * (2D or 3D) and labels the components.
	 * The input image must have type Gray8.
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 * </ul>
	 * @param labelingPolicy Can be 3D components labeling, or slice by slice against a given axis.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by filtering
	 * @param thresholdComponentVolume minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @param removalPredicate a predicate according to which components should be filtered out
	 * @param keepPredicate true if we should keep the components with a voxel satisfying removalPredicate, and false if we should remove the components with a voxel satisfying removalPredicate 
	 * @param setRandomColors true if the colors of the original image should be set according to the components labels.
	 * @return an instance of a concrete derived class for ConnectedComponent
	 * @throws IllegalStateException  in case the number of connected components exceeds the Short.MAX_VALUE (32767)
	 * 	 							 or if the image doesn't have type GRAY8.
	 */
	public ConnectedComponent getLabeledComponents(LabelingPolicy labelingPolicy,
												   int foregroundColor,
												   boolean removeBorderComponent, 
												   double thresholdComponentVolume,
												   ComponentRemovalPredicate removalPredicate,
												   boolean keepPredicate,
												   boolean setRandomColors
												  ) throws IllegalStateException;
}
