/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  TestImageDomainTransform.java                                     * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.io.stream.FileHelper;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * @author remy
 * 
 * Allows to test transfroms on images' domains, such are symmetries and rotations.
 *
 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainTransform
 */
public class TestImageDomainTransform {
	
	/**
	 * Allows to test swapping two axis in an image domain.
	 * @param  inputImageFile source image path on disk
	 * @param axis1 The first axis to be swapped
	 * @param axis2 The second axis to be swapped
	 * @throws IOException 
	 */
	public static void testGetAxisSwapped(String inputImageFile, CoordinateAxis axis1, CoordinateAxis axis2) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  		.getImageCore(inputImageFile,
														  false, // Convert to GRAY8
														  false, // Scale colors to maximize contrast
														  RetrievalPolicy.TryConfFileNoDialog);
		image.retrieveMetaData();
		ImageCore swappedImage = image.getImageDomainTransform().getAxisSwapped(axis1, axis2);

		swappedImage.setTitle("Swapped Axis (" + axis1 + ", " + axis2 + ")");
		System.err.println("Calibration swappedImage: " + swappedImage.getImageCalibration());
		
		BufferedImage img = swappedImage.getImageConvert()
										.projectionTomography(CoordinateAxis.Z, true);
		TestAwtImage.saveAwtImage(img, 
								  new File(
											FileHelper.changeDirectoryPostfixAndExtension(
											inputImageFile,
											TestImageThresholding.getTestOutputDir(),
											"_swapped_"+ axis1 + axis2 + "_projectionZ", "png")
										  )
								  );

	}
	
	
	/**
	 * Allows to test swapping two axis in an image domain.
	 * @param  inputImageFile source image path on disk
	 * @throws IOException 
	 */
	public static void testCopyTo(String inputImageFile) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  		.getImageCore(inputImageFile,
														  true, // Convert to GRAY8
														  true, // Scale colors to maximize contrast
														  RetrievalPolicy.TryConfFileNoDialog);
		image.retrieveMetaData();
		ImageCore copy = ImageCoreFactoryIJ.getInstance()
										   .getEmptyImageCore(image.getWidth(), 
															  image.getHeight(), 
															  image.getDepth(), 
															  image.getBitDepth()
															 );
		image.getImageDomainTransform().copyTo(copy);
		copy.setTitle("Image Copy");
		System.err.println("Calibration copy: " + copy.getImageCalibration());
		
		image.getPreferedRenderTool().display(image);
		image.getPreferedRenderTool().display(copy);

	}
	
	
	/**
	 * Allows to test reversal of voxels along axis in a given set of distinct axis
	 * @param  inputImageFile source image path on disk
	 * @param axisCollection The axis to be reversed
	 * @throws IOException 
	 */
	public static void testReverseAxis(String inputImageFile, CoordinateAxis[] axisCollection) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  		.getImageCore(inputImageFile,
														  true, // Convert to GRAY8
														  true, // Scale colors to maximize contrast
														  RetrievalPolicy.TryConfFileNoDialog);
			
		ImageCore reversedImage = image.duplicate();
		reversedImage.setTitle("Reversed Image");
		
		reversedImage.getImageDomainTransform().getAxisReversed(axisCollection);
		
		image.getPreferedRenderTool().display(image);
		image.getPreferedRenderTool().display(reversedImage);

	}
	
	
	/**
	 * Allows to test swapping two axis in an image domain, and copying the result to a new image,
	 * without changing the original image.
	 * @param  inputImageFile source image path on disk
	 * @param axis1 The first axis to be swapped
	 * @param axis2 The second axis to be swapped
	 * @throws IOException 
	 */
	public static void testCopyPlaneRotated90(String inputImageFile,
											  CoordinateAxis axis1, CoordinateAxis axis2) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile,
														  false, // Convert to GRAY8
														  false, // Scale colors to maximize contrast
														  RetrievalPolicy.TryConfFileNoDialog);
			
		image.retrieveMetaData();
		
		ImageCore rotatedImage = image.getImageDomainTransform().getAxisSwapped(axis1, axis2);
		rotatedImage.getImageSignPolicyEmbed(false).setAllWhite();
		
		image.getImageDomainTransform().copyPlaneRotated90(rotatedImage, axis1, axis2);

		rotatedImage.setTitle("Rotated Axis (" + axis1 + ", " + axis2 + ")");
		System.err.println("Calibration rotated: " + rotatedImage.getImageCalibration());
		
		BufferedImage img = rotatedImage.getImageConvert()
										.projectionTomography(CoordinateAxis.Z, true);
		TestAwtImage.saveAwtImage(img, 
								  new File(
											FileHelper.changeDirectoryPostfixAndExtension(
											inputImageFile,
											TestImageThresholding.getTestOutputDir(),
											"_copyRotated_"+ axis1 + axis2 + "_projectionZ",
											"png")
										  )
								  );
	}
	
	
	/**
	 * Allows to test swapping two axis in an image domain.
	 * @param  inputImageFile source image path on disk
	 * @param axis1 The first axis to be swapped
	 * @param axis2 The second axis to be swapped
	 * @throws IOException 
	 */
	public static void testGetPlaneRotated90(String inputImageFile, CoordinateAxis axis1, CoordinateAxis axis2) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  		.getImageCore(inputImageFile,
														  false, // Convert to GRAY8
														  false, // Scale colors to maximize contrast
														  RetrievalPolicy.TryConfFileNoDialog);
		image.retrieveMetaData();
		ImageCore rotatedImage = image.getImageDomainTransform().getPlaneRotated90(axis1, axis2);

		rotatedImage.setTitle("Rotated Axis (" + axis1 + ", " + axis2 + ")");
		System.err.println("Calibration rotated: " + rotatedImage.getImageCalibration());
		
		BufferedImage img = rotatedImage.getImageConvert()
										.projectionTomography(CoordinateAxis.Z, true);
		TestAwtImage.saveAwtImage(img, 
								  new File(
											FileHelper.changeDirectoryPostfixAndExtension(
											inputImageFile,
											TestImageThresholding.getTestOutputDir(),
											"_rotated_"+ axis1 + axis2 + "_projectionZ", "png")
										  )
								  );
			
	}
	
	/**
	 * Main function to run the tests in this class.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			//testGetAxisSwapped(TestImageThresholding.getSampleImage(0), AxisOption.X, AxisOption.Z);
			
			testCopyPlaneRotated90(TestImageThresholding.getSampleImage(0), CoordinateAxis.X, CoordinateAxis.Y);
				
			//testCopyTo(TestImageThresholding.getSampleImage(0));
			
			//testReverseAxis(TestImageThresholding.getSampleImage(0),
			//				new AxisOption[]{AxisOption.Y, AxisOption.X});
			
			//testGetPlaneRotated90(TestImageThresholding.getSampleImage(0), AxisOption.Z, AxisOption.X);
	
	
		} catch(IOException e){
			e.printStackTrace();
		}

		System.out.println(ResourcesMonitor.getRessourceInfo());
		
		System.out.println("The program ended normally.");
	}

}
