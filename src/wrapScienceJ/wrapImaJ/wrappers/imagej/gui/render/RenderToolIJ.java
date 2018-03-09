/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: RenderToolIJ.java                                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.gui.render;

import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;

/**
 * Main utility to manage Graphical User Interface implemented on the ImageJ Framework
 * Allows to include GUI elements in ImageJ plugins generated through WrapImaJ
 * 
 * Follows the Singleton Design Pattern
 */
public class RenderToolIJ implements RenderTool {

	/** Unique instance of this class as in the Singleton Design Pattern */
	private static RenderToolIJ m_instance = null;
	
	
	protected RenderToolIJ(){
	}
	
	/**
	 * @return The unique instance of GuiFrameworkIJ
	 */
	public static RenderToolIJ getInstance(){
		if (m_instance == null){
			m_instance = new RenderToolIJ();
		}
		return m_instance;
	}
	
	/**
	 * Opens a Graphical Human Interface window and displays the surface.
	 */
	@Override
	public void display(ImageCore image) {
		if (image instanceof ImageCoreIJ){
			((ImageCoreIJ)image).getImp().show();
			((ImageCoreIJ)image).getImp().updateAndRepaintWindow();
		}
	}

}
