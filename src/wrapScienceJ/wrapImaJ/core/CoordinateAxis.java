/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: CoordinateAxis.java                                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core;

/** 
 *
 * 
 */
public enum CoordinateAxis {
	/**
	 * First coordinate
	 */
	X(1), 
	/**
	 * Second coordinate
	 */
	Y(2), 
	/**
	 * Third Coordinate
	 */
	Z(3);

	private final int m_axis;

		/**
		 * sets the Axis Option
		 * @param axis
		 */
		private CoordinateAxis(int axis) throws IllegalArgumentException {
			if (axis < 1 || axis>3){
				throw new IllegalArgumentException("Undefined Axis.");
			}
			this.m_axis = axis;
		}

		/**
		 * @return the Axis Option
		 */
		public int getValue() {
			return this.m_axis;
		}

		/** 
		 * @return a human readable description of the thresholding method.
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			switch (this.m_axis) {
			case 1:
				return "X";
			case 2:
				return "Y";
			case 3:
				return "Z";
			default:
				throw new IllegalArgumentException("Undefined axis option.");
			}
		}
}// End of enum
