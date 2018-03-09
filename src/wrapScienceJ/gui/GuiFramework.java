/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GuiFramework.java                                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.gui;

import wrapScienceJ.io.stream.FileHelper;
import wrapScienceJ.wrapImaJ.core.ImageCore;
/**
 * Main interface to manage a Graphical User Interface system
 *
 */
public interface GuiFramework {

		/**
		 * Allows to retrieve the utility to display message box popups in the GUI
		 * @return The utility to display message box popups 
		 */
		public MessageBox getMessageBox();
		
		/**
		 * Allows to retrieve the utility to display a Browse for File Dialog in the GUI
		 * to open an ImageCore.
		 * @return The utility to display a Browse for File Dialog in the GUI
		 */
		public OpenImageDialog getOpenImageDialog();	
		
		/**
		 * Allows to retrieve an instance of a Generic Dialog Box to prompt the user
		 * for Generic Process Configuration Parameters.
		 * @return An instance of a Generic Dialog Box for input of Generic Process Configuration Parameters
		 */
		public GenericDialogBox getGenericDialog();	
		
		
		/**
		 * Allows to retrieve the current image (e.g. in the focused image window) as an ImageCore.
		 * @return The current image
		 */
		public ImageCore getCurrentImage();
		
		/**
		 * Allows to get access to default directories related to the framework
		 * @return The file helper to get default directories related to the framework
		 */
		public FileHelper getFileHelper();
		
		/**
		 * Duplicates the image and displays the new image in a new window.
		 * @param image An image for which a full copy is to be openned in a new window.
		 * @param newWindowTilte Title of the new Window;
		 */
		public void createWindow(ImageCore image, String newWindowTilte);
}
