/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: PluginFilterFake.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process.plugins;

import wrapScienceJ.process.ProcessInputOutput.OutputDataKind;
import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * @author remy
 *
 */
public interface PluginFilterFake {

	/**
	 * Initializes the current image on which the process will work.
	 * to initialize the current image.
	 * Only in the case when {@link PluginFilterGeneric#m_outputDataKind} is equal to
	 * @param metaDataTitle Title of the metaData that determines the metadata file names.
	 * {@link wrapScienceJ.process.ProcessInputOutput.OutputDataKind#EqualsInput} is the input image assumed to be modified.
	 * @param image The input image to use
	 */
	public void setupFake(ImageCore image, String metaDataTitle) ;
	
	/**
	 * @param inputObject The object to transmit to the process runProcess method
	 */
	public void runFake(Object inputObject);
}
