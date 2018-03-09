/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDomainProjectionGeneric.java                                 * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.generic;


import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.operation.ImageDomainProjection;
import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * @author remy
 *
*/
public class ImageDomainProjectionGeneric implements ImageDomainProjection {
	
	/**
	 * Underlying image on which operations are performed
	 */
	ImageCore m_image;
	
	/**
	 * @param image The image to process
	 */
	public ImageDomainProjectionGeneric(ImageCore image){

		this.m_image = image;
	}	
	
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainProjection#projectionTomography(wrapScienceJ.wrapImaJ.core.CoordinateAxis, boolean)
	 */
	@Override
	public ImageCore projectionTomography(CoordinateAxis axis, boolean maximizeContrast) {
		return projectionWithPolicy(axis, maximizeContrast, false);
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDomainProjection#projectionVolumeRendering(wrapScienceJ.wrapImaJ.core.CoordinateAxis, boolean)
	 */
	@Override
	public ImageCore projectionVolumeRendering(CoordinateAxis axis, boolean maximizeContrast) {
		return projectionWithPolicy(axis, maximizeContrast, true);
	}
	
	/**
	 * Performs the tomography projection along a coordinate axis.
	 * This method is for internal use in {@link #projectionTomography(CoordinateAxis, boolean)}
	 * and {@link #projectionVolumeRendering(CoordinateAxis, boolean)}.
	 * 
	 * @param axis The axis along which to project
	 * @param maximizeContrast If true, the gray scales in the output image should be scaled
	 * 						   so as to maximize contrast among possible colors.
	 * 						   A Blurring filter is also applied before scaling.
	 * @param max If true, a volume rendering is performed by computing the maximum
	 *			  of all gray levels values along the axis direction. Otherwise, averaging is
	 *			  performed.
	 * @return The 2D image with pixel's gray levels averaging the image along the Z direction.
	 */
	private ImageCore projectionWithPolicy(CoordinateAxis axis, boolean maximizeContrast, boolean max){
		ImageCore image2D;
		switch (axis){
			case Z:
				image2D = projectionZ(maximizeContrast, max);
				break;
			case Y:
				image2D = projectionTomographyY(maximizeContrast, max);
				break;
			case X:
				image2D = projectionTomographyX(maximizeContrast, max);				
				break;
			default:
				throw new IllegalArgumentException("Unsupported Projection Axis.");
		}
		return image2D;
	}
	
	/**
	 * Performs the tomography projection along Z. This method is for internal use in
	 * {@link #projectionTomography(CoordinateAxis, boolean)}.
	 * 
	 * @param maximizeContrast If true, the gray scales in the output image should be scaled
	 * 						   so as to maximize contrast among possible colors.
	 * 						   A Blurring filter is also applied before scaling.
	 * @param max If true, a volume rendering is performed by computing the maximum
	 *			  of all gray levels values along the Z direction. Otherwise, averaging is
	 *			  performed.
	 * @return The 2D image with pixel's gray levels averaging the image along the Z direction.
	 */
	private ImageCore projectionZ(boolean maximizeContrast, boolean max) {
		
		ImageCore image2D = this.m_image.getPreferedFactory().getEmptyImageCore(
									this.m_image.getWidth(), this.m_image.getHeight(), 1, this.m_image.getBitDepth());
		
		double[] sum = new double[this.m_image.getHeight()*this.m_image.getWidth()];
		for (int y=0 ; y<this.m_image.getHeight() ; y++){
			for (int x=0 ; x<this.m_image.getWidth() ; x++){
				sum[y*this.m_image.getWidth()+x] = 0.0;
			}
		}
		
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			int count = 0;
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					if (max){
						if (sum[count] < this.m_image.getPixel(x, y)){
							sum[count] = this.m_image.getPixel(x, y);
						}
					}else{
						sum[count] += this.m_image.getPixel(x, y);
					}
					count++;
				}
			}
		}

		double multiplicationFactor = max ? 1.0 // Case of a mere maximum of ray levels
										  : 1.0d/this.m_image.getDepth(); // case of a regular average
		if (maximizeContrast){
			double maxSum = 0.0;
			for (int count=0 ; count<this.m_image.getWidth()*this.m_image.getHeight() ; count++){
				if (sum[count] > maxSum){
					maxSum = sum[count];
				}
			}	
			multiplicationFactor = this.m_image.getWhiteValue()/maxSum;
		}
		
		int count = 0;
		image2D.setCurrentZ(0);
		
		for (int y=0 ; y<this.m_image.getHeight() ; y++){
			for (int x=0 ; x<this.m_image.getWidth() ; x++){
				image2D.setPixel(x, y, (int)(multiplicationFactor*sum[count]));
				count++;
			}
		}		
		
		return image2D;
	}

	/**
	 * Performs the tomography projection along Y. This method is for internal use in
	 * {@link #projectionTomography(CoordinateAxis, boolean)}.
	 * @param maximizeContrast If true, the gray scales in the output image should be scaled
	 * 						   so as to maximize contrast among possible colors.
	 * 						   A Blurring filter is also applied before scaling.
	 * @param max If true, a volume rendering is performed by computing the maximum
	 *			  of all gray levels values along the Y direction. Otherwise, averaging is
	 *			  performed.
	 * @return The 2D image with pixel's gray levels averaging the image along the Y direction.
	 */
	private ImageCore projectionTomographyY(boolean maximizeContrast, boolean max) {
		
		ImageCore image2D = this.m_image.getPreferedFactory().getEmptyImageCore(
				this.m_image.getWidth(), this.m_image.getDepth(), 1, this.m_image.getBitDepth());
		double[] sum = new double[this.m_image.getDepth()*this.m_image.getWidth()];
		
		for (int y=0 ; y<this.m_image.getDepth() ; y++){
			for (int x=0 ; x<this.m_image.getWidth() ; x++){
				sum[y*this.m_image.getWidth()+x] = 0.0;
			}
		}
		
		for (int z=0 ; z<this.m_image.getHeight() ; z++){
			int count = 0;
			for (int y=0 ; y<this.m_image.getDepth() ; y++){
				this.m_image.setCurrentZ(y);
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					if (max){
						if (sum[count] < this.m_image.getPixel(x, z)){
							sum[count] = this.m_image.getPixel(x, z);
						}
					}else{
						sum[count] += this.m_image.getPixel(x, z);
					}
					count++;
				}
			}
		}
		
		double multiplicationFactor = max ? 1.0 // Case of a mere maximum of ray levels
				  						  : 1.0d/this.m_image.getHeight(); // case of a regular average
		if (maximizeContrast){
			double maxSum = 0.0;
			for (int count=0 ; count<this.m_image.getDepth()*this.m_image.getWidth() ; count++){
				if (sum[count] > maxSum){
					maxSum = sum[count];
				}
			}	
			multiplicationFactor = this.m_image.getWhiteValue()/maxSum;
		}
		
		int count = 0;
		image2D.setCurrentZ(0);
		for (int y=0 ; y<this.m_image.getDepth() ; y++){
			for (int x=0 ; x<this.m_image.getWidth() ; x++){
				image2D.setPixel(x, y, (int)(multiplicationFactor*sum[count]));
				count++;
			}
		}		
		
		return image2D;
	}

	
	/**
	 * Performs the tomography projection along X. This method is for internal use in
	 * {@link #projectionTomography(CoordinateAxis, boolean)}.
	 * @param maximizeContrast If true, the gray scales in the output image should be scaled
	 * 						   so as to maximize contrast among possible colors.
	 * 						   A Blurring filter is also applied before scaling.
	 * @param max If true, a volume rendering is performed by computing the maximum
	 *			  of all gray levels values along the X direction. Otherwise, averaging is
	 *			  performed.
	 * @return The 2D image with pixel's gray levels averaging the image along the X direction.
	 */
	private ImageCore projectionTomographyX(boolean maximizeContrast, boolean max) {
		
		ImageCore image2D = this.m_image.getPreferedFactory().getEmptyImageCore(
				this.m_image.getHeight(), this.m_image.getDepth(), 1, this.m_image.getBitDepth());
		double[] sum = new double[this.m_image.getDepth()*this.m_image.getHeight()];
		
		for (int y=0 ; y<this.m_image.getDepth() ; y++){
			for (int x=0 ; x<this.m_image.getHeight() ; x++){
				sum[y*this.m_image.getHeight()+x] = 0.0;
			}
		}
		
		for (int z=0 ; z<this.m_image.getWidth() ; z++){
			int count = 0;
			for (int y=0 ; y<this.m_image.getDepth() ; y++){
				this.m_image.setCurrentZ(y);
				for (int x=0 ; x<this.m_image.getHeight() ; x++){
					if (max){
						if (sum[count] < this.m_image.getPixel(z, x)){
							sum[count] = this.m_image.getPixel(z, x);
						}
					}else{
						sum[count] += this.m_image.getPixel(z, x);
					}
					count++;
				}
			}
		}
		
		double multiplicationFactor = max ? 1.0 // Case of a mere maximum of ray levels
										  : 1.0d/this.m_image.getWidth(); // case of a regular average
		if (maximizeContrast){
			double maxSum = 0.0;
			for (int count=0 ; count<this.m_image.getDepth()*this.m_image.getHeight() ; count++){
				if (sum[count] > maxSum){
					maxSum = sum[count];
				}
			}	
			multiplicationFactor = this.m_image.getWhiteValue()/maxSum;
		}
		
		int count = 0;
		image2D.setCurrentZ(0);
		for (int y=0 ; y<this.m_image.getDepth() ; y++){
			for (int x=0 ; x<this.m_image.getHeight() ; x++){
				image2D.setPixel(x, y, (int)(multiplicationFactor*sum[count]));
				count++;
			}
		}		
		
		return image2D;
	}

}
