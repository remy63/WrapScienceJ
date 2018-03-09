/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageContrast.java                                                 * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core.operation;

import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * @author Rémy Malgouyres
 *
 * Provides methods to adjust Brightness and Contrast (i.e adjust the display range).
 */
public interface ImageContrast {
	/**
	 * Adjusts the contrast, brightness to change the display range of an image.
	 * The method maps linearly pixel values in the display range 
	 * to display values in the range 0--255. Pixels with a value less than the minimum
	 * are set to black and those with a value greater
	 * than the maximum are set to white.
	 * 
	 * Contrast change is applied first, and then the brightness shift.
	 * 
	 * @param contrastFactor ranges between 0.0 and 1.0, Increases or decreases image contrast by varying the width of the display range. The narrower the display range, the higher the contrast
	 * @param brightnessFactor  ranges between 0.0 and 1.0, Increases or decreases image brightness by moving the display range
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore adjustContrastBrightness(double contrastFactor, double brightnessFactor);


	/**
	 * Sets the minimum and maximum value of the display range.
	 * The gray levels are linearly scaled to adjust the min and max gray levels to given values,
	 * and then scale the gray levels to occupy all the range of possible colors.
	 * Pixels with a value greater than the maximum are set to white.
	 * @param min new minimum value of the display range, between 0.0 and 1.0, in fraction of the
	 * 			  total values range for the bit depth. 
	 * @param max new maximum value of the display range, between 0.0 and 1.0, in fraction of the
	 * 			  total values range for the bit depth. 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore adjustValuesRange(double min, double max);
	
	
	/**
	 * @return The minimum gray level in the image
	 */
	public int getMinValue();
	
	
	/**
	 * @return The maximum gray level in the image
	 */
	public int getMaxValue();
	
	
	/**
	 * Sets the minimum and maximum value of the display range to 0 and (2^getBitDepth()-1).
	 * The gray levels are linearly scaled to adjust the min and max gray levels to given values.
	 * Pixels with a value greater than the maximum are set to white.
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore maximizeValuesRange();
	
	/**
	 * Changes the tone curves of images. It should bring up the detail in the
	 * flat regions of your image. Histogram Equalization can enhance meaningless
	 * detail and hide important but small high-contrast features. This method uses
	 * a similar algorithm, but uses the square root of the histogram values, so its effects are less extreme.
	 * @param powExponent Exponent on the histogram value in the histogram integration
	 * @param powValue Exponent on the output value
	 * @param saturationReduction between -1 (Saturate more) and +1 (saturate less)
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore equalize(double powExponent, double powValue, double saturationReduction);
	
	/**
	 * 
	 * @param saturatedProportion The proportion between 0.0 (zero) and 1.0 (one)
	 * 		  of voxels which are allowed to ba clamped because their values go
	 * 		  beyond the maximal gray level value. 
	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore stretchHistogram(double saturatedProportion);
}
