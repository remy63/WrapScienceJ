/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDomainOperationGeneric.java                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.generic;

import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;

/**
 * @author remy
 *
 */
public abstract class ImageDomainOperationGeneric implements ImageDomainOperation{
	/**
	 * Underlying image on which operations are performed
	 */
	protected ImageCore m_image;
	
	
	/**
	 * @param image The image to process
	 */
	protected ImageDomainOperationGeneric(ImageCore image){
		this.m_image = image;
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation#crop(int, int, int, int, int, int)
	 */
	@Override
	public abstract ImageCoreIJ crop(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax);
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation#extractSlice(int)
	 */
	@Override
	public abstract ImageCoreIJ extractSlice(int zCoord);
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation#insertSlices(wrapScienceJ.wrapImaJ.core.ImageCore, int)
	 */
	@Override
	public abstract ImageCore insertSlices(ImageCore image, int zCoordMin);
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation#getEnlargedImage(int, int, int, wrapScienceJ.resource.BufferEnlargementPolicy)
	 */
	@Override
	public ImageCore getEnlargedImage(int xMargin, int yMargin, int zMargin,
									  BufferEnlargementPolicy enlargementPolicy) {

		if (xMargin > this.m_image.getWidth() || yMargin > this.m_image.getHeight() ||
			zMargin > this.m_image.getDepth()){

			throw new IllegalArgumentException("Enlargement margin cannot be greater than image size");
		}

		ImageCoreIJ enlargedImage = ImageCoreFactoryIJ.getInstance()
											  .getEmptyImageCore(this.m_image.getWidth() + 2*xMargin, 
																 this.m_image.getHeight() + 2*yMargin, 
																 this.m_image.getDepth() + 2*zMargin, 
																 this.m_image.getBitDepth()
																);
		
		enlargedImage.mergeMetaData(this.m_image);
		
		int white = this.m_image.getWhiteValue();
		// go over the image
		for (int z=0 ; z<enlargedImage.getDepth() ; z++){
			for (int y=0 ; y<enlargedImage.getHeight(); y++){
				for (int x=0 ; x<enlargedImage.getWidth() ; x++){
					
					int c = (z<zMargin ? 1 : ((z>=zMargin + this.m_image.getDepth()) ? -1 : 0));
					int b = (y<yMargin ? 1 : ((y>=yMargin + this.m_image.getHeight()) ? -1 : 0));
					int a = (x<xMargin ? 1 : ((x>=xMargin + this.m_image.getWidth()) ? -1 : 0));
					
					if (a==0 && b==0 && c==0){
						enlargedImage.setVoxel(x, y, z, 
											   this.m_image.getVoxel(x-xMargin, 
													   				 y-yMargin, 
													   				 z-zMargin));
					}else{
						switch (enlargementPolicy){
							case Zeros:
								enlargedImage.setVoxel(x, y, z, 0);
								break;
							case White:
								enlargedImage.setVoxel(x, y, z, white);
								break;
							case Pavement:
								enlargedImage.setVoxel(x, y, z, 
									this.m_image.getVoxel((x-xMargin)+a*this.m_image.getWidth(),
														  (y-yMargin)+b*this.m_image.getHeight(),
														  (z-zMargin)+c*this.m_image.getDepth()
														 )
													  );
								break;
							case Mirror:
								enlargedImage.setVoxel(x, y, z, 
									this.m_image.getVoxel(// Coordinate X
														  (a*a)*(
																  -(x-(xMargin-1)-((1-a)/2)*(this.m_image.getWidth() + 1))
																  + ((1-a)/2)*(this.m_image.getWidth() - 1)
																 )
														  + (1-a*a)*(x-xMargin),
														  // Coordinate Y
														  (b*b)*(
																  -(y-(yMargin-1)-((1-b)/2)*(this.m_image.getHeight() + 1))
																  + ((1-b)/2)*(this.m_image.getHeight() - 1)
																)
														  + (1-b*b)*(y-yMargin),
											  			  // Coordinate Z
														  (c*c)*(
																  -(z-(zMargin-1)-((1-c)/2)*(this.m_image.getDepth() + 1))
																  + ((1-c)/2)*(this.m_image.getDepth()-1)
																)
														  + (1-c*c)*(z-zMargin)
														 )
													  );
								break;
							default:
								throw new IllegalArgumentException("Undefined Enlargement Policy.");
						}
					}
				}
			}
		}
		
		return enlargedImage;
	}

	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation#extractSlices(int, int)
	 */
	@Override
	public ImageCore extractSlices(int zCoordMin, int zCoordMaxPlusOne) {
		if (zCoordMin < 0 || zCoordMaxPlusOne > this.m_image.getDepth() ||
				zCoordMin >= zCoordMaxPlusOne){
			throw new IllegalArgumentException("At least one slice must be extracted and " +
											   "all extracted slices must exist in the image.");
		}
		ImageCore resultImage = extractSlice(zCoordMin);
		ImageDomainOperation domainOperation = resultImage.getImageDomainOperation();
		
		for (int z = zCoordMin+1 ; z < zCoordMaxPlusOne ; z++){
			domainOperation.mergeSlices(extractSlice(z));
		}
		
		resultImage.mergeMetaData(this.m_image);
		return resultImage;
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation#domainConcatenation(wrapScienceJ.wrapImaJ.core.ImageCore, wrapScienceJ.wrapImaJ.core.CoordinateAxis)
	 */
	@Override
	public ImageCore domainConcatenation(ImageCore image, CoordinateAxis axis) {
		
		return insertImageSandwich(image, axis, this.m_image.getSize(axis));
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation#insertImageSandwich(wrapScienceJ.wrapImaJ.core.ImageCore, wrapScienceJ.wrapImaJ.core.CoordinateAxis, int)
	 */
	@Override
	public ImageCore insertImageSandwich(ImageCore image, CoordinateAxis axis,
										   int coordMinInsert) {
		
		if (this.m_image.getBitDepth() != image.getBitDepth() ||
				coordMinInsert<0){
			
			throw new IllegalArgumentException("Only images with the same bit depth" +
											   "can be concatenated/inserted at a " +
											   "non negative coordinate");
		}
		
		int width, height, depth;
		VoxelInt shiftVoxel = new VoxelInt(0, 0, 0);
		VoxelInt limitsBottomBread = new VoxelInt(this.m_image.getWidth(),
												  this.m_image.getHeight(),
												  this.m_image.getDepth()
												 );
		VoxelInt shiftCoordSize = new VoxelInt(0, 0, 0);
		
		switch (axis){
			case X:
				if (this.m_image.getHeight() != image.getHeight() ||
					this.m_image.getDepth() != image.getDepth() ||
					this.m_image.getWidth() < coordMinInsert){
		
					throw new IllegalArgumentException("Cannot copy to an image with incompatible size");
				}
				width = this.m_image.getWidth() + image.getWidth();
				height = this.m_image.getHeight();
				depth = this.m_image.getDepth();
				
				shiftVoxel.setX(coordMinInsert);
				limitsBottomBread.setX(coordMinInsert);
				shiftCoordSize.setX(image.getWidth());
				
				break;
			case Y:
				if (this.m_image.getWidth() != image.getWidth() ||
					this.m_image.getDepth() != image.getDepth() ||
					this.m_image.getHeight() < coordMinInsert){
		
					throw new IllegalArgumentException("Cannot copy to an image with incompatible size");
				}
				width = this.m_image.getWidth();
				height = this.m_image.getHeight() + image.getHeight();
				depth = this.m_image.getDepth();
				
				shiftVoxel.setY(coordMinInsert);
				limitsBottomBread.setY(coordMinInsert);
				shiftCoordSize.setY(image.getHeight());
				
				break;
			case Z:
				if (this.m_image.getWidth() != image.getWidth() ||
					this.m_image.getHeight() != image.getHeight() ||
					this.m_image.getDepth() < coordMinInsert){
		
					throw new IllegalArgumentException("Cannot copy to an image with incompatible size");
				}
				width = this.m_image.getWidth();
				height = this.m_image.getHeight();
				depth = this.m_image.getDepth() + image.getDepth();
				
				shiftVoxel.setZ(coordMinInsert);
				limitsBottomBread.setZ(coordMinInsert);
				shiftCoordSize.setZ(image.getDepth());
				
				break;
			default:
				throw new IllegalArgumentException("Unknown axis Option " + axis);
		}
		
		ImageCore sandwichImage = this.m_image.getPreferedFactory()
												 .getEmptyImageCore(
														 width, height, depth, 
														 this.m_image.getBitDepth()
												);
		
		for (int z=0 ; z< limitsBottomBread.getZ() ; z++){
			for (int y=0 ; y< limitsBottomBread.getY() ; y++){
				for (int x=0 ; x< limitsBottomBread.getX() ; x++){
					sandwichImage.setVoxel(x, y, z, this.m_image.getVoxel(x, y, z));
				}
			}
		}
		
		for (int z=0 ; z< image.getDepth() ; z++){
			for (int y=0 ; y< image.getHeight() ; y++){
				for (int x=0 ; x< image.getWidth() ; x++){
					sandwichImage.setVoxel(x+shiftVoxel.getX(),
										   y+shiftVoxel.getY(),
										   z+shiftVoxel.getZ(), 
										   image.getVoxel(x, y, z)
										  );
				}
			}
		}
		
		System.err.println("shiftCoordSize: " + shiftCoordSize);
		
		for (int z=shiftVoxel.getZ() ; z< this.m_image.getDepth() ; z++){
			for (int y=shiftVoxel.getY() ; y< this.m_image.getHeight() ; y++){
				for (int x=shiftVoxel.getX() ; x< this.m_image.getWidth() ; x++){
					sandwichImage.setVoxel(x+shiftCoordSize.getX(),
										   y+shiftCoordSize.getY(),
										   z+shiftCoordSize.getZ(),
										   this.m_image.getVoxel(x, y, z)
										  );
				}
			}
		}
		sandwichImage.mergeMetaData(this.m_image);
		return sandwichImage;
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation#mergeSlices(wrapScienceJ.wrapImaJ.core.ImageCore)
	 */
	@Override
	public ImageCore mergeSlices(ImageCore image) {
		if (this.m_image.getBitDepth() !=  image.getBitDepth() ||
			this.m_image.getWidth() != image.getWidth() ||
			this.m_image.getHeight() != image.getHeight()
		   ){
			throw new IllegalArgumentException("Wrong type or size of image to merge.");
		}
		
		if (!(image instanceof ImageCoreIJ)){
			throw new IllegalArgumentException("Wrong type of image to merge image.");
		}
		
		for (int z=0 ; z<image.getDepth() ; z++){
			image.setCurrentZ(z);
			switch (this.m_image.getBitDepth()){
				case 16:
					this.m_image.getImageConvert()
								.addSliceFromArray((short[])((ImageCoreIJ)image).getImp()
																				.getProcessor()
																				.getPixels(),
												   16
												  );
					break;
				case 8:
					this.m_image.getImageConvert()
								.addSliceFromArray((byte[])((ImageCoreIJ)image).getImp()
																			   .getProcessor()
																			   .getPixels(),
												   8
												  );
					break;
				default:
					throw new IllegalArgumentException("Merging image slices is not supported" +
													   "for this type of ImageCore.");
			}
		}
		
		return this.m_image;
	}

}
