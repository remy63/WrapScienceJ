/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MessageBoxIJ.java                                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.gui;

import wrapScienceJ.gui.*;
import ij.IJ;

/**
 * Allows to display messages for the user.
 * 
 */
public class MessageBoxIJ implements MessageBox {
	
	/**
	 * Displays a message to the user in a popup message box.
	 * @param message the text to display in the box
	 * @param title The title of the box
	 */
	public void show(String message, String title){
		IJ.showMessage(title, message);
	}
	
	/**
	 * Displays a message to the user in a popup message box.
	 * @param message the text to display in the box
	 */
	public void show(String message){
		IJ.showMessage("Attention, please", message);
	}
	
	/**
	 * Displays a message to the user in a popup message box, allowing a choice for the user.
	 * @param message the text to display in the box
	 * @param title The title of the box
	 * @return true if the user agreed, false if the user click "Cancel".
	 */
	public boolean showOkCancel(String message, String title){
		return IJ.showMessageWithCancel(title, message);
	}
	
	/**
	 * Displays a message to the user in a popup message box, allowing a choice for the user.
	 * @param message the text to display in the box
	 * @return true if the user agreed, false if the user click "Cancel".
	 */
	public boolean showOkCancel(String message){
		return IJ.showMessageWithCancel("Attention, please", message);
	}
}
