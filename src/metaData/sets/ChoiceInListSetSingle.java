/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ChoiceInListSetSingle.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.sets;

import java.io.IOException;
import java.util.ArrayList;

import wrapScienceJ.metaData.container.MetaData;
import wrapScienceJ.metaData.container.MetaDataRetriever;
import wrapScienceJ.metaData.container.attribute.MetaChoiceInList;



/**
 * Allows to manage meta data with a single value with type String.
 */


public class ChoiceInListSetSingle extends MetaValue{
	
	MetaChoiceInList m_metaValue;

	String m_value;
	
	/**
	 * Creates a BooleanValueConfig with initial value zero.
	 * @param fileNamePostfix The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 * @param choices The collection of all possible choices
	 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
	 * @see MetaData#MetaData(String)
	 */
	public ChoiceInListSetSingle(String fileNamePostfix, String shortDescription,
			String[] choices, RetrievalPolicy retrievalPolicy) {
		super(fileNamePostfix, retrievalPolicy, "");
		this.m_value = "default";
		this.m_metaValue = new MetaChoiceInList(shortDescription, choices);
		addAttribute(this.m_metaValue);
	}
	
	/**
	 * @param fileNamePostfix The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 * @param choices The collection of all possible choices
	 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
	 * @param initValue The Initial value of the parameter with type String
	 * @see MetaData#MetaData(String)
	 */
	public ChoiceInListSetSingle(String fileNamePostfix, String shortDescription, 
				String[] choices, RetrievalPolicy retrievalPolicy, String initValue) {
		super(fileNamePostfix, retrievalPolicy, "");
		this.m_value = initValue;
		this.m_metaValue = new MetaChoiceInList(shortDescription, choices,  initValue);
		addAttribute(this.m_metaValue);
	}
	
	
	/**
	 * @param fileNamePostfix The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 * @param choices The collection of all possible choices
	 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
	 * @param initValue The Initial value of the parameter with type String
	 * @param subdir Sub-directory of the default gobal directory in which the default global metadata
	 * 				file should be looked for.
	 * @see MetaData#MetaData(String)
	 */
	public ChoiceInListSetSingle(String fileNamePostfix, String shortDescription, 
			String[] choices, RetrievalPolicy retrievalPolicy, String initValue,
			String subdir) {
		super(fileNamePostfix, retrievalPolicy, subdir);
		this.m_value = initValue;
		this.m_metaValue = new MetaChoiceInList(shortDescription, choices,  initValue);
		addAttribute(this.m_metaValue);
	}

	/**
	 * @return a full copy of this instance.
	 */
	public ChoiceInListSetSingle duplicate(){
		return new ChoiceInListSetSingle(getTitle(),
				this.m_metaValue.getShortDescription(), 
				this.m_metaValue.getChoices(),
				RetrievalPolicy.UseKnownValues,
				this.m_metaValue.getValue().getValue());
	}
	
	
	/**
	 * @return The value from this predefined single value parameter
	 */
	public String getValue(){
		return this.m_value;
	}
	
	/**
	 * Updates the model's value from the config metadata
	 */
	@Override
	public void updateModelFromConfig(){
		this.m_value = this.m_metaValue.getValue().getValue();
	}
	
	/**
	 * @see wrapScienceJ.metaData.container.MetaDataRetriever#retrieveConfigFromModel()
	 */
	@Override
	public void retrieveConfigFromModel() {
		this.m_metaValue.setValue(this.m_value);
	}
	/**
	 * @param value The value to use for the parameter.
	 */
	public void setValue(String value){
		this.m_value = value;
		this.m_metaValue.setValue(this.m_value);
	}
	

	/**
	 * Allows to retrieve all parameters in a collection of metadata contents, each according to its policy.
	 * @param guessDir A temptative directory where to seek first for the metadata
	 * @param dialogTitle Title for the dialog box, if any, that prompts the user for data
	 * @throws IOException In case of file read error
	 */
	public void retrieveConfigsUsingPolicies(String guessDir, String dialogTitle) throws IOException{
		ArrayList<MetaDataRetriever> list = new ArrayList<MetaDataRetriever>();
		list.add(this);
		MetaDataRetriever.retrieveConfigsUsingPolicies(list, guessDir, dialogTitle);
	}

	
}