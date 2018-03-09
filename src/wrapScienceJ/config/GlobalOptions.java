/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GlobalOptions.java                                                 * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.config;

import java.io.File;

import wrapScienceJ.factory.gui.GuiFrameworkFactoryIJ;
import wrapScienceJ.factory.image.ImageCoreFactory;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.factory.render.RenderToolFactoryIJ;
import wrapScienceJ.gui.GuiFramework;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;


/**
 * Allows to set Global Preferences for frameworks used to do the work,
 * (e.g. image processing framework, rendering framework, GUI framework),
 * as well as default directories and other default values related to local installation.
 */
public class GlobalOptions {
	
	/**
	 * Global Option to set the default directory to find images or high weight Resources,
	 * when the last directory or the default directory of the GUI Framework are not available.
	 */
	public static final String m_defaultFilesDir = System.getProperty("user.home")
														+File.separator+"Images"
														+File.separator+"wrapScience"
														+File.separator+"wrapResourceDir"
														+File.separator;
	/**
	 * Global Option to set the default directory to find images and configuration files,
	 * when the last directory or the default directory of the GUI Framework are not available.
	 */
	public static final String m_defaultMetaDataDir = System.getProperty("user.home")
														+File.separator+"Images"
														+File.separator+"wrapScience"
														+File.separator+"wrapGlobalMetaData"
														+File.separator;
	
	/**
	 * Global Option to set the default directory to find images or high weight Resources,
	 * when the last directory or the default directory of the GUI Framework are not available.
	 */
	public static final String m_defaultTmpDir = System.getProperty("user.home")
														+File.separator+"Images"
														+File.separator+"wrapScience"
														+File.separator+"wrapTmpData"
														+File.separator;
	
	/**
	 * Specifies a default directory where images and other high weight data sets will be stored,
	 * so that in case of specification of a non absolute path, this directory will be used as root
	 * of the wrapScience Resources file system.
	 * @return The default directory to store data.
	 */
	public static String getDefaultInputDir(){
		return m_defaultFilesDir;
	}	
	
	/**
	 * Specifies a default directory where global metadata, representing absolute
	 * default configurations and parameters (relative to the installation) will be stored,
	 * In case of specification of a non absolute path, this directory will be used as root
	 * of the wrapScience metadata file system.
	 * @return The default directory to store data.
	 */
	public static String getGlobalMetaDataDir(){
		return m_defaultMetaDataDir;
	}
	
	/**
	 * Specifies a default directory where images and other high weight data sets will TEMPORARILY be stored,
	 * in case extra memory should be saved for computations.
	 * @return The default directory to store data.
	 */
	public static String getDefaultTmpDir(){
		return m_defaultTmpDir;
	}	
	
	/**
	 * @return the default Framework for 3D Image Pocessing
	 */
	public static ImageCoreFactory getDefaultImageWrapper(){
		return ImageCoreFactoryIJ.getInstance();
	}
	
	/**
	 * @return the default Framework for Graphical User Interface
	 */
	public static GuiFramework getDefaultGuiFramework(){
		return GuiFrameworkFactoryIJ.getInstance().getGuiFramework();
	}
	
	/**
	 * @return The default render tool.
	 */
	public static RenderTool getDefaultRenderTool(){
		return RenderToolFactoryIJ.getInstance().getRenderTool();
	}
}
