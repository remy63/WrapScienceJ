/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ConvolutionBase.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.convolve;


import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase;


/**
 * @author remy
 *
 * This is a base class for convolution masks and smoothing masks. Those include
 * smoothing and differential operators.
 * The class is intended to allow for integer only calculation and lazy computation,
 * especially when composing masks, where masks with integer only implementation are
 * applied when required by risk of overflow. 
 */
public interface ConvolutionBase {
	
	/**
	 * Allows to apply the mask on the image data and to normalize the results according to some policy.
	 * The policy can aim at accuracy of the results, maximizing contrast, or preservation
	 * of the values of an integer mask as is (without dividing by the weight of the mask).
	 * 
	 * @see ConvolutionNormalizationPolicy
	 * 
	 * @param normalizationPolicy The policy for normalizing the results.
	 * @return The resulting image with the convolution applied, as well as a normalization policy.
	 */
	public ImageCore getImageConvolved(ConvolutionNormalizationPolicy normalizationPolicy);
	
	
	/**
	 * Allows to retrieve the input image without any normalization.
	 * @return The embedded image in this current state.
	 */
	public ImageSignPolicyEmbed getInImageSignPolicyEmbed();
	
	
	/**
	 * Allows to retrieve the input image without any normalization.
	 * @return The embedded image in this current state.
	 */
	public ImageCore getCroppedInImage();
	
	
	/**
	 * Allows to retrieve the output image without any normalization.
	 * @return The embedded image in this current state.
	 */
	public ImageSignPolicyEmbed getOutImageSignPolicyEmbed();
	

	
	/**
	 * Getter to retrieve value of the value for the Convolution Mask's common denominator factor
	 * @return The Convolution Mask's common denominator factor
	 */
	public int getNormalizationDenominator();
	
	
	/**
	 * Getter to retrieve value of the value for the Convolution Mask's common denominator factor.
	 * Same as {@link ConvolutionBase#getNormalizationDenominator()}
	 * @return The Convolution Mask's total mass (sum of the coefficients)
	 */
	public int getMaskMass();
	

	
	/**
	 * Allows to create a mask by composing two masks one after another on an image.
	 * @param rightHandSide The mask to compose with this mask.
	 * @return A mask which, when applied on an image, consists in
	 */
	public ConvolutionBase composeWith(ConvolutionBase rightHandSide);
	
	
	/**
	 * Allows to Convolve the mask of this instance with the image underlying this instance.
	 * This is done by embedding the image (if necessary) and storing it in an attribute,
	 * which can then be retrieved after normalization.
	 * 
	 * Note that this operation might become protected (and therefore removed from this interface)
	 * 		 in the future because its use might be obscure and lead to errors.
	 * 
	 * @return The reference to this instance to allow for use of the cascade pattern.
	 */
	public ConvolutionBase applyMask();
	

	
}
