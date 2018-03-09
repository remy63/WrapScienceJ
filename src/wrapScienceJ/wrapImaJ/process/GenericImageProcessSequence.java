/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GenericImageProcessSequence.java                                   * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.process;


import wrapScienceJ.factory.gui.GuiFrameworkFactoryIJ;
import wrapScienceJ.gui.GuiFramework;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.process.plugins.PluginFilterGeneric;


/**
 * Abstract Base class to implement processes involving an resource.
 * Typically, this is used to implement plugins such as ImageJ plugins
 * using the WrapImaJ API and ResourceCore to encode an resource.
 **/
public abstract class GenericImageProcessSequence extends GenericImageProcessNode
						implements GenericImageProcess {

	
	/**
	 * @param image The image on which to apply all the processes sequentially
	 * @param processes An ordered collection of processes taking an ResourceCore as processArg Object
	 * @param outputDataKind The kind of output in relation to input data
	 * 						 (equal reference, copy, etc.)
	 * @param renderToolOutput The too to render the output of the process
	 * @param guiFramework The Graphical User Interface Framework for human machine interface.
	 * @see wrapScienceJ.process.GenericProcessNode#GenericProcessNode()
	 * @see wrapScienceJ.process.GenericProcessNode#runProcess(Object, String)
	 */
	public GenericImageProcessSequence(ImageCore image, PluginFilterGeneric[] processes,
			OutputDataKind outputDataKind,
			RenderTool renderToolOutput, GuiFramework guiFramework) {
		super(image, processes, renderToolOutput, guiFramework);
		setOutputDataKind(processes[0].getOutputDataKind());
	}
	
	/**
	 * The render tool used is the ImageCore implementer's prefered display tool.
	 * @param image The image on which to apply all the processes sequentially
	 * @param processes An ordered collection of processes taking an ResourceCore as processArg Object
	 * @param outputDataKind The kind of output in relation to input data
	 * 						 (equal reference, copy, etc.)
	 * @see wrapScienceJ.process.GenericProcessNode#GenericProcessNode()
	 * @see wrapScienceJ.process.GenericProcessNode#runProcess(Object, String)
	 */
	public GenericImageProcessSequence(ImageCore image, PluginFilterGeneric[] processes,
										OutputDataKind outputDataKind) {
		this(image, processes, outputDataKind, 
			 image.getPreferedRenderTool(), 
			 GuiFrameworkFactoryIJ.getInstance().getGuiFramework());
	}

	/**
	 * Dummy default constructor for compatibility as a subclass.
	 */
	protected GenericImageProcessSequence() {
		super();
		this.m_guiFramework = GuiFrameworkFactoryIJ.getInstance().getGuiFramework();
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.process.GenericImageProcessNode#runProcess(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object runProcess(Object arg, String option) {
		return super.runProcess(arg, option);
	}
	
}
