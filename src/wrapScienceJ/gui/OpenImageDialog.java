/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: OpenImageDialog.java                                               * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.gui;

import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * Allows to ask the user for an image path, with default; last opened, or a specified directory/file
 */
public interface OpenImageDialog {
	/**
	 * Allows to prompt the user to choose a file from a dialog box, specifying a directory. 
	 * If the argument directory dir exists as a directory on the disk, then that
	 * directory is used to initialize the Open File Dialog. Otherwise, the default
	 * directory of the Framework is used.
	 * 
	 * @param dir directory to choose an image from
	 * @return The image loaded after the path provided, null if cancelled
	 */
	public ImageCore openImage(String dir);
	
	/**
	 * Allows to prompt the user to choose a file from a dialog box.
	 * The default directory of the Framework is used to initialize
	 * the Open File Dialog.
	 * @return The image loaded after the path provided, null if cancelled
	 */
	public String getDefaultDirectory();
	
	/**
	 * Allows to prompt the user to choose a file from a dialog box.
	 * If the Last Used Directory is available in the Framework and exists as a directory
	 * on the disk, then that directory is used to initialize the Open File Dialog.
	 * The default directory of the Framework is used to initialize
	 * the Open File Dialog.
	 * @return The image loaded after the path provided, null if cancelled
	 */
	public String getLastDirectory();
	
	/** Attempt to return a valid directory 
	 * @return A string representing a valid directory
	 */
	public String getLastResortDirectory();
}
