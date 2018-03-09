/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: RenderToolFactoryIJ.java                                           * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.factory.render;

import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.wrappers.imagej.gui.render.RenderToolIJ;

/**
 * @author remy
 *
 */
public class RenderToolFactoryIJ extends RenderToolFactory {

	/**
	 * Unique instance of this factory
	 */
	private static RenderToolFactoryIJ m_instance;
	
	/*
	 * Private constructor for the singleton pattern
	 */
	private RenderToolFactoryIJ(){
		// Nothing to do
	}
	
	/**
	 * @return The unique instance of this Factory class
	 */
	public static RenderToolFactoryIJ getInstance(){
		if (m_instance == null){
			m_instance = new RenderToolFactoryIJ();
		}
		return m_instance;
	}
	

	/**
	 * @see wrapScienceJ.factory.render.RenderToolFactory#getRenderTool()
	 */
	@Override
	public RenderTool getRenderTool(){

		return RenderToolIJ.getInstance();
	}
}
