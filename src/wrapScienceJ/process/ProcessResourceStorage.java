/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ProcessResourceStorage.java                                        * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.process;


import wrapScienceJ.process.ProcessInputOutput;
import wrapScienceJ.resource.ModelCore;
import wrapScienceJ.resource.ResourceCore;

/**
 * @author remy
 *
 */
public class ProcessResourceStorage extends ProcessInputOutput implements PolicyResourceStorage{

	
	/** Input Resource on which this process is to be applied */
	private ResourceCore m_inputResource;

	
	/** Current Resource on which this process is currently working */
	private ResourceCore m_currentResource;
	
	/**
	 * Allows to initialize the input image.
	 * @param inputResource The image to use as input.
	 */

	protected void setInputResource(ResourceCore inputResource){
		this.m_inputResource = inputResource;
	}
	
	/** 
	 * @return Current Resource on which this process is currently working 
	 */
	@Override
	public ResourceCore getCurrentResource(){
		return this.m_currentResource;
	}

	
	/** 
	 * Getter allowing to access the path to the input image, if available
	 * @return The path stored in this.m_image, if any
	 */
	@Override
	public String getInputResourcePath() {
		return this.m_inputResource.getPath();
	}	
	

	
	/** 
	 * Getter allowing to access the MetaData associated with the input image
	 * @return The MetaData associated with the input image
	 */
	@Override
	public ModelCore getInputResourceMetaData() {
		return this.m_inputResource.getMetaData();
	}


	/**
	 * @see wrapScienceJ.process.PolicyResourceStorage#moveInputToCurrent(boolean)
	 */
	@Override
	public void moveInputToCurrent(boolean duplicate) {
		if (this.m_inputResource == null){
			throw new IllegalStateException("Plugin input image Cannot be found");
		}

		if (duplicate){
			this.m_currentResource = this.m_inputResource.duplicate();
		}else{
			this.m_currentResource = this.m_inputResource;
		}
	}

	/** Clears the Data to release memory */
	public void clearCurrentResource(){
		this.m_inputResource = null;
		this.m_currentResource = null;
		System.gc();
	}
	
	
	/** Sets the Current Resource as well as Input Resource
	 * @param resource The resource to use.
	 */
	public void setCurrentResource(ResourceCore resource){
		this.m_inputResource = resource;
		this.m_currentResource = resource;
	}

	

}