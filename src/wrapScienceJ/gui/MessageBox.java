/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MessageBox.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.gui;

/**
 * Allows to display messages for the user.
 * 
 */
public interface MessageBox {
	
	/**
	 * Displays a message to the user in a popup message box.
	 * @param message the text to display in the box
	 * @param title The title of the box
	 */
	public void show(String message, String title);
	
	/**
	 * Displays a message to the user in a popup message box.
	 * @param message the text to display in the box
	 */
	public void show(String message);
	
	/**
	 * Displays a message to the user in a popup message box, allowing a choice for the user.
	 * @param message the text to display in the box
	 * @param title The title of the box
	 * @return true if the user agreed, false if the user click "Cancel".
	 */
	public boolean showOkCancel(String message, String title);
	
	/**
	 * Displays a message to the user in a popup message box, allowing a choice for the user.
	 * @return true if the user agreed, false if the user click "Cancel".
	 */
	public boolean showOkCancel(String message);
}
