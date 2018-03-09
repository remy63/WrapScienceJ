/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaBoolean.java                                                   * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container.attribute;

import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeType;


/**
 * Allows to manage meta data with a single value with type boolean.
 */
public class MetaBoolean extends AttributeData{
	
	/**
	 * Creates a MetaBoolean with initial value zero.
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 */
	public MetaBoolean(String shortDescription) {
		super(AttributeType.BooleanAttrib, shortDescription, new Boolean(false));
	}
	
	
	/**
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 * @param initValue The Initial value of the parameter with type boolean
	 */
	public MetaBoolean(String shortDescription,  boolean initValue) {
		super(AttributeType.DoubleAttrib, shortDescription, new Boolean(initValue));
	}
	


	/**
	 * @return a full copy of this instance.
	 */
	public MetaBoolean duplicate(){
		return new MetaBoolean(this.getShortDescription(), this.getValue());
	}
	

	
	/**
	 * @return The value from this predefined single value parameter
	 */
	public boolean getValue(){
		return ((Boolean)this.getAttributeValue()).booleanValue();
	}
	

	/**
	 * @param value The value to use for the parameter.
	 */
	public void setValue(boolean value){
		setAttributeValue(new Boolean(value));
	}
	
}

