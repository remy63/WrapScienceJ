/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaChoiceInList.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container.attribute;

import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeChoiceList;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeType;


/**
 * Allows to manage meta data with a single value with type String.
 */
public class MetaChoiceInList extends AttributeData{
	
	/**
	 * Creates a MetaDoubleValue with initial value zero.
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 * @param choices The collection of all possible choices
	 * @param initValue The Initial value of the parameter with type String
	 */
	public MetaChoiceInList(String shortDescription, String[] choices, String initValue) {
		super(AttributeType.ChoiceInListAttrib, shortDescription, 
				new AttributeChoiceList(choices, initValue));
	}
	
	
	/**
	 * The value is initialized to default: the first element of the choices list
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 * @param choices The collection of all possible choices
	 */
	public MetaChoiceInList(String shortDescription,  String[] choices) {
		super(AttributeType.ChoiceInListAttrib, shortDescription, 
				new AttributeChoiceList(choices, choices[0]));
	}
	

	/**
	 * @return a full copy of this instance.
	 */
	public MetaChoiceInList duplicate(){
		return new MetaChoiceInList(this.getShortDescription(), 
				this.getValue().getChoices(), this.getValue().getValue());
	}
	

	
	/**
	 * @return The value from this predefined single value parameter
	 */
	public AttributeChoiceList getValue(){
		return (AttributeChoiceList)getAttributeValue();
	}
	

	/**
	 * @param choices The collection of all possible choices
	 * @param initValue The Initial value of the parameter with type String
	 */
	public void setValue(String[] choices, String initValue){
		setAttributeValue(new AttributeChoiceList(choices, initValue));
	}
	
	/**
	 * @return The collection of all possible choices
	 */
	public String[] getChoices(){
		return getValue().getChoices();
	}
	
	/**
	 * @param value The value to use for the parameter, 
	 * 		  which must exist among the possible choices.
	 */
	public void setValue(String value){
		getValue().setValue(value);
	}
}