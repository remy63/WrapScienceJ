/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageDifferentialOperatorGeneric.java                             * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.convolve.generic;

import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric;
import wrapScienceJ.wrapImaJ.core.operation.ImageDifferentialOperator;


/**
 * @author remy
 *
* TODO THIS PACKAGE IS CURRENTLY BROKEN AND HAS TO BE FIXED BEFORE USE.
* 
 */
public class ImageDifferentialOperatorGeneric implements ImageDifferentialOperator{
	
	/**
	 * The current convolution mask. Initialized to the identity mask,
	 * it can compose different mask before being applied to an image.
	 */
	protected ConvolutionBase m_convolutionMask;
	
	/**
	 * The image, possibly embedded to event overflows, on which to apply
	 * the convolutions and the normalization.
	 */
	protected ImageCore m_image;
	
	
	
	/**
	 * Constructs an instance allowing to apply different convolution masks
	 * to an image. The convolution operators could be a blur, or a differential
	 * operator, any convolution mask, or any composition of them.
	 * 
	 * @param image The input image to be processed.
	 * @param autoAllowSignedValues If true, the gray levels are shifted to start in the middle of
	 * 							the range of a short value. 
	 */
	public ImageDifferentialOperatorGeneric(ImageCore image, boolean autoAllowSignedValues){
		
		this.m_image = image;

		if (autoAllowSignedValues){
			this.m_image.getImageSignPolicyEmbed(true).getImageSignedClamp();
		}
		
		this.m_convolutionMask = new ConvolutionFactoryBaseGeneric(this.m_image)
									.getIdentityMask();
	}
	
	/**
	 * Constructs an instance allowing to apply different convolution masks
	 * to an image. The convolution operators could be a blur, or a differential
	 * operator, any convolution mask, or any composition of them.
	 * 
	 * 
	 * @param image The input image to be processed.
	 * @param autoAllowSignedValues If true, the gray levels are shifted to start in the middle of
	 * 							the range of a short value. 
	 * @param inputImageMarginX Margin breadth to enlarge the input image in X
	 * @param inputImageMarginY Margin breadth to enlarge the input image in Y
	 * @param inputImageMarginZ Margin breadth to enlarge the input image in Z
	 * @param enlargementPolicy Policy to fill the new margins with color
	 * @param embedBitDepth The target bit depth for the image to be embedded using the method
	 * 					{@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 					thus allowing values to be added AND subtracted without overflow.
	 * 					Otherwise, the image is assumed to already allow
	 * 					values to be added AND subtracted without overflow.
	 */
	public ImageDifferentialOperatorGeneric(ImageCore image, boolean autoAllowSignedValues,
											int inputImageMarginX, int inputImageMarginY, int inputImageMarginZ,
											BufferEnlargementPolicy enlargementPolicy,
											int embedBitDepth){
		
		this.m_image = image;

		this.m_image.getImageSignPolicyEmbed(true).getImageSignedClamp();
		
		this.m_convolutionMask = new ConvolutionFactoryBaseGeneric(this.m_image)
									.embedOutput(autoAllowSignedValues, embedBitDepth)
									.enlargeInImage(inputImageMarginX, inputImageMarginY, inputImageMarginZ,
													enlargementPolicy
												   )
									.getIdentityMask();
	}
	

	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDifferentialOperator#getConvolutionMask()
	 */
	@Override
	public ConvolutionBase getConvolutionMask(){
		return this.m_convolutionMask;
	}
	
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDifferentialOperator#composeWith(wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase)
	 */
	@Override
	public ConvolutionBase composeWith(ConvolutionBase rightHandSide){
		return this.m_convolutionMask.composeWith(rightHandSide);
	}
	
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDifferentialOperator#getImageConvolved(wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy)
	 */
	@Override
	public ImageCore getImageConvolved(
			ConvolutionNormalizationPolicy normalizationPolicy){
		return this.m_convolutionMask.getImageConvolved(normalizationPolicy);
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDifferentialOperator#finiteDifference(int, int, int, int, int, int)
	 */
	@Override
	public ConvolutionBase finiteDifference(
							int orderX, int orderY, int orderZ,
							int skippingStepX, int skippingStepY, int skippingStepZ){
		
		ConvolutionBase result =
			this.m_convolutionMask.composeWith(
					ConvolutionDifferentialGeneric.finiteDifference(
							this.m_image.getImageSignPolicyEmbed(true),
							orderX, orderY, orderZ,
							skippingStepX, skippingStepY, skippingStepZ
					)
			);
		this.m_convolutionMask = result;
		return result;
	}

	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDifferentialOperator#finiteDifferenceCalibrated(int, int, int, double, double, double)
	 */
	@Override
	public ConvolutionBase finiteDifferenceCalibrated(int orderX, int orderY, int orderZ,
										double realSkippingStepX, 
										double realSkippingStepY, 
										double realSkippingStepZ){

		ConvolutionBase result =
			this.m_convolutionMask.composeWith(
				ConvolutionDifferentialGeneric.finiteDifferenceCalibrated(
						this.m_image.getImageSignPolicyEmbed(true),
						this.m_image.getImageCalibration(),
						orderX, orderY, orderZ,
						realSkippingStepX, realSkippingStepY, realSkippingStepZ
				)
			);
		this.m_convolutionMask = result;
		return result;
	}
	
	
	/**
	 * Constructs a convolution mask to evaluate a finite difference along a given
	 * axis coordinate.
	 * @param axis The coordinate axis along which to evaluate a partial derivative
	 * @param order cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStep The skipping step used for finite differealues to be added AND subtracted without overflow.nce evaluation
	 * @return The convolution mask implementing the finite difference
	 */
	@Override
	public ConvolutionBase partialFiniteDifference(
												CoordinateAxis axis,
												int order, int skippingStep){
		
		ConvolutionBase result =
			this.m_convolutionMask.composeWith(
					ConvolutionDifferentialGeneric.partialFiniteDifference(
							this.m_image.getImageSignPolicyEmbed(true),
							axis, order, skippingStep
					)
			);
		this.m_convolutionMask = result;
		return result;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDifferentialOperator#partialFiniteDifferenceCalibrated(wrapScienceJ.wrapImaJ.core.CoordinateAxis, int, double)
	 */
	@Override
	public ConvolutionBase partialFiniteDifferenceCalibrated(
									CoordinateAxis axis,
									int order, double realSkippingStep){
		ConvolutionBase result =
			this.m_convolutionMask.composeWith(
					ConvolutionDifferentialGeneric.partialFiniteDifferenceCalibrated(
								this.m_image.getImageSignPolicyEmbed(true),
								this.m_image.getImageCalibration(),
								axis, order, realSkippingStep
					)
			);
		this.m_convolutionMask = result;
		return result;
	}
	
	
	/**
	 * Computes an image with unsigned values representing the gradient norm of the input
	 * image.
	 * 
	 * @param skippingStepX Skipping step in X
	 * @param skippingStepY Skipping step in Y
	 * @param skippingStepZ Skipping step in Z
	 * @param outputBitDepth The bit depth for the output image containing the gradient norm
	 * 						(should be able to contain the squared norm for intermediate calculations
	 * 						 to be carried out without overflow)
	 * @param scaleDenominatorIntermediateValues A value by which to divide the squared partial
	 * 											 differentials to ensure the result fits into the
	 * 											 bit depth of outputLinearCombination.
	 * @return The norm of the gradient of the image as an unsigned image.
	 */
	@Override
	public ImageCore getGradientNorm(int skippingStepX, int skippingStepY, int skippingStepZ,
			 							   int outputBitDepth, int scaleDenominatorIntermediateValues
			 							  ){
		
			return DifferentialOperatorGeneric.getGradientNorm(this.m_image.getImageSignPolicyEmbed(true),
							 skippingStepX, skippingStepY, skippingStepZ,
							 outputBitDepth, scaleDenominatorIntermediateValues);
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDifferentialOperator#getGradientNorm(int, int, int, wrapScienceJ.wrapImaJ.core.ImageCore, int)
	 */
	@Override
	public ImageCore getGradientNorm(int skippingStepX, int skippingStepY, int skippingStepZ,
			 							   ImageCore outputImageBuffer, int scaleDenominatorIntermediateValues
			 							  ){
		
		return DifferentialOperatorGeneric.getGradientNorm(
									 this.m_image.getImageSignPolicyEmbed(true),
									 skippingStepX, skippingStepY, skippingStepZ,
									 outputImageBuffer.getImageSignPolicyEmbed(), scaleDenominatorIntermediateValues
									);
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDifferentialOperator#getGradientNormCalibrated(double, double, double, int, int)
	 */
	@Override
	public ImageCore getGradientNormCalibrated(
							double realSkippingStepX, double realSkippingStepY, double realSkippingStepZ,
 							int outputBitDepth, int scaleDenominatorIntermediateValues
 							){
		int skippingStepX = (int) (realSkippingStepX/this.m_image.getImageCalibration().getVoxelWidth());
		if (skippingStepX <= 0){
			skippingStepX = 1;
		}
		int skippingStepY = (int) (realSkippingStepY/this.m_image.getImageCalibration().getVoxelWidth());
		if (skippingStepY <= 0){
			skippingStepY = 1;
		}
		int skippingStepZ = (int) (realSkippingStepZ/this.m_image.getImageCalibration().getVoxelWidth());
		if (skippingStepZ <= 0){
			skippingStepZ = 1;
		}
		return DifferentialOperatorGeneric.getGradientNorm(this.m_image.getImageSignPolicyEmbed(true),
							 skippingStepX, skippingStepY, skippingStepZ,
							 outputBitDepth, scaleDenominatorIntermediateValues);

	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageDifferentialOperator#getGradientNormCalibrated(double, double, double, wrapScienceJ.wrapImaJ.core.ImageCore, int)
	 */
	@Override
	public ImageCore getGradientNormCalibrated(
										double realSkippingStepX, double realSkippingStepY, double realSkippingStepZ,
									   ImageCore outputImageBuffer, int scaleDenominatorIntermediateValues
									  ){
		int skippingStepX = (int) (realSkippingStepX/this.m_image.getImageCalibration().getVoxelWidth());
		if (skippingStepX <= 0){
			skippingStepX = 1;
		}
		int skippingStepY = (int) (realSkippingStepY/this.m_image.getImageCalibration().getVoxelWidth());
		if (skippingStepY <= 0){
			skippingStepY = 1;
		}
		int skippingStepZ = (int) (realSkippingStepZ/this.m_image.getImageCalibration().getVoxelWidth());
		if (skippingStepZ <= 0){
			skippingStepZ = 1;
		}
		
		return DifferentialOperatorGeneric.getGradientNorm(
									 this.m_image.getImageSignPolicyEmbed(true),
									 skippingStepX, skippingStepY, skippingStepZ,
									 outputImageBuffer.getImageSignPolicyEmbed(), scaleDenominatorIntermediateValues
									);
	}
	
}
