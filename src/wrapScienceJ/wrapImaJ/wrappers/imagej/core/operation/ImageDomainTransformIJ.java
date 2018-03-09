/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDomainTransform.java                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation;


import ij.ImagePlus;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageDomainTransformGeneric;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;

/**
 * @author remy
 
 */
public class ImageDomainTransformIJ extends ImageDomainTransformGeneric {
	
	/**
	 * ImagePlus for access to ImageJ lower level methods.
	 */
	ImagePlus m_imp;
	
	/**
	 * @param image The image to process
	 */
	public ImageDomainTransformIJ(ImageCoreIJ image){
		super(image);
		this.m_imp = image.getImp();
	}
	


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.generic.ImageDomainTransformGeneric#copyTo(wrapScienceJ.wrapImaJ.core.ImageCore)
	 */
	@Override
	public ImageCore copyTo(ImageCore destinationImage) {
		if (!(destinationImage instanceof ImageCoreIJ)){
			throw new IllegalArgumentException("ImageCore copyTo requires destination to be an ImageCoreIJ");
		}
		if (this.m_image.getWidth() != destinationImage.getWidth() ||
			this.m_image.getHeight() != destinationImage.getHeight() ||
			this.m_image.getDepth() != destinationImage.getDepth()){
			
			throw new IllegalArgumentException("Cannot copy to an image with incompatible size");
		}
		int sliceSize = this.m_image.getWidth()*this.m_image.getHeight();
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			destinationImage.setCurrentZ(z);
			System.arraycopy(this.m_imp.getProcessor().getPixels(), 
							 0, ((ImageCoreIJ) destinationImage).getImp().getProcessor().getPixels(), 0,
							 sliceSize
							 );
		}
		destinationImage.mergeMetaData(this.m_image);
		
		return destinationImage;
	}



}
