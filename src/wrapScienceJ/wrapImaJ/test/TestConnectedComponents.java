/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: TestConnectedComponents.java                                       * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.test;

import java.io.IOException;


import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.connectivity.ConnectedComponent;
import wrapScienceJ.wrapImaJ.connectivity.LabelingPolicy;
import wrapScienceJ.wrapImaJ.connectivity.filtering.*;
import wrapScienceJ.wrapImaJ.connectivity.filtering.predefined.ComponentRemovalLinear;
import wrapScienceJ.wrapImaJ.connectivity.filtering.predefined.ComponentRemovalLinearComplement;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelDouble;
import wrapScienceJ.wrapImaJ.core.operation.ThresholdingOption;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy;



/**
 * Allows to test Connected Components labeling for images
 * (typically binary images or image's background).
 * 
 * @see wrapScienceJ.wrapImaJ.connectivity#ConnectedComponent
 * @see wrapScienceJ.wrapImaJ.connectivity#ComponentInfo
 * @see wrapScienceJ.wrapImaJ.connectivity.filtering#ComponentRemovalPredicate
 *  
 * @author Remy Malgouyres
 */
public class TestConnectedComponents {
	
	/**
	 * Test for labeling connected components of a binarized image.
	 * Only connected components with no voxel on the image's boundary
	 * are kept in the filtering process.
	 * 
	 * Connected components with a volume below some threshold are
	 * also removed.
	 * 
	 * a constant random gray level is set on each connected component.
	 * 
	 * @param inputImageFile the input image file on disk 
	 * @param labelingPolicy Can be 3D components labeling, or slice by slice against a given axis.
	 * @param componentVolumeThreshold Minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 

	 * @throws IllegalStateException In case of overflow of the number of connected components.
	 * @throws IOException In case of file read error
	 */
	public static void testComponentsLabeling(String inputImageFile,
											  LabelingPolicy labelingPolicy,
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
							   			.thresholdImageAndBinarize(ThresholdingOption.Otsu, true)
							   			;
		
		System.err.println("Thresholding Done.");	
		
		System.err.print("Labeling Connected Components... ");	
				
		int white = 255; // Only binary images with type GRAY8 are supported
		
		GlobalOptions.getDefaultRenderTool().display(binarizedImage);
	
		ConnectedComponent connectedComp = binarizedImage
												.duplicate()
												.setTitle("Labeled Image")
												.getImageConnectedComponents()
												.getLabeledComponents(
													labelingPolicy, // Binary image
													white, // Foreground
													false, // Remove components on the border
													componentVolumeThreshold, // Lower threshold on components volume
													true // Set a uniform random color on each component
												);
		
		ImageCore labeledImage = connectedComp.getImage();
		
        // print connected components informations :
        System.out.println(connectedComp);
        
		System.out.println("Ressource performance:\n" + ResourcesMonitor.getRessourceInfo());
		GlobalOptions.getDefaultRenderTool().display(labeledImage);

	}
	
	
	/**
	 * Test for labeling connected components of a binarized image.
	 * Only connected components with no voxel on the image's boundary
	 * are kept in the filtering process.
	 * 
	 * Connected components with a volume below some threshold are
	 * also removed.
	 * 
	 * a constant random gray level is set on each connected component.
	 * 
	 * @param inputImageFile the input image file on disk 
	 * @param labelingPolicy Can be 3D components labeling, or slice by slice against a given axis.
	 * @param componentVolumeThreshold Minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 

	 * @throws IllegalStateException In case of overflow of the number of connected components.
	 * @throws IOException In case of file read error
	 */
	public static void testGetLabelsAsImage(String inputImageFile,
											  LabelingPolicy labelingPolicy,
											  double componentVolumeThreshold) 
													  throws IllegalStateException, IOException {
		
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
			
		System.err.print("Labeling Connected Components... ");	
		
		int white = 255; // Only binary images with type GRAY8 are supported
		
		ConnectedComponent connectedComp = binarizedImage
												.duplicate()
												.setTitle("Labeled Image")
												.getImageConnectedComponents()
												.getLabeledComponents(
													labelingPolicy, // Full 3D or Slice by slice 2D
													white, // Foreground
													false, // Remove components on the border
													componentVolumeThreshold, // Lower threshold on components volume
													true // Set a uniform random color on each component
												);
		System.err.println("Labeling Connected Components Done");	
		ImageCore labeledImage = connectedComp.getImage()
											   .setTitle("Labeled Image ("
													 		+ ThresholdingOption.Otsu
													 		+ " Method)"
													    );
		
        // print connected components informations :
        System.out.println(connectedComp);

		ImageCore labels = connectedComp.getLabelsAsImage();
		
		GlobalOptions.getDefaultRenderTool().display(labels.getImageContrast()
														   .maximizeValuesRange());
		GlobalOptions.getDefaultRenderTool().display(labeledImage);
	}
	
	
	/**
	 * Test for filtering of connected components based on a thick surface.
	 * Only connected components with at least one voxel satisfying a predicate
	 * are kept through this filtering.
	 * In this example, the predicate is true if the voxel lies in a thick plane
	 * 
	 * @param inputImageFile the input image file on disk 
	 * @param keepPredicate true if we should keep the components with a voxel satisfying removalPredicate, and false if we should remove the components with a voxel satisfying removalPredicate 
	 * @param complementPredicate true if we should consider the complement of the predicate
	 * @throws IllegalStateException In case of overflow of the number of connected components.
	 * @throws IOException In case of file read error
	 */
	public static void testComponentsPredicateFiltering(String inputImageFile,
		boolean keepPredicate, boolean complementPredicate) throws IllegalStateException, IOException {
		
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
		
		GlobalOptions.getDefaultRenderTool().display(binarizedImage);
		
		System.err.println("Thresholding Done.");	
		
		// Thick plane predicate definition :
		ComponentRemovalPredicate removalPredicatePlane;
		if (!complementPredicate){
			// We consider the components which contain at least one voxel
			// INSIDE a thick plane
			removalPredicatePlane = new ComponentRemovalLinear(
					0.02, 0.0, -10.0, 20.0
					);
		}else{
			// We consider the components which contain at least one voxel
			// OUTSIDE a thick plane
			removalPredicatePlane = new ComponentRemovalLinearComplement(
					0.02, 0.0, -17.0, 35.0
					);
		}
		
		ImageCore labeledImage = binarizedImage.duplicate().setTitle("Labeled Image");

		// Construction and filtering of connected components :
	    ConnectedComponent.getLabeledComponents(labeledImage,
									    		LabelingPolicy.Full3D, 
									    		255, // Foreground
									    		false, // Remove components on the border
									    		15.0d, // Lowest component volume threshold
									    		removalPredicatePlane, 
									    		keepPredicate,
									    		true // Set color on each component
									    		);
	    // print connected components informations :
		System.out.println("Connected Composnents done.");
		System.out.println("Ressource performance:\n" + ResourcesMonitor.getRessourceInfo());
		
		GlobalOptions.getDefaultRenderTool().display(labeledImage);

		System.out.println("Ressource performance before the end of the program:\n" 
				+ ResourcesMonitor.getRessourceInfo());
	}
	
	/**
	 * Main function of the package's tests.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			//testComponentsLabeling(TestImageThresholding.getSampleImage(2),
			//					   LabelingPolicy.Z_2D, // Label 2D components slice by slice
			//					   0.0 // Volume for a 2D component inside a slice
			//					   );

			testComponentsLabeling(TestImageThresholding.getSampleImage(4),
					   LabelingPolicy.Full3D,
					   15.0 // Volume of a 3D component
					   );
			
			//testGetLabelsAsImage(TestImageThresholding.getSampleImage(3),
			//		   LabelingPolicy.Full3D,
			//		   15.0 // Volume of a 3D component
			//		   );
			
			// keep components  which contain AT LEAST ONE voxel inside a thick plane
			//testComponentsPredicateFiltering(TestImageThresholding.getSampleImageGray8(4),
			//								 true, false);

			// keep components  which are INCLUDED inside a thick plane		
			//testComponentsPredicateFiltering(TestImageThresholding.getSampleImageGray8(4),
			//									false,
			//									true);
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		System.err.println("The program ended normally.");
	}

}
