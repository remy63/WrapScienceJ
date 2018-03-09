/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ThresholdingOption.java                                            * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core.operation;


/** 
 * Represents the different options for an image thresholding method
 * TODO Improve documentation of the different options (enumeration constants below)
 * 
 * @author Ngoungoure Nsapngue and Remy Malgoures
 */
public enum ThresholdingOption {
	/**
	 * 
	 */
	Default(1), 
	/**
	 * 
	 */
	Huang(2), 
	/**
	 * 
	 */
	Intermodes(3), 
	/**
	 * 
	 */
	IsoData(4), 
	/**
	 * 
	 */
	Li(5), 
	/**
	 * 
	 */
	MaxEntropy(6),
	/**
	 * 
	 */
	Mean(7), 
	/**
	 * 
	 */
	MinError(8), 
	/**
	 * 
	 */
	Minimum(9), 
	/**
	 * 
	 */
	Moments(10), 
	/**
	 * 
	 */
	Otsu(11), 
	/**
	 * 
	 */
	Percentile(12),
	/**
	 * 
	 */
	RenyEntropy(13), 
	/**
	 * 
	 */
	Shanbhag(14), 
	/**
	 * 
	 */
	Triangle(15), 
	/**
	 * 
	 */
	Yen(16);

	/** Actual value for the enumeration instance */
	private final int m_methodId;

	/**
	 * sets the method's ID
	 * @param value
	 */
	private ThresholdingOption(int methodId) throws IllegalArgumentException {
		if (methodId < 1 || methodId > 16){
			throw new IllegalArgumentException("Undefined thresholding method.");
		}
		this.m_methodId = methodId;
	}
	
	/**
	 * Allows to get a Thresholding Option from a string description.
	 * @param option A string description of the option. Can be either of
	 * Huang, Intermodes, IsoData, Li, MaxEntropy, Mean, MinError, Minimum,
	 * Moments, Otsu, Percentile, RenyEntropy, Triangle, Yen
	 * @return The obtained ThresholdingOption
	 */
	public static ThresholdingOption getThresholdingOption(String option) {

		switch (option) {
			case "Huang":
				return Huang;
			case "Intermodes":
				return Intermodes;
			case "IsoData":
				return IsoData;
			case "Li":
				return Li;
			case "MaxEntropy":
				return MaxEntropy;
			case "Mean":
				return Mean;
			case "MinError":
				return MinError;
			case "Minimum":
				return Minimum;
			case "Moments":
				return Moments;
			case "Otsu":
				return Otsu;
			case "Percentile":
				return Percentile;
			case "RenyEntropy":
				return RenyEntropy;
			case "Shanbhag":
				return Shanbhag;
			case "Triangle":
				return Triangle;
			case "Yen":
				return Yen;
			default:
				return Otsu;
		}
	}
	
	/**
	 * @return the integer representation for the method's ID
	 */
	public int getValue() {
		return this.m_methodId;
	}
	
	/**
	 * @return All the possible choices of thresholding methods.
	 */
	public static String[] getPossibleChoices(){
		return new String[]{
				"Otsu", "Intermodes", "Huang", "IsoData", "Li",
				"MaxEntropy", "Mean", "MinError", "Minimum", "Moments",
				"Percentile", "RenyEntropy", "Triangle", "Yen"
		};
	}

	/** 
	 * @return a human readable description of the thresholding method.
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		switch (this.m_methodId) {
		case 2:
			return "Huang";
		case 3:
			return "Intermodes";
		case 4:
			return "IsoData";
		case 5:
			return "Li";
		case 6:
			return "MaxEntropy";
		case 7:
			return "Mean";
		case 8:
			return "MinError";
		case 9:
			return "Minimum";
		case 10:
			return "Moments";
		case 11:
			return "Otsu";
		case 12:
			return "Percentile";
		case 13:
			return "RenyEntropy";
		case 14:
			return "Shanbhag";
		case 15:
			return "Triangle";
		case 16:
			return "Yen";
		default:
			return "default";
		}
	}

} // End of enum
