/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  TestImageDomainOperation.java                                     * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.test;

import java.io.File;
import java.io.IOException;

import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * @author remy
 * 
 * Allows to test operations on an image domain , such as cropping or adding margins.
 *
 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation
 * 
 */
public class TestImageDomainOperation {
	
	
	/**
	 * Allows to crop an image and save the results
	 * @param  inputImageFile source image path on disk
	 * @throws IOException 
	 */
	public static void testImageCrop(String inputImageFile) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  		.getImageCore(inputImageFile,
														  false, // Convert to GRAY8
														  false, // Scale colors to maximize contrast
														  RetrievalPolicy.TryConfFileNoDialog);
			image.retrieveMetaData();

			ImageCore[] catImage = new ImageCore[]{
				image.getImageDomainOperation()
									  .crop(0, 0, 0, 531, 743, 48),
				image.getImageDomainOperation()
									  .crop(0, 0, 0, 733, 525, 45)					  
			};
			
			for (int i=0 ; i<catImage.length ; i++){
				
				catImage[i].setTitle("Image Crop");
				System.err.println("Calibration catImage: " + catImage[i].getImageCalibration());
				
				catImage[i].getPreferedRenderTool().display(catImage[i]);
				
				File inFile = new File(inputImageFile);
				catImage[i].writeToFileWithMetaData(inFile.getParent(), 
												"crop_" + catImage[i].getWidth() +
												"_" + catImage[i].getHeight() +
												"_" + catImage[i].getDepth() +
												"_" + inFile.getName(), "TIF");
				
				catImage[i].getImageContrast().maximizeValuesRange();
			}

	}	
	
	
	/**
	 * Allows to test concatenation of two images along one axis
	 * @param  inputImageFile source image path on disk
	 * @param axis The axis along which to place one image domain after the other.
	 * @param coordMinInsert The minimal coordinate of the planes of argument image domain within the
	 * 						 resulting image. The first slice (with coordinate Z=0) of image
	 * 						 is mapped onto the slice with coordinate Z=zCoordMin.
	 * @throws IOException 
	 */
	public static void testImageSandwich(String inputImageFile, CoordinateAxis axis, int coordMinInsert) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  		.getImageCore(inputImageFile,
														  true, // Convert to GRAY8
														  true, // Scale colors to maximize contrast
														  RetrievalPolicy.TryConfFileNoDialog);
			image.retrieveMetaData();

			ImageCore catImage = image.getImageDomainOperation()
									  .insertImageSandwich(image, axis, coordMinInsert);
			catImage.setTitle("Image Concatenation");
			System.err.println("Calibration catImage: " + catImage.getImageCalibration());
			
			catImage.getPreferedRenderTool().display(catImage);

	}
	

	/**
	 * Allows to test addition of slices from one image to another (without slice copy)
	 * @param  inputImageFile source image path on disk, which shall be added to itself.
	 * @throws IOException 
	 */
	public static void testExtractSlices(String inputImageFile) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  		.getImageCore(inputImageFile,
														  true, // Convert to GRAY8
														  true, // Scale colors to maximize contrast
														  RetrievalPolicy.TryConfFileNoDialog);
		image.retrieveMetaData();

		ImageCore imageExtractedSlices = image.getImageDomainOperation().extractSlices(15, 41);
		imageExtractedSlices.setTitle("Image Extracted slices");
		System.err.println("Calibration imageExtractedSlices: " 
						   + imageExtractedSlices.getImageCalibration()
						  );
		
		image.getPreferedRenderTool().display(image);
		imageExtractedSlices.getPreferedRenderTool().display(imageExtractedSlices);
			
	}
	
	/**
	 * Allows to test enlargement of an image's domain
	 * @param  inputImageFile source image path on disk
	 * @param xMargin Width of the margin to add on both sides
	 * @param yMargin Height of the margin to add on both sides
	 * @param zMargin Depth of the margin to add on both sides
	 * @param enlargementPolicy Specifies the policy to initialize the new voxel values.
	 * @throws IOException 
	 */
	public static void testEnlargedImage(String inputImageFile,
										 int xMargin, int yMargin, int zMargin,
										 BufferEnlargementPolicy enlargementPolicy) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  		.getImageCore(inputImageFile,
														  true, // Convert to GRAY8
														  true, // Scale colors to maximize contrast
														  RetrievalPolicy.TryConfFileNoDialog
														);
			
		image.retrieveMetaData();

		ImageCore enlargedImageImage = image.getImageDomainOperation()
											.getEnlargedImage(xMargin, yMargin, zMargin,
															  enlargementPolicy
															 );
		
		enlargedImageImage.setTitle("Image Enlarged with policy " + enlargementPolicy);
		System.err.println("Calibration enlargedImageImage: " + 
							enlargedImageImage.getImageCalibration()
						  );
		
		image.getPreferedRenderTool().display(image);
		enlargedImageImage.getPreferedRenderTool().display(enlargedImageImage);

	}
	
	/**
	 * Allows to test addition of slices from one image to another (without slice copy)
	 * @param  inputImageFile source image path on disk, which shall be added to itself.
	 * @throws IOException 
	 */
	public static void testMergeSlices(String inputImageFile) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  		.getImageCore(inputImageFile,
														  true, // Convert to GRAY8
														  true, // Scale colors to maximize contrast
														  RetrievalPolicy.TryConfFileNoDialog);
		image.retrieveMetaData();

		image.getImageDomainOperation().mergeSlices(image.duplicate());
		
		image.getPreferedRenderTool().display(image);
		
	}
	
	
	/**
	 * Allows to test addition of slices from one image to another (without slice copy)
	 * @param  inputImageFile source image path on disk, which shall be added to itself.
	 * @param zCoordMin The minimal depth coordinate of the slices of image within the
	 * 					resulting image. The first slice (with coordinate Z=0) of image
	 * 					is mapped onto the slice with coordinate Z=zCoordMin.
	 * @throws IOException 
	 */
	public static void testInsertSlices(String inputImageFile, int zCoordMin) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  		.getImageCore(inputImageFile,
														  true, // Convert to GRAY8
														  true, // Scale colors to maximize contrast
														  RetrievalPolicy.TryConfFileNoDialog);
		image.retrieveMetaData();
		
		ImageCore copy = image.duplicate();
		copy.setTitle("Original Image");

		image.getImageDomainOperation().insertSlices(image.duplicate()
														  .getImageDomainTransform()
														  .getAxisReversed(new CoordinateAxis[]{CoordinateAxis.Z}),
													 zCoordMin);
		image.setTitle("Image with a Reversed Duplicate Instert at Z="+zCoordMin);
		
		image.getPreferedRenderTool().display(copy);
		image.getPreferedRenderTool().display(image);

	}
	
	/**
	 * Main function to run the tests in this class.
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		try {
		//testImageCrop(TestImageThresholding.getSampleImage(1));
		
		testImageSandwich(TestImageThresholding.getSampleImage(0), CoordinateAxis.Z, 30);
	
		//testMergeSlices(TestImageThresholding.getSampleImage(0));
		
		//testExtractSlices(TestImageThresholding.getSampleImage(0));
		
		//testInsertSlices(TestImageThresholding.getSampleImage(0), 20);
		
		//testEnlargedImage(TestImageThresholding.getSampleImage(0),
		//							 250, 250, 10,
		//							 BufferEnlargementPolicy.Pavement // Mirror // Zeros // Pavement
		//				 );
		
	} catch(IOException e){
		e.printStackTrace();
	}
		
		System.out.println(ResourcesMonitor.getRessourceInfo());
		
		System.out.println("The program ended normally.");
	}

}
