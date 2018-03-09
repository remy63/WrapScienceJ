/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: AttributeChoiceList.java                                           * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container.attribute.baseTypes;

/**
 * Represents the type of an attribute that can take a finite number of possible values
 * (called choice) among a list of String.
 *
 */
public class AttributeChoiceList {
	
	/** Collection of all possible choices */
	private String[] m_choices;
	
	/** Actual value that is one among the possible choices */
	private String m_value;
	
	/**
	 * Determines if a given value exists among the list of possible choices.
	 * @param value The value to test
	 * @return true if the value is a valid choice
	 */
	protected boolean isValidValue(String value){
		for (String choice: this.m_choices){
			if (value.equals(choice)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param choices The collection of all possible choices
	 * @param value The actual value that is one among the possible choices
	 */
	public AttributeChoiceList(String[] choices, String value){
		this.m_choices = choices;
		if (!isValidValue(value)){
			throw new IllegalArgumentException("The choice does not exist in the list");
		}
		this.m_value = value;
	}

	/**
	 * @return The collection all possible of choices
	 */
	public String[] getChoices(){
		return this.m_choices;
	}

	/**
	 * @param value The actual value that is one among the possible choices
	 */
	public void setValue(String value){
		if (!isValidValue(value)){
			throw new IllegalArgumentException("The choice does not exist in the list");
		}
		this.m_value = value;
	}

	/**
	 * @return The actual value that is one among the possible choices
	 */
	public String getValue(){
		return this.m_value;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getValue();
	}
}
