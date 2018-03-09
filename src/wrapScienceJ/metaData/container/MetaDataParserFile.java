/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaDataParserFile.java                                            * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;
import wrapScienceJ.process.GenericProcessConcrete;
import wrapScienceJ.process.GenericProcessNode;



/**
 * Allows to read the configuration parameters for a Generic Process by parsing a Config File.
 *
 */
public class MetaDataParserFile implements MetaDataParser{
	
	/**
	 * Determines if a file or directory path exists on the disk
	 * @param fileOrDirName A string that might represent a path to a file or a directory
	 * @return true if the file or directory exists.
	 */
	public static boolean fileExists(String fileOrDirName){
		File file = new File(fileOrDirName);
		return file.exists();
	}
	
	/**
	 * Allows to read attributes values from a text configuration file.
	 * The file should contain one single line with semicolon (;) separated fields set with an = sign.
	 * Each field is of the form "DDD=VVV" where DDD is the short description of the attribute
	 * and VVV is the parsable value of the attribute, in coherence with the attribute's type.
	 * @see AttributeData#parseValue(String)
	 * e.g.
	 * @param config The generic process' configuration
	 * @param dirName the directory to retrieve the configuration file from
	 * @return true if the file is successfully parsed, false otherwise.
	 * 
	 * TODO optimize with a HashMap and enforce uniqueness for keys.
	 */
	public static boolean readConcreteConfig(MetaDataContainer config, String dirName){
		AttributeData attrib = config.getAttribute(0);
		if (attrib.getShortDescription().equals(MetaDataContainer
												.m_keyShortDescriptionForConfigFileName)){
			String fileName = (String)attrib.getAttributeValue();

			String confPath = dirName+
							  (dirName.endsWith(File.separator) ? "" : File.separator)+
							  fileName;
			File confFile = new File(confPath);
			boolean confExists = confFile.exists();
			if (confExists){
				System.err.println("Initializing MetaData from file "+confPath);
				try (BufferedReader inputReader = new BufferedReader(new FileReader(confFile))){
		            String strInput;
		            if ((strInput = inputReader.readLine()) != null){
		            	String[] options = strInput.split(";");
		            	for (String option: options){
		            		config.parseAttributeValue(option);
		            	}
		            	inputReader.close();
		            	System.err.println(config);
		            	return true;
		            }
		            inputReader.close();
		            return false;
		       }catch (IOException e){
		    	   System.err.println("Unable to parse the file "+dirName+
							  (dirName.endsWith(File.separator) ? "" : File.separator)+
							  fileName);
		    	   return false;
		       }
			}
		}
		return false;
	}
	
	/**
	 * Allows to read attributes values from a text configuration file.
	 * Each file should contain one single line with semicolon (;) separated fields set with an = sign.
	 * Each field is of the form "DDD=VVV" where DDD is the short description of the attribute
	 * and VVV is the parsable value of the attribute, in coherence with the attribute's type.
	 * @see AttributeData#parseValue(String)
	 * 
	 * @param config The generic process' configuration
	 * @param dirName the directory to retrieve the configuration file from
	 * @return A new version of the Configuration Data WITHOUT all the data which could be retrieved.
	 * Only the returned configuration data needs to be requested from the end user.
	 * 
	 * Note about Implementation : According to the GenericProcessConfig definition and the hierarchical
	 * implementation of @link{GenericProcess#getConfig()}, the config contains a sequence
	 * of concrete configurations, each beginning with an @link{AttributeData} with
	 * short description equal to @link{GenericProcessConfig.keyShortDescriptionForConfigFileName},
	 * immediately followed by by another  @link{AttributeData} with
	 * short description equal to @link{GenericProcessConfig.}.keyShortDescriptionForTitle
	 * Afterwards, the regular attributes for the individual configuration of Concrete Process follow.
	 * 
	 * @see MetaDataContainer
	 * @see GenericProcessConcrete
	 * @see GenericProcessNode#getConfig()
	 * TODO Clarify this by making getConfig return an aggregate with any number of GenericProcessConfig
	 * instead of a hidden sequence with hack codes in using th short description as a key.
	 * Consider using {@link MetaDataContainer#splitConfig()}
	 */
	public MetaDataContainer readConfig(MetaDataContainer config, String dirName){
		
		MetaDataContainer simplifiedConfig = new MetaDataContainer();
		
		int configTotalSize = config.getSize();
		for (int a=0 ; a < configTotalSize ; a++){
			AttributeData attribA = config.getAttribute(a);
			if (attribA.getShortDescription().equals(MetaDataContainer
													.m_keyShortDescriptionForConfigFileName)){
				String fileName = (String)attribA.getAttributeValue();
				
				if (a+1 >= configTotalSize ||
					!config.getAttribute(a+1).getShortDescription().equals(
										MetaDataContainer.m_keyShortDescriptionForTitle)){
					throw new IllegalArgumentException("Ill formed configuration");
				}
				String concreteProcessTitle = (String)config.getAttribute(a+1).getAttributeValue();
				
				MetaDataContainer concreteConfig = new MetaDataContainer(concreteProcessTitle, fileName);
				
				boolean concreteConfigComplete = false;
				for (int b=a+2 ; b < configTotalSize && !concreteConfigComplete ; b++){
					AttributeData attribB = config.getAttribute(b);
					if (attribB.getShortDescription() == MetaDataContainer.m_keyShortDescriptionForConfigFileName){
						concreteConfigComplete = true;
					}else{
						concreteConfig.addAttribute(attribB);
					}
				}
				
				boolean fileParsed = readConcreteConfig(concreteConfig, dirName);
				
				if (!fileParsed){
					simplifiedConfig.merge(concreteConfig);
				}
			}
		}

		return simplifiedConfig;
	}
	
	
}
