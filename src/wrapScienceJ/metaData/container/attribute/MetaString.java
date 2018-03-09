/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaString.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container.attribute;

import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeType;


/**
 * Allows to manage meta data with a single value with type String.
 */
public class MetaString extends AttributeData{
	
	/**
	 * Creates a MetaDoubleValue with initial value zero.
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 */
	public MetaString(String shortDescription) {
		super(AttributeType.StringAttrib, shortDescription, "");
	}
	
	
	/**
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 * @param initValue The Initial value of the parameter with type String
	 */
	public MetaString(String shortDescription,  String initValue) {
		super(AttributeType.StringAttrib, shortDescription, new String(initValue));
	}
	

	/**
	 * @return a full copy of this instance.
	 */
	public MetaString duplicate(){
		return new MetaString(this.getShortDescription(), this.getValue());
	}
	

	
	/**
	 * @return The value from this predefined single value parameter
	 */
	public String getValue(){
		return (String)this.getAttributeValue();
	}
	

	/**
	 * @param value The value to use for the parameter.
	 */
	public void setValue(String value){
		setAttributeValue(new String(value));
	}
	
}