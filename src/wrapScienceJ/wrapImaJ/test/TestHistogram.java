/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  TestHistogram.java                                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.test;

import java.io.IOException;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.io.stream.FileHelper;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.ImageThresholding;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy;

/**
 * @author remy
 *
 * This class documents Histogram Construction and Operations
 * 
 * @see wrapScienceJ.wrapImaJ.core.histogram.Histogram
 * @see wrapScienceJ.wrapImaJ.core.histogram.HistogramBaseGeneric
 * @see wrapScienceJ.wrapImaJ.core.histogram.HistogramThresholding
 * 
 * TODO Re-run the tests here, and evaluate histogram equalization and the like.
 */
public class TestHistogram {
		
	/**
	 * Validates binomial blur, then histogram construction.
	 * @param inputImageFile Path to the source file for the input image
	 * @param outputFile1 output text file for the histogram before contrast operation.
	 * @param outputFile2 output text file for the histogram after contrast operation.
	 * @param nPointsX cardinality of the support of the smoothing mask; first coordinate
	 * @param nPointsY cardinality of the support of the smoothing mask; second coordinate
	 * @param nPointsZ cardinality of the support of the smoothing mask; second coordinate
	 * @throws IOException 
	 */
	public static void testBinomialBlurHistogram(String inputImageFile, String outputFile1, String outputFile2,
										  int nPointsX, int nPointsY, int nPointsZ) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile)
											.getImageThresholding();
		
		ImageCore copy = image.duplicate();
		copy.setTitle("Original Image");
		copy.getImageContrast().maximizeValuesRange();
		image.setTitle("Blurred Image");
		GlobalOptions.getDefaultRenderTool().display(copy);
		
		copy.getImageThresholding().getHistogram().writeToFile(outputFile1);
		
		image = image.getImageBlur()
					 .getBinomialBlur(nPointsX, nPointsY, nPointsZ)
					 .getImageConvolved(ConvolutionNormalizationPolicy
										.Gray8_QuantitativeNormalization_Clamp);
		
		image.getImageContrast().maximizeValuesRange();
		
		image.getImageThresholding().getHistogram().writeToFile(outputFile2);
		
		GlobalOptions.getDefaultRenderTool().display(image);

	}
	
	
	/**
	 * Validates Gaussian blur, then histogram construction.
	 * @param inputImageFile Path to the source file for the input image
	 * @param outputFile1 output text file for the histogram before contrast operation.
	 * @param outputFile2 output text file for the histogram after contrast operation.
	 * @param sigmaX standard deviation of the Gaussian on the x coordinate
	 * @param sigmaY standard deviation of the Gaussian on the y coordinate
	 * @param sigmaZ standard deviation of the Gaussian on the z coordinate
	 * @throws IOException 
	 */
	public static void testGaussianBlurHistogram(String inputImageFile, 
										  String outputFile1,
										  String outputFile2,
										  double sigmaX, double sigmaY, double sigmaZ) throws IOException{
			
		ImageThresholding image = ImageCoreFactoryIJ.getInstance()
													.getImageCore(inputImageFile)
													.getImageThresholding();
		ImageCore copy = image.duplicate();
		copy.setTitle("Original Image");
		image.setTitle("Blurred Image");
		copy.getImageContrast().maximizeValuesRange();
		
		GlobalOptions.getDefaultRenderTool().display(copy);
		
		image.getHistogram().writeToFile(outputFile1);			
		image.getImageBlur().getGaussianBlur(sigmaX, sigmaY, sigmaZ);
		image.getImageContrast().maximizeValuesRange();
		
		
		image.getHistogram().writeToFile(outputFile2);
		
		GlobalOptions.getDefaultRenderTool().display(image);
			
	}


	/**
	 * Main function to run the tests.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
	
			testBinomialBlurHistogram(TestImageThresholding.getSampleImage(0),
					TestImageThresholding.getTestOutputDir() + 
					FileHelper.getBaseName(TestImageThresholding.getSampleImage(0))
					+ "_histogramRaw.txt",
					TestImageThresholding.getTestOutputDir() + 
					FileHelper.getBaseName(TestImageThresholding.getSampleImage(0))
					+ "_histogramBlurred.txt",
					6, 6, 6);
			
			//testGaussianBlurHistogram(TestImageThresholding.getSampleImage(1),
			//		FileHelper.changeDirectoryPostfixAndExtension(
			//				TestImageThresholding.getSampleImage(1),
			//				TestImageThresholding.getTestOutputDir(),
			//				"_histogramRaw", "txt"),
			//		FileHelper.changeDirectoryPostfixAndExtension(
			//				TestImageThresholding.getSampleImage(1),
			//				TestImageThresholding.getTestOutputDir(),
			//				"_histogramBlurred", "txt"),
			//		6.0d, 6.0d, 6.0d
			//);
	
		} catch(IOException e){
			e.printStackTrace();
		}
		
		System.out.println(ResourcesMonitor.getRessourceInfo());
		System.out.println("The program ended normally.");

	}
}
