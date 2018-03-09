/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: HistogramBaseGeneric.java                                          * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core.histogram;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import wrapScienceJ.wrapImaJ.core.ImageCoreGray8;


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
 * @author Remy
 */
public abstract class HistogramBaseGeneric implements Histogram {

	/**
	 * Array for storing the values of the histogram.
	 */
	protected long[] m_values;

	/**
	 * Binomial mask for histogram smoothing
	 */
	private static final double[] m_preComputedMask = {1 / 16.0, 4 / 16.0, 6 / 16.0, 4 / 16.0, 1 / 16.0};
	
	/**
	 * Constructor of a histogram with a given length, all values initialized to 0
	 * @param length number of values in the histogram
	 */
	public HistogramBaseGeneric(int length) {
		this.m_values = new long [length];
		for (int i=0 ; i<length ; i++){
			this.m_values[i] = 0;
		}
	}
	
	/**
	 * Constructor of a histogram with a given length, all values initialized to 0
	 * @param image the input image from which to build the histogram.
	 */
	public HistogramBaseGeneric(ImageCoreGray8 image) {
		this.m_values = image.buildHistogram();
	}
	
	/**
	 * Constructor which creates a copy of another instance
	 * @param otherHistogram the original instance.
	 */
	public HistogramBaseGeneric(Histogram otherHistogram) {
    	this.m_values = new long[otherHistogram.getLength()];
    	for (int i=0 ; i<this.m_values.length ; i++){
    		this.m_values[i] = getValue(i);
    	}
	}
	
		
	
	/**
	 * @return the length (number of values) of the histogram.
	 */
	@Override
	public int getLength() {
		return this.m_values.length;
	}
	
	/**
	 * @param grayLevel index (i.e. a given gray level)
	 * @return the ith value in the histogram (i.e. number of voxels with this color)
	 */
	@Override
	public long getValue(int grayLevel) {
		if (0 <= grayLevel && grayLevel < 256) {
			return this.m_values[grayLevel];
		} 
		return 0;
	}

 
	/**
	 * sets the ith value in the histogram (i.e. number of voxels with this color)
	 * @param grayLevel index (i.e. a given gray level)
	 * @param value the value to use for the i^th element in the histogram.
	 */
	@Override
	public void setValue(int grayLevel, long value) {
		if (0 <= grayLevel && grayLevel < 256) {
			this.m_values[grayLevel]  = value;
		} 
	}
	
	/**
	 * increments a value in the histogram from its index.
	 * @param i index (i.e. a given gray level)
	 */
	@Override
	public void incrementValue(int i) {
		this.m_values[i]++;
	}

	
	
	/**
	 * Saves the histogram to a file on disk.
	 * @param destinationFilePath the path to a file on disk where to save the histogram data
	 * @throws IOException in case of disk write failure (e.g. unknown directory or permission denied)
	 */
	@Override
	public void writeToFile(String destinationFilePath) throws IOException {

		File file = new File(destinationFilePath);

		if (!file.exists()) {
			file.createNewFile();
		}
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destinationFilePath)))){
			writer.write(this.toString());
		}
	}


	/**
	 * Loads the histogram from a text file with decimal encoded values into this instance.
	 * The file must contain nothing else than one integer value per line
	 * Each of these values is meant to be a value in the produced histogram.
	 * @param sourceFilePath Path to a text file with the histogram values
	 * @throws IOException if a disk read error occurs
	 */
	@Override
	public void readFromFile(String sourceFilePath) throws IOException {

		ArrayList<Integer> values = new ArrayList<Integer>();
		File file = new File(sourceFilePath);
		try(BufferedReader reader = new BufferedReader(new FileReader(file))){
			boolean finished = false;
			while (!finished){
				try{
					String inputString = reader.readLine();
					values.add(new Integer(Integer.parseInt(inputString)));
				} catch(Exception e){
					finished = true;
				}
			}
		}

		this.m_values = new long[values.size()];
		for (int i=0 ; i<values.size() ; i++){
			setValue(i, values.get(i).intValue());
		}
	}

	
	
	/**
	 * @return a string representation of the histogram width one histogram value per line (i.e. separated by '\n')
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			str.append(this.m_values[i]);
			str.append("\n");
		}
		String st = str.toString();
		return st;
	}


	/**
	 * @param i an integer index, which can be positive or negative.
	 * @return the i^th value of of a centered mask (returns zero out of the support)
	 */
	private static double getCenteredMaskValue(int i) {
		if (i < -2 || i > 2) {
			return 0.0d;
		}
		return m_preComputedMask[i + 2];
	}

	/**
	 * Performs the convolution with a centered binomial mask for the purpose of smoothing the histogram.
	 * @param grayLevel index in the histogram range.
	 * @return the smoothed value of the histogram.
	 */
	protected int getSmoothingConvolutionValue(int grayLevel) {
		double value, mask, conv, som = 0;
		for (int j = -2; j <= 2; j++) {
			value = getValue(grayLevel - j);
			mask = getCenteredMaskValue(j);
			conv = value * mask;
			som += conv;
		}
		int resultat = (int) som;

		return (resultat < 0) ? 0 : resultat;
	}

	/**
	 * Creates a new histogram with smoothed values obtained by convolution of this instance with a precomputer mask.
	 * @return the smoothed version of the histogram.
	 * @throws CloneNotSupportedException if the extension of Histogram does not support the clone operation.
	 */
	/*@Override
	public HistogramBase getSmoothedHistogram() {
		HistogramBase newHistogram = new HistogramBase(this);

		for (int i = 0; i < this.m_values.length; i++) {
			newHistogram.setValue(i, getSmoothingConvolutionValue(i));
		}
		return newHistogram;
	}*/

	/**
	 * Computes the average grey level value of the image.
	 * @return the average grey level value.
	 */
	@Override
	public double getGrayLevelAverage(){
		double sum = 0.0d;
		double nPicElem = 0.0d;
		for (int i=0 ; i<256 ; i++){
			sum += i*(double)this.m_values[i];
			nPicElem+=this.m_values[i];
		}
		return sum/nPicElem;
	}
	
	/**
	 * Computes the standard deviation grey level value of the image.
	 * @return the standard deviation grey level values.
	 */
	@Override
	public double getGrayLevelStandardDeviation(){
		double mean = this.getGrayLevelAverage();
		double sem = 0.0d;
		double nPicElem = 0.0d;
		for (int i=0 ; i<256 ; i++){
			sem += ((i-mean)*(i-mean))*this.m_values[i];
			nPicElem+=this.m_values[i];
		}
		return Math.sqrt(sem/nPicElem);
	}
} // End of class
