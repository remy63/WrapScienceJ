/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDomainTransformGeneric.java                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.generic;

import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCalibration;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelDouble;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.operation.ImageDomainTransform;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;

/**
 * @author remy
 *
 */
public abstract class ImageDomainTransformGeneric implements ImageDomainTransform {
	
	/**
	 * Underlying image on which operations are performed
	 */
	protected ImageCore m_image;
	
	/**
	 * @param image The image to process
	 */
	protected ImageDomainTransformGeneric(ImageCore image){
		this.m_image = image;
	}
	
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainTransform#copyTo(wrapScienceJ.wrapImaJ.core.ImageCore)
	 */
	@Override
	public abstract ImageCore copyTo(ImageCore destinationImage);
	

	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainTransform#getAxisSwapped(wrapScienceJ.wrapImaJ.core.CoordinateAxis, wrapScienceJ.wrapImaJ.core.CoordinateAxis)
	 */
	@Override
	public ImageCore getAxisSwapped(CoordinateAxis axis1, CoordinateAxis axis2) {
		ImageCoreIJ newImage;
		int width = this.m_image.getWidth();
		int height = this.m_image.getHeight();
		int depth = this.m_image.getDepth();
		
		if(axis1 == axis2){
			System.err.println("Swapping equal axis comes with needless cost." +
											   " Consider using duplicate() instead.");
			return this.m_image;
		}
		
		if (axis1 == CoordinateAxis.X && axis2 == CoordinateAxis.Y ||
			axis1 == CoordinateAxis.Y && axis2 == CoordinateAxis.X){
			width = this.m_image.getHeight();
			height = this.m_image.getWidth();
		}
		if (axis1 == CoordinateAxis.X && axis2 == CoordinateAxis.Z ||
			axis1 == CoordinateAxis.Z && axis2 == CoordinateAxis.X){
			width = this.m_image.getDepth();
			depth = this.m_image.getWidth();
		}
		if (axis1 == CoordinateAxis.Y && axis2 == CoordinateAxis.Z ||
			axis1 == CoordinateAxis.Z && axis2 == CoordinateAxis.Y){
			height = this.m_image.getDepth();
			depth = this.m_image.getHeight();
		}
		
		newImage = ImageCoreIJ.getEmptyImageCore(width, height, depth, this.m_image.getBitDepth());
		ImageCalibration cal = this.m_image.getImageCalibration();
		
		if (axis1 == CoordinateAxis.X && axis2 == CoordinateAxis.Y ||
			axis1 == CoordinateAxis.Y && axis2 == CoordinateAxis.X){
			
			newImage.getImageCalibration().setVoxelLength(
											new VoxelDouble(cal.getVoxelHeight(),
															cal.getVoxelWidth(),
															cal.getVoxelDepth()));
			for (int z=0 ; z<depth ; z++){
				for (int y=0 ; y<height ; y++){
					for (int x=0 ; x<width ; x++){
						newImage.setVoxel(x, y, z, this.m_image.getVoxel(y, x, z));
					}
				}
			}
		}
		
		if (axis1 == CoordinateAxis.X && axis2 == CoordinateAxis.Z ||
			axis1 == CoordinateAxis.Z && axis2 == CoordinateAxis.X){
			
			newImage.getImageCalibration().setVoxelLength(
											new VoxelDouble(cal.getVoxelDepth(),
															cal.getVoxelHeight(),
															cal.getVoxelWidth()));
			for (int z=0 ; z<depth ; z++){
				for (int y=0 ; y<height ; y++){
					for (int x=0 ; x<width ; x++){
						newImage.setVoxel(x, y, z, this.m_image.getVoxel(z, y, x));
					}
				}
			}			
		}
		
		if (axis1 == CoordinateAxis.Y && axis2 == CoordinateAxis.Z ||
			axis1 == CoordinateAxis.Z && axis2 == CoordinateAxis.Y){
			
			newImage.getImageCalibration().setVoxelLength(
											new VoxelDouble(cal.getVoxelWidth(),
															cal.getVoxelDepth(),
															cal.getVoxelHeight()));
			for (int z=0 ; z<depth ; z++){
				for (int y=0 ; y<height ; y++){
					for (int x=0 ; x<width ; x++){
						newImage.setVoxel(x, y, z, this.m_image.getVoxel(x, z, y));
					}
				}
			}		
		}
		
		return newImage;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainTransform#copyAxisSwapped(wrapScienceJ.wrapImaJ.core.ImageCore, wrapScienceJ.wrapImaJ.core.CoordinateAxis, wrapScienceJ.wrapImaJ.core.CoordinateAxis)
	 */
	@Override
	public ImageCore copyAxisSwapped(ImageCore destinationImage,
								CoordinateAxis axis1, CoordinateAxis axis2) {

		if (destinationImage.getBitDepth() != this.m_image.getBitDepth()){
			throw new IllegalArgumentException("Cannot copy to an image with different bit depth");
		}
		
		if (axis1 == axis2){
			if (this.m_image.getWidth() != destinationImage.getWidth() ||
				this.m_image.getHeight() != destinationImage.getHeight() ||
				this.m_image.getDepth() != destinationImage.getDepth()){
				
				throw new IllegalArgumentException("Cannot copy to an image with incompatible size");
			}
		}
		
		if (axis1 == CoordinateAxis.X && axis2 == CoordinateAxis.Y ||
			axis1 == CoordinateAxis.Y && axis2 == CoordinateAxis.X){
			if (this.m_image.getWidth() != destinationImage.getHeight() ||
					this.m_image.getHeight() != destinationImage.getWidth() ||
					this.m_image.getDepth() != destinationImage.getDepth()){
			
				throw new IllegalArgumentException("Cannot copy to an image with incompatible size");
			}
		}
		
		if (axis1 == axis2){
			for (int z=0 ; z<destinationImage.getDepth() ; z++){
				for (int y=0 ; y<destinationImage.getHeight() ; y++){
					for (int x=0 ; x<destinationImage.getWidth() ; x++){
						destinationImage.setVoxel(x, y, z, this.m_image.getVoxel(x, y, z));
					}
				}
			}			
		}
		
		if (axis1 == CoordinateAxis.X && axis2 == CoordinateAxis.Z ||
			axis1 == CoordinateAxis.Z && axis2 == CoordinateAxis.X){
			
			if (this.m_image.getWidth() != destinationImage.getDepth() ||
					this.m_image.getHeight() != destinationImage.getHeight() ||
					this.m_image.getDepth() != destinationImage.getWidth()){
					
				throw new IllegalArgumentException("Cannot copy to an image with incompatible size");
			}
		}
		
		if (axis1 == CoordinateAxis.Y && axis2 == CoordinateAxis.Z ||
			axis1 == CoordinateAxis.Z && axis2 == CoordinateAxis.Y){
			
			if (this.m_image.getWidth() != destinationImage.getWidth() ||
				this.m_image.getHeight() != destinationImage.getDepth() ||
				this.m_image.getDepth() != destinationImage.getHeight()){
				
				throw new IllegalArgumentException("Cannot copy to an image with incompatible size");
			}
		}

		if (axis1 == CoordinateAxis.X && axis2 == CoordinateAxis.Y ||
			axis1 == CoordinateAxis.Y && axis2 == CoordinateAxis.X){
			
			for (int z=0 ; z<destinationImage.getDepth() ; z++){
				for (int y=0 ; y<destinationImage.getHeight() ; y++){
					for (int x=0 ; x<destinationImage.getWidth() ; x++){
						destinationImage.setVoxel(x, y, z, this.m_image.getVoxel(y, x, z));
					}
				}
			}
		}
		
		if (axis1 == CoordinateAxis.X && axis2 == CoordinateAxis.Z ||
			axis1 == CoordinateAxis.Z && axis2 == CoordinateAxis.X){
				
			for (int z=0 ; z<destinationImage.getDepth() ; z++){
				for (int y=0 ; y<destinationImage.getHeight() ; y++){
					for (int x=0 ; x<destinationImage.getWidth() ; x++){
						destinationImage.setVoxel(x, y, z, this.m_image.getVoxel(z, y, x));
					}
				}
			}			
		}
		
		if (axis1 == CoordinateAxis.Y && axis2 == CoordinateAxis.Z ||
				axis1 == CoordinateAxis.Z && axis2 == CoordinateAxis.Y){
				
			for (int z=0 ; z<destinationImage.getDepth() ; z++){
				for (int y=0 ; y<destinationImage.getHeight() ; y++){
					for (int x=0 ; x<destinationImage.getWidth() ; x++){
						destinationImage.setVoxel(x, y, z, this.m_image.getVoxel(x, z, y));
					}
				}		
			}
		}
		
		return destinationImage;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainTransform#copyInto(wrapScienceJ.wrapImaJ.core.ImageCore, wrapScienceJ.wrapImaJ.core.VoxelInt, boolean)
	 */
	@Override
	public ImageCore copyInto(ImageCore destinationImage, VoxelInt shiftMargin, boolean scaleForBitDepth) {

		if (destinationImage == this.m_image){
			return this.m_image;
		}
	
		if (!(destinationImage instanceof ImageCoreIJ)){
			throw new IllegalArgumentException("ImageCore copyTo requires destination to be an ImageCoreIJ");
		}
		int multScale = 1;
		int divScale = 1;
		if (this.m_image.getBitDepth() < destinationImage.getBitDepth()){
			multScale = 256;
		}
		if (this.m_image.getBitDepth() > destinationImage.getBitDepth()){
			divScale = 256;
		}
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			if (z+shiftMargin.getZ()< 0 || z+shiftMargin.getZ() >= destinationImage.getDepth()){
				continue;
			}
			destinationImage.setCurrentZ(z+shiftMargin.getZ());
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				if (y+shiftMargin.getY()< 0 || y+shiftMargin.getY() >= destinationImage.getHeight()){
					continue;
				}
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					if (x+shiftMargin.getX()<0 || x+shiftMargin.getX() >= destinationImage.getWidth()){
						continue;
					}	
					destinationImage.setPixel(x+shiftMargin.getX(), y+shiftMargin.getY(),
											  (this.m_image.getVoxel(x, y, z)*multScale)/divScale);
				}
			}
		}
		
		destinationImage.mergeMetaData(this.m_image);
		
		return destinationImage;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainTransform#copyFrom(wrapScienceJ.wrapImaJ.core.ImageCore, wrapScienceJ.wrapImaJ.core.VoxelInt, boolean)
	 */
	@Override
	public ImageCore copyFrom(ImageCore destinationImage, VoxelInt shiftMargin,
							  boolean scaleForBitDepth) {
		
		if (destinationImage == this.m_image){
			return this.m_image;
		}
		
		if (!(destinationImage instanceof ImageCoreIJ)){
			throw new IllegalArgumentException("ImageCore copyTo requires destination to be an ImageCoreIJ");
		}
		int multScale = 1;
		int divScale = 1;
		if (this.m_image.getBitDepth() < destinationImage.getBitDepth()){
			multScale = 256;
		}
		if (this.m_image.getBitDepth() > destinationImage.getBitDepth()){
			divScale = 256;
		}
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			if (z+shiftMargin.getZ()< 0 || z+shiftMargin.getZ() >= destinationImage.getDepth()){
				continue;
			}
			destinationImage.setCurrentZ(z+shiftMargin.getZ());
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				if (y+shiftMargin.getY()< 0 || y+shiftMargin.getY() >= destinationImage.getHeight()){
					continue;
				}
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					if (x+shiftMargin.getX()<0 || x+shiftMargin.getX() >= destinationImage.getWidth()){
						continue;
					}	
					this.m_image.setPixel(x, y, 
										  (destinationImage.getPixel(x+shiftMargin.getX(),
													   				 y+shiftMargin.getY())*multScale
										  )/divScale
							 		);
				}
			}
		}
		
		this.m_image.mergeMetaData(destinationImage);
		
		return this.m_image;
	}

	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainTransform#getAxisReversed(wrapScienceJ.wrapImaJ.core.CoordinateAxis[])
	 */
	@Override
	public ImageCore getAxisReversed(CoordinateAxis[] axisCollection) {
		if (axisCollection.length == 0){
			System.err.println("Involutive axis reversal with no Axis.");
			return this.m_image;
		}
		if (axisCollection.length > 3 ||
			(axisCollection.length >= 2 && axisCollection[0] == axisCollection[1]) ||
			axisCollection.length >= 3 &&
			(axisCollection[2] == axisCollection[1] || axisCollection[0] == axisCollection[2])
			){
			System.err.println("Reflecting twice against the same axis is sub-optimal.");
		}
		
		int reverseX = 1;
		int reverseY = 1;
		int reverseZ = 1;
		
		for (CoordinateAxis axis: axisCollection){
			if (axis == CoordinateAxis.X){
				reverseX *= -1;
			}
			if (axis == CoordinateAxis.Y){
				reverseY *= -1;
			}
			if (axis == CoordinateAxis.Z){
				reverseZ *= -1;
			}
		}
		
		System.err.println("reverse = (" + reverseX + ", " + reverseY +", " + reverseZ + ")");
		
		
		// Reversal is performed through swapping to avoid buffering
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth()/2 ; x++){
					int swapX = reverseX*x + ((1-reverseX)/2)*(this.m_image.getWidth()-1);
					
					int tmpVal = this.m_image.getVoxel(x, y, z);
					this.m_image.setVoxel(x, y, z, this.m_image.getVoxel(
													swapX, y, z));
					this.m_image.setVoxel(swapX, y, z, tmpVal);
					
				}
			}		
		}

		// Reversal is performed through swapping to avoid buffering
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			for (int y=0 ; y<this.m_image.getHeight()/2 ; y++){
				int swapY = reverseY*y + ((1-reverseY)/2)*(this.m_image.getHeight()-1);
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					
					int tmpVal = this.m_image.getVoxel(x, y, z);
					this.m_image.setVoxel(x, y, z, 
										  this.m_image.getVoxel(x, swapY, z)
										 );
					this.m_image.setVoxel(x, swapY, z, tmpVal);
					
				}
			}		
		}
		
		// Reversal is performed through swapping to avoid buffering
		for (int z=0 ; z<this.m_image.getDepth()/2 ; z++){
			int swapZ = reverseZ*z + ((1-reverseZ)/2)*(this.m_image.getDepth()-1);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					
					int tmpVal = this.m_image.getVoxel(x, y, z);
					this.m_image.setVoxel(x, y, z, 
										  this.m_image.getVoxel(x, y, swapZ));
					this.m_image.setVoxel(x, y, swapZ, tmpVal);
					
				}
			}		
		}
		return this.m_image;
	}

	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainTransform#getPlaneRotated90(wrapScienceJ.wrapImaJ.core.CoordinateAxis, wrapScienceJ.wrapImaJ.core.CoordinateAxis)
	 */
	@Override
	public ImageCore getPlaneRotated90(CoordinateAxis axis1, CoordinateAxis axis2) {
		
		ImageCore swappedImage = getAxisSwapped(axis1, axis2);
		swappedImage.mergeMetaData(this.m_image);
		
		swappedImage.getImageDomainTransform()
					.getAxisReversed(new CoordinateAxis[]{axis1});
		return swappedImage;
	}
	
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainTransform#copyPlaneRotated90(wrapScienceJ.wrapImaJ.core.ImageCore, wrapScienceJ.wrapImaJ.core.CoordinateAxis, wrapScienceJ.wrapImaJ.core.CoordinateAxis)
	 */
	@Override
	public ImageCore copyPlaneRotated90(ImageCore destinationImage,
									CoordinateAxis axis1, CoordinateAxis axis2) {
		copyAxisSwapped(destinationImage, axis1, axis2);
		destinationImage.getImageDomainTransform()
						.getAxisReversed(new CoordinateAxis[]{axis1});
		return destinationImage;
	}


}
