/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: HistogramThresholding.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core.histogram;

import wrapScienceJ.wrapImaJ.core.operation.ThresholdingOption;


/**
 * This interface exposes the functionality of computing a gray level threshold
 * for binarization, implementing some of the methods refered to in the
 * {@link ThresholdingOption} enumeration.
 * 
 * @author remy
 *
 */
public interface HistogramThresholding extends Histogram {
	
	
	 /**
     * Computes the grey level threshold value for binarizing an image using this instance's histogram.
     * Different method can be used to compute the threshold.
     * @param method the thresholding method
     * @return the threshold for the image
	 * @throws UnsupportedOperationException if the thresholding option is not implemented
     */
    public int getBinarizationThreshold(ThresholdingOption method) throws UnsupportedOperationException;

}
