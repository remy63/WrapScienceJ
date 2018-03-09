/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDraw.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation;

/**
 * @author remy
 *
 * Provides operations to draw into the voxels of an image.
 * Currently, it only supports a Font Rendering functionality to write text
 * into an image slice.
 */
public interface ImageDraw {
	
	/**
	 * @param x The x coordinate of the center of the drawn text
	 * @param y The y coordinate of the center of the drawn text
	 * @param z The slice in which to draw the text
	 * @param textToDraw The text to draw in the image
	 * @param fontSize The Font Size
	 * @param gray8value Gray level between 0 and 255
	 */
	public void drawText(int x, int y, int z, String textToDraw, int fontSize, int gray8value);
	
}
