/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GuiFrameworkFactoryIJ.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.factory.gui;

import wrapScienceJ.gui.GuiFramework;
import wrapScienceJ.wrapImaJ.wrappers.imagej.gui.GuiFrameworkIJ;

/**
 * @author remy
 *
 */
public class GuiFrameworkFactoryIJ extends GuiFrameworkFactory {

	/**
	 * Unique instance of this factory
	 */
	private static GuiFrameworkFactoryIJ m_instance;
	
	/**
	 * Private constructor for the singleton pattern
	 */
	private GuiFrameworkFactoryIJ(){
		// Nothing to do
	}
	
	/**
	 * @return The unique instance of this Factory class
	 */
	public static GuiFrameworkFactoryIJ getInstance(){
		if (m_instance == null){
			m_instance = new GuiFrameworkFactoryIJ();
		}
		return m_instance;
	}
	
	/**
	 * Constructs an instance by loading the image from a source file.
	 * @return Instance of the Image as built by a constructor of the actual wrapper.
	 * @throws IOException  IOException in case of failure to load the image from file
	 */
	@Override
	public GuiFramework getGuiFramework(){

		return GuiFrameworkIJ.getInstance();
	}

}
