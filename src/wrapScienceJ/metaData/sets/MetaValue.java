/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaValue.java                                                     * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.sets;

import wrapScienceJ.metaData.container.MetaDataRetriever;

/**
 * Base class for predefined MetaData set value
 *
 */
public abstract class MetaValue extends MetaDataRetriever {

	/**
	 * Creates a DoubleValueConfig with initial value zero.
	 * @param fileNamePostfix The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
	 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
	 * @param subdir Sub-directory of the default gobal directory in which the default global metadata
	 * 				file should be looked for.
	 */
	public MetaValue(String fileNamePostfix, RetrievalPolicy retrievalPolicy, String subdir) {
		super(fileNamePostfix, retrievalPolicy, subdir);
	}

}
