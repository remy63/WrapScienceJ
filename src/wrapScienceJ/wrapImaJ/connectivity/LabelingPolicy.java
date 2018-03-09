/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: LabelingPolicy.java                                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.connectivity;

/**
 * @author remy
 *
 */
public enum LabelingPolicy {
	/**
	 * 2D components labeling in each slice, considering slices
	 * orthogonal to the X axis
	 */
	X_2D(1), 
	/**
	 * 2D components labeling in each slice, considering slices
	 * orthogonal to the Y axis
	 */
	Y_2D(2), 
	/**
	 * 2D components labeling in each slice, considering slices
	 * orthogonal to the Z axis
	 */
	Z_2D(3),
	/**
	 * 3D Connected Components labeling
	 */
	Full3D(4),
	/**
	 * 3D Connected Components labeling. No minimal number of points
	 */
	Full3D_noSecureSize(5);

	private final int m_labelingPolicy;

		/**
		 * sets the labeling policy
		 * @param policy
		 */
		private LabelingPolicy(int policy) throws IllegalArgumentException {
			if (policy < 1 || policy > 5){
				throw new IllegalArgumentException("Undefined Connected Component Labeling Policy.");
			}
			this.m_labelingPolicy = policy;
		}

		/**
		 * @return the method's ID
		 */
		public int getValue() {
			return this.m_labelingPolicy;
		}

		/** 
		 * @return a human readable description of the thresholding method.
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			switch (this.m_labelingPolicy) {
			case 1:
				return "2D labelling by slices orthogonal to X";
			case 2:
				return "2D labelling by slices orthogonal to Y";
			case 3:
				return "2D labelling by slices orthogonal to Z";
			case 5:
				return "3D labelling No secure component size";
			default:	
				return "3D labelling";
			}
		}
}
