/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageCalibrationIJ.java                                            * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.core;

import ij.ImagePlus;
import ij.measure.Calibration;
import wrapScienceJ.wrapImaJ.core.ImageCalibration;
import wrapScienceJ.wrapImaJ.core.VoxelDouble;

/**
 * Allows to manage non isotropic images, and to store and access voxel's sizes and volume
 */
public class ImageCalibrationIJ implements ImageCalibration {
	/**
	 * Image Data as an ImagePlus instance using ImageJ (shorthand)
	 */
	private ImagePlus m_imp;
	
	/**
	 * @param image The image to process
	 */
	public ImageCalibrationIJ(ImageCoreIJ image){
		this.m_imp = image.getImp();
	}
	
	/**
	 * Retrieves the edges lengths of each Voxel in a given unit
	 * e.g. in micro-meters for microscopy images, in centimeters for MNR images, etc.
	 * @return A voxel the coordinates of which give edges lengths for the voxels of an image
	 */
	@Override
	public VoxelDouble getVoxelLength() {
		return new VoxelDouble(this.m_imp.getCalibration().pixelWidth,
							  this.m_imp.getCalibration().pixelHeight,
							  this.m_imp.getCalibration().pixelDepth);
	}
	
	/**
	 * Allows to set the edges lengths of each Voxel in a given unit
	 * e.g. in micro-meters for microscopy images, in centimeters for MNR images, etc.
	 * @param voxelEdgesLength A voxel the coordinates of which give edges lengths for the voxels of an image
	 */
	@Override
	public void setVoxelLength(VoxelDouble voxelEdgesLength) {
		Calibration calibration = this.m_imp.getCalibration();
		calibration.pixelWidth = voxelEdgesLength.getX();
		calibration.pixelHeight = voxelEdgesLength.getY();
		calibration.pixelDepth = voxelEdgesLength.getZ();
		
		this.m_imp.setRoi(0,0,this. m_imp.getWidth()-1, this.m_imp.getHeight()-1);
		this.m_imp.killRoi();
	}	
	
	/**
	 * Allows to retrieve the unit length within an image
	 * e.g. in micro-meters ("µm" or "microns") for microscopy images, in centimeters for MNR images, etc.
	 * @return unit a valid string representing a length unit
	 */
	public String getUnitLength(){
		Calibration calibration = this.m_imp.getCalibration();
		return calibration.getUnit();
	}
	
	/**
	 * Allows to set the unit length within an image
	 * e.g. in micro-meters ("µm" or "microns") for microscopy images, in centimeters
	 * for MNR images, etc.
	 * @param unit a valid string representing a length unit
	 */
	@Override
	public void setUnitLenth(String unit) {
		Calibration calibration = this.m_imp.getCalibration();
		calibration.setUnit(unit);
		
		this.m_imp.setRoi(0,0,this. m_imp.getWidth()-1, this.m_imp.getHeight()-1);
		this.m_imp.killRoi();
	}	
	
	/**
	 * Retrieves the width of a voxel according to the (possibly non isotropic)
	 * image's calibration data.
	 * @return  the width of a voxel 
	 */
	@Override
	public double getVoxelWidth() {
		return this.m_imp.getCalibration().pixelWidth;
	}

	/**
	 * Retrieves the height of a voxel according to the (possibly non isotropic) image's calibration data.
	 * @return  the height of a voxel 
	 */
	@Override
	public double getVoxelHeight() {
		return this.m_imp.getCalibration().pixelHeight;
	}


	/**
	 * Retrieves the depth of a voxel according to the (possibly non isotropic) image's calibration data.
	 * @return  the depth of a voxel 
	 */
	@Override
	public double getVoxelDepth() {
		return this.m_imp.getCalibration().pixelDepth;
	}

	/**
	 * Retrieves the volume of each Voxel in a given unit
	 * e.g. in cubic micro-meters for microscopy images, in cubic centimeters for MNR images, etc.
	 * @return the volume as computed by multiplying the lengths of the edges
	 */
	@Override
	public double getVolume() {		
		Calibration calibration = this.m_imp.getCalibration();

		return calibration.pixelWidth*calibration.pixelHeight*calibration.pixelDepth;
	}
	
	/**
	 * @see ImageCalibration
	 */
	@Override
	public boolean isCalibrated() {
		return this.m_imp.getCalibration().scaled();
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
}
