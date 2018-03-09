/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaCalibration3D.java                                             * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.core.metaData;


import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.metaData.container.MetaDataContainer;
import wrapScienceJ.metaData.container.MetaDataRetriever;
import wrapScienceJ.wrapImaJ.core.ImageCalibration;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelDouble;


/**
 * Allows to ask the user for the calibration parameters for an image.
 */
public class MetaCalibration3D extends MetaDataRetriever {
	
	ImageCore m_image;
	
	MetaDataContainer m_config;
	
	protected static String m_configFilePostfix = "Calibration3D";

	/**
	 * @return Allows to retrieve the config title (file postfix) corresponding to calibraion meta data
	 */
	public static String getConfigFilePostfix(){
		return m_configFilePostfix;
	}

	/**
	 * @param image the image for which calibration data is required.
	 * (e.g. requires to calibrate from a configuration file with metadata or a dialog)
	 * The config file's postfix is hard coded in the static attribute {@link #m_configFilePostfix}.
	 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
	 * @param subdir Sub-directory of the default gobal directory in which the default global metadata
	 * 				file should be looked for.
	 * @param voxelsSize a voxel the coordinates of which represent the lengths of the edges of all
	 * 					 voxels in an anisotropic image.
	 * 					 e.g. Voxel width=0.103; Voxel height=0.103; Voxel depth=0.2; unit=µm
	 * @param lengthUnit the length unit in which the edges lengths are expressed (e.g. Lenth unit=µm)
	 */
	public MetaCalibration3D(ImageCore image, RetrievalPolicy retrievalPolicy, String subdir,
							 VoxelDouble voxelsSize, String lengthUnit) {
		super(m_configFilePostfix, retrievalPolicy, subdir);
		super.addAttribute("type=DoubleAttrib; description=Voxel width; value="+voxelsSize.getX());
		super.addAttribute("type=DoubleAttrib; description=Voxel height; value="+voxelsSize.getY());
		super.addAttribute("type=DoubleAttrib; description=Voxel depth; value="+voxelsSize.getZ());
		super.addAttribute("type=StringAttrib; description=Length unit; value="+lengthUnit);
		this.m_image = image;
		updateModelFromConfig();
	}
	
	/**
	 * 
	 * @param image the image for which calibration data is required.
	 * (e.g. requires to calibrate from a configuration file with metadata or a dialog)
	 * The config file's postfix is hard coded in th static attribute {@link #m_configFilePostfix}.
	 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
	 * @param subdir Sub-directory of the default gobal directory in which the default global metadata
	 * 				file should be looked for.
	 */
	public MetaCalibration3D(ImageCore image, RetrievalPolicy retrievalPolicy, String subdir){
		super(m_configFilePostfix, retrievalPolicy, subdir);
		super.addAttribute("type=DoubleAttrib; description=Voxel width; value=1.0");
		super.addAttribute("type=DoubleAttrib; description=Voxel height; value=1.0");
		super.addAttribute("type=DoubleAttrib; description=Voxel depth; value=1.0");
		super.addAttribute("type=StringAttrib; description=Length unit; value=1.0");
		this.m_image = image;
		retrieveConfigFromModel();
	}
	
	/**
	 * @return a full copy of this instance.
	 */
	public MetaCalibration3D duplicate(){
		MetaCalibration3D copy =  new MetaCalibration3D(this.m_image, RetrievalPolicy.UseKnownValues,
														this.m_subdir);
		copy.addDuplicate(this);
		copy.setMetaDataFileName(this.m_image.getPath(), GlobalOptions.getDefaultInputDir());
		return copy;
	}
	
	
	/**
	 * ALlows to calibrate an image from the data contained in the configuration.
	 */
	@Override
	public void updateModelFromConfig(){
		VoxelDouble voxelSize = new VoxelDouble(
				((Double)getAttribute("Voxel width").getAttributeValue()).doubleValue(),
				((Double)getAttribute("Voxel height").getAttributeValue()).doubleValue(),
				((Double)getAttribute("Voxel depth").getAttributeValue()).doubleValue());
    	String lengthUnit = (String)getAttribute("Length unit").getAttributeValue();
		this.m_image.getImageCalibration().setVoxelLength(voxelSize);
		this.m_image.getImageCalibration().setUnitLenth(lengthUnit);
		
		retrieveConfigFromModel();
	}
	
	/**
	 * @see wrapScienceJ.metaData.container.MetaDataRetriever#retrieveConfigFromModel()
	 */
	@Override
	public void retrieveConfigFromModel() {
		ImageCalibration cal = this.m_image.getImageCalibration();
		boolean isCalibrated = cal.isCalibrated();		

		if (isCalibrated){
			setAttributeValue("Voxel width", new Double(cal.getVoxelWidth()));
			setAttributeValue("Voxel height", new Double(cal.getVoxelHeight()));
			setAttributeValue("Voxel depth", new Double(cal.getVoxelDepth()));
			setAttributeValue("Length unit", cal.getUnitLength());
		}
	}
	
	/**
	 * @see wrapScienceJ.metaData.container.MetaDataContainer#toString()
	 */
	@Override
	public String toString(){
		return super.toString();
	}
}
