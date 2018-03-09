/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: Histogram.java                                                     * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

/******************************************************************************\
*     Copyright (C) 2016 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: Histogram.java                                                     * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrapScienceJ.wrapImaJ.core.histogram;

import java.io.IOException;

import wrapScienceJ.wrapImaJ.core.operation.ThresholdingOption;




/** 
 * The Histogram class manages basic routines about image histograms.
 * Such a histogram basically stores, for each grey level, 
 * the number of voxels (or pixels) un the image with this gray level as value.
 * These values are stored in an array with length the number of possible gray levels
 * (typically 256 for one byte gray levels).
 * 
 * The class performs operations such as equalization, brightness enhancement, etc.
 * the class performs efficient calculations for average gray level of an image.
 * 
 * @author Remy Malgoures
 */
public interface Histogram {
	
	
	/**
     * Duplicates the histogram (invocation of the operation)
     * @return a reference to a clone copy of this instance.
     */ 
	HistogramBaseGeneric duplicate();
	
	/**
	 * @return the length (number of values) of the histogram.
	 */
	public int getLength();
	
	/**
	 * @param grayLevel index (i.e. a given gray level)
	 * @return the ith value in the histogram (i.e. number of voxels with this color)
	 */
	public long getValue(int grayLevel);

 
	/**
	 * sets the ith value in the histogram (i.e. number of voxels with this color)
	 * @param grayLevel index (i.e. a given gray level)
	 * @param value the value to use for the i^th element in the histogram.
	 */
	public void setValue(int grayLevel, long value);
	
	/**
	 * increments a value in the histogram from its index.
	 * @param i index (i.e. a given gray level)
	 */
	public void incrementValue(int i);

	
	
	/**
	 * Saves the histogram to a file on disk.
	 * @param destinationFilePath the path to a file on disk where to save the histogram data
	 * @throws IOException in case of disk write failure (e.g. unknown directory or permission denied)
	 */
	public void writeToFile(String destinationFilePath) throws IOException;


	/**
	 * Loads the histogram from a text file with decimal encoded values into this instance.
	 * The file must contain nothing else than one integer value per line
	 * Each of these values is meant to be a value in the produced histogram.
	 * @param sourceFilePath Path to a text file with the histogram values
	 * @throws IOException if a disk read error occurs
	 */
	public void readFromFile(String sourceFilePath) throws IOException;



	/**
	 * Creates a new histogram with smoothed values obtained by convolution of this instance with a precomputer mask.
	 * @return the smoothed version of the histogram.
	 */
	//public Histogram getSmoothedHistogram();

	/**
	 * Computes the average grey level value of the image.
	 * @return the average grey level value.
	 */
	public double getGrayLevelAverage();
	
	/**
	 * Computes the standard deviation grey level value of the image.
	 * @return the standard deviation grey level values.
	 */
	public double getGrayLevelStandardDeviation();
	
	/**
	 * Computes the grey level threshold value for binarizing an image using this instance's histogram.
	 * Different method can be used to compute the threshold.
	 * @param method the thresholding method
	 * @return the threshold for the image
	 */
	public int getBinarizationThreshold(ThresholdingOption method);
	
} // End of class
