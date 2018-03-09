/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: AttributeData.java                                                 * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container.attribute.baseTypes;

import wrapScienceJ.metaData.container.attribute.MetaChoiceInList;

/**
 * Allows to store the characteristics of an attribute use as parameter for a Generic Process
 */
public class AttributeData {
	/** Represents the type of the attribute */
	private AttributeType m_type;
	
	/** A string with a short human readable representation of the field */
	private String m_shortDescription;
	
	/** An Object Representation of the Attribute value */
	private Object m_attributeValue;

	
	/**
	 * Allows to construct an instance of an input parameter for a generic process.
	 * @param type The enumeration representation of the attribute's type
	 * @param shortDescription A string with a short human readable representation of the field
	 * (e.g. dialog box's label text for a field)
	 * @param attributeValue An Object Representation of the Attribute value
	 * (e.g. a Float instance for an AttributeType m_type equal to FloatAttrib)
	 * The attributeValue parameter must be an instance of an immutable class if
	 * duplication is to be supported correctly.
	 * @see #duplicate()
	 */
	public AttributeData(AttributeType type, String shortDescription,
			Object attributeValue) {
		setType(type);
		setShortDescription(shortDescription);
		setAttributeValue(attributeValue);
	}
	
	/**
	 * @return a full copy of the AttributeData
	 */
	public AttributeData duplicate(){
		return new AttributeData(this.m_type, this.m_shortDescription, this.m_attributeValue);
	}
	
	/**
	 * Allows to construct an instance of an input parameter for a generic process by parsing a string.
	 * @param parsableStringRepresent A string of the form "type=TTT; description=DDD ; value=VVV"
	 * where TTT can be either one of StringAttrib, FloatAttrib, DoubleAttrib, IntAttrib, BooleanAttrib, ChoiceInListAttrib, StringNoAttrib.
	 * (can be parsed to get the enumeration representation of the attribute's type)
	 * DDD is a string with a short human readable representation of the field
	 * (e.g. dialog box's label text for a field)
	 * VVV is a parsable Representation of the Attribute value
	 * (e.g. a parsable Float an AttributeType TTT equal to FloatAttrib)
	 * @throws IllegalArgumentException if the string cannot be parsed (invalid format)
	 */
	public AttributeData(String parsableStringRepresent) throws IllegalArgumentException {
		String[] inputStringElements = parsableStringRepresent.split(";");
		if (inputStringElements.length != 3){
			throw new IllegalArgumentException("The string cannot be parsed for Attribute Data (invalid format)");
		}
		
		String[] typeElements = inputStringElements[0].trim().split("=");
		if (typeElements.length != 2 || !typeElements[0].trim().equals("type")){
			throw new IllegalArgumentException("The attribute type with length cannot be parsed (invalid format)");
		}
		setType(AttributeType.getInstance(typeElements[1]));
		
		String[] descriptionElements = inputStringElements[1].trim().split("=");
		if (descriptionElements.length != 2 || !descriptionElements[0].trim().trim().equals("description")){
			throw new IllegalArgumentException("The attribute description cannot be parsed (invalid format)");
		}		
		setShortDescription(descriptionElements[1]);
		
		String[] valueElements = inputStringElements[2].trim().split("=");
		if (valueElements.length != 2 || !valueElements[0].trim().equals("value")){
			throw new IllegalArgumentException("The attribute value cannot be parsed (invalid format)");
		}
		setAttributeValue(this.m_type.parseString(valueElements[1]));
	}
	
	/**
	 * Allows to set the value of an attribute by parsing a string
	 * @param parsableValueString A string of the form "DDD:VVV",
	 * where DDD must be equal to this attribute short description
	 * and VVV must be a correct parsable value for the attribute type.
	 * @see AttributeType#getInstance(String)
	 */
	public void parseValue(String parsableValueString){
		String trimmedValue = parsableValueString.trim();
		String[] elements = trimmedValue.split("=");
		if (elements.length != 2 || !elements[0].equals(this.m_shortDescription)){
			throw new IllegalArgumentException("The attribute description cannot be parsed (invalid format)");
		}
		Object parsedObject = this.m_type.parseString(elements[1]);
		if (this.m_type == AttributeType.ChoiceInListAttrib){
			((MetaChoiceInList)this).setValue((String)parsedObject);
		}else{
			this.m_attributeValue = this.m_type.parseString(elements[1]);
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new String(this.m_shortDescription+"="+this.m_attributeValue);
	}
	
	/**
	 * @return The enumeration representation of the attribute's type
	 */
	public AttributeType getType() {
		return this.m_type;
	}

	/**
	 * @param type enumeration representation of the attribute's type
	 */
	public void setType(AttributeType type) {
		this.m_type = type;
	}

	/**
	 * @return A string with a short human readable representation of the field
	 * (e.g. dialog box's label text for a field)
	 */
	public String getShortDescription() {
		return this.m_shortDescription;
	}

	/**
	 * @param shortDescription A string with a short human readable representation of the field
	 * (e.g. dialog box's label text for a field)
	 */
	public void setShortDescription(String shortDescription) {
		this.m_shortDescription = shortDescription.trim();
	}

	/**
	 * @return An Object Representation of the Attribute value
	 * (e.g. a Float instance for an AttributeType m_type equal to FloatAttrib)
	 */
	public Object getAttributeValue() {
		return this.m_attributeValue;
	}
	/**
	 * @param attributeValue An Object Representation of the Attribute value
	 * (e.g. a Float instance for an AttributeType m_type equal to FloatAttrib)
	 */
	public void setAttributeValue(Object attributeValue) {
		this.m_attributeValue = attributeValue;
	}
}
