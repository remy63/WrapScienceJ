/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: OpenImageDialogIJ.java                                             * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.gui;

import java.io.File;

import ij.IJ;
import ij.io.OpenDialog;
import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.gui.OpenImageDialog;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;

/**
 * Allows to ask the user for an image path, with default; last opened, or a specified directory/file
 */
public class OpenImageDialogIJ implements OpenImageDialog{
	/**
	 * Allows to prompt the user to choose a file from a dialog box, specifying a directory. 
	 * If the argument directory dir exists as a directory on the disk, then that
	 * directory is used to initialize the Open File Dialog. Otherwise, the default
	 * directory of ImageJ is used.
	 * 
	 * @param dir directory to choose an image from
	 * @return The image loaded after the path provided, null if cancelled
	 */
	public ImageCore openImage(String dir){
		String directory = OpenDialog.getDefaultDirectory();
		if (dir != null){
			File f = new File(dir);
			if (f.exists() && f.isDirectory()) {
			   directory = dir;
			}
		}
		OpenDialog dialog = new OpenDialog("Open an Image for the Plugin",
													directory,
													OpenDialog.getLastName());
		try {
			return new ImageCoreIJ(IJ.openImage(dialog.getPath()));
		}catch (Exception e){
			IJ.showMessage("This plugin requires a valid image.");
			return null;
		}
	}
	
	/**
	 * Allows to prompt the user to choose a file from a dialog box.
	 * The default directory of ImageJ is used to initialize
	 * the Open File Dialog.
	 * @return The default directory to look for images
	 */
	public String getDefaultDirectory(){
		return OpenDialog.getDefaultDirectory();
	}
	
	/**
	 * Allows to prompt the user to choose a file from a dialog box.
	 * If the Last Used Directory is available in ImageJ and exists as a directory
	 * on the disk, then that directory is used to initialize the Open File Dialog.
	 * The default directory of ImageJ is used to initialize
	 * the Open File Dialog.
	 * @return The directory of the last image loaded from file
	 */
	public String getLastDirectory(){
		return OpenDialog.getLastDirectory();
	}

	/**
	 * @see wrapScienceJ.gui.OpenImageDialog#getLastResortDirectory()
	 */
	@Override
	public String getLastResortDirectory() {
		return GlobalOptions.getDefaultInputDir();
	}
	
	
}
