/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageThresholding.java                                             * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core.operation;

import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.histogram.Histogram;
import wrapScienceJ.wrapImaJ.core.histogram.HistogramThresholding;


/**
 * This class proposes some functionalities such as histogram construction and management
 * for an image.
 * It also implements some classical thresholding based binarization methods,
 * or threshold computation for the same methods.
 * 
 * The class also exposes the public methods of ImageCore, thus avoiding
 * the need several class instances or references for dealing with the same image.
 * 
 * @see ThresholdingOption
 * @see Histogram
 * @see ImageCore
 * 
 * @author Remy Malgoures
 */
public interface ImageThresholding extends ImageCore{
    
    
    /**
     * Returns the underlying image of this instance. If the histogram has not been built, then it is built by this function. 
     * @return the histogram of the underlying image of this instance
     */
    public  HistogramThresholding getHistogram();
    
    /**
     * Performs thresholding on this image using a given threshold value,
     * i.e. Any voxel with a value greater than or equal to the threshold have their value set to 0,
     * and all other voxels have their values unchanged.
     * The input threshold is normalized, independently from the gray level representation (bit depth),
     * between 0 and 255. This means that the input threshold will be multiplied by 256
     * in case of 16 bits per voxel.
     *
     * @param threshold the normalized value for the threshold between 0 and 255.
     * @param convertToGray8 If true, the binarized image is to be converted to GRAY8.
     * 						 This option is ignored if this image already has type GRAY8.
 	 * @return The image underlying this instance to allow for use of the Cascade Pattern
     */
    public ImageCore applyImageThresholdKeepGray(int threshold, boolean convertToGray8);
    
    /**
     * Performs thresholding on this image using a given threshold value,
     * i.e. Any voxel with a value greater than or equal to the threshold have their value set to 0,
     * and all other voxels have their values set to the maximal possible gray level.
     * The input threshold is normalized, independently from the gray level representation (bit depth),
     * between 0 and 255. This means that the input threshold will be multiplied by 256
     * in case of 16 bits per voxel.
     *
     * @param threshold the value for the threshold between 0 and 255.
     * @param convertToGray8 If true, the binarized image is to be converted to GRAY8.
     * 						 This option is ignored if this image already has type GRAY8.
 	 * @return The image underlying this instance to allow for use of the Cascade Pattern
     */
    public ImageCore applyImageThresholdAndBinarize(int threshold, boolean convertToGray8);
    
	/**
	 * Sets to zero the voxels with values which are not between the minimum and maximum value
	 * given as parameters.
	 * The other voxels have their colors unchanged.
	 * @param min new minimum value of the display range, between 0.0 and 1.0, in fraction of the
	 * 			  total values range for the bit depth. 
	 * @param max new maximum value of the display range, between 0.0 and 1.0, in fraction of the
	 * 			  total values range for the bit depth. 
 	 * @return The image underlying this instance to allow for use of the Cascade Pattern
	 */
	public ImageCore applyThresholdMinAndMax(double min, double max);
	
    /**
     * Computes the grey level threshold value for binarizing an image using this instance's histogram.
     * Different method can be used to compute the threshold.
     * @param method the thresholding method
     * @return the threshold for the image
     */
    public int getBinarizationThreshold(ThresholdingOption method);

    /**
     * Performs thresholding on this image using a given method option,
     * i.e. Any voxel with a value greater than or equal to the threshold have their value set to 0.
     * All other voxels have their values kept unchanged.
     *
     * @param method the method to use for thresholding.
     * @param convertToGray8 If true, the binarized image is to be converted to GRAY8.
     * 						 This option is ignored if this image already has type GRAY8.
     * 
     * @see ThresholdingOption
 	 * @return The image underlying this instance to allow for use of the Cascade Pattern
     */
    public ImageCore thresholdImageKeepGray(ThresholdingOption method, boolean convertToGray8);
    
    /**
     * Performs thresholding on this image using a given method option,
     * i.e. Any voxel with a value greater than or equal to the threshold have their value set to 0.
     * all other voxels are set to white.
     * @see ImageCore#getWhiteValue()
     *
     * @param method the method to use for thresholding.
     * @param convertToGray8 If true, the binarized image is to be converted to GRAY8.
     * 						 This option is ignored if this image already has type GRAY8.
     * 
     * @see ThresholdingOption
  	 * @return The image underlying this instance to allow for use of the Cascade Pattern
    */
    public ImageCore thresholdImageAndBinarize(ThresholdingOption method, boolean convertToGray8);
    
    /**
     * Performs an adptive thresholding on this image using a given method option,
     * i.e. Any voxel with a value greater than or equal to the threshold have
     * their value set to 0.
     * However, the threshold is weighed to take into account the intensities
     * in a vicinity of the considered voxel, to account for a relative contrast
     * of the voxel in a neighborhood.
     * 
     * TODO Has to be tested and tuned.
     *
     * @param sigmaX The typical width of the neighborhood taken into account
     * @param sigmaY The typical height of the neighborhood taken into account
     * @param sigmaZ The typical depth of the neighborhood taken into account
 	 * @param powExponent Exponent on the histogram value in the histogram equalization's integration
     * @param exponentValues Increases the dynamics of veriy bright spots being attenuated.
     * @param impact A coefficient between zero and one. The lower, the less adaptive.
     * 				 Setting impact to zero is just a costly way to compute
     * 				 {@link #thresholdImageAndBinarize(ThresholdingOption, boolean)}.
     * @see ThresholdingOption
  	 * @return The image underlying this instance to allow for use of the Cascade Pattern
    */
    public ImageCore adaptiveThresholdPreprocess(double sigmaX, double sigmaY, double sigmaZ, 
						    		   		double powExponent, double exponentValues, double impact);
    
} // End of interface
