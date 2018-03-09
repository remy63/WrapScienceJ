/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  TestImageDraw.java                                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.test;

import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.factory.render.RenderToolFactoryIJ3D;
import wrapScienceJ.wrapImaJ.core.ImageCore;


/**
 * @author remy
 * 
 * Allows to test features to draw into image voxels colors.
 *
 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDraw
 * 
 */
public class TestImageDraw {

	static ImageCore getEllipsoid(int bitDepth, int radius){
		ImageCore image = ImageCoreFactoryIJ.getInstance()
				.getEmptyImageCore(1000, 1000, 101, bitDepth);
		
		image.setTitle("Ellipsoid");
		for (int z=0 ; z<image.getDepth() ; z++){
			for (int y=0 ; y<image.getHeight() ; y++){
				for (int x=0 ; x<image.getWidth() ; x++){
					if (Math.sqrt((x-500)*(x-500)+(y-500)*(y-500)+(10*(z-50)*10*(z-50)))<radius){
						image.setVoxel(x, y, z, image.getWhiteValue());
					}else{
						image.setVoxel(x, y, z, 0);
					}
				}
			}
		}
		return image;
	}
	
	
	/**
	 * @param x The x coordinate of the center of the drawn text
	 * @param y The y coordinate of the center of the drawn text
	 * @param z The slice in which to draw the text
	 * @param textToDraw The text to draw in the image
	 * @param fontSize The Font Size
	 * @param gray8value Gray level between 0 and 255
	 */
	public static void testDrawText(int x, int y, int z, String textToDraw, int fontSize, int gray8value){
		
		ImageCore ellipsoid = getEllipsoid(8, 300);
		
		ellipsoid.getImageDraw().drawText(x, y, z, textToDraw, fontSize, gray8value);
		
		ellipsoid.getPreferedRenderTool().display(ellipsoid);
		RenderToolFactoryIJ3D.getInstance().getRenderTool().display(ellipsoid);
		
	}
	
	
	/**
	 * Main function to run the tests in this class.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		testDrawText(500, 500, 0, "Hello World ! " + 2018, 24, 126);
		
	}
}
