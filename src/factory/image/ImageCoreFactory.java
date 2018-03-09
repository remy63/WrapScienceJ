/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageCoreFactory.java                                              * 
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

/**
 * Constructs Instances of ImageCore Implementers based on and Option
 */
public abstract class ImageCoreFactory {

	/**
	 * TODO This doesn't appear to be useful at all, but maybe for serializing.
	 * Should be considered to get deprecated.
	 * Is is set private in the meantime.
	 * 
	 * Enumeration of all possible options ID for wrapImaJ implementers.
	 *(currently, only one implementation is provided, based on ImageJ)
	 */
	@SuppressWarnings("unused")
	private enum WrapperID {
		
		/**
		 * ImageJ based wrapper
		 */
		ImageJ(1);
		
		private final int m_wrapperId;

		/**
		 * sets the wrapper's ID
		 * @param value
		 */
		private WrapperID(int wrapperID) throws IllegalArgumentException {
			if (wrapperID < 1 || wrapperID > 1){
				throw new IllegalArgumentException("Undefined thresholding method.");
			}
			this.m_wrapperId = wrapperID;
		}
	
		/**
		 * @return the integer representation for the wrapper's ID
		 */
		public int getValue() {
			return this.m_wrapperId;
		}
	
		/** 
		 * @return a human readable description of the wrapper.
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			switch(this.m_wrapperId){
				case 1: 
					return "ImageJ based Implementation";
				default:
					throw new IllegalArgumentException("Unknown Wrapper ID");
			}
		}
	} // End of enum WrapperID
	
	
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
	public abstract ImageCore getImageCore(String path, boolean convertTo8bits, 
										 boolean maximizeValuesRange,
										 RetrievalPolicy retrievalPolicy) throws IOException ;
	
	/**
	 * Constructs an instance by loading the image from a source file.
	 * No conversion or scaling of the values occurs; the image is left as is in the file.
	 * The Retrieval Policy for the metadata is left unspecified.
	 * @see RetrievalPolicy#Unspecified
	 * 
	 * @param path path to the image source file on disk.
	 * @return Instance of the Image as built by a constructor of the actual wrapper.
	 * @throws IOException IOException in case of failure to load the image from file
	 */
	public ImageCore getImageCore(String path) throws IOException {
		return getImageCore(path, false, false, RetrievalPolicy.Unspecified);
	}


	
	/**
	 * Constructs an instance by loading the image from a source file.
	 * @param path path to the image source file on disk.
	 * @param convertTo8bits If true, the image created is converted to color type GRAY8 to save resources.
	 * @param maximizeValuesRange If true, values are scaled linearly so as to set their maximum to (2<sup>getBitDepth()</sup>-1).
	 * @return Instance of the Image as built by a constructor of the actual wrapper.
	 * @throws IOException IOException in case of failure to load the image from file
	 */
	public ImageCore getImageCore(String path, boolean convertTo8bits, 
										 boolean maximizeValuesRange)
												 throws IOException {
		return getImageCore(path, convertTo8bits, maximizeValuesRange,
													RetrievalPolicy.Unspecified);
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
	public abstract ImageCore getEmptyImageCore(int width, int height, int depth, int bitDepth);
	
	/**
	 * Converts an array of java.awt.Image to an ImageCore. The returned image has only one slice.
	 * @param images An array of image as AWT Image instance to initialize slices of an image core. 
	 * @return The image as an ImageCore implementer's instance.
	 */
	public abstract ImageCore createFromAwtImages(BufferedImage[] images);
	
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
	public abstract ImageCore createFromRegularArray(short[][] voxelValues, int width, int bitDepth);
	
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
	public abstract ImageCore createFromRegularArray(byte[][] voxelValues, int width, int bitDepth);
	
}
