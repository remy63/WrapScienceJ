/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GuiFrameworkFactory.java                                           * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.factory.gui;

import wrapScienceJ.gui.GuiFramework;

/**
 * Constructs Instances of GuiFramework Implementers based on and Option
 *
 */
public abstract class GuiFrameworkFactory {
	
	/**
	 * TODO This doesn't appear to be useful at all, but maybe for serializing.
	 * Should be considered to get deprecated.
	 * Is is set private in the meantime.
	 * 
	 * Enumeration of all possible options ID for wrapImaJ implementers.
	 *(currently, only one implementation is provided, based on ImageJ)
	 */
	@SuppressWarnings("unused")
	private enum WrapperID {
		
		/**
		 * ImageJ based wrapper
		 */
		ImageJ(1);
		
		private final int m_wrapperId;

		/**
		 * sets the wrapper's ID
		 * @param value
		 */
		private WrapperID(int wrapperID) throws IllegalArgumentException {
			if (wrapperID < 1 || wrapperID > 1){
				throw new IllegalArgumentException("Undefined GUI Framework.");
			}
			this.m_wrapperId = wrapperID;
		}
	
		/**
		 * @return the integer representation for the wrapper's ID
		 */
		public int getValue() {
			return this.m_wrapperId;
		}
	
		/** 
		 * @return a human readable description of the wrapper.
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			switch(this.m_wrapperId){
				case 1: 
					return "ImageJ based GUI";
				default:
					throw new IllegalArgumentException("Unknown Wrapper ID");
			}
		}
	} // End of enum WrapperID
	
	/**
	 * Constructs an instance by loading the image from a source file.
	 * @return Instance of the Image as built by a constructor of the actual wrapper.
	 * @throws IOException  IOException in case of failure to load the image from file
	 */
	public abstract GuiFramework getGuiFramework();
	
}
