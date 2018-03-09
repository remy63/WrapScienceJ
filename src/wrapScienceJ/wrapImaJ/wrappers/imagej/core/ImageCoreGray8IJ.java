/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageCoreGray8IJ.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.core;

import ij.ImagePlus;
import wrapScienceJ.wrapImaJ.core.ImageCoreGray8;

/**
 * Implements specific operations for 8 bits per pixels gray levels images.
 *
 */
public class ImageCoreGray8IJ extends ImageCoreIJ implements ImageCoreGray8 {
	
	
    /**
     * Constructs an ImageProcessing instance as a copy of an ImageWrapper
     * @param image
     * @throws CloneNotSupportedException 
     */
    public ImageCoreGray8IJ(ImageCoreIJ image) {
    	super(image.getImp());
    }
    
    /**
	 * Constructs and retrieves the histogram of an image
	 * @return The histogram of the 3D image as an array of 256 values
	 * 
	 * TODO make this method handle 16 bits per pixels images.
	 */
	@Override
	public long[] buildHistogram() {

		long[] histogram = new long[256];
		for (int i=0 ; i< 256 ; i++){
			histogram[i] = 0;
		}
		
		int grayScaleFactor = (this.getImp().getType() == ImagePlus.GRAY16) ? 256 : 1;
		for (int z=0 ; z<getDepth() ; z++){
			setCurrentZ(z);
			for (int x=0 ; x<getWidth() ; x++){
				for (int y=0 ; y<getHeight() ; y++){
					histogram[getPixel(x, y)/grayScaleFactor]++;
					
				}
			}
		}
		
		System.err.println("grayScaleFactor = " + grayScaleFactor);
		
		return histogram;
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCoreGray8#buildHistogramExcludeBackground(int)
	 */
	@Override
	public long[] buildHistogramExcludeBackground(int backgroungGrayLevel) {

		long[] histogram = new long[256];
		for (int i=0 ; i< 256 ; i++){
			histogram[i] = 0;
		}
		
		int grayScaleFactor = (this.getImp().getType() == ImagePlus.GRAY16) ? 256 : 1;
		for (int z=0 ; z<getDepth() ; z++){
			setCurrentZ(z);
			for (int x=0 ; x<getWidth() ; x++){
				for (int y=0 ; y<getHeight() ; y++){
					histogram[getPixel(x, y)/grayScaleFactor]++;
					
				}
			}
		}
		histogram[backgroungGrayLevel] = 0;
		System.err.println("grayScaleFactor = " + grayScaleFactor);
		
		return histogram;
	}
	
}
