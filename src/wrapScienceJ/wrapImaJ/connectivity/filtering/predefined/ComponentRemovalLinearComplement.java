/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ComponentRemovalLinearComplement.java                              * 
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
 * to keep all components which are outside a thick plane.
 * The thick plane's equation is:
 * z <= m_x Coeff*x + m_yCoeff*y + m_constantCoeff 
 * or
 * z > m_thickness + m_x Coeff*x + m_yCoeff*y + m_constantCoeff 
 * 
 * @author Remy Malgouyres
 */
public class ComponentRemovalLinearComplement implements ComponentRemovalPredicate {

	private double m_xCoeff;
	private double m_yCoeff;
	private double m_constantCoeff;
	private double m_thickness;

	/**
	 * @param xCoeff first coefficient of the plane's equation
	 * @param yCoeff second coefficient of the plane's equation
	 * @param constantCoeff third coefficient of the plane's equation
	 * @param thickness thickness of the plane
	 */
	public ComponentRemovalLinearComplement(double xCoeff, double yCoeff, double constantCoeff, double thickness) {
		this.m_xCoeff = xCoeff;
		this.m_yCoeff = yCoeff;
		this.m_constantCoeff = constantCoeff;
		this.m_thickness = thickness;
	}
	/**
	 * @see wrapScienceJ.wrapImaJ.connectivity.filtering.ComponentRemovalPredicate#keepVoxelComponent(wrapScienceJ.wrapImaJ.core.VoxelShort, wrapScienceJ.wrapImaJ.connectivity.ComponentInfo)
	 */
	@Override
	public boolean keepVoxelComponent(VoxelShort voxel, ComponentInfo componentInfo) {
		double zValue = this.m_xCoeff*voxel.getX() + this.m_yCoeff*voxel.getY() 
				        + this.m_constantCoeff;
		return ((voxel.getZ() < zValue) || (voxel.getZ() >= zValue + this.m_thickness));
	}

}
