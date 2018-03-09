/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: FileProps.java                                                     * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.plugins;

import ij.*;
import ij.io.OpenDialog;

/**
 * This simple class can be called from an ImageJ macro via the "call" mechanism to
 * get and set the (string) properties of the active image (ImagePlus). 
 *
 * call("ImpProps.setProperty", "<key>", "<value>");
 *	set property <key> to <value>, returns <value> if successful, 
 *	"" otherwise (no active image)
 *
 * call("ImpProps.getProperty", "<key>");
 *	returns value of property <key> if set, "" otherwise (not found or no active image)
 *
 * @see    ij.ImagePlus#setProperty
 * @see    ij.ImagePlus#getProperty
 *
 * @author Joachim Wesner
 * @author Leica Microsystems CMS GmbH
 * @author joachim.wesner@leica-microsystems.com
 * @version 2008-3-15
 *
 *	Last Modified by Rémy Malgouyres 2017-10-28 for inclusion in WrapImaJ
 */
public class FileProps {

	/**
	 * @param arg1
	 * @return WindowManager.getCurrentImage().getProperty(arg1) if successful, empty string otherwise
	 */
	public static String getProperty(String arg1) {
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null)
			return "";
		Object prop = imp.getProperty(arg1);
		if (prop != null && prop instanceof String){
			return (String)prop;
		}
	
		return "";
	}

	/**
	 * @param arg1
	 * @param arg2
	 * @return arg2 if successful, empty string otherwise
	 */
	public static String setProperty(String arg1, String arg2) {
		OpenDialog.setDefaultDirectory(arg1);
		return arg2;
	}

}
