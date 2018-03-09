/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: RenderToolFactoryIJ3D.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.factory.render;

import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.wrappers.imagej.gui.render.RenderToolIJ3D;

/**
 * @author remy
 *
 */
public class RenderToolFactoryIJ3D extends RenderToolFactory {

	/**
	 * Unique instance of this factory
	 */
	private static RenderToolFactoryIJ3D m_instance;
	
	/**
	 * Private constructor for the singleton pattern
	 */
	private RenderToolFactoryIJ3D(){
		// Nothing to do
	}
	
	/**
	 * @return The unique instance of this Factory class
	 */
	public static RenderToolFactoryIJ3D getInstance(){
		if (m_instance == null){
			m_instance = new RenderToolFactoryIJ3D();
		}
		return m_instance;
	}
	

	/**
	 * @see wrapScienceJ.factory.render.RenderToolFactory#getRenderTool()
	 */
	@Override
	public RenderTool getRenderTool(){
		return RenderToolIJ3D.getInstance();
	}
}
