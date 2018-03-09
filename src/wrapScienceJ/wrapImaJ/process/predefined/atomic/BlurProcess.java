/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: BlurProcess.java                                                   * 
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

import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.process.GenericImageProcessConcrete;


/**
 * @author remy
 *
 */
public class BlurProcess extends GenericImageProcessConcrete {
	
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
	public BlurProcess(ImageCore image, String title, RetrievalPolicy policy,
						String subdir, OutputDataKind outputDataKind,
						RenderTool renderToolOutput, GuiFramework guiFramework){
		super(image, title, policy, subdir, outputDataKind, renderToolOutput, guiFramework);
		setInputImage(image);
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
	public BlurProcess(ImageCore image, String title, RetrievalPolicy policy,
			String subdir, OutputDataKind outputDataKind){
		super(image, title, policy, subdir, outputDataKind);
		setInputImage(image);
	}	
	
	/**
	 * Default Constructor in case this class is used as a plugin
	 */
	public BlurProcess(){
		super();
	}
	
	
	/**
	 * MetaData for the parameters of the connected components
	 *
	 */
	public class ProcessMetaData extends MetaDataSet {
		
		/** Kind of blurring mask ("binomial or gaussian) */
		private StringSetSingle m_metaKindOfBlur;
		
		/** Smoothing mask Width (for binomial blur) or standard deviation (gaussian blur) */
		private DoubleSetSingle m_metaSigmaX; 
		
		/** Smoothing mask Height (for binomial blur) or standard deviation (gaussian blur) */
		private DoubleSetSingle m_metaSigmaY; 
		
		/** Smoothing mask Depth (for binomial blur) or standard deviation (gaussian blur) */
		private DoubleSetSingle m_metaSigmaZ; 
		
		
		/**
		 * @param fileNamePostfix The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
		 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
		 * @param subdir Sub-directory of the default gobal directory in which the default global metadata
		 * 				file should be looked for.
		 */
		protected ProcessMetaData(String fileNamePostfix, RetrievalPolicy retrievalPolicy,
							   String subdir) {
			super(fileNamePostfix, retrievalPolicy, subdir);
			
			this.m_metaSigmaX = new DoubleSetSingle("", "Mask Width / X standard dev.", 
														RetrievalPolicy.UseKnownValues);
			this.m_metaSigmaY = new DoubleSetSingle("", "Mask Height / Y standard dev.", 
														RetrievalPolicy.UseKnownValues);
			this.m_metaSigmaZ = new DoubleSetSingle("", "Mask Depth / Z standard dev.", 
														RetrievalPolicy.UseKnownValues);
			this.m_metaKindOfBlur = new StringSetSingle("", "Kind of blur (binomial/gaussian)", 
														RetrievalPolicy.UseKnownValues);
			
			
			addMetaValue(this.m_metaSigmaX);
			addMetaValue(this.m_metaSigmaY);
			addMetaValue(this.m_metaSigmaZ);
			addMetaValue(this.m_metaKindOfBlur);
		}

		/** 
		 * @return Smoothing mask Width (for binomial blur) or standard deviation (gaussian blur)
		 */
		public double getSigmaX(){
			return this.m_metaSigmaX.getValue();
		}

		/** 
		 * @return Smoothing mask Height (for binomial blur) or standard deviation (gaussian blur)
		 */
		public double getSigmaY(){
			return this.m_metaSigmaY.getValue();
		}		
		
		/** 
		 * @return Smoothing mask Depth (for binomial blur) or standard deviation (gaussian blur)
		 */
		public double getSigmaZ(){
			return this.m_metaSigmaZ.getValue();
		}		
				
		/**
		 * @return Kind of blurring mask ("binomial or gaussian)
		 */
		public String getKindOfBlur(){
			return this.m_metaKindOfBlur.getValue();
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
		return "Blur3D";
	}
	
	/**
	 * @see wrapScienceJ.process.GenericProcess#getConfig()
	 */
	@Override
	public ModelCoreGeneric getConfig() {
		return this.m_processConcreteModelCore;
	}
	
	
	private void binomialBlur(ImageCore image){
		image.getImageBlur().getBinomialBlur(
				  (int)(this.m_processMetaData.getSigmaX()+0.1d),
				  (int)(this.m_processMetaData.getSigmaY()+0.1d),
				  (int)(this.m_processMetaData.getSigmaZ()+0.1d));
	}
	
	private void gaussianBlur(ImageCore image){
		image.getImageBlur().getGaussianBlur(
				this.m_processMetaData.getSigmaX(),
				this.m_processMetaData.getSigmaY(),
				this.m_processMetaData.getSigmaZ());
	}				
	
	/**
	 * @see wrapScienceJ.wrapImaJ.process.GenericImageProcess#runProcess(java.lang.Object, java.lang.String)
	 * @param option If set, describes the directory to retrieve data and configuration
	 */
	@Override
	public Object runProcess(Object arg, String option) {
		
		ImageCore image = getCurrentImage(); // Create a new image for result
		
		image.getPreferedRenderTool().display(image);
		
		System.err.println("Blurring image...");
		this.m_processMetaData.debugAttrib();
		
		switch(this.m_processMetaData.getKindOfBlur().toLowerCase()){
			case "binomial":
				binomialBlur(image);
				break;
			case "gaussian":
				gaussianBlur(image);
				break;
			default:
				System.err.println("Unknown kind of blur, using binomial...");
				binomialBlur(image);
		}
		System.err.println("Blurr done.");

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
			BlurProcess process = new BlurProcess(
				ImageCoreFactoryIJ.getInstance()
								  .getImageCore(wrapScienceJ.wrapImaJ.test.TestImageThresholding
								  .getSampleImageGray8(1)),
				"Blur3D", RetrievalPolicy.TryConfFileThenDialog,
				"wrapProcess"+File.separator+"predefined"+File.separator,
				OutputDataKind.CreatedFromInputCopy,
				RenderToolFactoryIJ.getInstance()
								   .getRenderTool(),
				GlobalOptions.getDefaultGuiFramework()
			);

			process.testPlugin(process.m_renderTool, "Blur3D", null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
