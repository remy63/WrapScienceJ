/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageCalibrationGeneric.java                                       * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.core;




/**
 * @author Remy Malgouyres
 * Allows to manage non isotropic images, and to store and access voxel's sizes and volume
 */
public class ImageCalibrationGeneric implements ImageCalibration {
	
	/**
	 * Voxel with floating point coordinates representing edges lengths of each Voxel in a given unit
	 */
	private VoxelDouble m_voxelEdgesLength;
	
	/**
	 * A string describing the length unit for interpretations of the voxel's edges length
	 */
	private String m_unit;

	/**
	 * Constructs an instance from a voxel with floating point coordinates
	 * representing edges lengths of each Voxel in a given unit
	 * @param voxelEdgesLength Voxel with floating point coordinates representing edges lengths
	 * @param unit A string describing the length unit for interpretations of the voxel's edges length
	 */
	public ImageCalibrationGeneric(VoxelDouble voxelEdgesLength, String unit){
		super();
		this.setVoxelLength(voxelEdgesLength);
		this.setUnitLenth(unit);
	}
	
	/**
	 * Constructs an instance from a voxel with floating point coordinates
	 * representing edges lengths of each Voxel in a given unit
	 * @param xLength floating point coordinates representing the X edge length
	 * @param yLength floating point coordinates representing the Y edge length
	 * @param zLength floating point coordinates representing the Z edge length
	 * @param unit A string describing the length unit for interpretations of the voxel's edges length
	 */
	public ImageCalibrationGeneric(float xLength, float yLength, float zLength, String unit){
		this.setVoxelLength(new VoxelDouble(xLength, yLength, zLength));
		this.setUnitLenth(unit);
	}
	
	/**
	 * Retrieves the edges lengths of each Voxel in a given unit
	 * e.g. in micro-meters for microscopy images, in centimeters for MNR images, etc.
	 * @return A voxel the coordinates of which give edges lengths for the voxels of an image
	 */
	@Override
	public VoxelDouble getVoxelLength() {
		return this.m_voxelEdgesLength;
	}
	
	/**
	 * Allows to set the edges lengths of each Voxel in a given unit
	 * e.g. in micro-meters for microscopy images, in centimeters for MNR images, etc.
	 * @param voxelEdgesLength A voxel the coordinates of which give edges lengths for the voxels of an image
	 */
	@Override
	public void setVoxelLength(VoxelDouble voxelEdgesLength) {
		this.m_voxelEdgesLength = voxelEdgesLength;
	}	
	
	/**
	 * Allows to retrieve the unit length within an image
	 * e.g. in micro-meters ("µm" or "microns") for microscopy images, in centimeters for MNR images, etc.
	 * @return unit a valid string representing a length unit
	 */
	@Override
	public String getUnitLength() {
		return this.m_unit;
	}
	
	/**
	 * Allows to set the unit length within an image
	 * e.g. in micro-meters ("µm" or "microns") for microscopy images, in centimeters for MNR images, etc.
	 * @param unit a valid string representing a length unit
	 */
	public void setUnitLenth(String unit){
		this.m_unit = unit;
	}
	
	/**
	 * Retrieves the width of a voxel according to the (possibly non isotropic) image's calibration data.
	 * @return  the width of a voxel 
	 */
	public double getVoxelWidth(){
		return this.m_voxelEdgesLength.getX();
	}

	/**
	 * Retrieves the height of a voxel according to the (possibly non isotropic) image's calibration data.
	 * @return  the height of a voxel 
	 */
	public double getVoxelHeight(){
		return this.m_voxelEdgesLength.getY();
	}

	/**
	 * Retrieves the depth of a voxel according to the (possibly non isotropic) image's calibration data.
	 * @return  the depth of a voxel 
	 */
	public double getVoxelDepth(){
		return this.m_voxelEdgesLength.getZ();
	}
	
	/**
	 * Retrieves the volume of each Voxel in a given unit
	 * e.g. in cubic micro-meters for microscopy images, in cubic centimeters for MNR images, etc.
	 * @return the volume as computed by multiplying the lengths of the edges
	 */
	@Override
	public double getVolume() {
		return this.m_voxelEdgesLength.getX()*this.m_voxelEdgesLength.getY()*this.getVoxelLength().getZ();
	}

	/**
	 * @see ImageCalibration#isCalibrated()
	 * As long as this instance exists, the calibration data exists. So the methods always returns true.
	 */
	@Override
	public boolean isCalibrated() {
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Calibration (in "+ this.getUnitLength() +
				"): Width(" + this.getVoxelWidth() + 
				") Height(" + this.getVoxelHeight() +
				") Depth(" + this.getVoxelDepth() +
				")";
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCalibration#debugAttributes()
	 */
	@Override
	public void debugAttributes() {
		System.err.println(this);
	}

} // End of class
