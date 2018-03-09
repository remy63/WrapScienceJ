/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ConvolutionFactoryGeneric.java                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.convolve.generic;

import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelDouble;
import wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase;


/**
 * @author remy
 *
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionFactoryBaseGeneric
 * 
 */
public abstract class BlurFactoryGeneric extends ConvolutionFactoryBaseGeneric implements BlurFactory {


	/**
	 * @param image Original reference to the ImageCore implementer 
	 */
	public BlurFactoryGeneric(ImageCore image){
		super(image);
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionFactoryBaseGeneric#embedOutput(boolean, int)
	 */
	@Override
	public BlurFactoryGeneric embedOutput(boolean autoAllowSignedValues, int bitDepth){
		return (BlurFactoryGeneric) super.embedOutput(autoAllowSignedValues, bitDepth);
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionFactoryBaseGeneric#enlargeInImage(int, int, int, wrapScienceJ.resource.BufferEnlargementPolicy)
	 */
	@Override
	public BlurFactoryGeneric enlargeInImage(int xMargin, int yMargin, int zMargin,
	 													BufferEnlargementPolicy enlargementPolicy){
		return (BlurFactoryGeneric) super.enlargeInImage(xMargin, yMargin, zMargin, enlargementPolicy);
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getIdentityMask()
	 */
	@Override
	public ConvolutionBaseGeneric getIdentityMask(){
		return new ConvolutionBaseGeneric(this.m_inImageSignPolicyEmbed, this.m_outImageSignPolicyEmbed, 1,
											this.m_shiftOuputMargin) {
			
			/**
			 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric#applyMask()
			 */
			@Override
			public ConvolutionBaseGeneric applyMask() {
				
				return this;
			}
		};
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getBinomialBlur(int, int, int, int, int, int)
	 */
	@Override
	public ConvolutionBase getBinomialBlur(int nPointsX, int nPointsY, int nPointsZ,
										   int skippingStepX, int skippingStepY, int skippingStepZ){

		BinomialBlurGeneric maskX = new BinomialBlurGeneric(this.m_inImageSignPolicyEmbed,
															this.m_outImageSignPolicyEmbed,
															CoordinateAxis.X, nPointsX,  skippingStepX,
															this.m_shiftOuputMargin
														    );
		BinomialBlurGeneric maskY = new BinomialBlurGeneric(this.m_inImageSignPolicyEmbed,
															this.m_outImageSignPolicyEmbed,				
								 							CoordinateAxis.Y, nPointsY,  skippingStepY,
								 							this.m_shiftOuputMargin
								 						    );
		BinomialBlurGeneric maskZ = new BinomialBlurGeneric(this.m_inImageSignPolicyEmbed,
															this.m_outImageSignPolicyEmbed,	
															CoordinateAxis.Z, nPointsZ,  skippingStepZ,
															this.m_shiftOuputMargin
														    );
		return maskX.composeWith(maskY.composeWith(maskZ));
	}


	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getBinomialBlur(int, int, int)
	 */
	@Override
	public ConvolutionBase getBinomialBlur(int nPointsX, int nPointsY, int nPointsZ){
		return this.getBinomialBlur(nPointsX, nPointsY, nPointsZ, 1, 1, 1);
	}
	
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getBinomialBlurCalibrated(int, int, int, double, double, double, VoxelDouble)
	 */
	@Override
	public ConvolutionBase getBinomialBlurCalibrated(int nPointsX, int nPointsY, int nPointsZ,
										   double realSkippingStepX, double realSkippingStepY, double realSkippingStepZ,
										   VoxelDouble voxelEdgesLength){

		BinomialBlurGeneric maskX = new BinomialBlurGeneric(getInImageSignPolicyEmbed(),
															getOutImageSignPolicyEmbed(),
															CoordinateAxis.X, nPointsX,
															realSkippingStepX, voxelEdgesLength.getX(),
															this.m_shiftOuputMargin
															);
		BinomialBlurGeneric maskY = new BinomialBlurGeneric(getInImageSignPolicyEmbed(),
															getOutImageSignPolicyEmbed(),
															CoordinateAxis.Y, nPointsY,
															realSkippingStepY, voxelEdgesLength.getY(),
															this.m_shiftOuputMargin
															);
		BinomialBlurGeneric maskZ = new BinomialBlurGeneric(getInImageSignPolicyEmbed(),
															getOutImageSignPolicyEmbed(),
															CoordinateAxis.Z, nPointsZ,
															realSkippingStepZ, voxelEdgesLength.getZ(),
															this.m_shiftOuputMargin
															);
		return maskX.composeWith(maskY.composeWith(maskZ));
	}
	
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getGaussianBlur(double, double, double)
	 */
	@Override
	public abstract ConvolutionBaseGeneric getGaussianBlur(
												double sigmaX, double sigmaY, double sigmaZ
											);
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory#getGaussianBlurCalibrated(double, double, double, VoxelDouble)
	 */
	@Override	
	public abstract ConvolutionBaseGeneric getGaussianBlurCalibrated(
												double sigmaX, double sigmaY, double sigmaZ,
												VoxelDouble voxelEdgesLength
												);

}
