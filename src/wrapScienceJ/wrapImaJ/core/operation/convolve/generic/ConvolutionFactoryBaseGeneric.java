/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ConvolutionFactoryBaseGeneric.java                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.convolve.generic;

import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric;

/**
 * @author remy
 *
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase
 */
public class ConvolutionFactoryBaseGeneric implements ConvolutionFactoryBase {
	
	/**
	 * An image, either built after the image underlying this instance, or retrieved
	 * from another instance of this class, which allows for values to be added (to some extent)
	 * without values overflow.
	 */
	protected ImageSignPolicyEmbedGeneric m_inImageSignPolicyEmbed;
	
	
	/**
	 * An image, either built after the image underlying this instance, or retrieved
	 * from another instance of this class, which allows for values to be added (to some extent)
	 * without values overflow.
	 */
	protected ImageSignPolicyEmbedGeneric m_outImageSignPolicyEmbed;
	
	
	/**
	 * Shift coordinates for the voxels of the output image when the input image has been
	 * enlarged by margins (typically to minimize border effects for masks).
	 */
	protected VoxelInt m_shiftOuputMargin;
	
	
	/**
	 * @param image Original reference to the ImageCore implementer 
	 */
	public ConvolutionFactoryBaseGeneric(ImageCore image){
		this.m_shiftOuputMargin = new VoxelInt(0,0,0);
		this.m_inImageSignPolicyEmbed = image.getImageSignPolicyEmbed();
		this.m_outImageSignPolicyEmbed = this.m_inImageSignPolicyEmbed;
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase#getIdentityMask()
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
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase#embedOutput(boolean, int)
	 */
	@Override
	public ConvolutionFactoryBaseGeneric embedOutput(boolean autoAllowSignedValues, int bitDepth){
		
		this.m_outImageSignPolicyEmbed = this.m_outImageSignPolicyEmbed
											 .getImageEmbedding(autoAllowSignedValues, bitDepth);
		return this;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase#enlargeInImage(int, int, int, wrapScienceJ.resource.BufferEnlargementPolicy)
	 */
	@Override
	public ConvolutionFactoryBaseGeneric enlargeInImage(int xMargin, int yMargin, int zMargin,
												 		BufferEnlargementPolicy enlargementPolicy) {
		
		this.m_shiftOuputMargin.setCordinates(xMargin, yMargin, zMargin);
		this.m_inImageSignPolicyEmbed = this.m_inImageSignPolicyEmbed
											.getImageRaw()
											.getImageDomainOperation()
											.getEnlargedImage(xMargin, yMargin, zMargin, enlargementPolicy)
											.getImageSignPolicyEmbed();
		return this;
	}
	
	
	/**
	 * Allows to set the embedded image without any normalization.
	 * 				The image allows linear combinations is embedded into
	 * 				another image allowing values to be added (and possibly subtracted) without
	 * 				overflow.
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added (and possibly subtracted)
	 * 				without overflow.
	 * 				Use {@link ImageSignPolicyEmbedGeneric#embedValues(boolean)} if necessary.
	 */	
	protected void setInImageLinearCombination(ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed){
		this.m_inImageSignPolicyEmbed = inImageSignPolicyEmbed;
	}
	
	
	/**
	 * Allows to set the embedded image without any normalization.
	 * 				The image allows linear combinations is embedded into
	 * 				another image allowing values to be added (and possibly subtracted) without
	 * 				overflow.
	 * @param outputLinearCombinationImage An original image on which to apply the mask.
	 * 				The image must allow values to be added (and possibly subtracted)
	 * 				without overflow.
	 * 				Use {@link ImageSignPolicyEmbedGeneric#embedValues(boolean)} if necessary.
	 */	
	protected void setOutputImageLinearCombination(ImageSignPolicyEmbedGeneric outputLinearCombinationImage){
		this.m_outImageSignPolicyEmbed = outputLinearCombinationImage;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase#getInImageSignPolicyEmbed()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric getInImageSignPolicyEmbed(){
		return this.m_inImageSignPolicyEmbed;
	}
	
	
	 
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase#getCroppedInImage()
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
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionFactoryBase#getOutImageSignPolicyEmbed()
	 */
	public ImageSignPolicyEmbedGeneric getOutImageSignPolicyEmbed(){
		return this.m_outImageSignPolicyEmbed;
	}
	
}
