/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: RenderTool.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.gui.render;

import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * General interface for rendering tools. Implementers are used to display data such as images.
 *
 */
public interface RenderTool {
	/**
	 * Displays Image data in a GUI Window using a rendering context.
	 * @param image An image to display
	 */
	public void display(ImageCore image);
}
