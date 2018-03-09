/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: PluginFilterGenericIJ.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.plugins;

import wrapScienceJ.process.ProcessInputOutput.OutputDataKind;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.process.GenericImageProcessConcrete;
import wrapScienceJ.wrapImaJ.process.plugins.PluginFilterGeneric;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


/**
 * @author remy
 *
 */
public abstract class PluginFilterGenericIJ implements PlugInFilter{
	
	GenericImageProcessConcrete m_processConcrete = null;
	
	/**
	 * Will be called once in the default constructor of this class,
	 * which must be called from a default constructor of a subclass.
	 * @param image The image on which to run the process
	 * @return The process to be executed in the plugin.
	 */
	public abstract GenericImageProcessConcrete getProcess(ImageCore image);
	
	/**
	 * Initializes the current image on which the process will work.
	 * to initialize the current image.
	 * Only in the case when {@link PluginFilterGeneric#m_outputDataKind} is equal to
	 * {@link OutputDataKind#EqualsInput} is the input image assumed to be modified.
	 * 
	 * @param metaDataTitle If the argument with type String provided by ImageJ
	 * to the plugin is not null and not empty, it is processed as the title of the metaData
	 * that determines the metadata file names.
	 * 
	 * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
	 * 
	 * TODO keep track of the current slice
	 */
	@Override
	public int setup(String metaDataTitle, ImagePlus imp) {
		
		ImageCoreIJ image = new ImageCoreIJ(imp);
		if (this.m_processConcrete == null){
			this.m_processConcrete = getProcess(image);
		}
				
		this.m_processConcrete.setupFake(image, metaDataTitle);
		
		if (this.m_processConcrete.getOutputDataKind() != OutputDataKind.EqualsInput){
			return DOES_ALL;
		}
		return DOES_ALL+NO_CHANGES;
		
	}
	/**
	 * @see ij.plugin.PlugIn#run(java.lang.String)
	 * TODO keep track of the current slice
	 */
	@Override
	public void run(ImageProcessor ip) {
		ij.io.LogStream.redirectSystem();
		this.m_processConcrete.runFake(null);
		ij.io.LogStream.revertSystem();
	}
	



}
