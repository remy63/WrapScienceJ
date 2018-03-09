/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ComponentInfo.java                                                 * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.connectivity;



import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.BoxROI;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.VoxelShort;


/**
 * Represents the informations relative to a connected component in a binary image.
 * 
 * @author Remy Malgouyres
 */
public class ComponentInfo
{

	/**
	 * Label (ID) of the connected component (i.e. color in the labels image array)
	 */
	private short m_label;

	/**
	 * Cardinality of the connected component. 
	 * Can be zero if the connected component has been filtered out
	 * (e.g. threshold size or border components exclusion)
	 */
	private long m_numberOfPoints;

	/**
	 * Voxel representative of the component (one voxel in the component)
	 * Currently, this representative has minimal depth Z
	 * TODO extend usage using a comparison predicate possibly other that comparing depth.
	 */
	private VoxelInt m_voxelRepresentant;

	/**
	 * Number of voxels in the component that are on the edge of the image.
	 * (allows filtering out connected component which touch the edge of the image.)
	 */
	private int m_nVoxelsOnTheBorder;
	
	/**
	 * Bounding Box for the voxels of this component.
	 */
	private BoxROI m_boundingBox;
	
	/**
	 * Constructor
	 * @param label  label of the connected component (i.e. color in the labels image array)
	 * @param numberOfPoints (initial) cardinality of the connected component. 
	 * @param voxelRepresentant (initial) voxel representative of the component (one voxel in the component)
	 * @param nVoxelsOnTheBorder Number of voxels in the component which are on the the edge of the image.
	 */
	public ComponentInfo(short label, int numberOfPoints,
			VoxelInt voxelRepresentant, int nVoxelsOnTheBorder) {
		this.m_label = label;
		this.m_numberOfPoints = numberOfPoints;
		this.m_voxelRepresentant = voxelRepresentant;
		this.m_nVoxelsOnTheBorder = nVoxelsOnTheBorder;
		this.m_boundingBox = new BoxROI();
	}

	/**
	 * Getter.
	 * @return the label of the component
	 */
	public short getLabel(){ 
		return this.m_label; 
	}

	/**
	 * Setter  for the label of the component
	 * @param label the label to use
	 */
	public void setLabel(short label){ 
		this.m_label = label; 
	}

	/**
	 * Getter
	 * @return the cardinality of the component
	 */
	public long getnumberOfPoints(){ 
		return this.m_numberOfPoints; 
	}
	
	
	/**
	 * Bounding Box for the voxels of this component.
	 * @return The bounding box of the voxels of this compnents.
	 */
	public BoxROI getBoundingBox(){
		return this.m_boundingBox;
	}
	
	/**
	 * Update the boundaries of the BoxROI to take into account that a given voxel
	 * must be in the box.
	 * @param voxelInTheBox A voxel that should be contained in the box.
	 */
	public void updateBox(VoxelShort voxelInTheBox){
		this.m_boundingBox.updateBox(voxelInTheBox);
	}
	
	/**
	 * Increments the cardinality
	 */
	public void incrementNumberOfPoints(){
		this.m_numberOfPoints++;
	}

	/**
	 * Setter
	 * @param numberOfPoints the cardinality to set
	 */
	public void setNumberOfPoints(int numberOfPoints){
		this.m_numberOfPoints = numberOfPoints;
	}

	/**
	 * Getter
	 * @return returns the component's flag indicating whether the component is on the border.
	 */
	public boolean isOnTheeBorder(){
		return this.m_nVoxelsOnTheBorder > 0;
	}

	/**
	 * Getter
	 * @return the voxel representative of the component (one voxel in the component)
	 */
	public VoxelInt getRepresentant() {
		return this.m_voxelRepresentant; 
	}

	/**
	 * Increments the number of voxels in the component which are on the edge of the image.
	 */
	public void incrementNVoxelsOnTheeBorder() {
		this.m_nVoxelsOnTheBorder++;
	}

	/**
	 * Allows to swap coordinates axis in the voxel's domain by modifying the attributes
	 * depending on coordinates such as {@link #m_voxelRepresentant} or {@link #m_boundingBox}.
	 * @param axis1 The first axis to be swapped
	 * @param axis2 The second axis to be swapped
	 */
	public void getAxisSwapped(CoordinateAxis axis1, CoordinateAxis axis2){
		this.m_voxelRepresentant.getAxisSwapped(axis1, axis2);
		this.m_boundingBox.getAxisSwapped(axis1, axis2);
	}
	
	/**
	 * @return a human readable string representation of this instance
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Component label : "+this.m_label+", Number of points : "+this.m_numberOfPoints;
	}

} // end of class ComponentInfo

