/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ModelCoreGeneric.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.resource.generic;



import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.metaData.container.MetaData;
import wrapScienceJ.metaData.container.MetaDataRetriever;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;
import wrapScienceJ.resource.ModelCore;



/**
 * Default implementation of the ModelCore interface to handle Meta Data.
 * Intended to be used as a base class.
 *
 */
public class ModelCoreGeneric implements ModelCore {
	
	protected ArrayList <MetaDataRetriever> m_metaData;
	
	
	/**
	 * Creates an empty collection of metadata configs and attributes
	 */
	public ModelCoreGeneric(){
		this.clear();
	}
	
	/**
	 * @see wrapScienceJ.resource.ModelCore#clear()
	 */
	public void clear(){
		this.m_metaData = new ArrayList<MetaDataRetriever>();
	}
	
	
	/**
	 * @see wrapScienceJ.resource.ModelCore#getConfigFromTitle(java.lang.String)
	 */
	@Override
	public MetaDataRetriever getConfigFromTitle(String title){
		if (title.isEmpty()){
			return null;
		}
		for (MetaDataRetriever config: this.m_metaData){
			if (config.getTitle() == title){
				return config;
			}
		}
		return null;
	}
	

	

	/**
	 * @see wrapScienceJ.resource.ModelCore#merge(wrapScienceJ.resource.ModelCore)
	 */
	@Override
	public void merge(ModelCore model){
		if (!(model instanceof ModelCoreGeneric)){
			throw new IllegalArgumentException("Incompatible ModelCore types");
		}
		for (MetaDataRetriever config: ((ModelCoreGeneric)model).m_metaData){
			addMetaData(config);
		}
	}
	
	
	/**
	 * @see wrapScienceJ.resource.ModelCore#getConfigFromTitle(java.lang.String)
	 */
	@Override
	public Object getAttributeValue(String shortDescription){
		for (MetaDataRetriever config: this.m_metaData){
			AttributeData attrib = config.getAttribute(shortDescription);
			if (attrib != null){
				return attrib.getAttributeValue();
			}
		}
		return null;
	}
	

	/**
	 * @see wrapScienceJ.resource.ModelCore#addMetaData(wrapScienceJ.metaData.container.MetaDataRetriever)
	 */
	@Override
	public void addMetaData(MetaDataRetriever config) {
		String title = config.getTitle();
		for (MetaDataRetriever mdt: this.m_metaData){
			if (mdt.getTitle() == title){
				mdt.merge(config);
				return;
			}
		}
		this.m_metaData.add(config);
	}
	
	/**
	 * @see wrapScienceJ.resource.ModelCore#getRawMetaData()
	 */
	@Override
	public MetaData getRawMetaData() {
		MetaData metaData = new MetaData("");
		for (MetaDataRetriever mdt: this.m_metaData){
			metaData.merge(mdt);
		}
		return metaData;
	}
	
	/**
	 * Writes the image to a file, including the metadata
	 * @throws UnsupportedEncodingException 
	 * @see wrapScienceJ.resource.ModelCore#writeToFile(java.lang.String, java.lang.String)
	 */
	@Override
	public void writeToFile(String directory, String basename) throws IOException{
		for (MetaDataRetriever config: this.m_metaData){
			config.writeToFile(directory, basename);
		}
	}
	
	/**
	 * Sets the metadata file name as obtained by appending ("__"+conftitle) to the basename
	 * and setting the metadata file extension. This is done for all metadata sets in this.m_metaData
	 * which have a given title (ususaly only one metadata set).
	 * @param pathString The path to a resource
	 * @param configTitle The metadata set title to retrieve for that resource.
	 * @param defaultDir The default directory to use if the resource doesn't exist
	 * @return The directory containing the resource, if exists, the default resource directory otherwise.
	 */
	public String setMetaDataFileName(String pathString, String configTitle, String defaultDir){
		String directory = defaultDir;
		String basename = "default";
		File path = new File(pathString == null ? "" : pathString);
		if (path.exists()){
			if (path.isDirectory()){
				directory = pathString;
			}else{
				directory = path.getParent();
				String baseNameWithExtension = path.getName();
				basename = baseNameWithExtension.substring(0, baseNameWithExtension.lastIndexOf('.'));
			}
		}
				
		for (MetaDataRetriever config: this.m_metaData){
			if (config.getTitle() == configTitle){
				config.setMetaDataFileName(basename);
			}
		}
		return directory;
	}
	
	/**
	 * Sets the metadata file name as obtained by appending ("__"+conftitle) to the basename
	 * and setting the metadata file extension. This is done for all metadata in this.m_metaData.
	 * @param pathString The path to a resource
	 * @param defaultDir The default directory to use if the resource does not exist
	 * @return The directory containing the resource, if exists, the default ressource directory otherwise.
	 */
	public String setMetaDataFileName(String pathString, String defaultDir){
		String directory = defaultDir;
		String basename = "default";
		File path = new File(pathString);
		if (path.exists()){
			if (path.isDirectory()){
				directory = pathString;
			}else{
				directory = path.getParent();
				String baseNameWithExtension = path.getName();
				basename = baseNameWithExtension.substring(0, baseNameWithExtension.lastIndexOf('.'));
			}
		}
				
		for (MetaDataRetriever config: this.m_metaData){
			config.setMetaDataFileName(basename);
		}
		return directory;
	}


	

	/**
	 * @see wrapScienceJ.resource.ModelCore#retrieve(java.lang.String)
	 */
	@Override
	public void retrieve(String resourcePath) throws IOException{
		
		String defaultDirectory = GlobalOptions.getDefaultGuiFramework().getFileHelper().retrieveLastDirectory(resourcePath);

		String path = resourcePath == null ? "" : resourcePath;
		String dirname = setMetaDataFileName(path, defaultDirectory);
			
		MetaDataRetriever.retrieveConfigsUsingPolicies(this.m_metaData, 
					  									dirname, "Parameters for Metadata");

	}
	
	/**
	 * @see wrapScienceJ.resource.ModelCore#retrieve(String confPostfix, String guessDir)
	 */
	@Override
	public void retrieve(String confPostfix, String guessDir) throws IOException{
		MetaDataRetriever config = getConfigFromTitle(confPostfix);
		config.setConfigFileName("default__"+config.getTitle()+MetaData.getFileExtension());
		MetaDataRetriever.retrieveConfigsUsingPolicies(this.m_metaData, 
				  guessDir, "Parameters for "+confPostfix);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		for (MetaDataRetriever config: this.m_metaData){
			stb.append(config);
			stb.append("\n");
		}
		return stb.toString();
	}
}
