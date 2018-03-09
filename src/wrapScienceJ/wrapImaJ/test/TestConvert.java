/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: TestConvert.java                                                   * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.io.stream.FileHelper;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * Allows to test image conversions from one format to another.
 * 
 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert
 */
public class TestConvert {
	
	/**
	 * Validates image 8 bits conversion and saving to a file, with possible scaling
	 * of the values as the image is loaded.
	 * @param  inputImageFile source image path on disk
	 * @param outputFilename path to the output file where the converted image is to be saved.
	 * @param outputFilenameScaled path to the output file where the converted and scaled
	 * 							   image is to be saved.
	 * @throws IOException 
	 */
	public static void testConvertTo8bits(String inputImageFile, String outputFilename,
								   		  String outputFilenameScaled) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile);
		image.getImageConvert().convertToGray8(false);
		ImageCore imageScaled = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile);
		imageScaled.getImageConvert().convertToGray8(true);			
		image.writeToFile(outputFilename);
		imageScaled.writeToFile(outputFilenameScaled);
		image.getImageContrast().maximizeValuesRange();
		GlobalOptions.getDefaultRenderTool().display(image);
		GlobalOptions.getDefaultRenderTool().display(imageScaled);

	}
	
	
	/**
	 * Validates image 8 bits conversion and saving to a file, with possible scaling
	 * of the values as the image is loaded.
	 * @param  inputImageFile source image path on disk
	 * @throws IOException 
	 */
	public static void testConvertTo8bitsClamp(String inputImageFile) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile);
		
		image.getImageContrast().maximizeValuesRange()
								.getImageSignPolicyEmbed(false)
								.multiplyValues(1.0/126.0);
		
		image.getImageConvert().convertToGray8Clamp();
		image.setTitle("Clamped");
		
		ImageCore imageScaled = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile);
		imageScaled.getImageConvert().convertToGray8(true);	
		imageScaled.setTitle("Natural scaled");
		
		image.getImageContrast().maximizeValuesRange();
		GlobalOptions.getDefaultRenderTool().display(image);
		GlobalOptions.getDefaultRenderTool().display(imageScaled);

	}
	
	/**
	 * Validates image 8 bits conversion and saving to a file, with possible scaling
	 * of the values as the image is loaded.
	 * @param  inputImageFile source image path on disk
	 * @throws IOException 
	 */
	public static void testConvertTo8bits1Slice(String inputImageFile) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
									.getImageCore(inputImageFile,
												  true, // Convert to gray8 
												  true // Scale values to maximize contrast
												 )
									.getImageDomainOperation()
									.extractSlice(16);
		
		//image.getImageConvert().convertToGray8(false);
		ImageCore imageScaled = image.duplicate();
		imageScaled.getImageConvert().convertToGray8(false);			
		imageScaled.getImageContrast().maximizeValuesRange();			
				//image.getImageContrast().maximizeValuesRange();
		GlobalOptions.getDefaultRenderTool().display(image);
		GlobalOptions.getDefaultRenderTool().display(imageScaled);

	}
	
	/**
	 * Validates image 8 bits conversion and saving to a file, with possible scaling
	 * of the values as the image is loaded.
	 * @param inputImageFile source image path on disk
	 * @throws IOException 
	 */
	public static void testConvertToRegularArrayShort(String inputImageFile) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile,
														  false, true);
		
		short[][] bufferShort = image.getImageConvert().getRegularShortArray();
		
		// Scale the values before direct conversion to byte
		image.getImageSignPolicyEmbed(false).multiplyValues(1.0d/256.0d);

		ImageCore imageConvertedAndBack = ImageCoreFactoryIJ
												.getInstance()
												.createFromRegularArray(bufferShort, 
																image.getWidth(), 
																8
												);
		imageConvertedAndBack.setTitle("Image Converted and Back");
		
		// Scale back values to restore approximately the original image :
		image.getImageSignPolicyEmbed(false).multiplyValues(256.0d);
		
		GlobalOptions.getDefaultRenderTool().display(image);
		GlobalOptions.getDefaultRenderTool().display(imageConvertedAndBack);

	}
	
	/**
	 * Validates image 8 bits conversion and saving to a file, with possible scaling
	 * of the values as the image is loaded.
	 * @param  inputImageFile source image path on disk
	 * @throws IOException 
	 */
	public static void testConvertToRegularArrayByte(String inputImageFile) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile,
												  false, // Convert to 8 bits
												  false // Scale values to maximize contrast
												 );
		
		image.getImageConvert().convertToGray8(true);
		byte[][] bufferByte = image.getImageConvert().getRegularByteArray();

		ImageCore imageConvertedAndBack = ImageCoreFactoryIJ.getInstance()
											.createFromRegularArray(bufferByte, 
																	image.getWidth(),
																	16
																	);

		imageConvertedAndBack.setTitle("Image Converted and Back");
		
		// Scale values for adapting the scale to 16 bits.
		imageConvertedAndBack.getImageSignPolicyEmbed(false)
							 .multiplyValues(256.0);
		
		GlobalOptions.getDefaultRenderTool().display(image);
		GlobalOptions.getDefaultRenderTool().display(imageConvertedAndBack);

	}
	
	/**
	 * Validates image 8 bits conversion and saving to a file, with possible scaling
	 * of the values as the image is loaded.
	 * @param  inputImageFile source image path on disk
	 * @throws IOException 
	 */
	public static void testConvertToRegularArrayByteShort(String inputImageFile) throws IOException{

		ImageCore image = ImageCoreFactoryIJ.getInstance()
											.getImageCore(inputImageFile,
														  false, 
														  false
														  );
		
		image.getImageConvert().convertToGray8(true);
		short[][] bufferShort = image.getImageConvert().getRegularShortArray();
		ImageCore imageConvertedAndBack = ImageCoreFactoryIJ.getInstance()
												.createFromRegularArray(bufferShort, 
																		image.getWidth(), 
																		8
																		);

		imageConvertedAndBack.setTitle("Image Converted and Back");

		GlobalOptions.getDefaultRenderTool().display(image);
		GlobalOptions.getDefaultRenderTool().display(imageConvertedAndBack);
		
	}
	

	
	/**
	 * Allows to test swapping two axis in an image domain.
	 * @param  inputImageFile source image path on disk
	 * @param axis The axis along which to project
	 * @throws IOException 
	 */
	public static void testProjectionTomography(String inputImageFile, CoordinateAxis axis) throws IOException{
		ImageCore image = ImageCoreFactoryIJ.getInstance()
									  .getImageCore(inputImageFile,
													  false, // Convert to GRAY8
													  false, // Scale colors to maximize contrast
													  RetrievalPolicy.TryConfFileNoDialog);
			
		BufferedImage imgTomography = image.getImageConvert().projectionTomography(axis, true);
		
		TestAwtImage.saveAwtImage(imgTomography, 
								  new File(
											FileHelper.changeDirectoryPostfixAndExtension(
											inputImageFile,
											TestImageThresholding.getTestOutputDir(),
											"_tomography" + axis, "png")
										  )
								  );
		BufferedImage imgVolumeRendering = image.getImageConvert().projectionVolumeRendering(axis, true);
		
		TestAwtImage.saveAwtImage(imgVolumeRendering, 
								  new File(
											FileHelper.changeDirectoryPostfixAndExtension(
											inputImageFile,
											TestImageThresholding.getTestOutputDir(),
											"_volumeRendering" + axis, "png")
										  )
								  );

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	
		try {
		
			//for (int i=0 ; i<6 ; i++){
			//	testConvertTo8bits(TestImageThresholding.getSampleImage(i),
			//		TestImageThresholding.getTestOutputDir() + 
			//			TestImageThresholding.m_testImages_8bits[i],
			//		TestImageThresholding.getTestOutputDir() + 
			//			TestImageThresholding.m_testImages_8bits_scaled[i]
			//		);
			//}
			
			//testConvertTo8bits(TestImageThresholding.getSampleImage(1), 
			//		TestImageThresholding.getTestOutputDir() + TestImageThresholding.m_testImages_8bits[1],
			//		TestImageThresholding.getTestOutputDir() + TestImageThresholding.m_testImages_8bits_scaled[1]
			//	
			//);
			
			//testConvertTo8bitsClamp(TestImageThresholding.getSampleImage(0));
			
			//testConvertTo8bits1Slice(TestImageThresholding.getSampleImage(0));
			testConvertTo8bits1Slice("/home/remy/Images/testDataGRED/170120_Col0_2d_Exp1.TIF");
			
			
			//testConvertToRegularArrayShort(TestImageThresholding.getSampleImage(0));
			
			//testConvertToRegularArrayByte(TestImageThresholding.getSampleImage(0));
			
			//testConvertToRegularArrayByteShort(TestImageThresholding.getSampleImage(0));
			
			//testProjectionTomography(TestImageThresholding.getSampleImage(0), CoordinateAxis.Z);
		} catch(IOException e){
			e.printStackTrace();
		}


		System.out.println(ResourcesMonitor.getRessourceInfo());
		
		System.out.println("The program ended normally.");
	}

}
