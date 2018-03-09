/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: PluginFilterGeneric.java                                           * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process.plugins;


import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.ImageThresholding;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;

import wrapScienceJ.wrapImaJ.process.GenericImageProcess;
import wrapScienceJ.wrapImaJ.process.generic.ProcessImageGuiDisplayGeneric;


/**
 * Generic implementation of a plugin based on the notion of a Generic Image Process.
 * This class implements the code ran by the plugin, executing the Generic Image Process
 * on the current image retrieved by the framework.
 * 
 * A method allows to test the plugin without deploying it on the platfrom,
 * by loading
 *
 */
public abstract class PluginFilterGeneric extends ProcessImageGuiDisplayGeneric implements GenericImageProcess {

	
	/**
	 * Method that retrieves the input image, and possibly copies it to initialize the current image.
	 * Then calls method @link{GenericImageProcess#runProcess()} method,
	 * which is to be implemented in subclasses.
	 * If {@link PluginFilterGeneric#m_outputDataKind} is equal to
	 * {@link wrapScienceJ.process.ProcessInputOutput.OutputDataKind#CreatedFromInputCopy}, then the input image is duplicated
	 * @param inputObject The object to transmit to the process runProcess method
	 */
	public void runOnCurrentImage(Object inputObject) {
		
		// Moved to setup()
		//moveInputToCurrent(this.m_outputDataKind == OutputDataKind.CreatedFromInputCopy);
		
		this.m_guiFramework.createWindow(getCurrentImage(), "WrapImaJ Plugin Result");
		getCurrentImage().getImageCalibration().debugAttributes();
		this.runProcess(inputObject, "default");
		processOutput();
	}
	
	
	
	/**
	 * Default output processing for the plugin. Assumes that the output object implements ImageCore.
	 * In case the output is not a single image, this method is to be overriden by a subclass class. 
	 */
	public void processOutput(){
		ImageCore outputImage;
		if (this.m_outputObject == null){
			outputImage = getCurrentImage();
		}else if (!(this.m_outputObject instanceof ImageCore) &&
				  !(this.m_outputObject instanceof ImageThresholding)){
			if (this.m_outputObject instanceof String){
				this.m_guiFramework.getMessageBox().show((String)this.m_outputObject);
				return;
			}
			throw new IllegalStateException("Default output processing assumes the output to be an image");
		}
		System.err.println("\nPluginProcess Complete.");
		outputImage = (ImageCore)this.m_outputObject;
		this.m_renderTool.display(outputImage);
	}
	
	/**
	 * Allows to test the plugin using an image file as input image.
	 * The RenderTool used to display the output is retrieved through
	 * Uses the internal attributes for the metadata title and the rendering tool for the output.
	 * @param inputObject The object to transmit to the process runProcess method
	 * {@link GlobalOptions#getDefaultRenderTool()}
	 */
	public void testPlugin(Object inputObject) {
		testPlugin(GlobalOptions.getDefaultRenderTool(), null, inputObject);
	}
	
	/**
	 * Allows to test the plugin on the input image
	 * @param metaDataTitle Title of the metaData (overrides the internal attributes) that determines the metadata file names.
	 * @param renderTool The RenderTool used to display the output (overrides the internal attributes)
	 * @param inputObject The object to transmit to the process runProcess method
	 */
	public void testPlugin(RenderTool renderTool, String metaDataTitle, 
							Object inputObject) {
		
		this.m_renderTool = renderTool;
		initMetaData(metaDataTitle);
		runOnCurrentImage(inputObject);
	}
}
