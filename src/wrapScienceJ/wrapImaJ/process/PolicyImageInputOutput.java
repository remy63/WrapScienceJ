/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: PolicyImageInputOutput.java                                        * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.process;

import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.metaData.sets.MetaDataSet;
import wrapScienceJ.process.ProcessInputOutput.OutputDataKind;
import wrapScienceJ.resource.ModelCore;

/**
 * @author remy
 *
 */
public interface PolicyImageInputOutput extends PolicyImageStorage {
	
	/**
	 * @param policy The policy to retrieve the metadata parameters associated with the process.
	 */
	public void setRetrievalPolicy(RetrievalPolicy policy);
	
	/**
	 * Returns the default title of this process, used when no title
	 * is specified and the process is not used as a sub-process
	 * (e.g. the process is used as a plugin)
	 * @return The default title of this process metadata, which determines
	 * 			the metadata file name.
	 */
	public String getDefautTitle();
	
	/**
	 * The concrete instance of this has a Concrete MetaData, which
	 * contains the specific parameters for this process.
	 * This is not to be confused with the returned value of {@link wrapScienceJ.process.GenericProcess#getConfig()},
	 * which contains the (possibly larger or even disjoint) total
	 * metadata to be retrieved, including metadata for the children processes.
	 * @param metaDataTitle Title for the metadata tha determines the metadata file name.
	 * @param policy Policy for retrieval of the metadata (file, dialog box...)
	 * @param subdir Sub-directory of the default process metadata directory in which to look for the metadata.
	 * @return The (possibly empty) concrete metadata set instance with the
	 * 			specific parameters used directly by this process.
	 */
	public MetaDataSet getConcreteProcessMetaData(
					String metaDataTitle, 
					RetrievalPolicy policy, String subdir);
	
	/**
	 * Allows to intanciate the metadata set used as parameters for the process
	 * @param metaDataTitle Title for the metadata tha determines the metadata file name.
	 * @param policy Policy for retrieval of the metadata (file, dialog box...)
	 * @param subdir Sub-directory of the default process metadata directory in which to
	 * 				 look for the metadata.
	 * @return The instance of the metadata that the process will use.
	 */
	public ModelCore instantiateMetaData(String metaDataTitle, RetrievalPolicy policy,
												String subdir);
	
	/**
	 * Allows to initialize and retrieve the metadata once the input image is created.
	 * Should at least attempt to retrieve the predefined meatadata of the input image.
	 * @param metaDataTitle Title of the metaData that determines the metadata file names.
	 * @param policy Policy for retrieval of the metadata (file, dialog box...)
	 * @param subdir Sub-directory of the default process metadata directory in which to look for the metadata.
	 * @param outputDataKind  The kind of output in relation to input data
	 * 						 (equal reference, copy, etc.)
	 * @see wrapScienceJ.resource.ResourceCore#retrieveMetaData()
	 * It can also be an opportunity to specify the output kind by setting the {@link #m_outputDataKind}
	 * attribute if it has to be other than the default {@link OutputDataKind#EqualsInput}.
	 * 
	 * @see OutputDataKind
	 */
	public void initMetaData(String metaDataTitle, 
							 RetrievalPolicy policy, String subdir,
							 OutputDataKind outputDataKind);
	
}
