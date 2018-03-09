/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageSignPolicyEmbedGeneric.java                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.generic;


import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed;


/**
 * @author remy
 *
 */
public abstract class ImageSignPolicyEmbedGeneric implements ImageSignPolicyEmbed {
	
	/**
	 * Underlying image on which operations are performed
	 */
	protected ImageCore m_image;
	
	
	/**
	 * Factory to generate an instance allowing to perform linear combinations
	 * with other images, or similar operations such as set constant values.
	 * 
	 * There are mainly two policies to perform these operations: with signed
	 * values or with unsigned values. Due to unsupported unsigned values in java,
	 * the implementation of some operations differs depending on the policy,
	 * thus leading to two derived classes.
	 * @see ImageEmbedSigned
	 * @see ImageEmbedUnsigned
	 * 
	 * Note that the input image is assumed to hold values allowing, to some extent,
	 * the linear operations to be performed without incurring overflows.
	 * Use {@link #embedValues(allowSignedValues)} if necessary to ensure this precondition.
	 * 
	 * @param image The image to process
	 * @param allowSignedValues If true, the gray levels are shifted to start in the middle of
	 * 							the range of a short value.
	 * @return The instance of the relevant linear combinations implementer
	 */
	public static ImageSignPolicyEmbedGeneric linearCombinationFactory(ImageCore image,
																  boolean allowSignedValues){
		if (allowSignedValues){
			return new ImageEmbedSigned(image);
		}
		return new ImageEmbedUnsigned(image);
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageRaw()
	 */
	@Override
	public ImageCore getImageRaw(){
		return this.m_image;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#isSigned()
	 */
	@Override
	public abstract boolean isSigned();
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getZero()
	 */
	@Override
	public abstract int getZero();
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getMinValue()
	 */
	@Override
	public int getMinValue(){
		return - getZero();
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getMinValue()
	 */
	@Override
	public int getMaxValue(){
		return this.m_image.getWhiteValue() - getZero();
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#setAllBlack()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric setAllBlack(){
		setConstantValue(0);
		return this;
	}

	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#setAllWhite()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric setAllWhite(){
		setConstantValue(this.m_image.getWhiteValue());
		return this;		
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#setAllZero()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric setAllZero(){
		setConstantValue(getZero());
		return this;
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)
	 */
	@Override
	public ImageSignPolicyEmbedGeneric getImageEmbedding(boolean allowSignedValues, int bitDepth){
		
		ImageCore image = getImageRaw();
		
		ImageCore imageNewBits;
		
		if (image.getBitDepth() == bitDepth){
			imageNewBits = image;
		}else{
			imageNewBits = image.getPreferedFactory()
								.getEmptyImageCore(
									image.getWidth(), image.getHeight(),  image.getDepth(), 
									bitDepth
								);
		}
		
		imageNewBits.setTitle("Image embedded");
		imageNewBits.getMetaData().merge(image.getMetaData());
		
		int zeroValue = getZero();
		
		ImageSignPolicyEmbedGeneric result = imageNewBits.getImageSignPolicyEmbed(allowSignedValues);
		
		int zeroValueNewBits = result.getZero();
		
		int inverseFactor = (image.getBitDepth() == bitDepth) ? (bitDepth == 16 ? 512 : 32) 
														   : ((image.getBitDepth() < bitDepth) ? 1 
																   							: 8192
														   );
		
		System.err.println("getImageEmbedding, zeroValue : " + zeroValue + ", zeroValueNewBits : " + zeroValueNewBits);
		System.err.println("getImageEmbedding, inverseFactor : " + inverseFactor);
		
		for (int z=0 ; z<image.getDepth() ; z++){
			imageNewBits.setCurrentZ(z);
			image.setCurrentZ(z);
			for (int y=0 ; y<image.getHeight() ; y++){
				for (int x=0 ; x<image.getWidth() ; x++){
					int value = zeroValueNewBits + (image.getPixel(x, y) - zeroValue)/inverseFactor;
					imageNewBits.setPixel(x, y, value);			
				}
			}
		}
		return result;
	}
		
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageSignedClamp()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric getImageSignedClamp() {
		
		int shiftToSigned = this.m_image.getBitDepth() == 8 ? -Byte.MIN_VALUE
															  : -Short.MIN_VALUE;

		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					int value = shiftToSigned + this.m_image.getPixel(x, y);

					this.m_image.setPixel(x, y, value);					
				}
			}
		}
		return this;
	}
		
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImagePositivePart()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric getImagePositivePart() {

		int zeroValue = getZero();

		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					int value = this.m_image.getPixel(x, y);
					if (value < zeroValue){
						value = zeroValue;
					}
					this.m_image.setPixel(x, y, value);					
				}
			}
		}
		return this;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageNegativePart()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric getImageNegativePart() {
		
		int zeroValue = getZero();

		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					int value = this.m_image.getPixel(x, y);
					if (value < zeroValue){
						value = 2*zeroValue - value;
					}else{
						value = 0;
					}
					this.m_image.setPixel(x, y, value);					
				}
			}
		}
		return this;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageAbsoluteValue()
	 */
	@Override
	public ImageSignPolicyEmbedGeneric getImageAbsoluteValue() {

		int zeroValue = getZero();
		
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					int value = this.m_image.getPixel(x, y);
					if (value < zeroValue){
						value = 2*zeroValue - value;
					}
					this.m_image.setPixel(x, y, value);
				}
			}
		}
		return this;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#setConstantValue(int)
	 */
	@Override
	public ImageSignPolicyEmbedGeneric setConstantValue(int value){
		if (value < 0 || value > this.m_image.getWhiteValue()){
			throw new IllegalArgumentException("Gray level must be between 0 and "
											   + this.m_image.getWhiteValue());
		}
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					this.m_image.setPixel(x, y, value);
				}
			}
		}
		return this;
	}
	/**
	 * The value is shifted by subtracting a value (Short.MAX_VALUE-Short.MIN_VALUE)/2 = 32767.
	 * in order to store a small (in absolute value) short value to be stored in the middle
	 * of a short.
	 * 
	 * @param value a signed value with an absolute value less than or equal to Short.MAX_VALUE.
	 * @param doShift If false, the input value is returned unchanged
	 * @return value+(Short.MAX_VALUE-Short.MIN_VALUE)/2 if doShift is true, and value otherwise.
	 */
	protected static int shiftValueToCentreShortRange(int value, boolean doShift){
		return doShift ? value+Short.MIN_VALUE: value;
	}
	
	
	/**
	 * The value is shifted by adding a value (Short.MAX_VALUE-Short.MIN_VALUE)/2 = 32767.
	 * shift back a small (in absolute value) short centered around the middle of the range
	 * of the short type to be centered around zero.
	 * 
	 * @param value an unsigned value with an absolute value less than or equal to
	 * 				(Short.MAX_VALUE-Short.MIN_VALUE-1).
	 * @param doShift If false, the input value is returned unchanged
	 * @return value-(Short.MAX_VALUE-Short.MIN_VALUE)/2 if doShift is true, and value otherwise.
	 */
	protected static int shiftValueFromCentreShortRange(int value, boolean doShift) {
		return doShift ? value-Short.MIN_VALUE : value;
	}
	
	
	/**
	 * The value is shifted by adding a value 125.
	 * in order to store a small (in absolute value) short value to be stored in the middle
	 * of a short.
	 * 
	 * @param value a signed value with an absolute value less than or equal to Short.MAX_VALUE.
	 * @param doShift If false, the input value is returned unchanged
	 * @return value+125 if doShift is true, and value otherwise.
	 */
	protected static int shiftValueToCentreByteRange(int value, boolean doShift){
		return doShift ? value-126: value;
	}
	
	
	/**
	 * The value is shifted by subtracting a value 125.
	 * shift back a small (in absolute value) short centered around the middle of the range
	 * of the short type to be centered around zero.
	 * 
	 * @param value an unsigned value with an absolute value less than or equal to
	 * 				(Short.MAX_VALUE-Short.MIN_VALUE-1).
	 * @param doShift If false, the input value is returned unchanged
	 * @return value-125 if doShift is true, and value otherwise.
	 */
	protected static int shiftValueFromCentreByteRange(int value, boolean doShift) {
		return doShift ? value+126 : value;
	}
	
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#multiplyValues(double)
	 */
	@Override
	public ImageSignPolicyEmbedGeneric multiplyValues(double factor){
	
		int zeroValue = getZero();
		
		System.err.println("multiplyValues, zeroValue : " + zeroValue);
	
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					int value = this.m_image.getPixel(x, y) - zeroValue;
					value = (int)(value*factor);
					this.m_image.setPixel(x, y, value + zeroValue);					
				}
			}
		}
		return this;
	}	



	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#multiplyValues(int)
	 */
	@Override
	public ImageSignPolicyEmbedGeneric multiplyValues(int factor){
	
		int zeroValue = getZero();
			
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					int value = this.m_image.getPixel(x, y) - zeroValue;
					value = value*factor;
					this.m_image.setPixel(x, y, value + zeroValue);					
				}
			}
		}
		return this;
	}	

	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#divideValues(int)
	 */
	@Override
	public ImageSignPolicyEmbedGeneric divideValues(int denominator) {
		
		int zeroValue = getZero();
	
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					int value = this.m_image.getPixel(x, y) - zeroValue;
					value /= denominator;
					this.m_image.setPixel(x, y, value + zeroValue);					
				}
			}
		}
		return this;
	}	
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#divideValues(double)
	 */
	@Override
	public ImageSignPolicyEmbedGeneric divideValues(double denominator) {
		return multiplyValues(1.0/denominator);
	}	

	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#copyInto(ImageSignPolicyEmbed, VoxelInt, boolean)
	 */
	@Override
	public ImageSignPolicyEmbed copyInto(ImageSignPolicyEmbed destImageSignPolicyEmbed, VoxelInt shiftMargin,
										 boolean scaleForBitDepth){
		
		ImageCore destImage = destImageSignPolicyEmbed.getImageRaw();
		
		if (destImage == getImageRaw()){
			return destImageSignPolicyEmbed;
		}
	
		int zeroValue = getZero();
		int zeroValueDest = destImageSignPolicyEmbed.getZero();
		
		int bitDepth = getImageRaw().getBitDepth();
		int destBitDepth = destImageSignPolicyEmbed.getImageRaw().getBitDepth();
		
		int inverseFactor = (!scaleForBitDepth || bitDepth == destBitDepth || bitDepth < destBitDepth) ? 1 : 256;

		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			
			this.m_image.setCurrentZ(z);
			if (z+shiftMargin.getZ()< 0 || z+shiftMargin.getZ() >= destImage.getDepth()){
				continue;
			}
			destImage.setCurrentZ(z+shiftMargin.getZ());
			
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				if (y+shiftMargin.getY()< 0 || y+shiftMargin.getY() >= destImage.getHeight()){
					continue;
				}
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					if (x+shiftMargin.getX()<0 || x+shiftMargin.getX() >= destImage.getWidth()){
						continue;
					}
					
					destImage.setPixel(x+shiftMargin.getX(), y+shiftMargin.getY(), 
									   ((this.m_image.getPixel(x, y)-zeroValue)/inverseFactor)+zeroValueDest
									  );
				}
			}
		}
		
		destImage.mergeMetaData(this.m_image);
		
		return destImageSignPolicyEmbed;
	}


	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#copyInto(ImageSignPolicyEmbed, VoxelInt, boolean)
	 */
	@Override
	public ImageSignPolicyEmbed copyFrom(ImageSignPolicyEmbed destImageSignPolicyEmbed, VoxelInt shiftMargin,
										 boolean scaleForBitDepth){
		
		ImageCore destImage = destImageSignPolicyEmbed.getImageRaw();
		
		if (destImage == getImageRaw()){
			return this;
		}
		
		int zeroValue = getZero();
		int zeroValueDest = destImageSignPolicyEmbed.getZero();
		
		int bitDepth = getImageRaw().getBitDepth();
		int destBitDepth = destImageSignPolicyEmbed.getImageRaw().getBitDepth();
		
		int valuesFactor = (!scaleForBitDepth || bitDepth == destBitDepth || bitDepth < destBitDepth) ? 1 : 256;

		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			
			this.m_image.setCurrentZ(z);
			if (z+shiftMargin.getZ()< 0 || z+shiftMargin.getZ() >= destImage.getDepth()){
				continue;
			}
			destImage.setCurrentZ(z+shiftMargin.getZ());
			
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				if (y+shiftMargin.getY()< 0 || y+shiftMargin.getY() >= destImage.getHeight()){
					continue;
				}
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					if (x+shiftMargin.getX()<0 || x+shiftMargin.getX() >= destImage.getWidth()){
						continue;
					}
					this.m_image.setPixel(x, y, 
										  zeroValue + valuesFactor*
															(destImage.getPixel(x+shiftMargin.getX(), y+shiftMargin.getY())
															 -zeroValueDest
															)
										 );

				}
			}
		}
		
		this.m_image.mergeMetaData(destImage);
		
		return this;
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#addValues(wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed)
	 */
	@Override
	public ImageSignPolicyEmbed addValues(ImageSignPolicyEmbed imageToAdd) {
		
		int zeroValueImageToAdd = imageToAdd.getZero();
		
		ImageCore imgAdd = imageToAdd.getImageRaw();
		
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			imgAdd.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					
					int value = this.m_image.getPixel(x, y);
					value += (imageToAdd.getImageRaw().getPixel(x, y) - zeroValueImageToAdd);
					if (value < 0){
						value = 0;
					}
					this.m_image.setPixel(x, y, value);	
					
				}
			}
		}
		return this;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#subtractValues(wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed)
	 */
	@Override
	public ImageSignPolicyEmbed subtractValues(ImageSignPolicyEmbed imageToSubtract) {
		
		int zeroValueImageToSubtract = imageToSubtract.getZero();
		
		ImageCore imgSubtract = imageToSubtract.getImageRaw();
		
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			this.m_image.setCurrentZ(z);
			imgSubtract.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					
					int value = this.m_image.getPixel(x, y);
					value -= (imageToSubtract.getImageRaw().getPixel(x, y) - zeroValueImageToSubtract);
					if (value < 0){
						value = 0;
					}
					this.m_image.setPixel(x, y, value);	
					
				}
			}
		}
		return this;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getOpposite()
	 */
	@Override
	public ImageSignPolicyEmbed getOpposite() {
		
		int zeroValueDouble = 2*getZero();
		
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			
			this.m_image.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					
					int value = zeroValueDouble-this.m_image.getPixel(x, y);
					if (value < 0){
						value=0;
					}
					this.m_image.setPixel(x, y, value);	
					
				}
			}
		}
		return this;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getReversedValues()
	 */
	@Override
	public ImageSignPolicyEmbed getReversedValues() {
		
		int maxValue = getZero() + getMaxValue();
		
		for (int z=0 ; z<this.m_image.getDepth() ; z++){
			
			this.m_image.setCurrentZ(z);
			for (int y=0 ; y<this.m_image.getHeight() ; y++){
				for (int x=0 ; x<this.m_image.getWidth() ; x++){
					
					int value = maxValue - this.m_image.getPixel(x, y);
					this.m_image.setPixel(x, y, value);	
					
				}
			}
		}
		return this;
	}
	
}
