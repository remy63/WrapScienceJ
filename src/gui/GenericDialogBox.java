/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GenericDialogBox.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.gui;

import wrapScienceJ.metaData.container.MetaDataContainer;
/**
 * 
 *
 */
public interface GenericDialogBox {
	/**
	 * Shows a dialog box with a given title and initialized with default values.
	 * The user's input is used to change values in the configuration data for the generic process.
	 * @param dialogTitle Title of the dialog box.
	 * @param configData Data configuration for structure, input and output values of the dialog.
	 */
	public void showDialog(String dialogTitle, MetaDataContainer configData);
}
