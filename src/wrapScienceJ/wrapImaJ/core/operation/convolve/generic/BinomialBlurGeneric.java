/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  BinomialBlurGeneric.java                                          * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.convolve.generic;

import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric;

/**
 * @author remy
 * 
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.ConvolutionBase
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.BlurFactory
 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.BlurFactoryGeneric
 */
public class BinomialBlurGeneric extends ConvolutionBaseGeneric {
	/**
	 * Cardinality of the support of the smoothing mask on one coordinate
	 */
	int m_nPoints;
	
	/**
	 * Skipping step for the binomial mask
	 */
	int m_skippingStep;
	
	/**
	 * The coordinate axis along which to apply the convolution mask.
	 */
	CoordinateAxis m_axis;
	
	
	/**
	 * Constructs a Convolution Mask for Binomial Blur on the image with a mask characterized
	 * by a given support cardinality and a given skipping step (distance between to elements
	 * of the mask support) in each direction.
	 * 
	 * @param inOutImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The buffer is used for both input and output.
	 * 				The image must allow values to be added without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param axis The coordinate axis along which to convolve
	 * @param nPoints cardinality of the support of the smoothing mask; first coordinate
	 * @param skippingStep Skipping step in the direction of the specified axis
	 * @param shiftOuputMargin Margins that were added through enlargement of input buffer.
	 */
	public BinomialBlurGeneric(ImageSignPolicyEmbed inOutImageSignPolicyEmbed,
							   CoordinateAxis axis, int nPoints, int skippingStep,
							   VoxelInt shiftOuputMargin) {
		
		this(inOutImageSignPolicyEmbed, inOutImageSignPolicyEmbed, axis, nPoints, skippingStep, shiftOuputMargin);
	}
	
	
	/**
	 * Constructs a Convolution Mask for Binomial Blur on the image with a mask characterized
	 * by a given support cardinality and a given skipping step (distance between to elements
	 * of the mask support) in each direction.
	 * 
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param outImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)} if necessary.
	 * @param axis The coordinate axis along which to convolve
	 * @param nPoints cardinality of the support of the smoothing mask; first coordinate
	 * @param skippingStep Skipping step in the direction of the specified axis
	 * @param shiftOuputMargin Margins that were added through enlargement of input buffer.
	 */
	public BinomialBlurGeneric(ImageSignPolicyEmbed inImageSignPolicyEmbed,
							   ImageSignPolicyEmbed outImageSignPolicyEmbed,
							   CoordinateAxis axis, int nPoints, int skippingStep,
							   VoxelInt shiftOuputMargin) {
		
		super((ImageSignPolicyEmbedGeneric)inImageSignPolicyEmbed, (ImageSignPolicyEmbedGeneric)outImageSignPolicyEmbed, computeMaskMass(nPoints), shiftOuputMargin);
		this.m_nPoints = nPoints;
		this.m_skippingStep = skippingStep;
		this.m_axis = axis;
	}
	
	
	/**
	 * Constructs a Convolution Mask for Binomial Blur on the image with a mask characterized
	 * by a given support cardinality and a given skipping step (distance between to elements
	 * of the mask support) in each direction.
	 * 
	 * This constructor does the same as 
	 * {@link #BinomialBlurGeneric(ImageSignPolicyEmbed, ImageSignPolicyEmbed, CoordinateAxis, int, int, VoxelInt)}
	 * except that the skipping steps are given in (approximately) multiples of the calibration data
	 * (Voxels edges lengths in a given length unit) instead of number of voxels.
	 * 
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param outImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param nPoints cardinality of the support of the smoothing mask; first coordinate
	 * @param axis The coordinate axis along which to convolve
	 * @param realSkippingStep Skipping step along the considered axis
	 * @param voxelEdgesLength The voxel's scale (edge's length) used as a factor to estimate
	 * 						   actual skipping steps in each direction.
	 * @param shiftOuputMargin Margins that were added through enlargement of input buffer.
	 */
	public BinomialBlurGeneric(ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed,
						ImageSignPolicyEmbedGeneric outImageSignPolicyEmbed,
						CoordinateAxis axis, int nPoints, 
						double realSkippingStep, double voxelEdgesLength,
						VoxelInt shiftOuputMargin) {
		
		super(inImageSignPolicyEmbed, outImageSignPolicyEmbed, computeMaskMass(nPoints), shiftOuputMargin);
		
		int skippingStep = (int) (realSkippingStep/voxelEdgesLength);
		if (skippingStep <= 0){
			skippingStep = 1;
		}
		this.m_nPoints = nPoints;
		this.m_skippingStep = skippingStep;
		this.m_axis = axis;
	}
	

	
	/**
	 * Allows to initialize the Normalization Denominator for the mask
	 * by multiplying (2^nPointsX * 2^nPointsY * 2^nPointsZ).
	 * 
	 * @param nPointsX cardinality of the support of the smoothing mask; first coordinate
	 * @param nPointsY cardinality of the support of the smoothing mask; second coordinate
	 * @param nPointsZ cardinality of the support of the smoothing mask; second coordinate
	 * @return The value of the mask weight (sum of coefficients of the mask)
	 * 
	 * TODO use a bit shift operator
	 */
	protected static int computeMaskMass(int nPoints){
		
		int mass = 1;
		
		for (int i=0; i< nPoints -1 ; i++){
			mass *=2;
		}
		
		return mass;
	}

	

	/**
	 * Builds the mask in the Z coordinate direction
	 * @return A binomial convolution mask allowing to convolve in the Z direction.
	 */
	private ConvolutionBaseGeneric getMask_Z(){
		
		final BinomialBlurGeneric that = this;
		
		return new ConvolutionBaseGeneric(getInImageSignPolicyEmbed(), getOutImageSignPolicyEmbed(), 
										  that.getNormalizationDenominator(), this.m_shiftOuputMargin) {
			/**
			 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric#applyMask()
			 */
			@Override
			public ConvolutionBaseGeneric applyMask() {
				
				ImageCore inputImage = getInImageSignPolicyEmbed().getImageRaw();
				ImageCore outputImage = getOutImageSignPolicyEmbed().getImageRaw();
				
				ImageCore sourceImage = inputImage;
				
				int [] latestValuesZ = new int[inputImage.getWidth()*inputImage.getHeight()];
				for (int i = 1 ; i < that.m_nPoints ; i++){
					
					if (i >= 2 && i <  that.m_nPoints-1){
						sourceImage = outputImage;
					}
					
					for (int j=0 ; j<that.m_skippingStep ; j++){
						
						for (int y=0 ; y<inputImage.getHeight() ; y++){
							for (int x=0 ; x<inputImage.getWidth() ; x++){
								latestValuesZ[y*inputImage.getWidth()+x] = 0;
							}
						}
			
						int shift = 2*(i & 0x00000001) -1; // -1 if i is even, 1 is i is odd
			
						for (int Z=j ; Z<inputImage.getDepth() ; Z += that.m_skippingStep){
							int z = Z;
							if (shift > 0){
								z = inputImage.getDepth() - 1 - Z;
							}
							if (Z>=that.m_skippingStep){
								inputImage.setCurrentZ(z);
								int zOut = z - this.m_shiftOuputMargin.getZ();
								if (zOut >= 0 && zOut < outputImage.getDepth()){
									outputImage.setCurrentZ(zOut);
								}
								for (int y=0 ; y<inputImage.getHeight() ; y++){
									for (int x=0 ; x<inputImage.getWidth() ; x++){
										
										int savePixel;
										
										int xOut = x - this.m_shiftOuputMargin.getX();
										int yOut = y - this.m_shiftOuputMargin.getY();
										if (xOut >= 0 && xOut < outputImage.getWidth() &&
											yOut >= 0 && yOut < outputImage.getHeight() &&
											zOut >= 0 && zOut < outputImage.getDepth()){
											
											if (i<2){
												savePixel = sourceImage.getPixel(x, y);
											}else{
												savePixel = sourceImage.getPixel(xOut, yOut);
											}
											outputImage.setPixel(xOut, yOut, savePixel
																	+ latestValuesZ[y*inputImage.getWidth()+x]);
											
										}else{
											savePixel = inputImage.getPixel(x, y);
										}
									
										latestValuesZ[y*inputImage.getWidth()+x] = savePixel;
									}
								}
							}
						}
					}
				}
				ConvolutionBaseGeneric resultMask = getIdentityMask();
				resultMask.setNormalizationDenominator(getNormalizationDenominator());
				return resultMask;
			}
		};
	}
	
	
	/**
	 * Builds the mask in the Y coordinate direction
	 * @return A binomial convolution mask allowing to convolve in the Y direction.
	 */
	private ConvolutionBaseGeneric getMask_Y(){
		
		final BinomialBlurGeneric that = this;
		
		return new ConvolutionBaseGeneric(getInImageSignPolicyEmbed(), getOutImageSignPolicyEmbed(),
										  getNormalizationDenominator(), this.m_shiftOuputMargin) {
			/**
			 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric#applyMask()
			 */
			@Override
			public ConvolutionBaseGeneric applyMask() {
				
				ImageCore inputImage = getInImageSignPolicyEmbed().getImageRaw();
				ImageCore outputImage = getOutImageSignPolicyEmbed().getImageRaw();
				
				ImageCore sourceImage = inputImage;

				int [] latestValuesY = new int[inputImage.getWidth()*inputImage.getDepth()];
				for (int i = 1 ; i < that.m_nPoints ; i++){
					
					if (i >= 2 && i <  that.m_nPoints-1){
						sourceImage = outputImage;
					}

					
					for (int j=0 ; j<that.m_skippingStep ; j++){
						for (int z=0 ; z<inputImage.getDepth() ; z++){
							
							for (int x=0 ; x<inputImage.getWidth() ; x++){
								latestValuesY[z*inputImage.getWidth()+x] = 0;
							}
						}
						int shift = 2*(i & 0x00000001) -1;// -1 if i is even, 1 is i is odd
			
						for (int z=0 ; z<inputImage.getDepth() ; z++){
							inputImage.setCurrentZ(z);
							int zOut = z - this.m_shiftOuputMargin.getZ();
							if (zOut >= 0 && zOut < outputImage.getDepth()){
								outputImage.setCurrentZ(zOut);
							}
							for (int Y=j ; Y<inputImage.getHeight() ; Y+=that.m_skippingStep){
								if (Y>=that.m_skippingStep){
									
									int y = Y;
									if (shift > 0){
										y = inputImage.getHeight() - 1 - Y;
									}

									for (int x=0 ; x<inputImage.getWidth() ; x++){
									
										int savePixel;
									
										int xOut = x - this.m_shiftOuputMargin.getX();
										int yOut = y - this.m_shiftOuputMargin.getY();
										if (xOut >= 0 && xOut < outputImage.getWidth() &&
											yOut >= 0 && yOut < outputImage.getHeight() &&
											zOut >= 0 && zOut < outputImage.getDepth()){
											
											if (i<2){
												savePixel = sourceImage.getPixel(x, y);
											}else{
												savePixel = sourceImage.getPixel(xOut, yOut);
											}

											outputImage.setPixel(xOut, yOut, savePixel
														+ latestValuesY[z*inputImage.getWidth()+x]);
											
										}else{
											savePixel = inputImage.getPixel(x, y);
										}

										latestValuesY[z*inputImage.getWidth()+x] = savePixel;

									}
								}
							}
						}
					}
				}
				ConvolutionBaseGeneric resultMask = getIdentityMask();
				resultMask.setNormalizationDenominator(getNormalizationDenominator());
				return resultMask;
			}
		};
	}

	
	/**
	 * Builds the mask in the X coordinate direction
	 * @return A binomial convolution mask allowing to convolve in the X direction.
	 */
	private ConvolutionBaseGeneric getMask_X(){
		
		final BinomialBlurGeneric that = this;
		
		return new ConvolutionBaseGeneric(getInImageSignPolicyEmbed(), getOutImageSignPolicyEmbed(), 
										  getNormalizationDenominator(), this.m_shiftOuputMargin) {
			/**
			 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric#applyMask()
			 */
			@Override
			public ConvolutionBaseGeneric applyMask() {
				
				System.err.println("getMask_X, ShiftOuputMargin = " + this.m_shiftOuputMargin);
				
				ImageCore inputImage = getInImageSignPolicyEmbed().getImageRaw();
				ImageCore outputImage = getOutImageSignPolicyEmbed().getImageRaw();
				
				ImageCore sourceImage = inputImage;
				

				int [] latestValuesX = new int[inputImage.getHeight()*inputImage.getDepth()];
				for (int i = 1 ; i < that.m_nPoints ; i++){
					
					if (i >= 2 && i <  that.m_nPoints-1){
						sourceImage = outputImage;
					}
					
					for (int j=0 ; j<that.m_skippingStep ; j++){

						for (int z=0 ; z<inputImage.getDepth() ; z++){
							for (int y=0 ; y<inputImage.getHeight() ; y++){
								latestValuesX[z*inputImage.getHeight()+y] = 0;
							}
						}
			
						int shift = 2*(i & 0x00000001) -1; // -1 if i is even, 1 is i is odd
						
						for (int z=0 ;z<inputImage.getDepth() ; z++){
							inputImage.setCurrentZ(z);
							for (int y=0 ; y<inputImage.getHeight() ; y++){
								for (int X=j ; X<inputImage.getWidth() ; X+=that.m_skippingStep){
									
									if (X >= that.m_skippingStep){
										int x = X;
										
										if (shift > 0){
											x = inputImage.getWidth() - 1 - X;
										}
			
										int savePixel;
										
										int xOut = x - this.m_shiftOuputMargin.getX();
										int yOut = y - this.m_shiftOuputMargin.getY();
										int zOut = z - this.m_shiftOuputMargin.getZ();
										if (xOut >= 0 && xOut < outputImage.getWidth() &&
											yOut >= 0 && yOut < outputImage.getHeight() &&
											zOut >= 0 && zOut < outputImage.getDepth()){
											
											if (i<2){
												savePixel = sourceImage.getPixel(x, y);
											}else{
												savePixel = sourceImage.getPixel(xOut, yOut);
											}

											outputImage.setVoxel(xOut, yOut, zOut, savePixel
																	+ latestValuesX[z*inputImage.getHeight()+y]);
										}else{
											savePixel = inputImage.getPixel(x, y);
										}

										latestValuesX[z*inputImage.getHeight()+y] = savePixel;
									}
								}
							}
						}
					}
				}

				ConvolutionBaseGeneric resultMask = getIdentityMask();
				resultMask.setNormalizationDenominator(getNormalizationDenominator());
				return resultMask;
			}
		};
	}

	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric#applyMask()
	 */
	@Override
	public ConvolutionBaseGeneric applyMask() {
		switch (this.m_axis){
			case X:
				return getMask_X().applyMask();
			case Y:
				return getMask_Y().applyMask();
			case Z:
				return getMask_Z().applyMask();
			default:
				throw new IllegalArgumentException("Unknown Coordinate Axis");
		}
	}
	
}
