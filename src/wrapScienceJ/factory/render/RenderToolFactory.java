/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: RenderToolFactory.java                                             * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.factory.render;

import wrapScienceJ.wrapImaJ.gui.render.RenderTool;

/**
 * Constructs Instances of GuiFramework Implementers based on and Option
 */
public abstract class RenderToolFactory {
	
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
		ImageJ(1),
		/**
		 * 3D Viewer (Java3D based)
		 */
		Viewer3D(2);
		
		private final int m_wrapperId;

		/**
		 * sets the wrapper's ID
		 * @param value
		 */
		private WrapperID(int wrapperID) throws IllegalArgumentException {
			if (wrapperID < 1 || wrapperID > 2){
				throw new IllegalArgumentException("Undefined Display and Rendering Tool.");
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
					return "ImageJ based Implementation";
				case 2:
					return "3D Viewer based on Java3D";
				default:
					throw new IllegalArgumentException("Unknown Wrapper ID");
			}
		}
	} // End of enum WrapperID
	
	/**
	 * Constructs an instance of a Render Tool.
	 * @see RenderTool
	 * @return Instance of the Render Tool which can display resources.
	 * @throws IOException  IOException in case of failure to load the image from file
	 */
	public abstract RenderTool getRenderTool();
}
