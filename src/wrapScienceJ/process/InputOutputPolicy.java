/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: InputOutputPolicy.java                                             * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.process;

import wrapScienceJ.process.ProcessInputOutput.OutputDataKind;
import wrapScienceJ.resource.ModelCore;

/**
 * @author remy
 *
 */
public interface InputOutputPolicy {

	
	
	/**
	 * @return The output object of the process
	 */
	public Object getOutputObject();
	
	/** 
	 * Allows to retrieve the kind of output of the process for generic post-processing.
	 * @return The kind of output of the process for generic post-processing.
	 * @see OutputDataKind
	 */
	public OutputDataKind getOutputDataKind();
	
	/** 
	 * Allows to specify the kind of output of the process for generic post-processing
	 * @param outputDataKind The kind of output of the process for generic post-processing.
	 * @see OutputDataKind
	 */
	public void setOutputDataKind(OutputDataKind outputDataKind);
	
	/** 
	 * Getter allowing to access the path to the input resource, if available
	 * @return The path where the resource comes from, if any
	 */
	public String getInputResourcePath();
	
	/** 
	 * Getter allowing to access the MetaData associated with the input resource
	 * @return The metadata associated to the resource
	 */
	public ModelCore getInputResourceMetaData();
}
