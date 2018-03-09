/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: TestDifferential.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.test;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.core.*;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy;
import wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionDifferentialGeneric;


/**
 * @author remy
 *
 * THE PACKAGE TO BE TESTED HERE IS CURRENTLY BROKEN AND WILL BE RELEASED SOON
 */
public class TestDifferential {
	
	static ImageCore getEllipsoid(){
		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getEmptyImageCore(1000,  1000, 100, 8);
		
		image.getImageCalibration().setVoxelLength(new VoxelDouble(0.1, 0.1, 1.0));
		image.setTitle("Ellipsoid");
		for (int z=0 ; z<image.getDepth() ; z++){
			for (int y=0 ; y<image.getHeight() ; y++){
				for (int x=0 ; x<image.getWidth() ; x++){
					if (Math.sqrt((x-500)*(x-500)+(y-500)*(y-500)+(10*(z-50)*10*(z-50)))<300){
						image.setVoxel(x, y, z, 255);
					}else{
						image.setVoxel(x, y, z, 0);
					}
				}
			}
		}
		return image;
	}
	
	
	/**
	 * Validates image blurring by binomial convolutions with skipping steps.
	 * @see ConvolutionDifferentialGeneric#partialFiniteDifference(int, int, int, int, int, int)
	 */
	static void testPartialFiniteDifferenceAbs(CoordinateAxis axis, int order, int skippingStep){

		ImageCore image = getEllipsoid();
		//ImageCore image = ImageCoreFactoryIJ.getInstance()
		//					.getEmptyImageCore(1000,  1000, 100, 8);
		
		GlobalOptions.getDefaultRenderTool().display(image);

		int orderX=0, orderY=0, orderZ=0;
		int skippingStepX=0, skippingStepY=0, skippingStepZ=0;
		
		switch (axis){
			case X: 
				orderX = order;
				skippingStepX = skippingStep;
				break;
			case Y: 
				orderY = order;
				skippingStepY = skippingStep;
				break;
			case Z: 
				orderZ = order;
				skippingStepZ = skippingStep;
				break;
			default:
				throw new IllegalArgumentException("Unknown axis");
		}
		
		
		int xMargin = skippingStepX*orderX;
		int yMargin = skippingStepY*orderY;
		int zMargin = skippingStepZ*orderZ;
		
	
		ImageCore resultImage = image.getImageBlur()
				.enlargeInImage(xMargin, yMargin, zMargin,
						BufferEnlargementPolicy
						.Mirror)
							.embedOutput(true, 16)
									.getGaussianBlurCalibrated(
											4.0, 4.0, 4.0,
											image.getImageCalibration().getVoxelLength()
									)
									.applyMask()
									//.partialFiniteDifference(axis, order, 25)
									.getImageConvolved(ConvolutionNormalizationPolicy
													   .No_Normalization)
									.getImageContrast()
									.maximizeValuesRange();
		
		resultImage.setTitle("Parial Derivative Scaled");
		
		GlobalOptions.getDefaultRenderTool().display(resultImage);
		
		System.out.println(ResourcesMonitor.getRessourceInfo());

	}
	
	
	/*
	 * Validates image blurring by binomial convolutions with skipping steps.
	 * @see ConvolutionDifferential#partialFiniteDifference(int, int, int, int, int, int)
	 */
	static void testGradientNormCalibrated(double realSkippingStepX,
										   double realSkippingStepY,
										   double realSkippingStepZ){
		
		ImageCore image = getEllipsoid();
		
		int xMargin = (int) (realSkippingStepX/image.getImageCalibration().getVoxelWidth());
		int yMargin = (int) (realSkippingStepY/image.getImageCalibration().getVoxelHeight());
		int zMargin = (int) (realSkippingStepZ/image.getImageCalibration().getVoxelDepth());
		
		ImageCore enlargedImage = image.getImageDomainOperation()
				   .getEnlargedImage(xMargin, yMargin, zMargin, 
						 			 BufferEnlargementPolicy
						 			 .Mirror
						 			);

		ImageCore embeddedCopy = enlargedImage.getImageSignPolicyEmbed()
										.getImageEmbedding(false, 16)
										.getImageRaw();
		
		embeddedCopy = embeddedCopy.getImageBlur()
									.getGaussianBlurCalibrated(4.0, 4.0, 4.0,
											embeddedCopy
											.getImageCalibration()
											.getVoxelLength())
									.getImageConvolved(ConvolutionNormalizationPolicy
													   .No_Normalization
													  );
			
		embeddedCopy= embeddedCopy.getImageDifferential(false, 
														0,0,0, BufferEnlargementPolicy.Mirror,
														16
								   )
								  .getGradientNormCalibrated(realSkippingStepX,
															 realSkippingStepY,
															 realSkippingStepZ, 16,
													  		 100 // Scaled Values Denominator
													  		)
								  .getImageDomainOperation()
								  .crop(xMargin, yMargin, zMargin,
									  image.getWidth()+ xMargin,
									  image.getHeight()+ yMargin,
									  image.getDepth() + zMargin)
								  .getImageContrast()
								  .maximizeValuesRange();
		
		embeddedCopy.setTitle("Gradient Norm Scaled");
		
		GlobalOptions.getDefaultRenderTool().display(embeddedCopy);
		
		System.out.println(ResourcesMonitor.getRessourceInfo());

	}
		

	/*
	 * Validates image blurring by binomial convolutions with skipping steps.
	 * @see ConvolutionDifferential#partialFiniteDifference(int, int, int, int, int, int)
	 */
	static void testGradientNormCalibratedAutoEnlarge(double realSkippingStepX,
										   double realSkippingStepY,
										   double realSkippingStepZ){
		
		ImageCore image = getEllipsoid();
		
		int xMargin = (int) (realSkippingStepX/image.getImageCalibration().getVoxelWidth());
		int yMargin = (int) (realSkippingStepY/image.getImageCalibration().getVoxelHeight());
		int zMargin = (int) (realSkippingStepZ/image.getImageCalibration().getVoxelDepth());
		
	
		ImageCore resultImage = image.getImageBlur()
									.enlargeInImage(xMargin, yMargin, zMargin,
											BufferEnlargementPolicy
											.Mirror)
									.embedOutput(false, 16)
									.getGaussianBlurCalibrated(
											4.0, 4.0, 4.0,
											image.getImageCalibration().getVoxelLength()
									)
									.applyMask()
									//.partialFiniteDifference(CoordinateAxis.X, 1, 25)
									.getImageConvolved(ConvolutionNormalizationPolicy.No_Normalization)
									/*.getGradientNormCalibrated(realSkippingStepX,
															   realSkippingStepY,
															   realSkippingStepZ,
													  		   100, // Scaled Values Denominator
															   new VoxelInt(0, 0, 0)
															  )*/
									.getImageContrast()
									.maximizeValuesRange();
		
		resultImage.setTitle("Gradient Norm Scaled");
		
		GlobalOptions.getDefaultRenderTool().display(resultImage);
		
		System.out.println(ResourcesMonitor.getRessourceInfo());

	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		testPartialFiniteDifferenceAbs(CoordinateAxis.Z, 1, 12);
		//testPartialFiniteDifferenceAbs(CoordinateAxis.Y, 1, 25);
		
		//testGradientNormCalibrated(2.0, 2.0, 2.0);
		
		//testGradientNormCalibratedAutoEnlarge(2.0, 2.0, 2.0);
	}

}
