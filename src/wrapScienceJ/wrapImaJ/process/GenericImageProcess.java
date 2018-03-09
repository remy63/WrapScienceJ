/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GenericImageProcess.java                                           * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.process;

import wrapScienceJ.process.GenericProcess;



/**
 * Abstract Base class to implement processes involving an image.
 * Typically, this is used to implement plugins such as ImageJ plugins
 * using the WrapImaJ API and ImageCore to encode an image.
 **/
public interface GenericImageProcess extends GenericProcess, PolicyImageStorage {



	
	/**
	 * @param arg a generic argument if the process requires additional data or methods
	 * @param option An optionnal string transmitted to the process (e.g. directory, path, etc.)
	 * @return a generic object in case the process is required to return an object
	 */
	public Object runProcess(Object arg, String option);
	
	
}
