/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ProcessImageStorageGeneric.java                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process.generic;

import wrapScienceJ.process.ProcessResourceStorage;
import wrapScienceJ.resource.ModelCore;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.process.PolicyImageStorage;

/**
 * @author remy
 *
 */
public class ProcessImageStorageGeneric extends ProcessResourceStorage implements PolicyImageStorage{


	/**
	 * Allows to initialize the input image.
	 * @param inputImage The image to use as input.
	 */

	public void setInputImage(ImageCore inputImage){
		setInputResource(inputImage);
	}
	
	/** 
	 * @return Current Image on which this process is currently working 
	 */
	public ImageCore getCurrentImage(){
		return (ImageCore)getCurrentResource();
	}

	
	/** 
	 * Getter allowing to access the path to the input image, if available
	 * @return The path stored in this.m_image, if any
	 */
	public String getInputImagePath() {
		return getInputResourcePath();
	}	
	
	
	/** 
	 * Getter allowing to access the MetaData associated with the input image
	 * @return The MetaData associated with the input image
	 */
	public ModelCore getInputImageMetaData() {
		return getInputResourceMetaData();
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.process.PolicyImageStorage#clearCurrentImage()
	 */
	@Override
	public void clearCurrentImage() {
		clearCurrentResource();
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.process.PolicyImageStorage#setCurrentImage(wrapScienceJ.wrapImaJ.core.ImageCore)
	 */
	@Override
	public void setCurrentImage(ImageCore image) {
		setCurrentResource(image);
	}


}
