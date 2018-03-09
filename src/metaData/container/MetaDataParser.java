/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaDataParser.java                                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container;

import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;


/**
 * @author remy
 *
 */
public interface MetaDataParser {
	
	/**
	 * Allows to read attributes values from a text configuration file or URL.
	 * Each file should contain one single line with semicolon (;) separated fields set with an = sign.
	 * Each field is of the form "DDD=VVV" where DDD is the short description of the attribute
	 * and VVV is the parsable value of the attribute, in coherence with the attribute's type.
	 * @see AttributeData#parseValue(String)
	 * 
	 * @param config The generic process' configuration
	 * @param dirName the directory to retrieve the configuration file from
	 * @return A new version of the Configuration Data WITHOUT all the data which could be retrieved.
	 * Only the returned configuration data needs to be requested from the end user.
	 */
	public MetaDataContainer readConfig(MetaDataContainer config, String dirName);
	
}
