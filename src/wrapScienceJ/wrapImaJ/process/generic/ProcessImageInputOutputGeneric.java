/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ProcessImageInputOutputGeneric.java                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process.generic;

import java.io.IOException;

import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.metaData.sets.MetaDataSet;
import wrapScienceJ.process.ProcessInputOutput.OutputDataKind;
import wrapScienceJ.resource.generic.ModelCoreGeneric;
import wrapScienceJ.wrapImaJ.process.GenericImageProcess;
import wrapScienceJ.wrapImaJ.process.PolicyImageInputOutput;

/**
 * @author remy
 *
 */
public abstract class ProcessImageInputOutputGeneric extends ProcessImageStorageGeneric implements PolicyImageInputOutput, GenericImageProcess{
	
	/** Policy for retrieval of the metadata (file, dialog box...) */
	protected RetrievalPolicy m_retrievalPolicy;
	
	/** The metadata for this process, as modeled below */
	protected ModelCoreGeneric m_processConcreteModelCore;
	
	/**
	 * @see wrapScienceJ.wrapImaJ.process.PolicyImageInputOutput#setRetrievalPolicy(wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy)
	 */
	@Override
	public void setRetrievalPolicy(RetrievalPolicy policy){
		this.m_retrievalPolicy = policy;
	}	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.process.PolicyImageInputOutput#getDefautTitle()
	 */
	public abstract String getDefautTitle();

	
	/**
	 * @see wrapScienceJ.wrapImaJ.process.PolicyImageInputOutput#getConcreteProcessMetaData(java.lang.String, wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy, java.lang.String)
	 */
	@Override
	public abstract MetaDataSet getConcreteProcessMetaData(
											String metaDataTitle, 
											RetrievalPolicy policy, String subdir);

	/**
	 * @see wrapScienceJ.wrapImaJ.process.PolicyImageInputOutput#instantiateMetaData(java.lang.String, wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy, java.lang.String)
	 */
	@Override
	public ModelCoreGeneric instantiateMetaData(String metaDataTitle, RetrievalPolicy policy,
			String subdir){
		
		String title = metaDataTitle;
		if (metaDataTitle == null || metaDataTitle.isEmpty()){
			title = getDefautTitle();
		}
		
		this.m_processConcreteModelCore.addMetaData(
						getConcreteProcessMetaData(title, policy, subdir));
		return this.m_processConcreteModelCore;
	}
	
	/**
	 * Allows to initialize and retrieve the metadata once the input image is created.
	 * Should at least attempt to retrieve the predefined meatadata of the input image.
	 * @param metaDataTitle Title of the metaData that determines the metadata file names.
	 * @param policy Policy for retrieval of the metadata (file, dialog box...)
	 * @param subdir Sub-directory of the default process metadata directory in which to look for the metadata.
	 * @param outputDataKind  The kind of output in relation to input data
	 * 						 (equal reference, copy, etc.)
	 * @see ImageCore#retrieveMetaData()
	 * It can also be an opportunity to specify the output kind by setting the {@link #m_outputDataKind}
	 * attribute if it has to be other than the default {@link OutputDataKind#EqualsInput}.
	 * 
	 * @see OutputDataKind
	 */
	protected void initMetaData(String metaDataTitle){

		getInputImageMetaData().merge(getConfig());
		moveInputToCurrent(this.m_outputDataKind == OutputDataKind.CreatedFromInputCopy);
		
		try{
			getCurrentImage().retrieveMetaData();
		}catch (IOException e){
			System.err.println("Error loading metadata ("+e.getStackTrace()+") "
					+ getCurrentImage().getMetaData().getRawMetaData());
		}
		
	}
	


	/**
	 * @see wrapScienceJ.wrapImaJ.process.PolicyImageInputOutput#initMetaData(java.lang.String, wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy, java.lang.String, wrapScienceJ.process.ProcessInputOutput.OutputDataKind)
	 */
	@Override
	public void initMetaData(String metaDataTitle, 
							RetrievalPolicy policy, String subdir,
		    				 OutputDataKind outputDataKind) {
		
		instantiateMetaData(metaDataTitle, policy, subdir);
		setOutputDataKind(outputDataKind);
		
		initMetaData(metaDataTitle);
	}

	
}
