/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: PluginFilterWrap.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process.plugins;

import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.process.GenericImageProcess;

/**
 * This class implements 
 *
 */
public interface PluginFilterWrap extends GenericImageProcess {
	
	
	/**
	 * Method that retrieves the input image, and possibly copies it to initialize the current image.
	 * Then calls method @link{GenericImageProcess#runProcess()} method,
	 * which is to be implemented in subclasses.
	 */
	public void runOnCurrentImage();
	
	/**
	 * Default output processing for the plugin. In general, the implementation
	 * depends on the result of {@link GenericImageProcess#getOutputDataKind()}.
	 * In case the output is not a single image, this method is to be overriden by a subclass class. 
	 */
	public void processOutput();
	
	/**
	 * Allows to test the process on the input image
	 * @param renderTool The RenderTool used to display the output
	 */
	public void testPlugin(RenderTool renderTool);
}
