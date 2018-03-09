/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaInteger.java                                                   * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container.attribute;

import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeType;



/**
 * Allows to manage meta data with a single value with type int.
 */
public class MetaInteger extends AttributeData{
	
	/**
	 * Creates a MetaDoubleValue with initial value zero.
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 */
	public MetaInteger(String shortDescription) {
		super(AttributeType.IntAttrib, shortDescription, new Integer(0));
	}
	
	
	/**
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 * @param initValue The Initial value of the parameter with type int
	 */
	public MetaInteger(String shortDescription,  int initValue) {
		super(AttributeType.IntAttrib, shortDescription, new Integer(initValue));
	}
	


	/**
	 * @return a full copy of this instance.
	 */
	public MetaInteger duplicate(){
		return new MetaInteger(this.getShortDescription(), this.getValue());
	}
	

	
	/**
	 * @return The value from this predefined single value parameter
	 */
	public int getValue(){
		return ((Integer)this.getAttributeValue()).intValue();
	}
	

	/**
	 * @param value The value to use for the parameter.
	 */
	public void setValue(int value){
		setAttributeValue(new Integer(value));
	}
	
}
