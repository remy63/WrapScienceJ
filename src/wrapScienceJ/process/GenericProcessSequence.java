/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GenericProcessSequence.java                                        * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.process;

import wrapScienceJ.wrapImaJ.core.*;
import wrapScienceJ.wrapImaJ.process.GenericImageProcess;

/**
 * Abstract Base class to implement processes involving an image.
 * Typically, this is used to implement plugins such as ImageJ plugins
 * using the WrapImaJ API and ImageCore to encode an image.
 **/
public abstract class GenericProcessSequence extends GenericProcessNode implements GenericImageProcess {

	protected ImageCore m_image;
	
	/**
	 * @param image The image on which to apply all the processes sequentially
	 * @param processes An ordered collection of processes taking an ImageCore as processArg Object
	 * @see GenericProcessNode#GenericProcessNode()
	 * @see GenericProcessNode#runProcess(Object, String)
	 */
	public GenericProcessSequence(ImageCore image, GenericProcessConcrete[] processes) {
		super();
		for (GenericProcessConcrete process: processes){
			addChild(process);
		}
	}
	
	/** Getter allowing to acces a reference to the image
	 * @return this.m_image
	 */
	public ImageCore getInputImage() {
		return this.m_image;
	}

	/** Setter to set the image
	 * @param image the image to use for this.m_image
	 */
	public void setInputImage(ImageCore image) {
		this.m_image = image;
	}
}
