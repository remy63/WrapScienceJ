/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: PolicyResourceStorage.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.process;

import wrapScienceJ.process.InputOutputPolicy;
import wrapScienceJ.resource.ResourceCore;

/**
 * @author remy
 *
 */
public interface PolicyResourceStorage extends InputOutputPolicy{
	
	/**
	 * Moves or copies the input resource to the current resource for processing
	 * @param duplicate If true, the input resource should be copied for saving the original.
	 */
	public void moveInputToCurrent(boolean duplicate);
	
	/** 
	 * @return Current Image on which this process is currently working 
	 */
	public ResourceCore getCurrentResource();
	
}
