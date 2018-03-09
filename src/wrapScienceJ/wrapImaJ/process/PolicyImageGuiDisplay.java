/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: PolicyImageGuiDisplay.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process;

import wrapScienceJ.gui.GuiFramework;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;

/**
 * @author remy
 *
 */
public interface PolicyImageGuiDisplay extends PolicyImageStorage {

	/**
	 * @return The Graphical User Interface framework use for this prcesses interactions.
	 */
	public GuiFramework getGuiFramework();
	
	/**
	 * @return The render tool specified for the policy
	 */
	public RenderTool getRenderTool();
}
