/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageEmbedUnsigned.java                                           * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.generic;

import wrapScienceJ.wrapImaJ.core.ImageCore;

/**
 * @author remy
 *
 */
public class ImageEmbedUnsigned extends ImageSignPolicyEmbedGeneric {

	/**
	 * Note that the input image is assumed to hold values allowing, to some extent,
	 * the linear operations to be performed without incurring overflows.
	 * Use {@link #getImageEmbedding(boolean, int)} if necessary to ensure this precondition.
	 * 
	 * @param image The image to process
	 */
	public ImageEmbedUnsigned(ImageCore image) {
		this.m_image = image;
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric#isSigned()
	 */
	@Override
	public boolean isSigned() {
		return false;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric#getZero()
	 */
	@Override
	public int getZero() {
		return 0;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric#getImagePositivePart()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric getImagePositivePart() {
		return this;
	}



	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric#getImageAbsoluteValue()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric getImageAbsoluteValue() {
		return this;
	}

}	
