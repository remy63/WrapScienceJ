/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageThresholdingIJ.java                                           * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.wrappers.imagej.core;


import ij.ImagePlus;
import wrapScienceJ.wrapImaJ.core.*;
import wrapScienceJ.wrapImaJ.core.histogram.Histogram;
import wrapScienceJ.wrapImaJ.core.operation.ImageThresholding;
import wrapScienceJ.wrapImaJ.core.operation.ThresholdingOption;

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
public class ImageThresholdingIJ extends ImageCoreGray8IJ implements ImageThresholding{
    
    /**
     * Histogram instance representing the image's histogram.
     */
    protected HistogramIJ m_histogram = null;

    /**
     * Constructs an ImageProcessing instance as a copy of an ImageWrapper
     * @param image
     * @throws CloneNotSupportedException 
     */
    public ImageThresholdingIJ(ImageCoreIJ image) {
    	super(image);     
    	System.err.println("getMin() : "+image.getImp().getProcessor().getMin()
    						+ ", getMax() : "+image.getImp().getProcessor().getMax());
    	extractHistogramFromImage();
    }
    

    
    /**
     * Override of the clone operation
     * @return a reference to a clone copy of this instance.
     */
    @Override
    protected Object clone() {
    	return new ImageThresholdingIJ(this);
    }
    

    /**
     * Returns the underlying image of this instance. If the histogram has not been built, then it is built by this function. 
     * @return the histogram of the underlying image of this instance
     */
    @Override
    public  HistogramIJ getHistogram() {
        return this.m_histogram;
    }
    
    
    /**
     * Constructs the histogram from the underlying image of this instance and initializes the m_histogram instance.
     */
    private void extractHistogramFromImage() {
    	//getImageConvert().convertToGray8();
        long[] hist = buildHistogram();
        HistogramIJ newHist = new HistogramIJ(hist.length);

        for (int i=0 ; i<hist.length ; i++){
        	newHist.setValue(i, hist[i]);
        }

       //System.err.println(newHist);
        this.m_histogram = newHist;
    }
    

    /**
     * @see wrapScienceJ.wrapImaJ.core.operation.ImageThresholding#applyImageThresholdAndBinarize(int, boolean)
     */
    @Override
    public ImageCoreIJ applyImageThresholdKeepGray(int threshold, boolean convertToGray8) {
    	
    	
    	int actualThreshold;
    	switch (this.m_imp.getType()){
			case ImagePlus.GRAY8 : 
				actualThreshold = threshold;
				break;   	
    		case ImagePlus.GRAY16 : 
    			actualThreshold = 256*threshold;
    			break;
    		default:
    			throw new IllegalArgumentException("Thresholding is not supported for that kind of" +
    												"images.");
    	}

    	System.err.println("threshold = " + threshold + ", actualThreshold = " + actualThreshold);
        for (int z = 0; z < getDepth(); z++) {
        	setCurrentZ(z);
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    if (getPixel(x, y) < actualThreshold) {
                    	setPixel(x, y, 0);
                    }
                }
            }
        }
        
        if (convertToGray8){
        	getImageConvert().convertToGray8(true);
        }
        return this;
    }

    /**
     * @see wrapScienceJ.wrapImaJ.core.operation.ImageThresholding#applyImageThresholdAndBinarize(int, boolean)
     */
    @Override
    public ImageCoreIJ applyImageThresholdAndBinarize(int threshold, boolean convertToGray8) {
    	
    	
    	int actualThreshold;
    	int white = getWhiteValue();
    	switch (this.m_imp.getType()){
			case ImagePlus.GRAY8 : 
				actualThreshold = threshold;
				break;   	
    		case ImagePlus.GRAY16 : 
    			actualThreshold = 256*threshold;
    			break;
    		default:
    			throw new IllegalArgumentException("Thresholding is not supported for that kind of" +
    												"images.");
    	}

    	System.err.println("threshold = " + threshold + ", actualThreshold = " + actualThreshold);
        for (int z = 0; z < getDepth(); z++) {
        	setCurrentZ(z);
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    if (getPixel(x, y) >= actualThreshold) {
                    	setPixel(x, y, white);
                    } else {
                    	setPixel(x, y, 0);
                    }
                }
            }
        }
        
        if (convertToGray8){
        	getImageConvert().convertToGray8(true);
        }
        return this;
    }
    
    /**
     * @see wrapScienceJ.wrapImaJ.core.operation.ImageThresholding#applyThresholdMinAndMax(double, double)
     */
    public ImageCoreIJ applyThresholdMinAndMax(double min, double max){
		int maxValue = getWhiteValue();

		int actualMin = (int)(min*maxValue+0.5);
		int actualMax = (int)(max*maxValue+0.5);
		
        for (int z = 0; z < getDepth(); z++) {
        	setCurrentZ(z);
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    if (getPixel(x, y) < actualMin || getPixel(x, y) > actualMax) {
                    	setPixel(x, y, 0);
                    }
                }
            }
        }
        return this;
    }


    /**
     * Performs thresholding on this image using a given threshold value,
     * i.e. Any voxel with a value greater than or equal to the threshold have their value set to 0.
     *
     * @param threshold the value for the threshold.
     * @param weightImage an image representing the local brightness/darkness of the
     * 					  original image.
     * @param impact A coefficient between zero and one. The lower, the less adaptive.
     * 				 Setting impact to zero is just a costly way to compute
     * 				 {@link #thresholdImage(ThresholdingOption)}.
     * 
     */
    protected void applyAdaptiveThresholdPreprocess(ImageCore weightImage, double impact) {
        for (int z = 0 ; z < getDepth(); z++) {
        	weightImage.setCurrentZ(z);
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                	int voxelValue = (int)(getPixel(x, y)
                			*(255-impact*weightImage.getPixel(x, y))/255.0);
                	if (voxelValue < 0){
                		voxelValue = 0;
                	}
                	if (voxelValue >255){
                		voxelValue = 255;
                	}                	
                	weightImage.setPixel(x, y, voxelValue);
                }
            }
        }
    }


    /**
     * Computes the grey level threshold value for binarizing an image using this instance's histogram.
     * Different method can be used to compute the threshold.
     * @param method the thresholding method
     * @return the threshold for the image
     */
    @Override
    public int getBinarizationThreshold(ThresholdingOption method){
    	return getHistogram().getBinarizationThreshold(method);
    }


    /**
     * @see wrapScienceJ.wrapImaJ.core.operation.ImageThresholding#thresholdImageKeepGray(wrapScienceJ.wrapImaJ.core.operation.ThresholdingOption, boolean)
     */
    @Override
    public ImageCoreIJ thresholdImageKeepGray(ThresholdingOption method, boolean convertToGray8) {
    	return applyImageThresholdKeepGray(getBinarizationThreshold(method), convertToGray8);
    }
    

    /**
     * @see wrapScienceJ.wrapImaJ.core.operation.ImageThresholding#thresholdImageAndBinarize(wrapScienceJ.wrapImaJ.core.operation.ThresholdingOption, boolean)
     */
    @Override
    public ImageCoreIJ thresholdImageAndBinarize(ThresholdingOption method, boolean convertToGray8) {
    	return applyImageThresholdAndBinarize(getBinarizationThreshold(method), convertToGray8);
    }


    /**
     * @see wrapScienceJ.wrapImaJ.core.operation.ImageThresholding#adaptiveThresholdPreprocess(double, double, double, double, double, double)
     */
    @Override
    public ImageCore adaptiveThresholdPreprocess(double sigmaX, double sigmaY, double sigmaZ, 
						    		   		double powExponent, double exponentValues, double impact) {

    	ImageCore imageCopy = duplicate();
    	imageCopy.setTitle("Weight for adaptive thresholding");
		imageCopy.getImageContrast().equalize(powExponent, exponentValues, 1.0);
		imageCopy.getImageBlur().getGaussianBlur(sigmaX, sigmaY, sigmaZ);
    	imageCopy.getPreferedRenderTool().display(imageCopy);

    	applyAdaptiveThresholdPreprocess(imageCopy, impact);
    	
    	return imageCopy;
    }
    
} // End of class
