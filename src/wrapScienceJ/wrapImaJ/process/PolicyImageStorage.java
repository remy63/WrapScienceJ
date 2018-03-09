/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: PolicyImageStorage.java                                            * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process;

import wrapScienceJ.process.InputOutputPolicy;
import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * @author remy
 *
 */
public interface PolicyImageStorage extends InputOutputPolicy{
	
	/**
	 * Moves or copies the input resource to the current resource for processing
	 * @param duplicate If true, the input resource should be copied for saving the original.
	 */
	public void moveInputToCurrent(boolean duplicate);
	
	/** 
	 * @return Current Image on which this process is currently working 
	 */
	public ImageCore getCurrentImage();
	
	/** Clears the Data to release memory */
	public void clearCurrentImage();
	
	
	/** Sets a new image as both current and input
	 * @param image The new image to set
	 */
	public void setCurrentImage(ImageCore image);

	
}
