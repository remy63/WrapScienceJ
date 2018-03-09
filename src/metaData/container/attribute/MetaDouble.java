/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaDouble.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container.attribute;


import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeType;


/**
 * Allows to manage meta data with a single value with type double.
 */
public class MetaDouble extends AttributeData{
	
	/**
	 * Creates a MetaDoubleValue with initial value zero.
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 */
	public MetaDouble(String shortDescription) {
		super(AttributeType.DoubleAttrib, shortDescription, new Double(0.0));
	}
	
	
	/**
	 * @param shortDescription The short description (key) of the unique attribute to create in the configuration
	 * @param initValue The Initial value of the parameter with type double
	 */
	public MetaDouble(String shortDescription,  double initValue) {
		super(AttributeType.DoubleAttrib, shortDescription, new Double(initValue));
	}
	


	/**
	 * @return a full copy of this instance.
	 */
	public MetaDouble duplicate(){
		return new MetaDouble(this.getShortDescription(), this.getValue());
	}
	

	
	/**
	 * @return The value from this predefined single value parameter
	 */
	public double getValue(){
		return ((Double)this.getAttributeValue()).doubleValue();
	}
	

	/**
	 * @param value The value to use for the parameter.
	 */
	public void setValue(double value){
		setAttributeValue(new Double(value));
	}
	
}
