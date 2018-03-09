/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ComponentRemovalBoundary.java                                      * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.connectivity.filtering.predefined;

import wrapScienceJ.wrapImaJ.connectivity.ComponentInfo;
import wrapScienceJ.wrapImaJ.connectivity.filtering.ComponentRemovalPredicate;
import wrapScienceJ.wrapImaJ.core.VoxelShort;

/**
 * This class is intended to implement the predicate on voxels and connected components
 * to filter out components of a binary image which touch the border.
 *
 * @author Remy Malgouyres
 */
public class ComponentRemovalBoundary implements ComponentRemovalPredicate {

	/**
	 * @see wrapScienceJ.wrapImaJ.connectivity.filtering.ComponentRemovalPredicate#keepVoxelComponent(wrapScienceJ.wrapImaJ.core.VoxelShort, wrapScienceJ.wrapImaJ.connectivity.ComponentInfo)
	 */
	@Override
	public boolean keepVoxelComponent(VoxelShort voxel, ComponentInfo componentInfo) {
		return !componentInfo.isOnTheeBorder();
	}

}
