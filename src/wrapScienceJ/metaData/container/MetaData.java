/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaData.java                                                      * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;


/**
 * This class is intended to contain metadata information regarding an image.
 * THis metadata can be configured on a directory by directory basis and read
 * from a configuration file in that directory.
 */
public class MetaData extends MetaDataContainer {

	/**
	 * File extension where WrapImaJ tries to read metadata from
	 */
	public static final String m_fileExtension = "wrapMdt";
	
	/**
	 * @return the file extension where WrapImaJ tries to read metadata from
	 */
	public static String getFileExtension(){
		return m_fileExtension;
	}
	
	/**
	 * Allows to construct a config for a generic process with an empty collection of attribute data.
	 * The config file name takes a default value starting with "default__"
	 * .
	 * @param title The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
	 * 
	 * The attribute list initially contains a StringNoAttrib with short description keyShortDescriptionForTitle
	 * and as value the title (e.g. to display in a dialog box);
	 * as well as  a StringNoAttrib with short description "configFileNameDoNotUse"
	 * and as value the configFileName, to retrieve the configuration file if possible.
	 * 
	 */
	public MetaData(String title){
		super(title, "default__" + title + "." + MetaData.getFileExtension());
	}
	
	/**
	 * Allows to construct a config for a generic process with an empty collection of attribute data
	 * @param title The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
	 * @param configFileName The configuration file to be placed in a given directory
	 * 
	 * The attribute list initially contains a StringNoAttrib with short description keyShortDescriptionForTitle
	 * and as value the title (e.g. to display in a dialog box);
	 * as well as  a StringNoAttrib with short description "configFileNameDoNotUse"
	 * and as value the configFileName, to retrieve the configuration file if possible.
	 * 
	 */
	public MetaData(String title, String configFileName){
		super(title, configFileName);
	}
	
	/**
	 * Allows to construct a config for a generic process with a collection of attribute data parsed from strings
	 * @param parsableStringRepresents A collection of strings representing Attribute Characteristics.
	 * @param title The short human readable title (e.g. for use as dialog box title)
	 * @param configFileName The configuration file to be placed in a given directory
	 * @see AttributeData#AttributeData(String)
	 */
	public MetaData(String[] parsableStringRepresents, 
								String title, String configFileName){
		super(parsableStringRepresents, title, configFileName);
	}	
	
	/**
	 * Allows to construct a config for a generic process by concatenation of a collection of configs
	 * @param configs The configurations to concatenate
	 */
	public MetaData(MetaDataContainer[] configs){		
		super(configs);
	}	
	
	/**
	 * Saves the configuration to a parsable text file
	 * @param directory The directory in which to save the file
	 * @param basename The basename to use as a file name prefix before the metadata config file name
	 * @throws IOException 
	 */
	public void writeToFile(String directory, String basename) throws IOException{
		ArrayList<MetaDataContainer> individualConfigs = this.splitConfig();
		for (MetaDataContainer config: individualConfigs){
			String path = directory + basename + "__" + config.getConfigFileName();
			config.writeToFile(path);
		}
	}
	
	/**
	 * Saves the configuration to a parsable text file
	 * @param directory The directory in which to save the file
	 * @param basename The basename to use as a file name prefix before the metadata config file name
	 * @param configName The specific config name (file basename postfix).
	 * @return The metaData which could be retrieved OR null if the file could not be parsed
	 * @throws IOException 
	 */
	public static MetaData readFromFile(String directory, String basename, String configName) throws IOException{
		MetaData md = new MetaData(basename + "__" + configName, 
									basename + "__" + configName);
		if (MetaDataParserFile.readConcreteConfig(md, directory)){
			return null;
		}
		return md;
	}
	
	/**
	 * Sets the metadata file name as obtained by appending ("__"+conftitle) to the basename
	 * and setting the metadata file extension. This is done for all metadata in ths.m_metaData.
	 * @param basename The base name of the resource associated with the metadata
	 */
	public void setMetaDataFileName(String basename){
		setConfigFileName(basename+"__"+ getTitle() +"."+MetaData.getFileExtension());
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
				
		setMetaDataFileName(basename);
		
		return directory;
	}
}
