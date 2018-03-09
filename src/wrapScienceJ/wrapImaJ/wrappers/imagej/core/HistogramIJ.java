/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: HistogramIJ.java                                                   * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.wrappers.imagej.core;

import wrapScienceJ.wrapImaJ.core.*;
import wrapScienceJ.wrapImaJ.core.histogram.Histogram;
import wrapScienceJ.wrapImaJ.core.histogram.HistogramBaseGeneric;
import wrapScienceJ.wrapImaJ.core.histogram.HistogramThresholding;
import wrapScienceJ.wrapImaJ.core.operation.ThresholdingOption;

import fiji.threshold.Auto_Threshold;

/**
 * This class is the ImageJ based extension of the abstract class Histogram
 * 
 * @author remy
 *
 */
public class HistogramIJ extends HistogramBaseGeneric implements HistogramThresholding{

	/**
	 * Constructor of a histogram with a given length, all values initialized to 0
	 * @param length number of values in the histogram
	 */
	public HistogramIJ(int length) {
		super(length);
	}
	
	/**
	 * Constructor of a histogram with a given length, all values initialized to 0
	 * @param image the input image from which to build the histogram.
	 */
	public HistogramIJ(ImageCoreGray8 image) {
		super(image);
	}
	
	/**
	 * Constructor which creates a copy of another instance
	 * @param otherHistogram the original instance.
	 */
	public HistogramIJ(Histogram otherHistogram) {
    	super(otherHistogram);
	}    
	
	/**
	 * Override of the clone operation
	 * @return a reference to a clone copy of this instance.
	 */
	@Override
	protected Object clone() {
		return new HistogramIJ(this);
	}
	
	/**
     * Duplicates the histogram (invocation of the operation)
     * @return a reference to a clone copy of this instance.
     */   
	@Override
	public HistogramIJ duplicate(){
		return (HistogramIJ)clone();
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.histogram.Histogram#getBinarizationThreshold(wrapScienceJ.wrapImaJ.core.operation.ThresholdingOption)
	 */
	@Override
	public int getBinarizationThreshold(ThresholdingOption method){
		return getBinarizationThreshold(method, this.m_values);
	}


	/**
	 * Computes the grey level threshold value for binarizing an image, using its histogram.
	 * Different method can be used to compute the threshold.
	 * 
	 * Note that this is not used. The method could be static but is not because
	 * static methods are not supported in Java 7 interfaces (ImageCoreGray8)
	 * 
	 * @param method the thresholding method
	 * @param histogram the previously computed histogram of the image
	 * @return the threshold for the image
	 * 
	 * @see ThresholdingOption
	 */
	protected static int getBinarizationThreshold(ThresholdingOption method, long[] histogram) {
		
		long max = 0;
		for (int i=0 ; i<histogram.length ; i++){
			if (histogram[i] > max){
				max = histogram[i];
			}
		}
		
		int[] histogramInt = new int[256];
		int ratioSample = histogram.length/256;
		if (ratioSample == 0){
			ratioSample = 1;
		}
		int ratioValue = (int)(max/Integer.MAX_VALUE);
		if (ratioValue == 0){
			ratioValue = 1;
		}		
		System.err.println("ratioSample = "+ ratioSample +", ratioValue = "+ ratioValue);
		for (int i=0 ; i<histogram.length ; i++){
			histogramInt[i/ratioSample] += histogram[i]/ratioValue;
			//System.err.println("i = "+ i +", hist = "+histogramInt[i/ratioSample]);
		}	
		
		switch (method) {
		case Huang:
			return Auto_Threshold.Huang(histogramInt);
		case Intermodes:
			return Auto_Threshold.Intermodes(histogramInt);
		case IsoData:
			return Auto_Threshold.IsoData(histogramInt);
		case Li:
			return Auto_Threshold.Li(histogramInt);
		case MaxEntropy:
			return Auto_Threshold.MaxEntropy(histogramInt);
		case Mean:
			return Auto_Threshold.Mean(histogramInt);
		case MinError:
			return Auto_Threshold.MinErrorI(histogramInt);
		case Minimum:
			return Auto_Threshold.Minimum(histogramInt);
		case Moments:
			return Auto_Threshold.Moments(histogramInt);
		case Otsu:
			return Auto_Threshold.Otsu(histogramInt);
		case Percentile:
			return Auto_Threshold.Percentile(histogramInt);
		case RenyEntropy:
			return Auto_Threshold.RenyiEntropy(histogramInt);
		case Shanbhag:
			return Auto_Threshold.Shanbhag(histogramInt);
		case Triangle:
			return Auto_Threshold.Triangle(histogramInt);
		case Yen:
			return Auto_Threshold.Yen(histogramInt);
		default:
			return Auto_Threshold.IJDefault(histogramInt);
		}
	}

}
