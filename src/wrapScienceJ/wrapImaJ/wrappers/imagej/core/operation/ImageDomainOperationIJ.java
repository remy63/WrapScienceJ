/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDomainOperationIJ.java                                       * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageDomainOperationGeneric;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;

/**
 * @author remy
 *
 *	TODO Make generic and independent from ImageJ
 */
public class ImageDomainOperationIJ extends ImageDomainOperationGeneric {
	
	/**
	 * Image Data as an ImagePlus instance using ImageJ (shorthand)
	 */
	private ImagePlus m_imp;
	

	/**
	 * @param image The image to process
	 */
	public ImageDomainOperationIJ(ImageCoreIJ image){
		super(image);
		this.m_imp = image.getImp();
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.generic.ImageDomainOperationGeneric#crop(int, int, int, int, int, int)
	 */
	@Override
	public ImageCoreIJ crop(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax){
		
		int xMin = xmin >= 0 ? xmin : 0;
		int yMin = ymin >= 0 ? ymin : 0;
		int zMin = zmin >= 0 ? zmin : 0;
		
		int xMax = xmax < this.m_image.getWidth() ? xmax : this.m_image.getWidth();
		int yMax = ymax < this.m_image.getHeight() ? ymax : this.m_image.getHeight();
		int zMax = zmax < this.m_image.getDepth() ? zmax : this.m_image.getDepth();

		
    	ImageStack iStack =  this.m_imp.getStack();
    	ImagePlus imp = new ImagePlus();
    	imp.setStack(iStack.crop(xMin, yMin, zMin, xMax-xMin, yMax-yMin, zMax-zMin));
       	
    	ImageCoreIJ imageCrop = new ImageCoreIJ(imp);
    	imageCrop.mergeMetaData(this.m_image);
    	imp.killRoi();
    	return imageCrop;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation#extractSlice(int)
	 */
	@Override
	public ImageCoreIJ extractSlice(int zCoord) {
		
		ImagePlus imp = IJ.createImage(null, 
					this.m_image.getWidth(), this.m_image.getHeight(),
				   0, 
				   this.m_image.getBitDepth());
		
		ImageProcessor ip;
		switch (this.m_image.getBitDepth()){
			case 16:
				ip = new ShortProcessor(this.m_image.getWidth(), this.m_image.getHeight(), 
										(short[])this.m_image.getImageConvert().getSliceAsRawArray(zCoord), 
										null
									   ); 
				break;
			case 8:
				ip = new ByteProcessor(this.m_image.getWidth(), this.m_image.getHeight(),
									   (byte[])this.m_image.getImageConvert().getSliceAsRawArray(zCoord),
									   null
									  );
				break;
			default:
				throw new IllegalArgumentException("Convertion to AWT images is not supported" +
												   " for this type of ImageCore.");
		}		
		
		imp.setProcessor(ip);
		ImageCoreIJ image2D = new ImageCoreIJ(imp);
		
		image2D.mergeMetaData(this.m_image);
		
		return image2D;
	}

	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation#insertSlices(wrapScienceJ.wrapImaJ.core.ImageCore, int)
	 */
	@Override
	public ImageCoreIJ insertSlices(ImageCore image, int zCoordMin) {
		
		if (this.m_image.getBitDepth() !=  image.getBitDepth() ||
				this.m_image.getWidth() != image.getWidth() ||
				this.m_image.getHeight() != image.getHeight() ||
				zCoordMin < 0 || zCoordMin > this.m_image.getDepth()
			   ){
				throw new IllegalArgumentException("Wrong type or size of image to merge.");
			}
		if (!(image instanceof ImageCoreIJ)){
			throw new IllegalArgumentException("Wrong type of image to merge image.");
		}
		
		int initialDepth = this.m_image.getDepth();
		
		// Save pixel data for the slices above zCoordMin in the original image
		Object[] saveSlices = new Object[initialDepth-zCoordMin];
		for (int z=zCoordMin ; z<initialDepth ; z++){
			this.m_image.setCurrentZ(z);
			saveSlices[z-zCoordMin] = this.m_imp.getProcessor().getPixels();
		}
		// Delete slices above zCoordMin in the original image
		ImageStack stack = this.m_imp.getStack();
		for (int z=initialDepth-1 ; z>=zCoordMin ; z--){
			stack.deleteLastSlice();
		}
		// Add the slices of the input image to inster.
		for (int z=0 ; z<image.getDepth() ; z++){
			image.setCurrentZ(z);
			stack.addSlice(((ImageCoreIJ)image).getImp().getProcessor());
		}
		this.m_imp.setStack(stack);
		
		// Add back the previously saved slices of the original images
		for (int z=0 ; z<initialDepth-zCoordMin ; z++){

			switch (this.m_image.getBitDepth()){
				case 16:
					this.m_image.getImageConvert()
								.addSliceFromArray((short[])saveSlices[z], 16);
					break;
				case 8:
					this.m_image.getImageConvert()
								.addSliceFromArray((byte[])saveSlices[z], 8);
					break;
				default:
					throw new IllegalArgumentException("Merging image slices is not supported" +
													   "for this type of ImageCore.");
			}
		}
		
		return (ImageCoreIJ)this.m_image;
	}
}
