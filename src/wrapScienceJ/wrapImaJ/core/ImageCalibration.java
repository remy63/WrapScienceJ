/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageCalibration.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core;


/**
 * Allows to manage non isotropic images, and to store and access voxel's sizes and volume
 */
public interface ImageCalibration {
	/**
	 * Retrieves the edges lengths of each Voxel in a given unit
	 * e.g. in micro-meters for microscopy images, in centimeters for MNR images, etc.
	 * @return A voxel the coordinates of which give edges lengths for the voxels of an image
	 */
	public VoxelDouble getVoxelLength();
	
	/**
	 * Allows to set the edges lengths of each Voxel in a given unit
	 * e.g. in micro-meters for microscopy images, in centimeters for MNR images, etc.
	 * @param voxelEdgesLength A voxel the coordinates of which give edges lengths for the voxels of an image
	 */
	public void setVoxelLength(VoxelDouble voxelEdgesLength);
	
	/**
	 * Allows to retrieve the unit length within an image
	 * e.g. in micro-meters ("µm" or "microns") for microscopy images, in centimeters for MNR images, etc.
	 * @return unit a valid string representing a length unit
	 */
	public String getUnitLength();
	
	/**
	 * Allows to set the unit length within an image
	 * e.g. in micro-meters ("µm" or "microns") for microscopy images, in centimeters for MNR images, etc.
	 * @param unit a valid string representing a length unit
	 */
	public void setUnitLenth(String unit);
	/**
	 * Retrieves the width of a voxel according to the (possibly non isotropic) image's calibration data.
	 * @return  the width of a voxel 
	 */
	public double getVoxelWidth();

	/**
	 * Retrieves the height of a voxel according to the (possibly non isotropic) image's calibration data.
	 * @return  the height of a voxel 
	 */
	public double getVoxelHeight();

	/**
	 * Retrieves the depth of a voxel according to the (possibly non isotropic) image's calibration data.
	 * @return  the depth of a voxel 
	 */
	public double getVoxelDepth();
	
	/**
	 * Retrieves the volume of each Voxel in a given unit
	 * e.g. in cubic micro-meters for microscopy images, in cubic centimeters for MNR images, etc.
	 * @return the volume as computed by multiplying the lengths of the edges
	 */
	public double getVolume();
	
	/**
	 * Allows to determine if the image's metadata contains calibration information or not.
	 * @return true if the image's metadata contains calibration information, false otherwise.
	 */
	public boolean isCalibrated();
	
	/**
	 * Writes the attributes (calibration data) on the standard error stream.
	 */
	public void debugAttributes();
}
