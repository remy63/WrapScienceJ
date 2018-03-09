/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ConvolutionBaseGeneric.java                                       * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.convolve.generic;

import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCalibration;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric;



/**
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase
 */
public abstract class ConvolutionBaseGeneric implements ConvolutionBase {

	/**
	 * Common denominator for all the values of the image.
	 * Corresponds to the product of masses (sums of values) of the masks
	 * that were convolved.
	 */
	private int m_normalizationDenominator;
	
	
	/**
	 * An image, either built after the image underlying this instance, or retrieved
	 * from another instance of this class, which allows for values to be added (to some extent)
	 * without values overflow.
	 */
	private ImageSignPolicyEmbedGeneric m_inImageSignPolicyEmbed;
	
	/**
	 * An image, either built after the image underlying this instance, or retrieved
	 * from another instance of this class, which allows for values to be added (to some extent)
	 * without values overflow.
	 */
	private ImageSignPolicyEmbedGeneric m_outImageSignPolicyEmbed;
	
	/**
	 * Shift coordinates for the voxels of the output image when the input image has been
	 * enlarged by margins (typically to minimize border effects for masks).
	 */
	protected VoxelInt m_shiftOuputMargin;
	
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase#getImageConvolved(wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionNormalizationPolicy)
	 */
	@Override
	public ImageCore getImageConvolved(ConvolutionNormalizationPolicy normalizationPolicy){
		this.applyMask();
		switch (normalizationPolicy){
			case Gray8_divide_256:
				return getOutputImageRaw().getImageConvert().convertToGray8(false);
			case Gray8_No_Normalization:
				return getOutputImageRaw().getImageConvert().convertToGray8Clamp();
			case Gray8_Scale_MaximizeContrast:
				return getOutputImageRaw().getImageConvert()
										  .convertToGray8(true);
			case Gray16_Scale_MaximizeContrast:
				return getOutputImageRaw().getImageContrast()
											  .maximizeValuesRange();
			case Gray16_QuantitativeNormalization:
				return getOutputImageNormalized(false);
			case Gray8_QuantitativeNormalization_Clamp:
				return getOutputImageNormalized(true);
			default: // No Normalization
				return getOutputImageRaw();
		}
	}
	/**
	 * Allows to set the value for the Convolution Mask's common denominator factor
	 * and the embedding image.
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param outImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link ImageSignPolicyEmbedGeneric#embedValues(true)} if necessary.
	 * @param denominator the value to use for the denominator.
	 * @param shiftOuputMargin Margins that were added through enlargement of input buffer.
	 */
	protected ConvolutionBaseGeneric(ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed,
									 ImageSignPolicyEmbedGeneric outImageSignePolicyEmbed,
									 int denominator,
									 VoxelInt shiftOuputMargin){
		
		this.m_shiftOuputMargin = shiftOuputMargin;
		this.m_normalizationDenominator = denominator;
		this.m_inImageSignPolicyEmbed = inImageSignPolicyEmbed;
		this.m_outImageSignPolicyEmbed = outImageSignePolicyEmbed;
	}
	
	
	/**
	 * Allows to set the value for the Convolution Mask's common denominator factor
	 * and the embedding image.
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param outImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link ImageSignPolicyEmbedGeneric#embedValues(true)} if necessary.
	 * @param denominator the value to use for the denominator.
	 */
	/*protected ConvolutionBaseGeneric(ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed,
									 ImageSignPolicyEmbedGeneric outImageSignePolicyEmbed,
									 int denominator){
		
		this(inImageSignPolicyEmbed, outImageSignePolicyEmbed, denominator, new VoxelInt(0, 0, 0));
	}*/
	
	

	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase#getInImageSignPolicyEmbed()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric getInImageSignPolicyEmbed(){
		return this.m_inImageSignPolicyEmbed;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase#getCroppedInImage()
	 */
	@Override
	public ImageCore getCroppedInImage(){
		
		int xMargin = this.m_shiftOuputMargin.getX();
		int yMargin = this.m_shiftOuputMargin.getY();
		int zMargin = this.m_shiftOuputMargin.getZ();
		if (xMargin == 0 && yMargin == 0 && zMargin == 0){
			return this.m_inImageSignPolicyEmbed.getImageRaw();
		}
		return this.m_inImageSignPolicyEmbed
				.getImageRaw()
				.getImageDomainOperation()
				.crop(xMargin, yMargin, zMargin,
				  this.m_inImageSignPolicyEmbed.getImageRaw().getWidth() - xMargin,
				  this.m_inImageSignPolicyEmbed.getImageRaw().getHeight() - yMargin,
				  this.m_inImageSignPolicyEmbed.getImageRaw().getDepth()  - zMargin
				);
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase#getOutImageSignPolicyEmbed()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric getOutImageSignPolicyEmbed(){
		return this.m_outImageSignPolicyEmbed;
	}
	

	/**
	 * Allows to set the value for the Convolution Mask's common denominator factor.
	 * @param denominator the value to use for the denominator.
	 */
	public void setNormalizationDenominator(int denominator){
		this.m_normalizationDenominator = denominator;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase#getNormalizationDenominator()
	 */
	@Override
	public int getNormalizationDenominator(){
		return this.m_normalizationDenominator;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase#getMaskMass()
	 */
	@Override
	public int getMaskMass(){
		return this.m_normalizationDenominator;
	}
	

	/**
	 * @return The identity mask doing nothing with the same input and output buffers as this instance.
	 */
	public ConvolutionBaseGeneric getIdentityMask(){
		
		return new ConvolutionBaseGeneric(getInImageSignPolicyEmbed(), getOutImageSignPolicyEmbed(), 1,
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
	 * Allows to multiply the common denominator factor by some value.
	 * @param factor The value by which the Convolution Mask's common denominator factor
	 * 				 should be multiplied.
	 */
	public void multiplyNormalizationDenominator(int factor){
		this.m_normalizationDenominator *= factor;
	}
	
	
	/**
	 * Normalizes the image for quantitative accuracy by dividing by the masks
	 * accumulated weights. In other words, the values are divided by the
	 * {@link #m_normalizationDenominator} value. This denominator is then set to 1.
	 * 
	 * @return The reference to this instance to allow for use of the cascade pattern.
	 */
	public ConvolutionBaseGeneric normalizeOutputValues(){
		System.err.println("normalizeOutputValues(), normalizationDenominator = "
							+ this.m_normalizationDenominator);
		this.m_outImageSignPolicyEmbed.divideValues(this.m_normalizationDenominator);
		this.m_normalizationDenominator = 1;
		return this;
	}
	
	
	/**
	 * Normalizes the image for quantitative accuracy by dividing by the masks
	 * accumulated weights. In other words, the values are divided by the
	 * {@link #m_normalizationDenominator} value. This denominator is then set to 1.
	 * 
	 * @param convertToGray8 If true, the values will be scaled (unsigned case) or
	 * 						 scaled and shifted by (-Short.MAX_VALUE + Byte.MIN_VALUE)
	 * 						 (signed case) and then clamped, so as to fit in a Byte value.
	 * @return The underlying image after a possible values shift to fit the values
	 *         in a GRAY8 image and format conversion.
	 */
	protected ImageCore getOutputImageNormalized(boolean convertToGray8) {
		normalizeOutputValues();
		if (convertToGray8){
			this.m_outImageSignPolicyEmbed.getImageRaw()
										  .getImageConvert()
										  .convertToGray8Clamp();
		}
		return this.m_outImageSignPolicyEmbed.getImageRaw();
	}
	
	
	/**
	 * This method is meant to be used to retrieve the underlying image
	 * with raw values, so as to get the best contrast. Thi generally doesn't
	 * allow to get quantitatively correct values, unless you know what you are
	 * doing about unsigned values representation.
	 * @return The underlying input image as it is, meant to represent either signed
	 * 		   or unsigned values.
	 */
	protected ImageCore getInputImageRaw(){
		return this.m_inImageSignPolicyEmbed.getImageRaw();
	}
	
	
	/**
	 * This method is meant to be used to retrieve the underlying image
	 * with raw values, so as to get the best contrast. Thi generally doesn't
	 * allow to get quantitatively correct values, unless you know what you are
	 * doing about unsigned values representation.
	 * @return The underlying image as it is, meant to represent either signed
	 * 		   or unsigned values.
	 */
	protected ImageCore getOutputImageRaw(){
		return this.m_outImageSignPolicyEmbed.getImageRaw();
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase#applyMask()
	 */
	@Override
	public abstract ConvolutionBaseGeneric applyMask();
	

	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase#composeWith(wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase)
	 */
	@Override
	public ConvolutionBase composeWith(ConvolutionBase rightHandSide){
		
		if (!(rightHandSide instanceof ConvolutionBaseGeneric)){
			throw new IllegalArgumentException("Convolution between two implementors of "
											  +"ConvolutionBase is not implemented");
		}
		
		final ConvolutionBaseGeneric rhs = (ConvolutionBaseGeneric)rightHandSide;
		
		
		// Prevent overflow of doubled size integers due to growing lengths and denominators
		if (this.m_normalizationDenominator*rightHandSide.getNormalizationDenominator() > 125){
			if (this.m_normalizationDenominator >= rightHandSide.getNormalizationDenominator()){
				// Apply this mask and return rhs
				this.applyMask().normalizeOutputValues();
				return rightHandSide;
			}
			// Else, apply rhs mask and return this mask
			rhs.applyMask().normalizeOutputValues();
			return this;
		}
		
		final ConvolutionBaseGeneric that = this;
		final ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed = this.m_inImageSignPolicyEmbed;
		final ImageSignPolicyEmbedGeneric outImageSignPolicyEmbed = this.m_outImageSignPolicyEmbed;
		
		
		ConvolutionBaseGeneric convolutionResult = new ConvolutionBaseGeneric(
																this.m_inImageSignPolicyEmbed,
																rhs.m_outImageSignPolicyEmbed,
																this.getNormalizationDenominator()
																*rightHandSide.getNormalizationDenominator(),
																this.m_shiftOuputMargin
																) {
			@Override
			public ConvolutionBaseGeneric applyMask() {
				
				System.err.println("composeWith, inImage : " +
									inImageSignPolicyEmbed.getImageRaw().getWidth()
									+ ", " +
									inImageSignPolicyEmbed.getImageRaw().getBitDepth()
								  );
				System.err.println("composeWith, outImage : " +
									outImageSignPolicyEmbed.getImageRaw().getWidth()
									+ ", " +
									outImageSignPolicyEmbed.getImageRaw().getBitDepth()
								  );

				that.applyMask();
				that.getOutImageSignPolicyEmbed()
					.copyInto(that.getInImageSignPolicyEmbed(),
							  that.m_shiftOuputMargin, false
							 );
				rhs.applyMask();

				ConvolutionBaseGeneric resultMask = getIdentityMask();
				
				resultMask.setNormalizationDenominator(getNormalizationDenominator());
				return resultMask;
			}
		};
		return convolutionResult;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	////
	////   EXPOSURE OF THE MAIN METHODS FROM ConvolutionDifferentialGeneric
	////
	/////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Allows to apply a finite difference to compute a partial differential along the Z axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepZ.
	 * This process is iterated orderZ times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepZ)^orderZ
	 * should be taken care of later if required.
	 * 
	 * @param axis The coordinate axis along which to evaluate a partial derivative
	 * @param order cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStep The skipping step used for finite difference evaluation
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */	
	public ConvolutionBaseGeneric partialFiniteDifference(CoordinateAxis axis, int order, int skippingStep){
		return ConvolutionDifferentialGeneric.partialFiniteDifference(this.m_outImageSignPolicyEmbed,
																	  axis, order, skippingStep);
	}
	
	
	/**
	 * Allows to apply a finite difference to compute a partial differential along the Z axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepZ.
	 * This process is iterated orderZ times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepZ)^orderZ
	 * should be taken care of later if required.
	 * 
	 * @param imageCalibration Image Calibration metadata (see {@link ImageCore#getImageCalibration()}).
	 * @param axis The coordinate axis along which to evaluate a partial derivative
	 * @param order cardinality of the support of the smoothing mask; second coordinate
	 * @param realSkippingStep The skipping step used for finite difference evaluation,
	 * 						   considered as a multiple of the calibration's voxel edge's length.
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public ConvolutionBaseGeneric partialFiniteDifferenceCalibrated(
												ImageCalibration imageCalibration,
												CoordinateAxis axis, int order, double realSkippingStep
											){
		return ConvolutionDifferentialGeneric.partialFiniteDifferenceCalibrated(
													this.m_outImageSignPolicyEmbed,
													imageCalibration, axis, order, realSkippingStep);
	}
	
	
	/**
	 * Computes a Partial Differential of arbitrary order in each direction by (iterated) finite
	 * differences with skipping steps.
	 * A centered symmetric difference is computed between point distant from 2*skippingStep.
	 * This process is iterated order times for each direction.
	 * 
	 * The results can be normalized according to different policies.
	 * 
	 * @param orderX cardinality of the support of the smoothing mask; first coordinate
	 * @param orderY cardinality of the support of the smoothing mask; second coordinate
	 * @param orderZ cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStepX Skipping step in X
	 * @param skippingStepY Skipping step in Y
	 * @param skippingStepZ Skipping step in Z
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public ConvolutionBase finiteDifference(int orderX, int orderY, int orderZ,
											int skippingStepX, int skippingStepY, int skippingStepZ){
		return ConvolutionDifferentialGeneric.finiteDifference(
											 this.m_outImageSignPolicyEmbed,
											 orderX, orderY, orderZ,
											 skippingStepX, skippingStepY, skippingStepZ
				);
	}
	
	
	/**
	 * Computes a Partial Differential of the image by (iterated) finite differences
	 * with skipping steps.
	 * A centered symmetric difference is computed between point distant from 2*skippingStep.
	 * This process is iterated order times for each direction.
	 * 
	 * The results can be normalized according to different policies.
	 * The metadata associated to this image is copied to the metadata of the resulting image.
	 *
	 * This method does the same as
	 * {@link #finiteDifference(int, int, int, int, int, int)}
	 * except the skipping steps are given in (approximately) multiples of the calibration data
	 * (Voxels edges lengths in a given length unit) instead of number of voxels.
	 * 
	 * @param imageCalibration Image Calibration metadata (see {@link ImageCore#getImageCalibration()}).
	 * @param orderX cardinality of the support of the smoothing mask; first coordinate
	 * @param orderY cardinality of the support of the smoothing mask; second coordinate
	 * @param orderZ cardinality of the support of the smoothing mask; second coordinate
	 * @param realSkippingStepX Skipping step in X
	 * @param realSkippingStepY Skipping step in Y
	 * @param realSkippingStepZ Skipping step in Z
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public ConvolutionBase finiteDifferenceCalibrated(
							ImageCalibration imageCalibration,
							int orderX, int orderY, int orderZ, 
							double realSkippingStepX, double realSkippingStepY, double realSkippingStepZ) {
		return ConvolutionDifferentialGeneric.finiteDifferenceCalibrated(
					 this.m_outImageSignPolicyEmbed,
					 imageCalibration,
					 orderX, orderY, orderZ,
					 realSkippingStepX, realSkippingStepY, realSkippingStepZ
				 );
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	////
	////   EXPOSURE OF THE MAIN METHODS FROM DifferentialOperatorGeneric
	////
	/////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Computes an image with unsigned values representing the gradient norm of the current
	 * result image.
	 * The current output image (from a previous calculation) is copied to the input image,
	 * performing a scale if necessary for bit depth compatibility.
	 * 
	 * @param skippingStepX Skipping step in X
	 * @param skippingStepY Skipping step in Y
	 * @param skippingStepZ Skipping step in Z
	 * @param scaleDenominatorIntermediateValues A value by which to divide the squared partial
	 * 											 differentials to ensure the result fits into the
	 * 											 bit depth of outputLinearCombination.
	 * @return The norm of the gradient of the image as an unsigned image.
	 */	
	public ImageCore getGradientNorm(int skippingStepX, int skippingStepY, int skippingStepZ,
			 						 int scaleDenominatorIntermediateValues) {
		
		normalizeOutputValues();
		
		if (this.m_inImageSignPolicyEmbed.getImageRaw() != this.m_outImageSignPolicyEmbed.getImageRaw()){
			this.m_outImageSignPolicyEmbed.copyInto(this.m_inImageSignPolicyEmbed,
													this.m_shiftOuputMargin,
													true
												   );
		}
		return DifferentialOperatorGeneric.getGradientNorm(this.m_inImageSignPolicyEmbed,
														   skippingStepX, skippingStepY, skippingStepZ,
														   this.m_outImageSignPolicyEmbed,
														   scaleDenominatorIntermediateValues
														  );
	}
	

	/**
	 * Computes an image with unsigned values representing the gradient norm of the input
	 * image.
	 * 
	 * @param skippingStepX Skipping step in X
	 * @param skippingStepY Skipping step in Y
	 * @param skippingStepZ Skipping step in Z
	 * @param outputImageBuffer An image to store the result, which should be able to store
	 * 							the gray levels without overflow.
	 * @param scaleDenominatorIntermediateValues A value by which to divide the squared partial
	 * 											 differentials to ensure the result fits into the
	 * 											 bit depth of outputLinearCombination.
	 * @return The norm of the gradient of the image as an unsigned image.
	 */
	public ImageCore getGradientNorm(int skippingStepX, int skippingStepY, int skippingStepZ,
									   ImageCore outputImageBuffer, int scaleDenominatorIntermediateValues
									  ){

		return DifferentialOperatorGeneric.getGradientNorm(
				this.m_outImageSignPolicyEmbed,
				 skippingStepX, skippingStepY, skippingStepZ,
				 outputImageBuffer.getImageSignPolicyEmbed(), scaleDenominatorIntermediateValues
				);
	}

	
	/**
	 * Computes an image with unsigned values representing the gradient norm of the current
	 * result image.
	 * The current output image (from a previous calculation) is copied to the input image,
	 * performing a scale if necessary for bit depth compatibility.
	 * 
	 * @param realSkippingStepX Skipping step in X
	 * @param realSkippingStepY Skipping step in Y
	 * @param realSkippingStepZ Skipping step in Z
	 * @param scaleDenominatorIntermediateValues A value by which to divide the squared partial
	 * 											 differentials to ensure the result fits into the
	 * 											 bit depth of outputLinearCombination.
	 * @return The norm of the gradient of the image as an unsigned image.
	 */
	public ImageCore getGradientNormCalibrated(
							double realSkippingStepX, double realSkippingStepY, double realSkippingStepZ,
							int scaleDenominatorIntermediateValues
 							){
		
		normalizeOutputValues();
		
		if (this.m_inImageSignPolicyEmbed.getImageRaw() != this.m_outImageSignPolicyEmbed.getImageRaw()){
			this.m_outImageSignPolicyEmbed.copyInto(this.m_inImageSignPolicyEmbed,
													this.m_shiftOuputMargin,
													true
												   );
		}
		
		ImageCalibration cal = this.m_outImageSignPolicyEmbed.getImageRaw().getImageCalibration();
		int skippingStepX = (int) (realSkippingStepX/cal.getVoxelWidth());
		if (skippingStepX <= 0){
			skippingStepX = 1;
		}
		int skippingStepY = (int) (realSkippingStepY/cal.getVoxelWidth());
		if (skippingStepY <= 0){
			skippingStepY = 1;
		}
		int skippingStepZ = (int) (realSkippingStepZ/cal.getVoxelWidth());
		if (skippingStepZ <= 0){
			skippingStepZ = 1;
		}
		
		//return this.m_inputLinearCombination.getImageRaw();
		
		return DifferentialOperatorGeneric.getGradientNorm(this.m_inImageSignPolicyEmbed,
														   skippingStepX, skippingStepY, skippingStepZ,
														   this.m_outImageSignPolicyEmbed,
														   scaleDenominatorIntermediateValues
														  );
		
	}
	
	
	/**
	 * Computes an image with unsigned values representing the gradient norm of the input
	 * image.
	 * 
	 * @param realSkippingStepX Skipping step in X
	 * @param realSkippingStepY Skipping step in Y
	 * @param realSkippingStepZ Skipping step in Z
	 * @param outputImageBuffer 
	 * @param scaleDenominatorIntermediateValues A value by which to divide the squared partial
	 * 											 differentials to ensure the result fits into the
	 * 											 bit depth of outputLinearCombination.
	 * @return The norm of the gradient of the image as an unsigned image.
	 */
	public ImageCore getGradientNormCalibrated(
									double realSkippingStepX, double realSkippingStepY, double realSkippingStepZ,
									ImageCore outputImageBuffer, int scaleDenominatorIntermediateValues
								){
		applyMask();
		ImageCalibration cal = this.m_outImageSignPolicyEmbed.getImageRaw().getImageCalibration();
		int skippingStepX = (int) (realSkippingStepX/cal.getVoxelWidth());
		if (skippingStepX <= 0){
			skippingStepX = 1;
		}
		int skippingStepY = (int) (realSkippingStepY/cal.getVoxelWidth());
		if (skippingStepY <= 0){
			skippingStepY = 1;
		}
		int skippingStepZ = (int) (realSkippingStepZ/cal.getVoxelWidth());
		if (skippingStepZ <= 0){
			skippingStepZ = 1;
		}
		
		return DifferentialOperatorGeneric.getGradientNorm(this.m_inImageSignPolicyEmbed,
														   skippingStepX, skippingStepY, skippingStepZ,
														   outputImageBuffer.getImageSignPolicyEmbed(),
														   scaleDenominatorIntermediateValues
														  );
	}
}
