/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  BlurFactoryIJ.java                                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation;

import wrapScienceJ.wrapImaJ.core.VoxelDouble;
import wrapScienceJ.wrapImaJ.core.operation.convolve.generic.BlurFactoryGeneric;
import wrapScienceJ.wrapImaJ.core.operation.convolve.generic.GaussianBlurGeneric;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;

/**
 * @author remy
 *
 */
public class BlurFactoryIJ extends BlurFactoryGeneric {

	/**
	 * @param image Original reference to the ImageCore implementer 
	 */
	public BlurFactoryIJ(ImageCoreIJ image) {
		super(image);
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.BlurFactoryGeneric#getGaussianBlur(double, double, double)
	 */
	@Override
	public GaussianBlurGeneric getGaussianBlur(double sigmaX, double sigmaY, double sigmaZ) {

		return new GaussianBlurIJ(getInImageSignPolicyEmbed(), getOutImageSignPolicyEmbed(),
									sigmaX, sigmaY, sigmaZ,
									  this.m_shiftOuputMargin);
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.BlurFactoryGeneric#getGaussianBlurCalibrated(double, double, double, wrapScienceJ.wrapImaJ.core.VoxelDouble)
	 */
	@Override
	public GaussianBlurGeneric getGaussianBlurCalibrated(double sigmaX, double sigmaY, double sigmaZ,
										VoxelDouble voxelEdgesLength) {

		return new GaussianBlurIJ(getInImageSignPolicyEmbed(), getOutImageSignPolicyEmbed(),
								  sigmaX, sigmaY, sigmaZ,
								  voxelEdgesLength,
								  this.m_shiftOuputMargin
								 );
	}


}
