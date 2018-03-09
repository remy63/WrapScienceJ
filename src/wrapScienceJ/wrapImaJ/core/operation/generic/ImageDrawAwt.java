/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDrawAwt.java                                                 * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.generic;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.ImageDraw;

/**
 * @author remy
 *
 */
public class ImageDrawAwt implements ImageDraw {

	protected ImageCore m_image;
	
	/**
	 * @param image
	 */
	public ImageDrawAwt(ImageCore image){
		this.m_image = image;
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDraw#drawText(int, int, int, java.lang.String, int, int)
	 */
	@Override
	public void drawText(int x, int y, int z, String textToDraw, int fontSize, int gray8value){
		
		BufferedImage buffer = this.m_image.getImageConvert().getSliceAsAwtImage(z);
	    Graphics2D graphics2D = buffer.createGraphics();
	    
	    graphics2D.setColor(new Color(gray8value, gray8value, gray8value));
	    
	    graphics2D.setFont(new Font( "SansSerif", Font.BOLD, fontSize));
	    
	    FontMetrics fontMetrics = graphics2D.getFontMetrics();
	    int xCenter = x - fontMetrics.stringWidth(textToDraw)/2;
	    int yCenter = y - fontMetrics.getHeight()/2;
	    graphics2D.drawString(textToDraw, xCenter, yCenter);
	    this.m_image.getImageConvert().setSliceFromAwtImage(buffer, z);
	  }
}
