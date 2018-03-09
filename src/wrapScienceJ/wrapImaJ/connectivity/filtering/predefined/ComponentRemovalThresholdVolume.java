/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ComponentRemovalThresholdVolume.java                               * 
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
 * to filter out components with a number of voxels lower than threshold.
 * 
 * @author Remy Malgouyres
 */
public class ComponentRemovalThresholdVolume implements ComponentRemovalPredicate {

	private int m_thresholdCardinality;
	
	/**
	 * @param thresholdComponentVolume minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @param unitVoxelVolume volume of a single voxel (taking into account the image's calibration)
	 */
	public ComponentRemovalThresholdVolume(double thresholdComponentVolume, double unitVoxelVolume){
		this.m_thresholdCardinality = (int)(thresholdComponentVolume/unitVoxelVolume);
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.connectivity.filtering.ComponentRemovalPredicate#keepVoxelComponent(wrapScienceJ.wrapImaJ.core.VoxelShort, wrapScienceJ.wrapImaJ.connectivity.ComponentInfo)
	 */
	@Override
	public boolean keepVoxelComponent(VoxelShort voxel, ComponentInfo componentInfo) {
		return (componentInfo.getnumberOfPoints() >= this.m_thresholdCardinality);
	}

}
