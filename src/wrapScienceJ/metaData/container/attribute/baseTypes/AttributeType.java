/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: AttributeType.java                                                 * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container.attribute.baseTypes;

/**
 * Allows to distinguish between different attribute types
 * (String, float, double, int, boolean, choice in a list, etc.)
 */
public enum AttributeType {
	/**
	 * Attribute of type string
	 */
	StringAttrib(1),
	/**
	 * Attribute of type float
	 */
	FloatAttrib(2),
	/**
	 * Attribute of type double
	 */
	DoubleAttrib(3),
	/**
	 * Attribute of type int
	 */
	IntAttrib(4),
	/**
	 * Attribute of type boolean
	 */		
	BooleanAttrib(5),
	/**
	 * Attribute of type Choice in a list
	 */				
	ChoiceInListAttrib(6),
	/**
	 * String to be displayed for end user information (message) with no corresponding input
	 */				
	StringNoAttrib(7);
	
	/** Actual value for the enumeration instance */
	private final int m_attribType;

	/**
	 * Constructs an attribute type from a string representation of the enumeration element
	 * @param attribTypeString Can be either one of StringAttrib, FloatAttrib, DoubleAttrib, IntAttrib, BooleanAttrib, ChoiceInListAttrib, StringNoAttrib.
	 * (can be parsed to get the enumeration representation of the attribute's type)
	 * @return An instance constructed for a string representation of the enumeration element
	 * @throws IllegalArgumentException 
	 */
	public static AttributeType getInstance(String attribTypeString) throws IllegalArgumentException {
		String trimmedInputStr = attribTypeString.trim();
		switch(trimmedInputStr){
			case "StringAttrib" : 
				return StringAttrib;
			case "FloatAttrib" :
				return FloatAttrib;
			case "DoubleAttrib" :
				return DoubleAttrib;
			case "IntAttrib" :
				return IntAttrib;
			case "BooleanAttrib" :
				return BooleanAttrib;
			case "ChoiceInListAttrib" :
				return ChoiceInListAttrib;
			case "StringNoAttrib" :
				return StringNoAttrib;
			default:
				throw new IllegalArgumentException("String cannot be parsed to get an Attribute type");
		}
	}
	
	/**
	 * sets the attribute type
	 * @param attribType an integer representation of the attribute type.
	 */
	private AttributeType(int attribType) throws IllegalArgumentException {
		if (attribType < 1 || attribType > 7){
			throw new IllegalArgumentException("Undefined thresholding method.");
		}
		this.m_attribType = attribType;
	}
	
	/**
	 * @return the integer representation for the attribute type
	 */
	public int getValue() {
		return this.m_attribType;
	}
	
	/**
	 * Allows to parse a string and return an instance of the class representing the data.
	 * Depending on the type of attribute, a different type of data is parsed and returned.
	 * (e.g. an instance of Float is returned for an attribute type FloatAttrib)
	 * @param inputString A (non necessarily trimmed) string representing the data.
	 * @return An Object representation of the parsed data.
	 * @throws NumberFormatException if the input string does not contain a parsable value with the right type
	 * @throws NullPointerException if the input string is null
	 */
	Object parseString(String inputString) throws NumberFormatException, NullPointerException {
		String trimmedInputStr = inputString.trim();
		switch(this.m_attribType){
			case 1 : // StringAttrib 
				return trimmedInputStr;
			case 2 : // FloatAttrib
				return new Float(Float.parseFloat(trimmedInputStr));
			case 3 : // DoubleAttrib
				return new Double(Double.parseDouble(trimmedInputStr));
			case 4 : // IntAttrib
				return new Integer((int)Double.parseDouble(trimmedInputStr));
			case 5: // Boolean Attrib
				return new Boolean(Boolean.parseBoolean(trimmedInputStr));
			case 6: // ChoiceInListAttrib
				return trimmedInputStr;
			case 7: // StringNoAttrib
				return trimmedInputStr;					
			default:
				System.err.println("Hummm... enumeration element not in enum list. Returning null. Hope that's OK...");
				return null;
		}
	}
	
	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		switch(this.m_attribType){
		case 1 : // StringAttrib 
			return "String";
		case 2 : // FloatAttrib
			return "Floatting point number";
		case 3 : // DoubleAttrib
			return "Double Precision Floatting point number";
		case 4 : // IntAttrib
			return "Integer";
		case 5: // BooleanAttrib
			return "Boolean";
		case 6: // ChoiceInListAttrib
			return "Choice in a list";
		case 7: // StringNoAttrib
			return "No Value";					
		default:
			System.err.println("Hummm... enumeration element not in enum list. Returning null. Hope that's OK...");
			return null;
		}			
	}
}
