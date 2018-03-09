/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: BufferEnlargementPolicy.java                                       * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.resource;

/** 
 * Allows to specify how new values in a buffer should be initialized
 * as a buffer is enlarged. This can be "pavement", "mirror along borders",
 * "zeros", etc.
 * 
 */
public enum BufferEnlargementPolicy {
	/**
	 * Fill with black (zero)
	 */
	Zeros(1), 
	/**
	 * Fill with white (maximal gray level)
	 */
	White(2),	
	/**
	 * Prolong by a periodic pavement (toric topology on the domain)
	 */
	Pavement(3), 
	/**
	 * Prolong by a mirrored function (symmetrical values across the original boundary)
	 */
	Mirror(4);

	private final int m_enlargementPolicy;

	/**
	 * sets the enlargement policy
	 * @param enlargementPolicy
	 */
	private BufferEnlargementPolicy(int enlargementPolicy) throws IllegalArgumentException {
		if (enlargementPolicy < 1 || enlargementPolicy > 4){
			throw new IllegalArgumentException("Undefined Enlargement Policy.");
		}
		this.m_enlargementPolicy = enlargementPolicy;
	}

	/**
	 * @return the Axis Option
	 */
	public int getValue() {
		return this.m_enlargementPolicy;
	}

	/** 
	 * @return a human readable description of the thresholding method.
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		switch (this.m_enlargementPolicy) {
		case 1:
			return "Zeros";
		case 2:
			return "White";			
		case 3:
			return "Pavement";
		case 4:
			return "Mirror";
		default:
			throw new IllegalArgumentException("Undefined Enlargement Policy.");
		}
	}
	
}// End of enum
