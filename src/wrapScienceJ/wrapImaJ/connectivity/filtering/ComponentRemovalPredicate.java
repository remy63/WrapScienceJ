/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ComponentRemovalPredicate.java                                     * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.connectivity.filtering;

import wrapScienceJ.wrapImaJ.connectivity.ComponentInfo;
import wrapScienceJ.wrapImaJ.core.VoxelShort;

/**
 * This interface is intended for the purpose of selectively removing connected components in a binary image,
 * according to a predicate satisfied by some voxels of the component.
 * 
 * The options are:
 * <ul>
 * 	<li>To remove the components such as one voxel satisfies the predicate</li>
 * 	<li>To remove the components such none of the voxel satisfies the predicate</li>
 * </ul>
 * 
 * @author Remy Malgouyres
 */
public interface ComponentRemovalPredicate {

	/**
	 * @param voxel the voxel at which to test the predicate
	 * @param componentInfo The information concerning the connected component of the voxel
	 * @return the predicate's value
	 */
	boolean keepVoxelComponent(VoxelShort voxel, ComponentInfo componentInfo);
}
