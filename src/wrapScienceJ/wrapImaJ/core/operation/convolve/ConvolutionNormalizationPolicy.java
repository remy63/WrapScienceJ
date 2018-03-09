/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ConvolutionNormalizationPolicy.java                               * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.convolve;


/**
 * Allows to specify how the results of a finite difference, with its
 * skipping steps implying a multiplication of the result by some integer,
 * are to be normalized. It can be either quantitatively correct by dividing
 * by a correct combination of the skipping steps, or for example
 * with contrast maximized between -126 and 125 and shifted by 126 to fit into
 * a Byte value.
 */

public enum ConvolutionNormalizationPolicy {
	
	/**
	 * Dont't re-normalize at all and return 16 bit per voxel values without dividing
	 * by skipping steps.
	 */
	No_Normalization(1), 
	/**
	 * Convert to GRAY_8 by dividing by 256
	 */
	Gray8_No_Normalization(2), 		
	/**
	 * Convert to GRAY_8 by dividing by 256
	 */
	Gray8_divide_256(3), 
	/**
	 * Convert to GRAY_8 after maximizing the contrast
	 */
	Gray16_Scale_MaximizeContrast(4),
	/**
	 * Convert to GRAY_8 after maximizing the contrast
	 */
	Gray8_Scale_MaximizeContrast(5),
	/**
	 * Divide by the mask mass (e.g. skipping steps) for quantitatively correct values.
	 * No other conversion. Overflow might occur for large differentiation orders
	 * or skipping steps.
	 */
	Gray16_QuantitativeNormalization(6),
	/**
	 * Divide by the mask mass (e.g. skipping steps) for quantitatively correct values.
	 * Then Convert to Gray_8, clamping values to 0 or 255 if out of bounds
	 */
	Gray8_QuantitativeNormalization_Clamp(7);

	
	private final int m_differentialNormalizationPolicy;

	/**
	 * sets the differential normalization policy
	 * @param policy
	 */
	private ConvolutionNormalizationPolicy(int policy) throws IllegalArgumentException {
		if (policy < 1 || policy > 7){
			throw new IllegalArgumentException("Undefined Differential Normalization Policy.");
		}
		this.m_differentialNormalizationPolicy = policy;
	}

	/**
	 * @return the method's ID
	 */
	public int getValue() {
		return this.m_differentialNormalizationPolicy;
	}

	/** 
	 * @return a human readable description of the thresholding method.
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		switch (this.m_differentialNormalizationPolicy) {
			case 1:
				return "No re-normalizing at all and return 16 bit per voxel values";
			case 2:
				return "No re-normalizing at all and return 8 bit per voxel values";				
			case 3:
				return "Convert to GRAY_8 by dividing by 256";
			case 4:
				return "Return as GRAY_16 after maximizing the contrast";
			case 5:
				return "Convert to GRAY_8 after maximizing the contrast";
			case 6:
				return "Divide by the mask mass (e.g. skipping steps) for quantitatively correct values.";
			case 7:
				return "Divide by the mask mass (e.g. skipping steps) for quantitatively correct values, Clamped to Bytes";
			default:	
				return "No normalization";
		}
	}
	
}
