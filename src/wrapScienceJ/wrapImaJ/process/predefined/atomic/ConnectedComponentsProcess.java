/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ConnectedComponentsProcess.java                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process.predefined.atomic;


import java.io.File;
import java.io.IOException;



import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.factory.render.RenderToolFactoryIJ;
import wrapScienceJ.gui.GuiFramework;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;

import wrapScienceJ.metaData.sets.*;
import wrapScienceJ.resource.generic.ModelCoreGeneric;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.connectivity.ConnectedComponent;
import wrapScienceJ.wrapImaJ.connectivity.LabelingPolicy;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.process.GenericImageProcessConcrete;


/**
 * @author remy
 *
 */
public class ConnectedComponentsProcess extends GenericImageProcessConcrete {
	
	/**
	 * Default Constructor which invokes the constructor of PluginFilterGeneric
	 * optionally providing it with an argument
	 * @param image The image to process, when not used as a plugin
	 * @param title Title for the metadata tha determines the metadata file name.
	 * @param policy Policy for retrieval of the metadata (file, dialog box...)
	 * @param subdir Sub-directory of the default process metadata directory in which to look for the metadata.
	 * @param outputDataKind The kind of output in relation to input data
	 * 						 (equal reference, copy, etc.)
	 * @param renderToolOutput The too to render the output of the process
	 * @param guiFramework The Graphical User Interface Framework for human machine interface.
	 */
	public ConnectedComponentsProcess(ImageCore image, String title, RetrievalPolicy policy,
									  String subdir, OutputDataKind outputDataKind, 
									  RenderTool renderToolOutput, GuiFramework guiFramework){
		super(image, title, policy, subdir, outputDataKind, renderToolOutput, guiFramework);
	}
	
	/**
	 * Default Constructor which invokes the constructor of PluginFilterGeneric
	 * optionally providing it with an argument
	 * @param image The image to process, when not used as a plugin
	 * @param title Title for the metadata tha determines the metadata file name.
	 * @param policy Policy for retrieval of the metadata (file, dialog box...)
	 * @param subdir Sub-directory of the default process metadata directory in which to look for the metadata.
	 * @param outputDataKind The kind of output in relation to input data
	 * 						 (equal reference, copy, etc.)
	 */
	public ConnectedComponentsProcess(ImageCore image, String title, RetrievalPolicy policy,
									String subdir, OutputDataKind outputDataKind){
		super(image, title, policy, subdir, outputDataKind);
	}
	
	/**
	 * Default Constructor in case this class is used as a plugin
	 */
	public ConnectedComponentsProcess(){
		super();
	}

	/**
	 * MetaData for the parameters of the connected components
	 *
	 */
	public class ProcessMetaData extends MetaDataSet {
		
		/** Lowest volume for a component to consider */
		private DoubleSetSingle m_metaVolumeThreshold;
		
		/** Voxels with that gray level (e.g. 255) will be considered in the foreground */
		private IntegerSetSingle m_metaForegroundColor;
		
		/** If true, the components touching the border of the image will be removed */
		private BooleanSetSingle m_metaRemoveBorderComponents;
		
		/** If true, each connected component in the original image will be filled with
		 * a uniform color, which is chosen randomly. */
		private BooleanSetSingle m_metaSetRandomColors;
		
		/**
		 * @param fileNamePostfix The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
		 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
		 * @param subdir Sub-directory of the default gobal directory in which the default global metadata
		 * 				file should be looked for.
		 */
		public ProcessMetaData(String fileNamePostfix, RetrievalPolicy retrievalPolicy,
							   String subdir) {
			super(fileNamePostfix, retrievalPolicy, subdir);
			
			this.m_metaVolumeThreshold = new DoubleSetSingle("", "Lowest volume", 
														RetrievalPolicy.UseKnownValues);
			this.m_metaForegroundColor = new IntegerSetSingle("", "Foreground Color", 
														RetrievalPolicy.UseKnownValues);
			this.m_metaRemoveBorderComponents  = new BooleanSetSingle("", "Remove Border Components", 
														RetrievalPolicy.UseKnownValues);
			this.m_metaSetRandomColors = new BooleanSetSingle("", "Set Components Colors", 
														RetrievalPolicy.UseKnownValues);
			
			addMetaValue(this.m_metaVolumeThreshold);
			addMetaValue(this.m_metaForegroundColor);
			addMetaValue(this.m_metaRemoveBorderComponents);
			addMetaValue(this.m_metaSetRandomColors);
		}
		
		/** 
		 * @return The lowest volume for a component to consider
		 */
		public double getVolumeThreshold(){
			return this.m_metaVolumeThreshold.getValue();
		}
		
		/** Voxels with that gray level (e.g. 255) will be considered in the foreground 
		 * @return The foreground Color
		 */
		public int getForegroungColor(){
			return this.m_metaForegroundColor.getValue();
		}
		
		/** If true, the components touching the border of the image will be removed
		 * @return true if the components touching the border of the image must be removed
		 */
		public boolean removeBorderComponents(){
			return this.m_metaRemoveBorderComponents.getValue();
		}
		
		/** 
		 * If true, each connected component in the original image will be filled with
		 * a uniform color, which is chosen randomly.
		 * @return true if the colors are to be changed on each component.
		 */
		public boolean setRandomColors(){
			return this.m_metaSetRandomColors.getValue();
		}
		
	}

	
	/**
	 * @see wrapScienceJ.wrapImaJ.process.generic.ProcessImageInputOutputGeneric#getConcreteProcessMetaData(java.lang.String, wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy, java.lang.String)
	 */
	@Override
	public ProcessMetaData getConcreteProcessMetaData(String metaDataTitle, 
										RetrievalPolicy policy, String subdir) {
		if (this.m_processMetaData == null){
			this.m_processMetaData = new ProcessMetaData(metaDataTitle, policy, subdir);
		}
		return this.m_processMetaData;
	}
	
	/** The process concrete metadata as an instance of the ProcessMetaData class above */
	private ProcessMetaData m_processMetaData;
	
	/**
	 * @see GenericImageProcessConcrete#getDefautTitle()
	 */
	@Override
	public String getDefautTitle(){
		return "ConnectedComponents3D";
	}
	
	/**
	 * @see wrapScienceJ.process.GenericProcess#getConfig()
	 */
	@Override
	public ModelCoreGeneric getConfig() {
		return this.m_processConcreteModelCore;
	}

	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.process.GenericImageProcess#runProcess(java.lang.Object, java.lang.String)
	 * @param option If set, describes the directory to retrieve data and configuration
	 */
	@Override
	public Object runProcess(Object arg, String option) {
		
		ImageCore image = getCurrentImage(); // Create a new image for result
		
		image.getPreferedRenderTool().display(image);
		
		ConnectedComponent cc;
		this.m_processMetaData.debugAttrib();
		
		try { 
			//System.err.println("Labelling Components with parameters\n"+getConfig());
			cc = ConnectedComponent.getLabeledComponents(image,
											LabelingPolicy.Full3D,
											this.m_processMetaData.getForegroungColor(), 
											this.m_processMetaData.removeBorderComponents(),
											this.m_processMetaData.getVolumeThreshold(), 
											this.m_processMetaData.setRandomColors());
			// print connected components informations :
			System.err.println(cc);
			System.err.println("Ressource performance:\n" 
				+ ResourcesMonitor.getRessourceInfo());
		}catch (IllegalStateException e) {
		return new String("Too many connected components");
		}
		image.getPreferedRenderTool().display(image);
		
		this.m_outputObject = image;
		
		return this.m_outputObject;
	}
			


	/**
	 * Allows to test the plugin
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ConnectedComponentsProcess process = new ConnectedComponentsProcess(
				ImageCoreFactoryIJ.getInstance()
								  .getImageCore(wrapScienceJ.wrapImaJ.test.TestImageThresholding
								  .getSampleImageGray8(1)),
				"Blur3D", RetrievalPolicy.TryConfFileThenDialog,
				"wrapProcess"+File.separator+"predefined"+File.separator,
				OutputDataKind.CreatedFromInputCopy,
				RenderToolFactoryIJ.getInstance().getRenderTool(),
				GlobalOptions.getDefaultGuiFramework()
			);
			
			process.testPlugin(process.m_renderTool, "ConnectedComponents3D", null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
