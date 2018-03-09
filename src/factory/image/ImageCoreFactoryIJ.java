/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageCoreFactoryIJ.java                                            * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.factory.image;


import java.awt.image.BufferedImage;
import java.io.IOException;

import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.resource.generic.ModelCoreImageGeneric;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation.ImageConvertIJ;



/**
 * @author remy
 *
 */
public class ImageCoreFactoryIJ extends ImageCoreFactory {

	/**
	 * Unique instance of this factory
	 */
	private static ImageCoreFactoryIJ m_instance;
	
	/**
	 * Private constructor for the singleton pattern
	 */
	private ImageCoreFactoryIJ(){
		// Nothing to do
	}
	
	/**
	 * @return The unique instance of this Factory class
	 */
	public static ImageCoreFactoryIJ getInstance(){
		if (m_instance == null){
			m_instance = new ImageCoreFactoryIJ();
		}
		return m_instance;
	}
	
	/**
	 * Constructs an instance by loading the image from a source file.
	 * @param path path to the image source file on disk.
	 * @param convertTo8bits If true, the image created is converted to color type GRAY8 to save resources.
	 * @param maximizeValuesRange If true, values are scaled linearly so as to set their maximum to (2<sup>getBitDepth()</sup>-1).
	 * @param retrievalPolicy The policy to retrieve predefined metadata for the resource of type ImageCore.
	 * @see RetrievalPolicy
	 * @return Instance of the Image as built by a constructor of the actual wrapper.
	 * @throws IOException IOException in case of failure to load the image from file
	 */
	@Override
	public ImageCoreIJ getImageCore(String path, boolean convertTo8bits, 
										 boolean maximizeValuesRange,
										 RetrievalPolicy retrievalPolicy)
												 throws IOException {

		ImageCoreIJ image =  new ImageCoreIJ(path, convertTo8bits, maximizeValuesRange,
										   	retrievalPolicy);
		return image;
	}
	
	
	/**
	 * Allocates and returns a black image with given dimensions and type (GRAY8 or GRAY16).
	 * The gray levels are uninitialized, the image has a default title and no meatdata
	 * such as calibration data.
	 * @see ImageCore#getImageCalibration()
	 * @see ModelCoreImageGeneric#addMetaData(wrapScienceJ.metaData.container.MetaDataRetriever)
	 * 
	 * @param width Width of the image in the first voxel coordinate
	 * @param height Height of the image in the second voxel coordinate
	 * @param depth Depth  of the image in the third voxel coordinate
	 * @param bitDepth Number of bits per voxel
	 * @return An instance of ImageCore implementer
	 */
	public ImageCoreIJ getEmptyImageCore(int width, int height, int depth, int bitDepth){

		ImageCoreIJ image =  ImageCoreIJ.getEmptyImageCore(width, height, depth, bitDepth);
		return image;
	}
	
	/**
	 * Converts an array of java.awt.Image to an ImageCore. The returned image has only one slice.
	 * @param images An array of image as AWT Image instance to initialize slices of an image core. 
	 * @return The image as an ImageCore implementer's instance.
	 */
	public ImageCoreIJ createFromAwtImages(BufferedImage[] images){

		ImageCoreIJ image =  ImageConvertIJ.createFromAwtImages(images);
		return image;
	}
	
	/**
	 * Converts an array of voxel values to an ImageCore. 
	 * The size of the array, which is an array of 2D image values,
	 * must be the same as (or larger than) the size of the image which generated
	 * this instance.
	 * The memory from voxelValues is recycled into the new image if bitDepth is 16.
	 * Otherwise, bitDepth must be 8, the memory from voxelValues is left unchanged
	 * and a new byte image is allocated.
	 * 
	 * @param voxelValues A 3D array containing the values of the image.
	 * @param width Width of each slice of the image to create (the height is obtained through width
	 * 				and voxelValues[0].length, which must exist and be multiple of width. 
	 * @param bitDepth The number of bits per voxel of the new image
	 * 				   (values are truncated in case of overflow).
	 * @return The image as an ImageCore implementer's instance.
	 */
	public ImageCore createFromRegularArray(short[][] voxelValues, int width, int bitDepth){
		ImageCore image =  ImageConvertIJ.createFromRegularArray(voxelValues, width, bitDepth);
		return image;
	}
	
	/**
	 * Converts an array of voxel values to an ImageCore. 
	 * The size of the array, which is an array of 2D image values,
	 * must be the same as (or larger than) the size of the image which generated
	 * this instance.
	 * The memory from voxelValues is recycled into the new image if bitDepth is 8.
	 * Otherwise, bitDepth must be 16, the memory from voxelValues is left
	 * unchanged and their values are copied (embedded) as is into a newly created image.
	 * 
	 * @param voxelValues A 3D array containing the values of the image.
	 * @param width Width of each slice of the image to create (the height is obtained through width
	 * 				and voxelValues[0].length, which must exist and be multiple of width. 
	 * @param bitDepth The number of bits per voxel of the new image
	 * 				   (values are truncated in case of overflow).
	 * @return The image as an ImageCore implementer's instance.
	 */
	public ImageCoreIJ createFromRegularArray(byte[][] voxelValues, int width, int bitDepth){

		ImageCoreIJ image =  ImageConvertIJ.createFromRegularArray(voxelValues, width, bitDepth);
		return image;
	}
}
