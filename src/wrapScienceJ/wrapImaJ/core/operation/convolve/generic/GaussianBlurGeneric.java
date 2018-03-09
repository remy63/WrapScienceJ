/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  GaussianBlurGeneric.java                                          * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.convolve.generic;

import wrapScienceJ.wrapImaJ.core.VoxelDouble;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric;


/**
 * This class is an abstract base class for Gaussian Blur Implementations.
 * 
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.BlurFactoryGeneric
 *
 */
public abstract class GaussianBlurGeneric extends ConvolutionBaseGeneric {
	
	/**
	 * Standard deviation of the Gaussian on the x coordinate.
	 */
	protected double m_sigmaX;
	
	/**
	 * Standard deviation of the Gaussian on the y coordinate.
	 */
	protected double m_sigmaY;
	
	/**
	 * Standard deviation of the Gaussian on the z coordinate.
	 */
	protected double m_sigmaZ;
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric#applyMask()
	 */
	@Override
	public abstract ConvolutionBaseGeneric applyMask();
	
	
	/**
	 * Applies a Gaussian Blur on the image, with a given standard deviation in
	 * each coordinate.
	 * 
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param outImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param sigmaX standard deviation of the Gaussian on the x coordinate
	 * @param sigmaY standard deviation of the Gaussian on the y coordinate
	 * @param sigmaZ standard deviation of the Gaussian on the z coordinate
	 * @param shiftOuputMargin Margins that were added through enlargement of input buffer.
	 */
	public GaussianBlurGeneric(ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed, 
							   ImageSignPolicyEmbedGeneric outImageSignPolicyEmbed,
							   double sigmaX, double sigmaY, double sigmaZ,
							   VoxelInt shiftOuputMargin){
		
		super(inImageSignPolicyEmbed, outImageSignPolicyEmbed, 1, shiftOuputMargin);
		
		this.m_sigmaX = sigmaX;
		this.m_sigmaY = sigmaY;
		this.m_sigmaZ = sigmaZ;
	}
	

	/**
	 * Applies a Gaussian Blur on the image, with a given standard deviation in
	 * each coordinate.
	 * This method does the same as 
	 * {@link #GaussianBlurGeneric(ImageSignPolicyEmbedGeneric, ImageSignPolicyEmbedGeneric, double, double, double, VoxelInt)}
	 * except that the standard deviations are given in (approximately) multiples of the calibration
	 * data (Voxels edges lengths in a given length unit) instead of number of voxels.
	 * 
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param outImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param realSigmaX standard deviation of the Gaussian on the x coordinate
	 * @param realSigmaY standard deviation of the Gaussian on the y coordinate
	 * @param realSigmaZ standard deviation of the Gaussian on the z coordinate
	 * @param voxelEdgesLength The voxel's scale (edges' length) used as a factor to estimate
	 * 						   actual skipping steps in each direction.
	 * @param shiftOuputMargin Margins that were added through enlargement of input buffer.
	 */
	public GaussianBlurGeneric(ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed, 
							   ImageSignPolicyEmbedGeneric outImageSignPolicyEmbed,
							   double realSigmaX, double realSigmaY, double realSigmaZ,
							   VoxelDouble voxelEdgesLength,
							   VoxelInt shiftOuputMargin){
		
		super(inImageSignPolicyEmbed, outImageSignPolicyEmbed, 1, shiftOuputMargin);
		
		double sigmaX = realSigmaX/voxelEdgesLength.getX();
		double sigmaY = realSigmaY/voxelEdgesLength.getY();
		double sigmaZ = realSigmaZ/voxelEdgesLength.getZ();
		
		if (sigmaX <= 0.0d || sigmaY <= 0.0d || sigmaZ <= 0.0d){
			throw new IllegalArgumentException("The Standard Deviation of a gaussian kernel" +
											   " must be positive");
		}
		
		this.m_sigmaX = sigmaX;
		this.m_sigmaY = sigmaY;
		this.m_sigmaZ = sigmaZ;
	}
	
}
