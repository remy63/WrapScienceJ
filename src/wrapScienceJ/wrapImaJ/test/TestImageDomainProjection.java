/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  TestImageDomainProjection.java                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.test;


import java.io.IOException;

import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * @author remy
 * 
 * Allows to test operations to project an image on a coordinate plane and render the pixels.
 *
 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainProjection
 * 
 */
public class TestImageDomainProjection {
	
	/**
	 * Allows to test swapping two axis in an image domain.
	 * @param  inputImageFile source image path on disk
	 * @param axis The axis along which to project
	 * @throws IOException 
	 */
	public static void testProjectionRendering(String inputImageFile, CoordinateAxis axis) throws IOException{
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  		.getImageCore(inputImageFile,
													  false, // Convert to GRAY8
													  false, // Scale colors to maximize contrast
													  RetrievalPolicy.TryConfFileNoDialog);
			
		ImageCore tomographyImage = image.getImageDomainProjection()
										 .projectionTomography(axis, true);
		tomographyImage.setTitle("Tomographic Projection Along " + axis);
		
		ImageCore volumeRenderingImage = image.getImageDomainProjection()
											  .projectionVolumeRendering(axis, true);
		volumeRenderingImage.setTitle("Volume Rendering Projection Along " + axis);
		
		tomographyImage.getPreferedRenderTool().display(tomographyImage);
		volumeRenderingImage.getPreferedRenderTool().display(volumeRenderingImage);
			
	}

	/**
	 * Main function to run the tests in this class.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			testProjectionRendering(TestImageThresholding.getSampleImage(0), CoordinateAxis.Y);
			
			System.out.println(ResourcesMonitor.getRessourceInfo());
			
			System.out.println("The program ended normally.");
		
		} catch(IOException e){
			e.printStackTrace();
		}
		
		System.out.println(ResourcesMonitor.getRessourceInfo());
		System.out.println("The program ended normally.");

	}
}
