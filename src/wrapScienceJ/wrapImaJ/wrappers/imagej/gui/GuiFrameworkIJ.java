/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GuiFrameworkIJ.java                                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.gui;

import ij.IJ;
import wrapScienceJ.gui.GuiFramework;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;
import wrapScienceJ.wrapImaJ.wrappers.imagej.io.FileHelperIJ;

/**
 * Main utility to manage Graphical User Interface implemented on the ImageJ Framework
 * Allows to include GUI elements in ImageJ plugins generated through WrapImaJ
 * 
 * Follows the Singleton Design Pattern
 */
public class GuiFrameworkIJ implements GuiFramework{

	/** Unique instance of this class as in the Singleton Design Pattern */
	private static GuiFrameworkIJ m_instance = null;
	
	/** Utility to popup message boxes to the user */
	private MessageBoxIJ m_messageBox = null;
	
	/** The utility to prompt the user to choose a file by browsing */
	private OpenImageDialogIJ m_openImageDialog = null;
	
	/** Generic Dialog Box for input of Generic Process Configuration Parameters */
	private GenericDialogBoxIJ m_genericDialog = null;	
	
	private FileHelperIJ m_fileHelper = null;
	
	protected GuiFrameworkIJ(){
		if (this.m_messageBox == null){
			this.m_messageBox = new MessageBoxIJ();
		}
		if (this.m_openImageDialog == null){
			this.m_openImageDialog = new OpenImageDialogIJ();
		}
		if (this.m_genericDialog == null){
			this.m_genericDialog = new GenericDialogBoxIJ();
		}
		if (this.m_fileHelper == null){
			this.m_fileHelper = new FileHelperIJ();
		}		
	}
	
	/**
	 * @return The unique instance of GuiFrameworkIJ
	 */
	public static GuiFrameworkIJ getInstance(){
		if (m_instance == null){
			m_instance = new GuiFrameworkIJ();
		}
		return m_instance;
	}
	
	/**
	 * Allows to retrieve the utility to popup message boxes to the user
	 * @see wrapScienceJ.gui.GuiFramework#getMessageBox()
	 */
	@Override
	public MessageBoxIJ getMessageBox() {
		return this.m_messageBox;
	}

	/**
	 * Allows to retrieve the utility to prompt the user to choose a file by browsing
	 * @see wrapScienceJ.gui.GuiFramework#getOpenImageDialog()
	 */
	@Override
	public OpenImageDialogIJ getOpenImageDialog() {
		return this.m_openImageDialog;
	}
	
	
	/**
	 * Allows to retrieve an instance of a Generic Dialog Box to prompt the user
	 * for Generic Process Configuration Parameters.
	 * @return An instance of a Generic Dialog Box for input of Generic Process Configuration Parameters
	 */	
	@Override
	public GenericDialogBoxIJ getGenericDialog(){
		return this.m_genericDialog;
	}
	

	/**
	 * @see wrapScienceJ.gui.GuiFramework#getFileHelper()
	 */
	@Override
	public FileHelperIJ getFileHelper(){
		return this.m_fileHelper;
	}

	/**
	 * @see wrapScienceJ.gui.GuiFramework#getCurrentImage()
	 */
	@Override
	public ImageCoreIJ getCurrentImage() {
		return new ImageCoreIJ(IJ.getImage());
	}


	/**
	 * @see wrapScienceJ.gui.GuiFramework#createWindow(wrapScienceJ.wrapImaJ.core.ImageCore, java.lang.String)
	 */
	@Override
	public void createWindow(ImageCore image, String newWindowTilte) {
		ImageCoreIJ imageij = (ImageCoreIJ)image;
		imageij.setTitle(newWindowTilte);
		imageij.getImp().show();
		imageij.getImp().updateAndRepaintWindow();
	}
}
