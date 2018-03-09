/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  TestSignPolicyEmbed.java                                          * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.test;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed;

/**
 * @author remy
 * 
 * Tests the functionalities to represent and use signed and unsigned valued images.
 *
 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed
 */
public class TestSignPolicyEmbed {

	static ImageCore getEllipsoid(int bitDepth, int radius){
		ImageCore image = ImageCoreFactoryIJ.getInstance()
				.getEmptyImageCore(989, 1011, 101, bitDepth);
		
		image.setTitle("Ellipsoid");
		for (int z=0 ; z<image.getDepth() ; z++){
			for (int y=0 ; y<image.getHeight() ; y++){
				for (int x=0 ; x<image.getWidth() ; x++){
					if (Math.sqrt((x-500)*(x-500)+(y-500)*(y-500)+(10*(z-50)*10*(z-50)))<radius){
						image.setVoxel(x, y, z, image.getWhiteValue());
					}else{
						image.setVoxel(x, y, z, 0);
					}
				}
			}
		}
		return image;
	}
	
	/**
	 * Validates image embedding to create a signed/unsigned instance.
	 * @param allowSignedValues If true, the values in the created image should be signed, else unsigned.
	 * @param bitDepth The new bit depth for the image's embedding.
	 * @param multiplyFactor Factor by which the output values are multiplied
	 * @param divideFactor  Factor by which the output values are divided
	 */
	public static void testSignPolicyEmbed(boolean allowSignedValues, int bitDepth, int multiplyFactor, int divideFactor){
		
		ImageCore imagePositive = getEllipsoid(8, 400);
		
		ImageCore imageNegative = getEllipsoid(8, 200);
		
		
		GlobalOptions.getDefaultRenderTool().display(imagePositive);
		
		ImageCore embeddedCopy = imagePositive.getImageSignPolicyEmbed()
									  .getImageEmbedding(allowSignedValues, bitDepth)
									  .multiplyValues(multiplyFactor)
									  .divideValues(divideFactor)
									  .subtractValues(imageNegative
											  	.getImageSignPolicyEmbed()
											  	.getImageEmbedding(true, bitDepth)
											  	.divideValues(2)
										)
									  .getOpposite()
									  .getImageRaw()
									  .getImageContrast()
									  .maximizeValuesRange()
									  ;
		
		GlobalOptions.getDefaultRenderTool().display(embeddedCopy);
	}
	
	
	/**
	 * Validates image copying into another one.
	 * @param allowSignedValues If true, the values in the created image should be signed, else unsigned.
	 * @param bitDepth The new bit depth for the image's embedding.
	 * @param multiplyFactor Factor by which the output values are multiplied
	 * @param divideFactor  Factor by which the output values are divided
	 */
	public static void testCopyInto(boolean allowSignedValues, int bitDepth, int multiplyFactor, int divideFactor){
		
		ImageCore imagePositive = getEllipsoid(8, 400);
		
		ImageCore imageNegative = getEllipsoid(8, 200);
		
		
		GlobalOptions.getDefaultRenderTool().display(imagePositive);
		
		ImageCore embeddedCopy = imagePositive.getImageSignPolicyEmbed()
											  .getImageEmbedding(allowSignedValues, bitDepth)
											  .multiplyValues(multiplyFactor)
											  .divideValues(divideFactor)
											  .subtractValues(imageNegative
													  	.getImageSignPolicyEmbed()
													  	.getImageEmbedding(true, bitDepth)
													  	.divideValues(2)
												)
											  .getOpposite()
											  .getImageRaw()
											  .getImageContrast()
											  .maximizeValuesRange()
											  ;

		int marginX = 50;
		int marginY = 50;
		int marginZ = 25;

		ImageSignPolicyEmbed imageEmbedding = imagePositive.getImageDomainOperation()
														   .getEnlargedImage(marginX, marginY, marginZ,
																  			 BufferEnlargementPolicy.White
																  			)
														   .getImageSignPolicyEmbed()
														   .setAllWhite()
														   .divideValues(2);

		embeddedCopy.getImageSignPolicyEmbed().copyInto(imageEmbedding,
														new VoxelInt(marginX, marginY, marginZ),
														true
													   );

		GlobalOptions.getDefaultRenderTool().display(embeddedCopy);
		GlobalOptions.getDefaultRenderTool().display(imageEmbedding.getImageRaw());
	}


	/**
	 * Main function to run the tests in this class.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		//testSignPolicyEmbed(true, 8, 1, 3);
		//testCopyInto(true, 8, 1, 3);
		
	}
	
}
