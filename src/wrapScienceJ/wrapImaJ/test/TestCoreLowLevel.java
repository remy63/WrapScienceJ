/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: TestCoreLowLevel.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

/******************************************************************************\
*     Copyright (C) 2016 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: TestCoreLowLevel.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.test;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.wrapImaJ.connectivity.filtering.predefined.ComponentRemovalLinear;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelShort;

import java.io.IOException;
import java.util.Random;

/**
 * Allows to test the methods for basic access to gray level values
 * and image loading from a file.
 * 
 * @author Remy Malgouyres
 * 
 * @see wrapScienceJ.wrapImaJ.core.ImageCore
 */
public class TestCoreLowLevel {
		
	
	/**
	 * Validates image orderly access to voxel's gray levels by going over an image
	 * This slice by slice access to the 8 bits per voxel image is faster than random access.
	 * @param inputImageFile Path to the input image.
	 */	 
	public static void testOrderlyAccessVoxels(String inputImageFile){
			// define a thick plane
			ComponentRemovalLinear thickPlanePredicate = new ComponentRemovalLinear(
					0.02, 0.0, -17.0, 35.0
					);
			// pre-allocated voxel (to avoid saturating the garbage collector)
			VoxelShort voxel = new VoxelShort();
			
			try{
				ImageCore image = ImageCoreFactoryIJ.getInstance().getImageCore(inputImageFile);
				// Go over the image slice by slice
				for (short z=0 ; z< image.getDepth() ; z++){
					// set the slice
					image.setCurrentZ(z);
					for (short y=0 ; y<image.getHeight() ; y++){
						for (short x=0 ; x<image.getWidth() ; x++){
							voxel.setCoordinates(x, y, z);
							// (the predicate does not use the ComponentInfo, so, set it to null)
							if (!thickPlanePredicate.keepVoxelComponent(voxel, null)){
								// Set the pixel on the current slice
								image.setPixel(x, y, 150);
							}
						}
							
					}
				}
				// reinitialize the slice to update the last slice
				image.setCurrentZ(1);
				GlobalOptions.getDefaultRenderTool().display(image);
			} catch(IOException e){
				e.printStackTrace();
			}
		}
		
	
	/**
	 * Validates image random access to voxel's gray levels
	 * This random access is slower than the slice by slice access to the image
	 * @param inputImageFile Path to the input image.
	 */	 
	public static void testRandomAccessVoxels(String inputImageFile){
			Random random = new Random();
			// pre-allocated voxel (to avoid saturating the garbage collector)
			VoxelShort voxel = new VoxelShort();
		try{
			ImageCore image = ImageCoreFactoryIJ.getInstance().getImageCore(inputImageFile)
											  .getImageThresholding();
			
			// we add random noise to voxels' gray-level for a number of random voxels
			for (int i=0 ; i < 1.0e8 ; i++){
				// Get a random voxel :
				voxel.setCoordinates((short)(random.nextInt(image.getWidth()+1)), 
									 (short)(random.nextInt(image.getHeight()+1)), 
									 (short)(random.nextInt(image.getDepth())+1));
				// retrieve the original gray-level
				int grayLevel = image.getVoxel(voxel);
				// add noise
				grayLevel += random.nextInt(50);
				// clamp
				short newGrayLevel = (short)((grayLevel < 0) ? 0 : (grayLevel > 255) ? 255 : grayLevel);
				// update the voxel's gray-level in the image
				image.setVoxel(voxel, newGrayLevel);
			}
			GlobalOptions.getDefaultRenderTool().display(image);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Validates image loading and display, with possible conversion to GRAY8, and possible
	 * scaling of the gray levels to match the color space range.
	 * @param inputImageFile source image file
	 * @param convertTo8bits If true, the image is to be converted to GRAY8 if necessary
	 * @param maximizeRangeValues if true, the gray levels are to be scaled so as to
	 * 							  occupy the whole color space.
	 * @throws IOException 
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageContrast#maximizeValuesRange()
	 */
	public static void testLoadImageAnDisplay(String inputImageFile, boolean convertTo8bits,
										boolean maximizeRangeValues) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance().getImageCore(
											inputImageFile,
											convertTo8bits, // Convert to 8 bits
											maximizeRangeValues, // Maximize values range
												RetrievalPolicy.Unspecified
												);
 
		GlobalOptions.getDefaultRenderTool().display(image);

	}
	
		
	/**
	 * Main function of the package's tests.
	 * @param args
	 */
	public static void main(String[] args) {

		// Add some random noise to the image (Random Access: slow)
		//testRandomAccessVoxels(TestCoreMethods.getSampleImageGray8(0));
		
		// Add some random noise to the image (Orderly Access: faster)
		testOrderlyAccessVoxels(TestImageThresholding.getSampleImageGray8(0));
		
		//testLoadImageAnDisplay(getSampleImage(0), // file name
		//					   false, // convert to 8bits per voxel
		//					   true // Maximize color range
		//					   );

		System.err.println("The program ended normally.");
	}
}
