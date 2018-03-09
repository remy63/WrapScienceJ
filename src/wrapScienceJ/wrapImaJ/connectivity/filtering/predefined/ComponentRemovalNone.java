/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ComponentRemovalNone.java                                          * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.connectivity.filtering.predefined;

import wrapScienceJ.wrapImaJ.connectivity.ComponentInfo;
import wrapScienceJ.wrapImaJ.connectivity.filtering.ComponentRemovalPredicate;
import wrapScienceJ.wrapImaJ.core.VoxelShort;

/**
 * @author Remy Malgouyres
 * This class is intended to implement the predicate on voxels and connected components
 * to keep all of the components. This is used to disable predicate filtering.
 * 
 * @author Remy Malgouyres
 */
public class ComponentRemovalNone implements ComponentRemovalPredicate {

	/**
	 * @return true
	 * @see wrapScienceJ.wrapImaJ.connectivity.filtering.ComponentRemovalPredicate#keepVoxelComponent(wrapScienceJ.wrapImaJ.core.VoxelShort, wrapScienceJ.wrapImaJ.connectivity.ComponentInfo)
	 */
	@Override
	public boolean keepVoxelComponent(VoxelShort voxel, ComponentInfo componentInfo) {
		return true;
	}

}
