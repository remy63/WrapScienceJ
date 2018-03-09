/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: FileHelperIJ.java                                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.io;

import ij.IJ;

import java.io.File;

import wrapScienceJ.io.stream.FileHelper;
import wrapScienceJ.resource.ResourceCore;
import wrapScienceJ.wrapImaJ.wrappers.imagej.gui.GuiFrameworkIJ;

/**
 * @see wrapScienceJ.io.stream.FileHelper
 */
public class FileHelperIJ extends FileHelper {
	
	/**If guessDir is neither null nor "default", and the directory guessDir exists, then guessDir is used.
	 * If that fails or guessDir is "default" or null, an attempt is made to retrieve the last used directory
	 * in ImageJ.
	 * If that also fails, null is returned.
	 * @param image An image assumed to be opened in ImageJ
	 * @param guessDir if equals  "defaultDir" returns the default directory
	 * 					if equals  "lastDir" and a last directory is defined returns the last directory
	 * 					if existing directory, then return guessDir
	 * 					In all other cases return  the last directory
	 * @return a directory that might be null.
	 */
	@Override
	public String retrieveResourceDirectory(ResourceCore image, String guessDir){
		return retrieveLastDirectory(guessDir);
	}
	
	
	/**
	 * @return The default directory for ImageJ macros
	 */
	public static String getMacroDir(){
		return IJ.getDir("macros");
	}


	/**
	 * @see wrapScienceJ.io.stream.FileHelper#retrieveLastDirectory(java.lang.String)
	 */
	@Override
	public String retrieveLastDirectory(String guessDir) {
		String dir = null;
		if (guessDir != null){
			if (guessDir == "defaultDir"){
				dir = GuiFrameworkIJ.getInstance().getOpenImageDialog().getDefaultDirectory();
			}else if (guessDir == "lastDir"){
				dir =  GuiFrameworkIJ.getInstance().getOpenImageDialog().getLastDirectory();
			}else{
				dir = guessDir;
			}
			if (dir != null){
				File file = new File(dir);
				if (file.exists()){
					return dir;
				}		
			}
			dir = GuiFrameworkIJ.getInstance().getOpenImageDialog().getDefaultDirectory();
		}
		
		if (dir == null){
			return GuiFrameworkIJ.getInstance().getOpenImageDialog().getLastResortDirectory();
		}
		return dir;
	}
	
}
