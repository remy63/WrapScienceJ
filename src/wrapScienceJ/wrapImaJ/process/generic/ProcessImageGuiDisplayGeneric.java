/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ProcessImageGuiDisplayGeneric.java                                 * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process.generic;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.gui.GuiFramework;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.process.PolicyImageGuiDisplay;

/**
 * Implements storage for the Graphical User Interface Information
 */
public abstract class ProcessImageGuiDisplayGeneric extends ProcessImageInputOutputGeneric
									implements PolicyImageGuiDisplay {

	/** Instance of the Framework managing the GUI */
	protected GuiFramework m_guiFramework;
	
	/** Rendering tool used to display the output of the plugin */	
	protected RenderTool m_renderTool;

	protected ProcessImageGuiDisplayGeneric(){
		this(GlobalOptions.getDefaultGuiFramework(), GlobalOptions.getDefaultRenderTool());
	}
	
	protected ProcessImageGuiDisplayGeneric(GuiFramework guiFramework, RenderTool renderTool){
		this.m_guiFramework = guiFramework;
		this.m_renderTool = renderTool;
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.process.PolicyImageGuiDisplay#getGuiFramework()
	 */
	@Override
	public GuiFramework getGuiFramework() {
		return this.m_guiFramework;
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.process.PolicyImageGuiDisplay#getRenderTool()
	 */
	@Override
	public RenderTool getRenderTool() {
		return this.m_renderTool;
	}
	
}
