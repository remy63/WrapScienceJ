/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ModelCore.java                                                     * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.resource;

import java.io.IOException;

import wrapScienceJ.metaData.container.MetaData;
import wrapScienceJ.metaData.container.MetaDataRetriever;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeType;



/**
 * Represents a Model with some data (e.g. an ImageCore) and some metadata.
 * Routines are defined to retrieve or update the meta data, and to save the model
 * (usually as a set of files with the same prefix and distinct extensions)
 */
public interface ModelCore {
	
	/**
	 * Clear all metadata to creat an empty collection
	 */
	public void clear();
	
	/**
	 * @param config Appends some metadata to already existing meta data in this model.
	 */
	public void addMetaData(MetaDataRetriever config);
	
	/**
	 * @return All the meta data associated to the model, concatenated in a unique collection
	 * of attributes.
	 */
	public MetaData getRawMetaData();
	

	
	/** 
	 * Merges the configurations. Overwrites existing data with the same title (file posfix) 
	 * @param model The model to merge with this instance.
	 */
	public void merge(ModelCore model);
	
	/**
	 * Returns the first ConfigRetriever in the collection with a given title
	 * @param title The title of the config to retrieve.
	 * @return the found config, if any, or null if none found.
	 */
	public MetaDataRetriever getConfigFromTitle(String title);
	
	/**
	 * Returns the first Attribute Value with short description (attribute key) in the first
	 * ConfigRetriever in the collection that contains one.
	 * @param shortDescription The short description (key) of the config attribute to retrieve.
	 * @return the value of the attribute which is one of the classes used in {@link AttributeType}.
	 * @see AttributeData
	 */	
	public Object getAttributeValue(String shortDescription);
	
	/**
	 * Saves the model (usually as a set of files with the same prefix and distinct extensions)
	 * @param directory
	 * @param basename
	 * @throws IOException In case of wrong file extension, directory, or permissions.
	 */
	public void writeToFile(String directory, String basename) throws IOException;
	
	/**
	 * Allows to load all meta data which could be found with the basename of the resource
	 * and in the same directory as the resource by appending a postfix and the metadata
	 * file name extension.
	 * @param resourcePath The path to the main resource file (e.g. image file)
	 * @throws IOException
	 */
	public void retrieve(String resourcePath) throws IOException;
	
	/**
	 * Allows to load all meta data which could be found with the basename of the resource
	 * and in the same directory as the resource by appending a postfix and the metadata
	 * file name extension.
	 * @param confPostfix The postfix of the meta data to be loaded
	 * @param guessDir The directory that hopefully contains the configuration file
	 * @throws IOException if the file with that postfix could not be read
	 */
	public void retrieve(String confPostfix, String guessDir) throws IOException;

	/**
	 * @param pathString Path to the metadata file name
	 * @param metaDataTitle Title for the considered metadata
	 * @param defaultDir Default directory to search for the matadata file
	 * @return The directory containing the resource, if exists, the default resource directory otherwise.
	 */
	public String setMetaDataFileName(String pathString, String metaDataTitle, String defaultDir);
	
}
