/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ConnectedComponentsSegmentation.java                               * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process.predefined.sequence;

import java.io.File;
import java.io.IOException;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.gui.GuiFrameworkFactoryIJ;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.factory.render.RenderToolFactoryIJ;
import wrapScienceJ.gui.GuiFramework;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.metaData.sets.MetaDataSet;
import wrapScienceJ.metaData.sets.StringSetSingle;
import wrapScienceJ.resource.generic.ModelCoreGeneric;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.process.GenericImageProcessConcrete;
import wrapScienceJ.wrapImaJ.process.GenericImageProcessSequence;
import wrapScienceJ.wrapImaJ.process.predefined.atomic.BlurProcess;
import wrapScienceJ.wrapImaJ.process.predefined.atomic.ConnectedComponentsProcess;
import wrapScienceJ.wrapImaJ.process.predefined.atomic.ThresholdingProcess;


/**
 * @author remy
 *
 */
public class ConnectedComponentsSegmentation extends GenericImageProcessSequence {
	
	
	/**
	 * @param image The input Image for the sequence of processes
	 * @param policyOverride A common policy to retrieve metadata, overriding the policies
	 * 		  of the individual processes. If {@link RetrievalPolicy#Unspecified}, the
	 *        policies can be chosen independently for the different processes, or the
	 *        default can be used.
	 * @param renderToolOutput The too to render the output of the process
	 * @param guiFramework The Graphical User Interface Framework for human machine interface.
	 * @throws IOException 
	 */
	public ConnectedComponentsSegmentation(ImageCore image, RetrievalPolicy policyOverride,
			   							   RenderTool renderToolOutput, GuiFramework guiFramework) {
		super(image,
				new GenericImageProcessConcrete[]{
				new BlurProcess(
						image,
						"Blur3D", 
						(policyOverride == RetrievalPolicy.Unspecified) ? 
												RetrievalPolicy.TryConfFileThenDialog : 
												policyOverride,
						"wrapProcess"+File.separator+"predefined"+File.separator,
						OutputDataKind.EqualsInput),
				new ThresholdingProcess(
						image,
						"Thresholding", 
						(policyOverride == RetrievalPolicy.Unspecified) ? 
								RetrievalPolicy.TryConfFileThenDialog : 
								policyOverride,
						"wrapProcess"+File.separator+"predefined"+File.separator,
						OutputDataKind.CreatedFromInputCopy,
						RenderToolFactoryIJ.getInstance().getRenderTool(),
						GlobalOptions.getDefaultGuiFramework()),
				new ConnectedComponentsProcess(
						image,
						"ConnectedComponents3D", 
						(policyOverride == RetrievalPolicy.Unspecified) ? 
								RetrievalPolicy.TryConfFileThenDialog : 
								policyOverride,
						"wrapProcess"+File.separator+"predefined"+File.separator,
						OutputDataKind.EqualsInput,
						RenderToolFactoryIJ.getInstance().getRenderTool(),
						GlobalOptions.getDefaultGuiFramework())
				},
				OutputDataKind.CreatedFromInputCopy,
				renderToolOutput, guiFramework);
	}
	
	/**
	 * The render tool used is the ImageCore implementer's preferred display tool.
	 * @param image The input Image for the sequence of processes
	 * @param policyOverride A common policy to retrieve metadata, overriding the policies
	 * 		  of the individual processes. If {@link RetrievalPolicy#Unspecified}, the
	 *        policies can be chosen independently for the different processes, or the
	 *        default can be used.
	 * @throws IOException 
	 */
	public ConnectedComponentsSegmentation(ImageCore image, RetrievalPolicy policyOverride) {
		this(image, policyOverride, image.getPreferedRenderTool(),
				GlobalOptions.getDefaultGuiFramework());
		
	}
	
	/**
	 * Dummy default constructor for compatibility as a subclass.
	 */
	protected ConnectedComponentsSegmentation() {
		super();
		this.m_guiFramework = GuiFrameworkFactoryIJ.getInstance().getGuiFramework();
	}
	
	/**
	 * MetaData for the parameters of the connected components
	 *
	 */
	public class ProcessMetaData extends MetaDataSet {

		/** Title of the blurring process' metadata, which determines the blur metadata file name */
		private StringSetSingle m_metaBlurProcessTitle;
		
		/** Title of the Connected components metadata, which determines the components metadata
		 * file name */
		private StringSetSingle m_metaComponentsProcessTitle;

		
		/**
		 * @param fileNamePostfix The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
		 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
		 * @param subdir Sub-directory of the default gobal directory in which the default global metadata
		 * 				file should be looked for.
		 */
		public ProcessMetaData(String fileNamePostfix, RetrievalPolicy retrievalPolicy,
							   String subdir) {
			super(fileNamePostfix, retrievalPolicy, subdir);
			
			this.m_metaBlurProcessTitle = new StringSetSingle("", "Blur Process Title", 
														RetrievalPolicy.UseKnownValues);
			this.m_metaBlurProcessTitle.setValue("Blur3D");
			
			this.m_metaComponentsProcessTitle = new StringSetSingle("", "Connected Components Title", 
					RetrievalPolicy.UseKnownValues);
			
			this.m_metaComponentsProcessTitle.setValue("ConnectedComponents3D");
			
			addMetaValue(this.m_metaBlurProcessTitle);
			addMetaValue(this.m_metaComponentsProcessTitle);
		}
		
		/** 
		 * @return Title of the blurring process' metadata, which determines the blur
		 * 		   metadata file name 
		 */
		public String getBlurProcessTitle(){
			return this.m_metaBlurProcessTitle.getValue();
		}
		
		/** 
		 * @return Title of the blurring process' metadata, which determines the blur
		 * 		   metadata file name 
		 */
		public String getComponentsProcessTitle(){
			return this.m_metaComponentsProcessTitle.getValue();
		}

		
		/**
		 * Writes the metadata set to the standard error stream.
		 */
		public void debugAttrib(){
			System.err.println("Attributes Blur: getBlurProcessTitle("+getBlurProcessTitle()
														 +"), (getComponentsProcessTitle("
														 +getComponentsProcessTitle()
														 +")");
		}
	}
	
	
	/**
	 * @see wrapScienceJ.process.GenericProcess#getConfig()
	 */
	@Override
	public ModelCoreGeneric getConfig() {
		return getChildrenConfig();
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.process.generic.ProcessImageInputOutputGeneric#getDefautTitle()
	 */
	@Override
	public String getDefautTitle() {
		return "ConnectedComponentsSequence";
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
	 * @see wrapScienceJ.wrapImaJ.process.GenericImageProcessNode#runProcess(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object runProcess(Object arg, String option) {
		return super.runProcess(arg, option);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ConnectedComponentsSegmentation process = new ConnectedComponentsSegmentation(
				ImageCoreFactoryIJ.getInstance()
								  .getImageCore(wrapScienceJ.wrapImaJ.test.TestImageThresholding
											 		.getSampleImageGray8(1)
												),
				RetrievalPolicy.Unspecified,
				RenderToolFactoryIJ.getInstance().getRenderTool(),
				GlobalOptions.getDefaultGuiFramework());

			process.testPlugin(process.getRenderTool(), "ConnectedComponentsSegementation", null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}
