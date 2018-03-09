/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageConvertIJ.java                                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation;

import java.awt.image.BufferedImage;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ShortProcessor;
import ij.process.ImageProcessor;


import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.operation.ImageConvert;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;

/**
 * General Interface implementation to convert from and to other usual image formats.
 */
public class ImageConvertIJ implements ImageConvert {
	
	/**
	 * Image Data as an ImagePlus instance using ImageJ (shorthand)
	 */
	private ImagePlus m_imp;
	
	/**
	 * Underlying image on which operations are performed
	 */
	ImageCoreIJ m_image;
	
	/**
	 * @param image The image to process
	 */
	public ImageConvertIJ(ImageCoreIJ image){
		this.m_imp = image.getImp();
		this.m_image = image;
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#addSLiceFromAwtImage(java.awt.image.BufferedImage)
	 */
	@Override
	public ImageCoreIJ addSLiceFromAwtImage(BufferedImage bufferedImage) {
		addAwtImage(bufferedImage, false);
		
		return this.m_image;
	}
	
	/**
	 * Adds a slice to an image using an java.awt.Image.
	 * If the image underlying this instance is empty, then init must be true,
	 * otherwise, init must be false. This makes sense because an empty image
	 * with depth zero using imagej cannot be created.
	 * 
	 * This method is meant for internal use to {@link #addSLiceFromAwtImage(BufferedImage)}
	 * and {@link #createFromAwtImages(BufferedImage[])} and follows the same policy
	 * for memory management.
	 * 
	 * @param bufferedImage An image as AWT Image instance to add a slice to an image core. 
	 * @param init Must be true if this image should be considered empty
	 */	
	private ImageCoreIJ addAwtImage(BufferedImage bufferedImage, boolean init) {

		if (bufferedImage.getType() != BufferedImage.TYPE_BYTE_GRAY && 
			bufferedImage.getType() != BufferedImage.TYPE_USHORT_GRAY){
			throw new IllegalArgumentException("Cannot crate an ImageCoreIJ from zero AWT images or wrong images types.");
		}
		int bitDepth = 16;
		if (bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY){
			bitDepth = 8;
		}
		if (bitDepth != this.m_image.getBitDepth()){
			throw new IllegalArgumentException("Adding a slice requires an image with the same bitdepth.");
		}
		
		ImageProcessor ip;
		if (bitDepth == 16){
			ip = new ShortProcessor(bufferedImage);
		}else{
			ip = new ByteProcessor(bufferedImage);
		}
		
		int currentDepth = this.m_image.getDepth();
		
		ImageStack stack = this.m_imp.getStack();
		
		if (init){ // Work around the fact that image cannot create truly empty images
			stack.setProcessor(ip, 1);
			stack.setSliceLabel("Slice"+1, 1);
		}else{
			stack.addSlice("Slice"+(currentDepth+1), ip);
		}
		this.m_imp.setStack(stack);
		
		return this.m_image;
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#initializeFromAwtImages(java.awt.image.BufferedImage[])
	 */
	@Override
	public ImageCoreIJ initializeFromAwtImages(BufferedImage[] bufferedImages){
		if (bufferedImages.length <= 0){
				throw new IllegalArgumentException("Cannot crate an ImageCoreIJ from zero AWT images.");
		}
		int z = 0;
		for (BufferedImage bufferedImage: bufferedImages){
			this.m_image.setCurrentZ(z);
			z++;
			if ((this.m_image.getBitDepth() == 8 && bufferedImage.getType() != BufferedImage.TYPE_BYTE_GRAY) ||
				(this.m_image.getBitDepth() == 16 && bufferedImage.getType() != BufferedImage.TYPE_USHORT_GRAY) ||
				bufferedImage.getWidth() != this.m_image.getWidth() ||
				bufferedImage.getHeight() != this.m_image.getHeight()){
				
				throw new IllegalArgumentException("Wrong type or size of AWT image.");
			}
			if (this.m_image.getBitDepth() == 16){
				System.arraycopy(((java.awt.image.DataBufferUShort) bufferedImage
											.getRaster().getDataBuffer()).getData(), 
											0, this.m_imp.getProcessor().getPixels(), 0,
											bufferedImage.getWidth()*bufferedImage.getHeight()
								);
			}else{
				System.arraycopy(((java.awt.image.DataBufferByte) bufferedImage
											.getRaster().getDataBuffer()).getData(), 
											0, this.m_imp.getProcessor().getPixels(), 0,
											bufferedImage.getWidth()*bufferedImage.getHeight()
								);
			}
		}
		return this.m_image;
	}

	/**
	 * Converts an array of java.awt.Image to an ImageCore. The returned image has only one slice.
	 * @param bufferedImages An array of image as AWT Image instance to initialize slices of an image core. 
	 * @return The image as an ImageCore implementer's instance.
	 */
	public static ImageCoreIJ createFromAwtImages(BufferedImage[] bufferedImages){
		if (bufferedImages.length <= 0 ||
			(bufferedImages[0].getType() != BufferedImage.TYPE_BYTE_GRAY && 
			 bufferedImages[0].getType() != BufferedImage.TYPE_USHORT_GRAY)
			){
			throw new IllegalArgumentException("Cannot crate an ImageCoreIJ from zero AWT images or wrong images types.");
		}
		int bitDepth = 16;
		if (bufferedImages[0].getType() == BufferedImage.TYPE_BYTE_GRAY){
			bitDepth = 8;
		}
		
		int width = bufferedImages[0].getWidth();
		int height = bufferedImages[0].getHeight();
		
		ImagePlus imp = IJ.createImage(null, 
				width, height,
				0,
				bitDepth);
		
		ImageConvertIJ newImage = (new ImageCoreIJ(imp)).getImageConvert();
		
		// Work around the fact that image cannot create truly empty images
		boolean init = true;

		for (BufferedImage image : bufferedImages){
			newImage.addAwtImage(image, init);
			init = false;
		}
		
		return newImage.m_image;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#getSliceAsAwtImage(int)
	 */
	@Override
	public BufferedImage getSliceAsAwtImage (int zCoord){
		switch (this.m_image.getBitDepth()){
			case 16:
				this.m_image.setCurrentZ(zCoord);
				return ((ShortProcessor) this.m_imp.getProcessor()).get16BitBufferedImage();
			case 8:
				this.m_image.setCurrentZ(zCoord);
				return ((ByteProcessor) this.m_imp.getProcessor()).getBufferedImage();
			default:
				throw new IllegalArgumentException("Convertion to AWT images is not supported for this type of ImageCore.");
		}
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#getSliceAsRawArray(int)
	 */
	@Override
	public Object getSliceAsRawArray(int zCoord) {
		this.m_image.setCurrentZ(zCoord);
		return this.m_imp.getProcessor().getPixels();
	}
	


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#setSliceFromAwtImage(java.awt.image.BufferedImage, int)
	 */
	@Override
	public ImageCoreIJ setSliceFromAwtImage(BufferedImage bufferedImage, int zCoord) {

		this.m_image.setCurrentZ(zCoord);
	
		if ((this.m_image.getBitDepth() == 8 && bufferedImage.getType() != BufferedImage.TYPE_BYTE_GRAY) ||
			(this.m_image.getBitDepth() == 16 && bufferedImage.getType() != BufferedImage.TYPE_USHORT_GRAY) ||
			bufferedImage.getWidth() != this.m_image.getWidth() ||
			bufferedImage.getHeight() != this.m_image.getHeight()){
			
			throw new IllegalArgumentException("Wrong type or size of AWT image.");
		}
		
		if (this.m_image.getBitDepth() == 16){
			System.arraycopy(((java.awt.image.DataBufferUShort) bufferedImage
										.getRaster().getDataBuffer()).getData(), 
										0, this.m_imp.getProcessor().getPixels(), 0,
										bufferedImage.getWidth()*bufferedImage.getHeight());
		}else{
			System.arraycopy(((java.awt.image.DataBufferByte) bufferedImage
										.getRaster().getDataBuffer()).getData(), 
										0, this.m_imp.getProcessor().getPixels(), 0,
										bufferedImage.getWidth()*bufferedImage.getHeight());
		}
		
		return this.m_image;
	}

	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#projectionTomography(wrapScienceJ.wrapImaJ.core.CoordinateAxis, boolean)
	 */
	@Override
	public BufferedImage projectionTomography(CoordinateAxis axis, boolean maximizeContrast) {
		
		return this.m_image.getImageDomainProjection()
						   .projectionTomography(axis, maximizeContrast)
						   .getImageConvert()
						   .getSliceAsAwtImage(0);
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#projectionVolumeRendering(wrapScienceJ.wrapImaJ.core.CoordinateAxis, boolean)
	 */
	@Override
	public BufferedImage projectionVolumeRendering(CoordinateAxis axis, boolean maximizeContrast) {
		
		return this.m_image.getImageDomainProjection()
				   .projectionVolumeRendering(axis, maximizeContrast)
				   .getImageConvert()
				   .getSliceAsAwtImage(0);
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#getRegularShortArray()
	 */
	@Override
	public short[][] getRegularShortArray() {
		short[][] shortArray = new short[this.m_image.getDepth()][];
		for (int z=0 ; z < this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			switch (this.m_image.getBitDepth()){
				case 16:
					shortArray[z] = (short[])this.m_imp.getProcessor().getPixels();
					break;
				case 8:
					int buffSize = this.m_image.getWidth()*this.m_image.getHeight();
					shortArray[z] = new short[buffSize];
					byte[] pixels = (byte[])this.m_imp.getProcessor().getPixels();
					for (int i=0 ; i<buffSize ; i++){
						shortArray[z][i] = (short)(pixels[i] & 0xff);
					}
					break;
				default:
					throw new IllegalArgumentException("Convertion to AWT images " +
													   "is not supported for this type " +
													   "of ImageCore.");
			}
		}
		return shortArray;
	}

	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#getRegularByteArray()
	 */
	@Override
	public byte[][] getRegularByteArray() {
		byte[][] byteArray = new byte[this.m_image.getDepth()][];
		for (int z=0 ; z < this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			switch (this.m_image.getBitDepth()){
				case 16:
					int buffSize = this.m_image.getWidth()*this.m_image.getHeight();
					byteArray[z] = new byte[buffSize];
					
					short[] pixels = (short[])this.m_imp.getProcessor().getPixels();
					for (int i=0 ; i<buffSize ; i++){
						byteArray[z][i] = (byte)(pixels[i] & 0xffff);
					}
					break;
				case 8:
					byteArray[z] = (byte[])this.m_imp.getProcessor().getPixels();
					break;
				default:
					throw new IllegalArgumentException("Convertion to AWT images is not supported" +
													   "for this type of ImageCore.");
			}
		}
		return byteArray;
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#addSliceFromArray(short[], int)
	 */
	@Override
	public ImageCoreIJ addSliceFromArray(short[] shortArray, int bitDepth) {
		addShortArray(shortArray, bitDepth, false);
		return this.m_image;
	}
	
	
	/**
	 * Adds a slice to an image using an array of short.
	 * If the image underlying this instance is empty, then init must be true,
	 * otherwise, init must be false. This makes sense because an empty image
	 * with depth zero using imagej cannot be created.
	 * 
	 * This method is meant for internal use to {@link #addSLiceFromRegularArray(short[])}
	 * and {@link #createFromRegularArray(short[][])} and follows the same policy
	 * for memory management.
	 * 
	 * @param shortArray A 2D image as a one-dimensional buffer to add a slice to an image core. 
	 * @param bitDepth The number of bits per voxel of the new image
	 * 				   (values are truncated in case of overflow).
	 * @param init Must be true if this image should be considered empty
	 */
	protected void addShortArray(short[] shortArray, int bitDepth, boolean init) {

		if (bitDepth != this.m_image.getBitDepth()){
			throw new IllegalArgumentException("Adding a slice requires an image with the same bitdepth.");
		}
		
		ImageProcessor ip;
		switch (bitDepth){
			case 16:
				ip = new ShortProcessor(this.m_image.getWidth(), this.m_image.getHeight(), shortArray, null); 
				break;
			case 8:
				int nPixels = shortArray.length;
				byte[] pixels = new byte[nPixels];
				
				for (int count=0 ; count < nPixels ; count++){
					pixels[count] = (byte)(shortArray[count] & 0xffff);
				}
				ip = new ByteProcessor(this.m_image.getWidth(), this.m_image.getHeight(), pixels, null);
				break;
			default:
				throw new IllegalArgumentException("Convertion to AWT images is not supported" +
												   " for this type of ImageCore.");
		}

		int currentDepth = this.m_image.getDepth();
		
		ImageStack stack = this.m_imp.getStack();

		if (init){ // Work around the fact that image cannot create truly empty images
			stack.setProcessor(ip, 1);
			stack.setSliceLabel("Slice"+1, 1);
		}else{
			stack.addSlice("Slice"+(currentDepth+1), ip);
		}
		this.m_imp.setStack(stack);
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
	public static ImageCoreIJ createFromRegularArray(short[][] voxelValues, int width, int bitDepth){
		
		if (voxelValues.length == 0 || voxelValues[0].length == 0){
			throw new IllegalArgumentException("Cannot create an ImageCore from an empty array.");
		}
		if ((voxelValues[0].length%width) != 0){
			throw new IllegalArgumentException("Incompatible image width and array size.");
		}
		
		int height = voxelValues[0].length/width;
		ImagePlus imp = IJ.createImage(null, 
									   width, height,
									   0, 
									   bitDepth);
		
		ImageConvertIJ newImage = (new ImageCoreIJ(imp)).getImageConvert();
		
		// Work around the fact that image cannot create truly empty images
		boolean init = true;

		for (int z=0 ; z < voxelValues.length ; z++){
			newImage.addShortArray(voxelValues[z], bitDepth, init);
			init = false;
			if (z%10 == 0){
				System.gc();
			}
		}

		return newImage.m_image;
	}		


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#addSliceFromArray(byte[], int)
	 */
	@Override
	public ImageCoreIJ addSliceFromArray(byte[] byteArray, int bitDepth) {
		addByteArray(byteArray, bitDepth, false);
		return this.m_image;
	}
	
	
	/**
	 * Adds a slice to an image using an array of short.
	 * If the image underlying this instance is empty, then init must be true,
	 * otherwise, init must be false. This makes sense because an empty image
	 * with depth zero using imagej cannot be created.
	 * 
	 * This method is meant for internal use to {@link #addSLiceFromRegularArray(short[])}
	 * and {@link #createFromRegularArray(short[][])} and follows the same policy
	 * for memory management.
	 * 
	 * @param byteArray A 2D image as a one-dimensional buffer to add a slice to an image core. 
	 * @param bitDepth The number of bits per voxel of the new image
	 * @param init Must be true if this image should be considered empty
	 */
	protected void addByteArray(byte[] byteArray, int bitDepth, boolean init) {

		if (bitDepth != this.m_image.getBitDepth()){
			throw new IllegalArgumentException("Adding a slice requires an image with the same bitdepth.");
		}
		
		ImageProcessor ip;
		switch (bitDepth){
			case 16:
				int nPixels = byteArray.length;
				short[] pixels = new short[nPixels];
				
				for (int count=0 ; count < nPixels ; count++){
					pixels[count] = (short)(byteArray[count] & 0xff);
				}
				ip = new ShortProcessor(this.m_image.getWidth(), this.m_image.getHeight(), pixels, null);
				break;
			case 8:
				ip = new ByteProcessor(this.m_image.getWidth(), this.m_image.getHeight(), byteArray, null); 
				break;
			default:
				throw new IllegalArgumentException("Convertion to AWT images is not supported for this type of ImageCore.");
		}
		
		int currentDepth = this.m_image.getDepth();
		
		ImageStack stack = this.m_imp.getStack();
		
		if (init){ // Work around the fact that image cannot create truly empty images
			stack.setProcessor(ip, 1);
			stack.setSliceLabel("Slice"+1, 1);
		}else{
			stack.addSlice("Slice"+(currentDepth+1), ip);
		}
		this.m_imp.setStack(stack);
		
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
	 * @return The image as an ImageCore implementer's instance.
	 */
	public static ImageCoreIJ createFromRegularArray(byte[][] voxelValues, int width, int bitDepth){
		
		if (voxelValues.length == 0 || voxelValues[0].length == 0){
			throw new IllegalArgumentException("Cannot create an ImageCore from an empty array.");
		}
		if ((voxelValues[0].length%width) != 0){
			throw new IllegalArgumentException("Incompatible image width and array size.");
		}
		
		int height = voxelValues[0].length/width;
		
		ImagePlus imp = IJ.createImage(null, 
									   width, height,
									   0, 
									   bitDepth);
		
		ImageConvertIJ newImage = (new ImageCoreIJ(imp)).getImageConvert();
		
		// Work around the fact that image cannot create truly empty images
		boolean init = true;
		
		for (int z=0 ; z < voxelValues.length ; z++){
			newImage.addByteArray(voxelValues[z], bitDepth, init);
			init = false;
			if (z%10 == 0){
				System.gc();
			}
		}

		return newImage.m_image;
	}
	
	/**
	 * Converts the image to one Byte gray levels (colors from 0 to 255)
	 * @param clampValues If true, values are clamped so that the
	 * voxel's values are set to 255 if the original value is greater than 255.
	 * Otherwise, if this image is Gray16, the values are divided by 256
	 */
	private ImageCoreIJ doConvertToGray8(boolean clampValues) {
		
		if (this.m_imp.getType() == ImagePlus.GRAY8){
			System.err.println("Warning: request to convert GRAY8 to GRAY8.");
			return this.m_image;
		}		
		
		ImagePlus impOut = IJ.createImage(null, 
				   this.m_imp.getWidth(), this.m_imp.getHeight(),
				   0, 
				   8);

		// Work around the fact that image cannot create truly empty images
		boolean init = true;
		
		int stackSize = this.m_imp.getStackSize();
		int sliceSize = this.m_imp.getWidth()*this.m_imp.getHeight();
		ImageStack oldStack = this.m_imp.getStack();
		ImageStack newStack = impOut.getStack();
		
		for (int z=0 ; z < stackSize ; z++){
			
			short[] shortSlice = (short[])oldStack.getProcessor(1).getPixels();
			byte [] pixels = new byte[sliceSize];
			for (int count=0 ; count<sliceSize ; count++){
				if (clampValues){
					int val = shortSlice[count] & 0xffff;
					pixels[count] = (byte)(val >= 255 ? 255 : val);
				}else{
					pixels[count] = (byte)((shortSlice[count] & 0xffff)/256);
				}
			}
			oldStack.deleteSlice(1);
			
			ImageProcessor ip = new ByteProcessor(impOut.getWidth(), impOut.getHeight(), pixels, null); 
			
			if (init){ // Work around the fact that image cannot create truly empty images
				newStack.setProcessor(ip, 1);
				newStack.setSliceLabel("Slice"+1, 1);
			}else{
				newStack.addSlice("Slice"+(z+1), ip);
			}
			
			init = false;
			if (z%10 == 0){
				System.gc();
			}
		}

		this.m_imp.setStack(null, newStack);
		
		return this.m_image;
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#convertToGray8(boolean)
	 */
	@Override
	public ImageCoreIJ convertToGray8(boolean scaleMaxContrast) {
		if (scaleMaxContrast){
			this.m_image.getImageContrast().maximizeValuesRange();
		}
		doConvertToGray8(false);
		return this.m_image;
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#convertToGray8Clamp()
	 */
	@Override
	public ImageCoreIJ convertToGray8Clamp(){
		doConvertToGray8(true);
		return this.m_image;
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#getCopyAsGray8(boolean)
	 */
	@Override
	public ImageCoreIJ getCopyAsGray8(boolean scaleMaxContrast) {
		
		ImageCoreIJ resultImage = ImageCoreFactoryIJ.getInstance()
											.getEmptyImageCore(this.m_image.getWidth(),
															   this.m_image.getHeight(),
															   this.m_image.getDepth(), 8);
		
		int minValue = scaleMaxContrast ? this.m_image.getImageContrast().getMinValue() 
										: 0;
		int maxValue = scaleMaxContrast ? this.m_image.getImageContrast().getMaxValue() 
										: this.m_image.getWhiteValue();
		int interval = (maxValue - minValue)*255;
		int whiteValue = this.m_image.getWhiteValue();
		
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			resultImage.setCurrentZ(z);
			for (int x=0 ; x<this.m_image.getWidth() ; x++){
				for (int y=0 ; y<this.m_image.getHeight() ; y++){
					int pixelValue = this.m_image.getPixel(x, y);
					int outValue = ((pixelValue-minValue)*whiteValue)/interval;
					if (outValue >= 256){
						outValue = 255;
					}
					resultImage.setPixel(x, y, outValue);
				}
			}
		}

		return resultImage;
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConvert#getCopyAsGray8Clamp()
	 */
	@Override
	public ImageCoreIJ getCopyAsGray8Clamp(){
		ImageCoreIJ resultImage = ImageCoreFactoryIJ.getInstance()
													.getEmptyImageCore(this.m_image.getWidth(),
																	   this.m_image.getHeight(),
																	   this.m_image.getDepth(), 8);
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			resultImage.setCurrentZ(z);
			for (int x=0 ; x<this.m_image.getWidth() ; x++){
				for (int y=0 ; y<this.m_image.getHeight() ; y++){
					int pixelValue = this.m_image.getPixel(x, y);
					if (pixelValue >= 256){
						pixelValue = 255;
					}
					resultImage.setPixel(x, y, pixelValue);
				}
			}
		}

		return resultImage;
	}
}
