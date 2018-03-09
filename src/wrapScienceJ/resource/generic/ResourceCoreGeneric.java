/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ResourceCoreGeneric.java                                           * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.resource.generic;

import java.io.File;
import java.io.IOException;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.io.stream.FileHelper;
import wrapScienceJ.metaData.container.MetaDataRetriever;
import wrapScienceJ.resource.ResourceCore;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;

/**
 * @author remy
 *
 */
public abstract class ResourceCoreGeneric implements ResourceCore {
	
	
	protected ModelCoreImageGeneric m_modelCoreImage;
	

	/**
	 * @see wrapScienceJ.resource.ResourceCore#duplicate()
	 */
	public abstract ResourceCore duplicate();
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#getPath()
	 */
	public abstract String getPath();

	/**
	 * @see wrapScienceJ.resource.ResourceCore#getTitle()
	 */
	public abstract String getTitle();

	/**
	 * @see wrapScienceJ.resource.ResourceCore#setTitle(java.lang.String)
	 */
	public abstract ResourceCore setTitle(String title);

	/**
	 * @see wrapScienceJ.resource.ResourceCore#writeToFile(java.lang.String)
	 */
	public abstract ResourceCore writeToFile(String destinationFilePath) throws IOException;

	/**
	 * @see wrapScienceJ.resource.ResourceCore#getPreferedRenderTool()
	 */
	public abstract RenderTool getPreferedRenderTool();
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#getFileHelper()
	 */
	public abstract FileHelper getFileHelper();

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getMetaData()
	 */
	@Override
	public ModelCoreImageGeneric getMetaData() {
		return this.m_modelCoreImage;
	}


	/**
	 * @see wrapScienceJ.resource.ResourceCore#addMetaData(wrapScienceJ.metaData.container.MetaDataRetriever)
	 */
	@Override
	public ResourceCoreGeneric addMetaData(MetaDataRetriever config) {
		this.m_modelCoreImage.addMetaData(config);
		return this;
	}	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#writeToFileWithMetaData(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ResourceCoreGeneric writeToFileWithMetaData(String directory, String basename, String extension) throws IOException{
		writeToFile(directory+File.separator+basename+"."+extension);
		this.m_modelCoreImage.writeToFile(directory, basename);
		return this;
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getDefaultMetaDataDir()
	 */
	@Override
	public String getDefaultMetaDataDir() {
		return GlobalOptions.getGlobalMetaDataDir()+"wrapImaJ"+File.separator;
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#retrieveMetaData()
	 */
	@Override
	public ResourceCoreGeneric retrieveMetaData() throws IOException {
		getMetaData().retrieve(getPath());
		return this;
	}

	/**
	 * @see wrapScienceJ.resource.ResourceCore#mergeMetaData(wrapScienceJ.resource.ResourceCore)
	 */
	@Override
	public ResourceCore mergeMetaData(ResourceCore resource) {
		getMetaData().merge(resource.getMetaData());
		getMetaData().updateResourceFromMetaData();
		return this;
	}

}
