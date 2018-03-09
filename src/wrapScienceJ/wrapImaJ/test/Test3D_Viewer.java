/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: Test3D_Viewer.java                                                 * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.test;

import java.io.IOException;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.factory.render.RenderToolFactoryIJ3D;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.connectivity.ConnectedComponent;
import wrapScienceJ.wrapImaJ.connectivity.LabelingPolicy;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelDouble;
import wrapScienceJ.wrapImaJ.core.operation.ThresholdingOption;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy;

/**
 * @author remy
 * 
 * This class is intended to test the 3D Viewer use.
 * Note the remarks about possible deprecation in the render tool's own documentation.
 * 
 * @see wrapScienceJ.wrapImaJ.wrappers.imagej.gui.render.RenderToolIJ3D
 * 
 */
public class Test3D_Viewer {
	
	/**
	 * Test for labeling connected components of a binarized image and
	 * display the results in 3D.
	 * 
	 * Connected components with a volume below some threshold are
	 * also removed.
	 * a constant random gray level is set on each connected component.
	 *
	 * @see TestConnectedComponents
	 * 
	 * @param inputImageFile the input image file on disk 
	 * @param componentVolumeThreshold Minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @throws IOException
	 * 
	 */
	public static void testComponentsLabeling(String inputImageFile, 
											  double componentVolumeThreshold) throws IOException {
		
		ImageCore image = ImageCoreFactoryIJ.getInstance()
		  		.getImageCore(inputImageFile,
					  		  true, // Convert to 8 bits
							  true, // Maximize values range
							  RetrievalPolicy.TryConfFileThenDialog
							);

		VoxelDouble voxelEdgesLength = image.setTitle("Original Image")
						.retrieveMetaData()
						.getImageCalibration()
						.getVoxelLength();
		
		GlobalOptions.getDefaultRenderTool().display(image);
		
		System.err.print("Blurring and Thresholding... ");
		
		ImageCore binarizedImage = image.duplicate().getImageBlur()
					.getGaussianBlurCalibrated(0.6, 0.6, 0.6, voxelEdgesLength)
					.getImageConvolved(ConvolutionNormalizationPolicy
							   			  .Gray8_Scale_MaximizeContrast)
		   			.setTitle("Thresholded Image (" 
		   					    + ThresholdingOption.Otsu + " Method)"
		   					   )
		   			.getImageThresholding()
		   			.thresholdImageAndBinarize(ThresholdingOption.Otsu, true);
		
		System.err.println("Thresholding Done.");	
		
		
		System.err.print("Labelling Connected Components... ");	
		
		System.out.println("Ressource performance:\n" + ResourcesMonitor.getRessourceInfo());
		
		int white = 255; // Only binary images with type GRAY8 are supported
		
		GlobalOptions.getDefaultRenderTool().display(binarizedImage);
		
		ImageCore labelledImage = binarizedImage.duplicate().setTitle("Labelled Image");
		
		ConnectedComponent cc = ConnectedComponent.getLabeledComponents(
						labelledImage, // Image
						LabelingPolicy.Full3D, // Binary image
						white, // Foreground
						false, // Remove components on the border
						componentVolumeThreshold, // Lower threshold on components volume
						true // Set a uniform random color on each component
						);
		
		// print connected components informations :
		System.out.println(cc);
				
		RenderToolFactoryIJ3D.getInstance().getRenderTool().display(labelledImage);

	}



	/**
	 * Main function of the package's tests.
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			testComponentsLabeling(TestImageThresholding.getSampleImageGray8(0), 15.0);
		} catch(IOException e){
			e.printStackTrace();
			return;
		}
		
		System.out.println("Ressource performance:\n" + ResourcesMonitor.getRessourceInfo());
		
		System.err.println("The program ended normally.");
	}
}
