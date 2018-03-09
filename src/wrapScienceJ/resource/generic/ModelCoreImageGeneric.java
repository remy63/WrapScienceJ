/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ModelCoreImageGeneric.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.resource.generic;

import java.io.File;

import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.metaData.MetaCalibration3D;

/**
 * Specialization of ModelCoreGeneric including handling of standard image-related metadata
 * such as calibration data for non isotropic images.
 */
public class ModelCoreImageGeneric extends ModelCoreGeneric {

	/** The image for which the metadata is to be processed and stored */
	protected ImageCore m_image;
	
	protected MetaCalibration3D m_metaCalibration3D;
	
	/**
	 * The default directory for the predefined metadata is sought in the "predefined" sub-directory. 
	 * @param image The image for which the metadata is to be processed and stored
	 * @param retrievalPolicy The policy to retrieve the predefined metadata associated with images
	 */
	public ModelCoreImageGeneric(ImageCore image, RetrievalPolicy retrievalPolicy) {
		super();
		this.m_metaCalibration3D = new MetaCalibration3D(image, retrievalPolicy, "wrapImaJ"+File.separator+"predefined");
		addMetaData(this.m_metaCalibration3D);
		this.m_image = image;
	}
	
	/**
	 * Creates an instance with empty metadata.
	 * The retrieval policy is {@link RetrievalPolicy#TryConfFileNoDialog}
	 * @param image The image for which the metadata is to be processed and stored
	 */
	public ModelCoreImageGeneric(ImageCore image){
		this(image, RetrievalPolicy.TryConfFileNoDialog);
	}
	
	/**
	 * Allows to copy the predefined metadata from one image to another for them to have the same attributes.
	 * 
	 * @param imageTarget The image for which the metadata is to be defined
	 * @param imageSource The image for which the metadata is to be copied from.
	 */
	public ModelCoreImageGeneric(ImageCore imageTarget, ImageCore imageSource) {
		this(imageTarget);
		merge(imageSource.getMetaData());	
		updateResourceFromMetaData();
	}
	
	
	/**
	 * Allows to apply the predefined metadata associated with this resource on the resource internal state.
	 */
	public void updateResourceFromMetaData() {
		this.m_metaCalibration3D.updateModelFromConfig();
	}
	
}
