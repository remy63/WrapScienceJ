/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageCoreGray8.java                                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core;

/**
 * Defines specific operations for 8 bits per pixels gray levels images.
 *
 */
public interface ImageCoreGray8 extends ImageCore {
	
	/**
	 * Constructs and retrieves the histogram of an image
	 * @return The histogram of the 3D image as an array of 256 values
	 * 
	 * TODO make this method handle any 16 bits image.
	 */
	public long[] buildHistogram();
	

	/**
	 * 
	 * Constructs and retrieves the histogram of an image.
	 * The background, characterized by its normalized gray level
	 * (between 0 and 255), is excluded from the histogram.
	 * 
	 * @param backgroungGrayLevel The gray level to set to zero in the resulting histogram.
	 * @return The histogram of the 3D image as an array of 256 values
	 * 
	 * TODO make this method handle any 16 bits image.
	 */
	public long[] buildHistogramExcludeBackground(int backgroungGrayLevel);
	
} // end of interface
