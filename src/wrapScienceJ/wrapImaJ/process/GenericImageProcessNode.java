/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GenericImageProcessNode.java                                       * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process;



import java.util.ArrayList;

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
public abstract class GenericImageProcessNode extends GenericImageProcessConcrete
										implements PluginFilterFake{
	
	ArrayList<PluginFilterGeneric> m_childrenProcesses;
	
	
	/**
	 * @param image The image on which connected components must be labeled and processed.
	 * @param renderToolOutput The too to render the output of the process
	 * @param guiFramework The Graphical User Interface Framework for human machine interface.
	 */
	public GenericImageProcessNode(ImageCore image, RenderTool renderToolOutput, GuiFramework guiFramework) {
		this.m_childrenProcesses = new ArrayList<PluginFilterGeneric>();
		this.m_renderTool = renderToolOutput;
		setInputImage(image);
		this.m_guiFramework = guiFramework;
	}
	
	/**
	 * Dummy default constructor for compatibility as a subclass.
	 */
	protected GenericImageProcessNode() {
		this.m_guiFramework = GuiFrameworkFactoryIJ.getInstance().getGuiFramework();
	}

	
	/**
	 * Allows to create a Generic Process Node with a given collection of children
	 * @param image The image on which connected components must be labeled and processed.
	 * @param children a given collection of children to create in the node of proceesses
	 * @param renderToolOutput The too to render the output of the process
	 * @param guiFramework The Graphical User Interface Framework for human machine interface.
	 */
	public GenericImageProcessNode(ImageCore image, PluginFilterGeneric[] children,
								   RenderTool renderToolOutput, GuiFramework guiFramework){
		this (image, renderToolOutput, guiFramework);
		for (PluginFilterGeneric child: children){
			this.addChild(child);
		}
	}
	
	/**
	 * The render tool used is the ImageCore implementer's prefered display tool.
	 * @param image The image on which connected components must be labeled and processed.
	 */
	public GenericImageProcessNode(ImageCore image) {
		this (image, image.getPreferedRenderTool(), 
					 GuiFrameworkFactoryIJ.getInstance().getGuiFramework());
	}
	
	/**
	 * Allows to append a child Generic Process Node to the collection of children processes
	 * @param child a given process to append to the node of processes
	 */
	public void addChild(PluginFilterGeneric child){
		getInputImageMetaData().merge(child.getInputImageMetaData());
		this.m_childrenProcesses.add(child);
	}
	
	/**
	 * Allows to append a child Generic Process Node to the collection of children processes
	 * @param child a given process to append to the node of processes
	 * @param title Title for the metadata (override) that determines the metadata file name.
	 * @param policy Policy (override) for retrieval of the metadata (file, dialog box...)
	 * @param subdir Sub-directory (override) of the default process metadata directory in which to look for the metadata.
	 */
	public void addChild(PluginFilterGeneric child, String title, RetrievalPolicy policy, String subdir){
		getInputImageMetaData().merge(child.instantiateMetaData(title, policy, subdir));
		this.m_childrenProcesses.add(child);
	}
	

	
	
	/**
	 * @return The aggregation of the metadata of all children processes
	 * as obtained by {@link wrapScienceJ.process.GenericProcess#getConfig()}
	 */
	public ModelCoreGeneric getChildrenConfig(){
		ModelCoreGeneric modelCore = new ModelCoreGeneric();
		for (PluginFilterGeneric process: this.m_childrenProcesses){
			modelCore.merge(process.getConfig());
		}
		return modelCore;
	}
	
	/**
	 * @param arg a generic argument if the process requires additional data or methods
	 * @param option An optional string transmitted to the process (e.g. directory, path, etc.)
	 * @return a generic object in case the process is required to return an object
	 */
	@Override
	public Object runProcess(Object arg, String option){
		
		int countProcess = 0;	
		int nbProcess = this.m_childrenProcesses.size();
		PluginFilterGeneric lastProcess = null;
		for (PluginFilterGeneric process: this.m_childrenProcesses){
			countProcess++;
			if (countProcess == 1){
				process.setInputImage(getCurrentImage());
				lastProcess = process;
			}else{
				process.setInputImage((ImageCore)lastProcess.getOutputObject());
				lastProcess = process;
			}
			process.moveInputToCurrent(false);
			process.runProcess(arg, option);
			if (countProcess == nbProcess){
				this.m_outputObject = process.getOutputObject();
			}
		}
		
		return this.m_outputObject;
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
		moveInputToCurrent(this.m_outputDataKind == OutputDataKind.CreatedFromInputCopy);
		initMetaData(metaDataTitle);
	}

	
	/**
	 * @see wrapScienceJ.wrapImaJ.process.GenericImageProcessConcrete#runFake(java.lang.Object)
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
		
		getInputImageMetaData().setMetaDataFileName(pathString, metaDataTitle, defaultDir);
		System.err.println("testPlugin "+getInputImageMetaData());
		
		moveInputToCurrent(this.m_outputDataKind == OutputDataKind.CreatedFromInputCopy);
		super.testPlugin(renderTool, metaDataTitle, inputObject);

	}
	




}
