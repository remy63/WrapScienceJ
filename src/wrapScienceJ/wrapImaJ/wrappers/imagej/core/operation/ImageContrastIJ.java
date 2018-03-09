/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageContrastIJ.java                                               * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation;

import ij.ImagePlus;
import ij.process.ImageStatistics;
import ij.process.StackStatistics;
import wrapScienceJ.wrapImaJ.core.VoxelShort;
import wrapScienceJ.wrapImaJ.core.operation.ImageContrast;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;

/**
 * Provides methods to adjust Brightness and Contrast (i.e adjust the display range).
 */
public class ImageContrastIJ implements ImageContrast {
	
	/**
	 * Image Data as an ImagePlus instance using ImageJ (shorthand)
	 */
	private ImagePlus m_imp;
	
	/** 
	 * Original reference to the ImageCore implementer 
	 */
	private ImageCoreIJ m_image;
	
	/**
	 * @param image
	 */
	public ImageContrastIJ(ImageCoreIJ image){
		this.m_imp = image.getImp();
		this.m_image = image;
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageContrast#adjustContrastBrightness(double, double)
	 */
	@Override
	public ImageCoreIJ adjustContrastBrightness(double contrastFactor, double brightnessFactor) {
		adjustContrast(contrastFactor);
		adjustBrightness(brightnessFactor);
		updateDataColorRange();
		return this.m_image;
	}


	/**
	 * Adjusts the contrast, and consequently the display range of an image.
	 * The narrower the display range, the higher the contrast.
	 * 
	 * @param contrastFactor ranges between 0.0 and 1.0, Increases or decreases image contrast by varying the width of the display range. 
	 */
	protected void adjustContrast(double contrastFactor) {	
		// Clamp the contrast :
		double contrast = contrastFactor < 0.0d ? 0.0d : 
			(contrastFactor > 1.0d ? 1.0d : contrastFactor);
		double defaultMin = this.m_imp.getDisplayRangeMin();
		double defaultMax = this.m_imp.getDisplayRangeMax();

		double slope;
		double center = (defaultMin + defaultMax)/ 2.0;
		double range = defaultMax - defaultMin;
		double mid = 0.5;
		if (contrast <= mid) {
			slope = contrast / mid; // between 0 and 1
		} else {
			slope = mid / (1.0 - contrast); // greater than 1
		}
		// test just in case the floating points get wrong sign
		if (slope > 0.0) {
			double min = center - (0.5 * range) / slope;
			double max = center + (0.5 * range) / slope;
			// Apply the changes
			adjustValuesRange(min, max);
		}
	}

	/**
	 * Adjusts the brightness, and consequently the display range of an image.
	 * @param brightnessFactor  ranges between 0.0 and 1.0, Increases or decreases image brightness by moving the display range
	 */
	protected void adjustBrightness(double brightnessFactor) {
		//Clamp the brightness :
		double brightness = brightnessFactor < 0.0d ? 0.0d : 
			(brightnessFactor > 1.0d ? 1.0d : brightnessFactor);
		double defaultMin = this.m_imp.getDisplayRangeMin();
		double defaultMax = this.m_imp.getDisplayRangeMax();

		double center = defaultMin + (defaultMax - defaultMin) * (1.0 - brightness);
		double width = defaultMax - defaultMin;
		double min = center - width / 2.0;
		double max = center + width / 2.0;
		adjustValuesRange(min, max);
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageContrast#adjustValuesRange(double, double)
	 */
	@Override
	public ImageCoreIJ adjustValuesRange(double min, double max) {
		int maxValue = this.m_image.getWhiteValue();

		this.m_imp.getProcessor().setMinAndMax(min*maxValue, max*maxValue);
		updateDataColorRange();
		
		return this.m_image;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageContrast#getMinValue()
	 */
	@Override
	public int getMinValue(){
		int maxValue = this.m_image.getWhiteValue();
		int minRange = maxValue;
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int x=0 ; x<this.m_image.getWidth() ; x++){
				for (int y=0 ; y<this.m_image.getHeight() ; y++){
					int pixelValue = this.m_image.getPixel(x, y);
					if (pixelValue < minRange){
						minRange = pixelValue;
					}
				}
			}
		}
		return minRange;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageContrast#getMaxValue()
	 */
	@Override
	public int getMaxValue(){
		int maxRange = 0;
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int x=0 ; x<this.m_image.getWidth() ; x++){
				for (int y=0 ; y<this.m_image.getHeight() ; y++){
					int pixelValue = this.m_image.getPixel(x, y);
					if (pixelValue > maxRange){
						maxRange = pixelValue;
					}
				}
			}
		}
		return maxRange;
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageContrast#maximizeValuesRange()
	 */
	@Override
	public ImageCoreIJ maximizeValuesRange(){
		
		int maxValue = this.m_image.getWhiteValue();
		int minRange = maxValue;
		int maxRange = 0;
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int x=0 ; x<this.m_image.getWidth() ; x++){
				for (int y=0 ; y<this.m_image.getHeight() ; y++){
					int pixelValue = this.m_image.getPixel(x, y);
					if (pixelValue < minRange){
						minRange = pixelValue;
					}
					if (pixelValue > maxRange){
						maxRange = pixelValue;
					}
				}
			}
		}
		System.err.println("ImageContrast.maximizeValuesRange, minRange: " + minRange + ", maxRange: " + maxRange);
		this.m_imp.getProcessor().setMinAndMax(minRange, maxRange);
		updateDataColorRange();
		
		return this.m_image;
	}

	/**
	 * Updates the member data related to this instance after an operation on the display range.
	 * The color of the voxels are really changed in the image data and the min and max
	 * are reset to 0 and 255 respectively. 
	 * (e.g. contrast enhancement) liable to change the image.
	 * 
	 * <strong>Remark:</strong> Note that an observer pattern could also be used and might be implemented in the future.
	 */
	protected ImageCoreIJ updateDataColorRange(){
		int tableSize = (this.m_image.getBitDepth() == 16) ? 65536 : 256;
		int[] table = new int[tableSize];
		int min = (int)this.m_imp.getDisplayRangeMin();
		int max = (int)this.m_imp.getDisplayRangeMax();

		for (int i=0; i<tableSize; i++) {
			if (i <= min){
				table[i] = 0;
			}else{
				if (i >= max){
					table[i] = tableSize-1;
				}else{
					table[i] = (int)(((double)(i-min)/(max-min))*tableSize);
				}
			}
		}
		
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					this.m_image.setPixel(x, y, table[this.m_image.getPixel(x, y)]);
				}
			}
		}
		this.m_imp.getProcessor().setMinAndMax(0, tableSize-1);
		this.m_image.deleteHistogram();
		
		return this.m_image;
	}
	
	/**
	 * Computes the new value of the given gray level for the weighed integral
	 * in equalization. Classical equalization is when exponent is equal to 1 (one).
	 * @param histogram The histogram on which equalization is performed
	 * @param grayLevel Input gray level
	 * @param powExponent Exponent on the histogram value in the histogram integration
	 * @return
	 */
	private static double getWeightedValue(long[] histogram, int grayLevel, double powExponent) {
		long h = histogram[grayLevel];
		if (h < 2){
			return h;
		}
		return Math.pow(h, powExponent);
	}
	
	/**
	 * @param value a value between zero and one.
	 * @param saturationReduction between -1 (Saturate more) and +1 (saturate less)
	*/
	private static int reduceSaturation(double value, double saturationReduction){
		int result = (int)(255.0d*(value - 0.5*saturationReduction*value*(1.0d-value)*(2.0d-value)));
		if (result < 0){ 
			result = 0;
		}
		if (result > 255){
			result = 255;
		}
		return result;
	}
	
	/**
	 * 
	 * @param histogram The histogram on which equalization is performed
	 * @param powExponent Exponent on the histogram value in the histogram integration
	 * @param saturationReduction between -1 (Saturate more) and +1 (saturate less)
	 * @return The equalized histogram
	 */
	private static int[] equalizeHistogram(long[] histogram, double powExponent,
															 double powValue,
															 double saturationReduction){
		double[] equalizedHistogram = new double[256];
		for (int i=0 ; i<256 ; i++){
			equalizedHistogram[i] = getWeightedValue(histogram, i, powExponent);
		}
		for (int i=1 ; i<256 ; i++){
			equalizedHistogram[i] += equalizedHistogram[i-1];
		}
		double scale = 255.0/equalizedHistogram[255];
		double[] dynamicsEnhancedHistogram = new double[256];
		for (int i=0 ; i<256 ; i++){
			double wouldBeValue = Math.pow((equalizedHistogram[i]*scale)/255.0,
											powValue);
			dynamicsEnhancedHistogram[i] = wouldBeValue;

			//System.err.println(resultHistogram[i]+", ");
		}
		int[] resultHistogram = new int[256];
		for (int i=0 ; i<256 ; i++){
			int result = (int)(255.0d*dynamicsEnhancedHistogram[reduceSaturation(i/255.0d, saturationReduction)]);
			if (result < 0){ 
					result = 0;
				}
				if (result > 255){
					result = 255;
				}
				resultHistogram[i] = result;
		}
		return resultHistogram;
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageContrast#equalize(double, double, double)
	 */
	@Override
	public ImageCoreIJ equalize(double powExponent, double powValue, double saturationReduction) {
		
		ImageStatistics stats = new StackStatistics(this.m_imp);
		long[] histogram = stats.getHistogram();
		int[] equalizedHistogram = equalizeHistogram(histogram, powExponent, powValue, saturationReduction);

		// pre-allocated voxel (to aImageCoreIJ saturating the garbage collector)
		VoxelShort voxel = new VoxelShort();
		for (short z=1 ; z<= this.m_image.getDepth() ; z++){
			// set the slice
			this.m_image.setCurrentZ(z);
			for (short y=0 ; y<this.m_image.getHeight() ; y++){
				for (short x=0 ; x<this.m_image.getWidth() ; x++){
					voxel.setCoordinates(x, y, z);
					int equalizedValue = equalizedHistogram[this.m_image.getPixel(x, y)];
					
					this.m_image.setPixel(x, y, equalizedValue);
				}
					
			}
		}
		// reinitialize the slice to update the last slice
		this.m_image.setCurrentZ(1);
		return this.m_image;
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageContrast#stretchHistogram(double)
	 */
	@Override
	public ImageCoreIJ stretchHistogram(double saturatedProportion) {
		throw new IllegalStateException("Sorry, not imlemented..");
	}

}
