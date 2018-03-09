/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaDataSet.java                                                   * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.sets;

import java.util.ArrayList;

import wrapScienceJ.metaData.container.MetaDataContainer;
import wrapScienceJ.metaData.container.MetaDataRetriever;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;

/**
 * This class allows to construct sets of metadata values, all of which are contained
 * a single MetaData set, i.e. with a single metadata file.
 * The values should then be retrieved for use in the subclasses, which can
 * use the data with base types (double, string, boolean, etc.)
 */
public class MetaDataSet extends MetaDataRetriever implements MetaStandardTypes{

	private ArrayList<MetaValue> m_metaValues;
	
	/**
	 * @param fileNamePostfix The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
	 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
	 * @param subdir Sub-directory of the default global directory in which the default global metadata
	 * 				file should be looked for.
	 */
	public MetaDataSet(String fileNamePostfix, RetrievalPolicy retrievalPolicy,
			String subdir) {
		super(fileNamePostfix, retrievalPolicy, subdir);
		this.m_metaValues = new ArrayList<MetaValue>();
	}
	

	
	/**
	 * @param fileNamePostfix The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
	 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
	 */
	public MetaDataSet(String fileNamePostfix, RetrievalPolicy retrievalPolicy) {
		super(fileNamePostfix, retrievalPolicy, "");
		this.m_metaValues = new ArrayList<MetaValue>();
	}
	

	
	/**
	 * @param metaValue An instance of MetaValue to add to the set
	 */
	public void addMetaValue(MetaValue metaValue){
		concatNoTitleOrFile(metaValue);
		this.m_metaValues.add(metaValue);
	}
	


	/**
	 * @see wrapScienceJ.metaData.container.MetaDataRetriever#updateModelFromConfig()
	 */
	@Override
	public void updateModelFromConfig() {
		for (MetaValue metaValue: this.m_metaValues){
			metaValue.updateModelFromConfig();
		}
		
	}

	/**
	 * @see wrapScienceJ.metaData.container.MetaDataRetriever#retrieveConfigFromModel()
	 */
	@Override
	public void retrieveConfigFromModel() {
		for (MetaValue metaValue: this.m_metaValues){
			metaValue.retrieveConfigFromModel();
		}	
		
	}

	/**
	 * @see wrapScienceJ.metaData.container.MetaDataRetriever#duplicate()
	 */
	@Override
	public MetaDataSet duplicate() {
		MetaDataSet copy = new MetaDataSet(getTitle(), this.m_retrievalPolicy, this.m_subdir);
		for (MetaValue metaValue: this.m_metaValues){
			copy.addMetaValue(metaValue);
		}	
		return copy;
	}
	
	
	/**
	 * Prints the attributes short descriptions and values on the standard error stream.
	 */
	public void debugAttrib(){
		System.err.print("Attributes  for "+getTitle()+" : ");
		for (AttributeData attrib : this.m_attributesList){
			if (attrib.getShortDescription() != MetaDataContainer.m_keyShortDescriptionForTitle){
				if (attrib.getShortDescription() == 
						MetaDataContainer.m_keyShortDescriptionForConfigFileName){
					System.err.print("Init File");
					System.err.println("(" + attrib.getAttributeValue() + "):");
				}else{
					System.err.print(attrib.getShortDescription());
					System.err.print("(" + attrib.getAttributeValue() + ") ");
				}
				
			}
		}
		System.err.println("");
	}
	
	/**
	 * @see wrapScienceJ.metaData.container.MetaDataContainer#toString()
	 */
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		int count = 0;
		for (MetaValue metaValue : this.m_metaValues){
			if (count >= 1){
				stb.append("; ");
			}
			count++;
			stb.append(metaValue);
		}
		return stb.toString();
	}
}
