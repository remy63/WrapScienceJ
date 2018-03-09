/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GenericImageProcessConcrete.java                                   * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process;


import wrapScienceJ.config.GlobalOptions;

import wrapScienceJ.factory.gui.GuiFrameworkFactoryIJ;
import wrapScienceJ.gui.GuiFramework;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.process.ProcessInputOutput.OutputDataKind;
import wrapScienceJ.resource.generic.ModelCoreGeneric;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.process.plugins.PluginFilterFake;
import wrapScienceJ.wrapImaJ.process.plugins.PluginFilterGeneric;

/**
 * 
 *
 */
public abstract class GenericImageProcessConcrete extends PluginFilterGeneric
										implements GenericImageProcess, PluginFilterFake{
	
	/**
	 * @param image The image on which connected components must be labeled and processed.
	 * @param title Title for the metadata tha determines the metadata file name.
	 * @param policy Policy for retrieval of the metadata (file, dialog box...)
	 * @param subdir Sub-directory of the default process metadata directory in which to look for the metadata.
	 * @param outputDataKind The kind of output in relation to input data
	 * 						 (equal reference, copy, etc.)
	 * @param renderToolOutput The tool to render the output of the process
	 * @param guiFramework The Graphical User Interface Framework for human machine interface.
	 */
	public GenericImageProcessConcrete(ImageCore image,
									   String title, RetrievalPolicy policy, String subdir,
									   OutputDataKind outputDataKind,
									   RenderTool renderToolOutput, GuiFramework guiFramework) {
		setInputImage(image);
		setRetrievalPolicy(policy);
		this.m_renderTool = renderToolOutput;
		this.m_guiFramework = guiFramework;
		this.m_processConcreteModelCore = new ModelCoreGeneric();
		
		setOutputDataKind(outputDataKind);
		instantiateMetaData(title, policy, subdir);
	}
	
	/**
	 * The render tool used is the ImageCore implementer's prefered display tool.
	 * @param image The image on which connected components must be labeled and processed.
	 * @param title Title for the metadata tha determines the metadata file name.
	 * @param policy Policy for retrieval of the metadata (file, dialog box...)
	 * @param subdir Sub-directory of the default process metadata directory in which to look for the metadata.
	 * @param outputDataKind  The kind of output in relation to input data
	 * 						 (equal reference, copy, etc.)
	 */
	public GenericImageProcessConcrete(ImageCore image, String title,
									   RetrievalPolicy policy, String subdir, 
									   OutputDataKind outputDataKind) {
		this(image, title, policy, subdir, outputDataKind, image.getPreferedRenderTool(),
			 GuiFrameworkFactoryIJ.getInstance().getGuiFramework());
	}

	/**
	 * Dummy default constructor for compatibility as a subclass.
	 */
	protected GenericImageProcessConcrete() {
		this.m_guiFramework = GuiFrameworkFactoryIJ.getInstance().getGuiFramework();
	}
	

	
	
	/**
	 * Initializes the current image on which the process will work.
	 * to initialize the current image.
	 * Only in the case when {@link PluginFilterGeneric#m_outputDataKind} is equal to
	 * @param metaDataTitle Title of the metaData that determines the metadata file names.
	 * {@link OutputDataKind#EqualsInput} is the input image assumed to be modified.
	 * 
	 */
	@Override
	public void setupFake(ImageCore image, String metaDataTitle) {
		setInputImage(image);
		initMetaData(metaDataTitle);
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.process.plugins.PluginFilterFake#runFake(java.lang.Object)
	 */
	@Override
	public void runFake(Object inputObject) {
		runOnCurrentImage(inputObject);
	}

	
	/**
	 * Allows to test the plugin using an image file as input image.
	 * @param metaDataTitle Title of the metaData that determines the metadata file names.
	 * @param renderTool The RenderTool used to display the output
	 * @param inputObject The object to transmit to the process runProcess method
	 */
	@Override
	public void testPlugin(RenderTool renderTool, String metaDataTitle, Object inputObject) {
		
		// Emulate "Last Opened Image" properties in an actual imagej plugin.
		String defaultDir = GlobalOptions.getDefaultInputDir();
		String pathString;
		if (getInputImagePath() == null){
			pathString = GlobalOptions.getDefaultInputDir();	
		}else{
			pathString = getInputImagePath();
		}
		
		getInputImageMetaData().setMetaDataFileName(pathString, "Calibration3D", defaultDir);
		System.err.println("testPlugin "+getInputImageMetaData());
		
		moveInputToCurrent(this.m_outputDataKind == OutputDataKind.CreatedFromInputCopy);
		super.testPlugin(renderTool, metaDataTitle, inputObject);

	}
	

}
