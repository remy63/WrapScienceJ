/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaDataContainer.java                                             * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeType;


/**
 * Allows to represent a collection of parameters for use as input of a Generic Process.
 * Each parameter has a fundamental type (String, float, double, int, boolean, choice in a list, etc.)
 *
 */
public class MetaDataContainer {

	/** Collection of attributes characteristics and values */
	protected ArrayList<AttributeData> m_attributesList;
	
	/**
	 * Unique short description for an attribute with type StringNoAttrib, the value of which is
	 * the configuration file name of the Generic Process Configuration.
	 */
	public static final String m_keyShortDescriptionForConfigFileName = "configFileNameDoNotUse0213987";
	
	/**
	 * Unique short description for an attribute with type StringNoAttrib, the value of which is
	 * the title of the Generic Process Configuration.
	 */
	public static final String m_keyShortDescriptionForTitle = "processTitleDoNotUse0213987";
	
	/**
	 * Allows to retrieve the short human readable title (e.g. for use as dialog box title)
	 * @return The short human readable title (e.g. for use as dialog box title)
	 */
	public String getTitle() {
		AttributeData attrib = this.getAttribute(m_keyShortDescriptionForTitle);
		if (attrib != null){
			Object title = attrib.getAttributeValue();
			return title == null ? "" : (String)title;
		}
		return "";
	}

	/** 
	 * Allows to set the short human readable title (e.g. for use as dialog box title)
	 * @param title The short human readable title (e.g. for use as dialog box title)
	 */
	public void setTitle(String title) {
		this.setAttributeValue(m_keyShortDescriptionForTitle, title);
	}

	/**
	 * Allows to retrieve the configuration file (or URL) to be placed in a given directory
	 * @return The configuration file to be placed in a given directory
	 */
	public String getConfigFileName() {
		AttributeData attrib = this.getAttribute(m_keyShortDescriptionForConfigFileName);
		if (attrib != null){
			Object fileName = attrib.getAttributeValue();
			return fileName == null ? "" : (String)fileName;
		}
		return "";
	}

	/**
	 * Allows to set the configuration file (or URL) to be placed in a given directory
	 * @param configFileName The configuration file to be placed in a given directory
	 */
	public void setConfigFileName(String configFileName) {
		this.setAttributeValue(m_keyShortDescriptionForConfigFileName, configFileName);
	}

	/**
	 * The policy for managing and updating configs can be
	 * <ul>
	 * <li>"overwrite": An existing config is overwritten if a new config is obtained;</li>
	 * <li>"Always ask": A new configuration is sought at each invocation without changing the default config
	 * (the GenericProcessConfig.duplicate() method can be used)</li>
	 * <li>"Always use default": No configuration input is sought at all and the configuration is taken as is,
	 * assumed to be "one size fits all"</li>
	 * </ul>
	 */
	public enum UpdatePolicy {
		/**
		 * "overwrite": An existing config is overwritten if a new config is obtained
		 */
		Overwrite(1),
		/**
		 * Always ask": A new configuration is sought at each invocation without changing the default config
		 * (the GenericProcessConfig.duplicate() method can be used)
		 */
		AlwaysAsk(2),
		/**
		 * "Always use default": No configuration input is sought at all and the configuration is taken as is,
		 * assumed to be "one size fits all"
		 */
		Constant(3);
		
		/** Actual value for the enumeration instance */
		private int m_policy;
		
		private UpdatePolicy(int policy){
			if (policy < 1 || policy > 3){
				throw new IllegalArgumentException("Undefined policy for process configuration");
			}
			this.m_policy = policy;
		}	
		
		/**
		 * @return the integer representation for the method's ID
		 */
		public int getValue() {
			return this.m_policy;
		}
		
		/**
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			switch(this.m_policy){
			case 1:
				return "Overwrite current configuration";
			case 2:
				return "Always Ask for an new configuration";
			case 3:
				return "Constant configuration";
			default:
				throw new IllegalArgumentException("Undefined policy for process configuration");
			}
		}
		
	}
	

	/**
	 * Allows to construct a config for a generic process with an empty collection of attribute data
	 * and no title or config file.
	 * @see wrapScienceJ.process.GenericProcessConcrete#getConfig()
	 */
	public MetaDataContainer(){
		this.m_attributesList = new ArrayList<AttributeData>();
	}

	
	
	/**
	 * Allows to construct a config for a generic process with an empty collection of attribute data
	 * @param title The short human readable title (e.g. for use as dialog box title)
	 * @param configFileName The configuration file to be placed in a given directory
	 * 
	 * The attribute list initially contains a StringNoAttrib with short description keyShortDescriptionForTitle
	 * and as value the title (e.g. to display in a dialog box);
	 * as well as  a StringNoAttrib with short description "configFileNameDoNotUse"
	 * and as value the configFileName, to retrieve the configuration file if possible.
	 * 
	 */
	public MetaDataContainer(String title, String configFileName){
		initializeBasicAttributes(title, configFileName);
	}
	
	/**
	 * Allows to construct a config for a generic process with a collection of attribute data parsed from strings
	 * @param parsableStringRepresents A collection of strings representing Attribute Characteristics.
	 * @param title The short human readable title (e.g. for use as dialog box title)
	 * @param configFileName The configuration file to be placed in a given directory
	 * @see AttributeData#AttributeData(String)
	 */
	public MetaDataContainer(String[] parsableStringRepresents, 
								String title, String configFileName){
		initializeBasicAttributes(title, configFileName);
		for (String string : parsableStringRepresents) {
			this.m_attributesList.add(new AttributeData(string));
		}
	}	
	

	
	/**
	 * Allows to construct a config for a generic process by concatenation of a collection of configs
	 * @param configs The configurations to concatenate
	 */
	public MetaDataContainer(MetaDataContainer[] configs){		
		this.m_attributesList = new ArrayList<AttributeData>();
		for (MetaDataContainer config: configs) {
			merge(config);
		}
	}	
	
	private void setInitAttributesForTitleAndConfFile(String title, String configFileName){
		this.m_attributesList.add(new AttributeData(AttributeType.StringAttrib, 
										m_keyShortDescriptionForConfigFileName, configFileName));
		this.m_attributesList.add(new AttributeData(AttributeType.StringAttrib,
														m_keyShortDescriptionForTitle, title));
	}
	
	private void initializeBasicAttributes(String title, String configFileName){
		this.m_attributesList = new ArrayList<AttributeData>();
		setInitAttributesForTitleAndConfFile(title, configFileName);
	}
	
	/**
	 * @return the number of attributes of the collection of parameters
	 */
	public int getSize(){
		return this.m_attributesList.size();
	}
	
	/**
	 * @return true if the attributes of the collection of parameters contains only constant strings
	 * (i.e. contains only attributes with type {@link AttributeType#StringNoAttrib})
	 */
	public boolean isEmpty(){
		for (AttributeData attrib : this.m_attributesList){
			if (attrib.getType() != AttributeType.StringNoAttrib &&
				!attrib.getShortDescription().equals(m_keyShortDescriptionForConfigFileName) &&
				!attrib.getShortDescription().equals(m_keyShortDescriptionForTitle)){
				return false;
			}
		}
		return true;
	}	
	
	/**
	 * Sets the basename before the prefix to "default__", which is the prefix for
	 * a default file name.
	 * @return true if the file name has changed, false if left unchanged.
	 */
	public boolean setFileNameToDefault(){
		String fileName = getConfigFileName();
		if (!fileName.startsWith("default__")){
			String[] splittedName = fileName.split("_{2}");
			if (splittedName.length >= 2){ // there is a non empty postfix
				setConfigFileName("default__" + splittedName[splittedName.length-1]);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param index position between 0 and (getSize()-1) of the attribute in the collection.
	 * @return changeable instance of the attribute.
	 */
	public AttributeData getAttribute(int index){
		if (index <0 || index >= getSize()){
			throw new IllegalArgumentException("Index out of range");
		}
		return this.m_attributesList.get(index);
	}
	

	/**
	 * Allows to set a new value for an attribute present in the collection of attributes.
	 * @param attribShortDescription Short description of the attribute used as a unique key
	 * @param value value to use as the new attribute's value
	 */
	public void setAttributeValue(String attribShortDescription, Object value){
		for (AttributeData attrib: this.m_attributesList){
			if (attrib.getShortDescription().equals(attribShortDescription.trim())){
				attrib.setAttributeValue(value);
				return;
			}
		}
		if (attribShortDescription != m_keyShortDescriptionForConfigFileName &&
			attribShortDescription != m_keyShortDescriptionForTitle){
			throw new IllegalArgumentException("Generic Process Attribute not found.");
		}
	}
	
	/**
	 * Allows to set a new value for an attribute present in the collection of attributes.
	 * @param attribShortDescription Short description of the attribute used as a unique key
	 * @return The value of the attribute, if exists, null otherwise.
	 */
	public AttributeData getAttribute(String attribShortDescription){
		for (AttributeData attrib: this.m_attributesList){
			if (attrib.getShortDescription().equals(attribShortDescription.trim())){
				return attrib;
			}
		}
		return null;
	}
	
	/**
	 * Allows to parse a new value for an attribute present in the collection of attributes.
	 * @param parsableValueString A string of the form "DDD:VVV",
	 * where DDD must be equal to this attribute short description
	 * and VVV must be a correct parsable value for the attribute type.
	 */
	public void parseAttributeValue(String parsableValueString){
		String[] elements = parsableValueString.split("=");
		if (elements.length != 2){
			throw new IllegalArgumentException("Impossible to parse config attribute (wrong format)");
		}
		AttributeData attrib = getAttribute(elements[0]);
		attrib.parseValue(parsableValueString);
	}
	
	
	/**
	 * Allows to split the different configurations corresponding to distinct files
	 * which can be contained in distinct configuration file, and possibly unrelalted parameters.
	 * @return The collection of individual configurations, each associated with one config file postfix.
	 */
	public ArrayList<MetaDataContainer> splitConfig(){
		
		ArrayList<MetaDataContainer> configList = new ArrayList<MetaDataContainer>();
		
	
		
		int configTotalSize = this.getSize();
		for (int a=0 ; a < configTotalSize ; a++){
			AttributeData attribA = this.getAttribute(a);
			if (attribA.getShortDescription().equals(MetaDataContainer
													.m_keyShortDescriptionForConfigFileName)){
				String fileName = (String)attribA.getAttributeValue();
				
				if (a+1 >= configTotalSize ||
					!this.getAttribute(a+1).getShortDescription().equals(
										MetaDataContainer.m_keyShortDescriptionForTitle)){
					throw new IllegalArgumentException("Ill formed configuration");
				}
				String concreteProcessTitle = (String)this.getAttribute(a+1).getAttributeValue();
				
				MetaDataContainer concreteConfig = new MetaDataContainer(concreteProcessTitle, fileName);
				
				boolean concreteConfigComplete = false;
				for (int b=a+2 ; b < configTotalSize && !concreteConfigComplete ; b++){
					AttributeData attribB = this.getAttribute(b);
					if (attribB.getShortDescription() == MetaDataContainer.m_keyShortDescriptionForConfigFileName){
						concreteConfigComplete = true;
					}else{
						concreteConfig.addAttribute(attribB);
					}
				}
				
				configList.add(concreteConfig);
			}
		}

		return configList;
	}
	
	
	/**
	 * Allows to add an attribute to a config for a generic process 
	 * (using the cascade design pattern)
	 * @param attributeData the instance of AttributeData to add to the collection
	 * @return this instance for the cascade pattern
	 */
	public MetaDataContainer addAttribute(AttributeData attributeData){
		this.m_attributesList.add(attributeData);
		return this;
	}
	
	/**
	 * Allows to add an attribute to a config for a generic process by parsing a string.
	 * (using the cascade design pattern)
	 * @param parsableStringRepresent A string representing Attribute Characteristics.
	 * @see AttributeData#AttributeData(String)
	 * @return this instance for the cascade pattern
	 */
	public MetaDataContainer addAttribute(String parsableStringRepresent){
		this.m_attributesList.add(new AttributeData(parsableStringRepresent));
		return this;
	}
	
	/**
	 * Determines if an individual config with a given title is present in the configuration data.
	 * @param title The title of the config to find
	 * @return true if a subconfig with that title is present in the config, false otherwise.
	 */
	public boolean containsConfigWithTitle(String title){
		for (AttributeData attrib: this.m_attributesList){
			if (attrib.getShortDescription() == m_keyShortDescriptionForTitle &&
					attrib.getAttributeValue() == title){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.generateConfString();
	}
	
	/** Generates a parsable string representing the configuration data */
	private String generateConfString(){
		boolean secondLine = false;
		StringBuilder stb = new StringBuilder();
		for (AttributeData attrib: this.m_attributesList){
			if (attrib.getShortDescription() == m_keyShortDescriptionForTitle){
				//stb.append("Metadata Set: \""+attrib.getAttributeValue()+"\" ");
			}else{
				if (attrib.getShortDescription() == m_keyShortDescriptionForConfigFileName){
					//stb.append("Config file: \""+attrib.getAttributeValue()+"\" ");
				}else{
					if (!secondLine){
						secondLine = true;
						//stb.append("\n");
					}
					stb.append(attrib+";");
				}
			}
		}
		if (stb.length() == 0){
			return "";
		}
		stb.deleteCharAt(stb.length()-1);
		return stb.toString().trim();
	}
	
	/**
	 * Saves the configuration to a parsable text file
	 * @param path path to the texte file
	 * @throws IOException 
	 */
	public void writeToFile(String path) throws IOException{
		File configFile=new File(path);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))){
			writer.write(this.generateConfString());
			writer.close();
		}catch (Exception e){
			throw new IOException("Impossible to write the configuration/metadata file.");
		}
	}

		
	/**
	 * @return a full copy of this instance.
	 */
	public MetaDataContainer duplicate(){
		MetaDataContainer copy = new MetaDataContainer(
											getTitle(), getConfigFileName());
		for (AttributeData attrib: this.m_attributesList){
			copy.addAttribute(attrib.duplicate());
		}
		return copy;
	}
	
	
	
	/**
	 * Allows to append all elements of a given config, either at the end of this config
	 * or in replacement of the existing attributes, if any.
	 * @param config a configuration to merge with the current config
	 */
	public void merge(MetaDataContainer config){
		if (config == null){
			return;
		}
		ArrayList<MetaDataContainer> listConfigs = this.splitConfig();
		MetaDataContainer foundConfig = null;
		String configTitle = config.getTitle();
		for (MetaDataContainer indConfig: listConfigs){
			String indTitle = indConfig.getTitle();
			if (indTitle == configTitle && foundConfig == null){
				foundConfig = indConfig;
				break;
			}
		}
		if (foundConfig != null){
			for (AttributeData attrib: config.m_attributesList){
				foundConfig.setAttributeValue(attrib.getShortDescription(), attrib.getAttributeValue());
			}
		}else{
			for (AttributeData attrib: config.m_attributesList){
				this.m_attributesList.add(attrib);
			}
		}
	}
	
	/**
	 * Allows to append all elements of a given config, either at the end of this config
	 * or in replacement of the existing attributes, if any.
	 * @param config a configuration to merge with the current config
	 */
	public void concatNoTitleOrFile(MetaDataContainer config){
		for (AttributeData attrib: config.m_attributesList){
			if (attrib.getShortDescription() != m_keyShortDescriptionForTitle &&
				attrib.getShortDescription() != m_keyShortDescriptionForConfigFileName){
				this.m_attributesList.add(attrib);
			}
		}
	}
	
}
