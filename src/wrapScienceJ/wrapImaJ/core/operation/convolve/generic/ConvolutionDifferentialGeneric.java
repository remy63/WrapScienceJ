/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ConvolutionDifferentialGeneric.java                                * 
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
 * TODO THIS PACKAGE IS CURRENTLY BROKEN AND HAS TO BE FIXED BEFORE USE.
 */
public abstract class ConvolutionDifferentialGeneric implements ConvolutionBase {

	/**
	 * Allows to apply a finite difference to compute a partial differential along the Z axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepZ.
	 * This process is iterated orderZ times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepZ)^orderZ
	 * should be taken care of later if required.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted
	 * 				without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param axis The coordinate axis along which to evaluate a partial derivative
	 * @param order cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStep The skipping step used for finite difference evaluation
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public static ConvolutionBaseGeneric partialFiniteDifference(
												ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
												CoordinateAxis axis, int order, int skippingStep){
		switch (axis){
			case X:
				return partialFiniteDifferenceX(imageSignPolicyEmbed, order, skippingStep);
			case Y:
				return partialFiniteDifferenceY(imageSignPolicyEmbed, order, skippingStep);
			case Z:
				return partialFiniteDifferenceZ(imageSignPolicyEmbed, order, skippingStep);
			default:
				throw new IllegalArgumentException("Unknown Coordinate Axis");
		}
	}
	
	
	/**
	 * Allows to apply a finite difference to compute a partial differential along the Z axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepZ.
	 * This process is iterated orderZ times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepZ)^orderZ
	 * should be taken care of later if required.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted
	 * 				without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param imageCalibration Image Calibration metadata (see {@link ImageCore#getImageCalibration()}).
	 * @param axis The coordinate axis along which to evaluate a partial derivative
	 * @param order cardinality of the support of the smoothing mask; second coordinate
	 * @param realSkippingStep The skipping step used for finite difference evaluation,
	 * 						   considered as a multiple of the calibration's voxel edge's length.
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public static ConvolutionBaseGeneric partialFiniteDifferenceCalibrated(
											ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
											ImageCalibration imageCalibration,
											CoordinateAxis axis, int order, double realSkippingStep){

		int skippingStep;
		switch (axis){
			case X:
				skippingStep = (int) (realSkippingStep/imageCalibration.getVoxelWidth());
				if (skippingStep <= 0){
					skippingStep = 1;
				}
				return partialFiniteDifferenceX(imageSignPolicyEmbed, order, skippingStep);
			case Y:
				skippingStep = (int)(realSkippingStep/imageCalibration.getVoxelHeight());
				if (skippingStep <= 0){
					skippingStep = 1;
				}
				return partialFiniteDifferenceY(imageSignPolicyEmbed, order, skippingStep);
			case Z:
				skippingStep = (int) (realSkippingStep/imageCalibration.getVoxelDepth());
				if (skippingStep <= 0){
					skippingStep = 1;
				}
				return partialFiniteDifferenceZ(imageSignPolicyEmbed, order, skippingStep);
			default:
				throw new IllegalArgumentException("Unknown Coordinate Axis");
		}
	}
	
	
	/**
	 * Allows to apply a finite difference to compute a partial differential along the Z axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepZ.
	 * This process is iterated orderZ times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepZ)^orderZ
	 * should be taken care of later if required.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask, and which
	 * 				is used for both input and output (overwritten).
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param orderZ cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStepZ The skipping step used for finite difference evaluation
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public static ConvolutionBaseGeneric partialFiniteDifferenceZ(
													ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
													final int orderZ, final int skippingStepZ){
		return partialFiniteDifferenceZ(imageSignPolicyEmbed, orderZ, skippingStepZ, 
										imageSignPolicyEmbed,
										false, false, false, 1
									    );
	}

	
	/**
	 * Allows to apply a finite difference to compute a partial differential along the Z axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepZ.
	 * This process is iterated orderZ times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepZ)^orderZ
	 * should be taken care of later if required.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask, and which
	 * 				is used for both input and output (overwritten).
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param orderZ cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStepZ The skipping step used for finite difference evaluation
	 * @param absoluteValue If true, the absolute value of the partial difference.
	 * 
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public static ConvolutionBaseGeneric partialFiniteDifferenceZ(
													ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
													final int orderZ, final int skippingStepZ,
													final boolean absoluteValue){
		return partialFiniteDifferenceZ(imageSignPolicyEmbed, orderZ, skippingStepZ,
										imageSignPolicyEmbed,
										false, absoluteValue, false, 1
									   );
	}
	
	
	/**
	 * Allows to apply a finite difference to compute a partial differential along the Y axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepY.
	 * This process is iterated orderY times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepY)^orderY
	 * should be taken care of later if required.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask, and which
	 * 				is used for both input and output (overwritten).
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param skippingStepY The skipping step used for finite difference evaluation
	 * @param orderY cardinality of the support of the smoothing mask; second coordinate
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public static ConvolutionBaseGeneric partialFiniteDifferenceY(
													ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
													final int orderY, final int skippingStepY){
		return partialFiniteDifferenceY(imageSignPolicyEmbed, orderY, skippingStepY,
										imageSignPolicyEmbed,
										false, false, false, 1
									   );
	}
	
	
	/**
	 * Allows to apply a finite difference to compute a partial differential along the Y axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepY.
	 * This process is iterated orderY times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepY)^orderY
	 * should be taken care of later if required.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask, and which
	 * 				is used for both input and output (overwritten).
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param skippingStepY The skipping step used for finite difference evaluation
	 * @param orderY cardinality of the support of the smoothing mask; second coordinate
	 * @param absoluteValue If true, the absolute value of the partial difference.
	 * 
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public static ConvolutionBaseGeneric partialFiniteDifferenceY(
													ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
													final int orderY, final int skippingStepY,
													final boolean absoluteValue){
		return partialFiniteDifferenceY(imageSignPolicyEmbed, orderY, skippingStepY,
										imageSignPolicyEmbed,
										false, absoluteValue, false, 1
									   );
	}
	
	
	/**
	 * Allows to apply a finite difference to compute a partial differential along the X axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepX.
	 * This process is iterated orderX times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepX)^orderX
	 * should be taken care of later if required.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask, and which
	 * 				is used for both input and output (overwritten).
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param orderX cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStepX  The skipping step used for finite difference evaluation
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public static ConvolutionBaseGeneric partialFiniteDifferenceX(
													ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
													final int orderX, final int skippingStepX){
		return partialFiniteDifferenceX(imageSignPolicyEmbed, orderX, skippingStepX,
										imageSignPolicyEmbed,
										false, false, false, 1
									   );		
	}
	
	
	/**
	 * Allows to apply a finite difference to compute a partial differential along the X axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepX.
	 * This process is iterated orderX times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepX)^orderX
	 * should be taken care of later if required.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask, and which
	 * 				is used for both input and output (overwritten).
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param orderX cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStepX  The skipping step used for finite difference evaluation
	 * @param absoluteValue If true, the absolute value of the partial difference.
	 * 
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public static ConvolutionBaseGeneric partialFiniteDifferenceX(
													ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
			   										final int orderX, final int skippingStepX,
													final boolean absoluteValue){
		return partialFiniteDifferenceX(imageSignPolicyEmbed, orderX, skippingStepX,
										imageSignPolicyEmbed,
										false, absoluteValue, false, 1);		
	}
	
			
	/**
	 * Allows to apply a finite difference to compute a partial differential along the Z axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepZ.
	 * This process is iterated orderZ times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepZ)^orderZ
	 * should be taken care of later if required.
	 * 
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param outImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link ImageSignPolicyEmbedGeneric#embedValues(true)} if necessary.
	 * @param orderZ cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStepZ The skipping step used for finite difference evaluation
	 * @param addToOutput If true, the differential is added to the output
	 * @param absoluteValue If true, the absolute value of the partial difference.
	 * @param squared If true, the finite difference should be squared (for instance to compute gradient norm)
	 * @param scaleDenominator A value by which to divide to ensure the result fits into the bit depth.
	 * 
	 * @note addToOutput in combination with order higher than one is unsupported.
	 * @note If squared is true, then the values are divided by the normalization denominator
	 *       one right away.
	 * @note If addToOutput, then the values normalized right before addition as it is applied.
	 * @note the method has been made protected because its multi-purpose use makes it obscure.
	 * 		 It might be duplicated for optimization (avoiding tests), and then ba added
	 *		 to the public interface.
	 * 
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	protected static ConvolutionBaseGeneric partialFiniteDifferenceZ(
											final ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed,
											final int orderZ, final int skippingStepZ,
											final ImageSignPolicyEmbedGeneric outImageSignPolicyEmbed,
											final boolean addToOutput, final boolean absoluteValue,
											final boolean squared, final int scaleDenominator){
		
		if (orderZ > 1 && addToOutput){
			throw new UnsupportedOperationException("Higher Order Differential cannot be added to output");
		}
		
		return new ConvolutionBaseGeneric(inImageSignPolicyEmbed, outImageSignPolicyEmbed, 
					 					  ((int)(Math.pow(2*skippingStepZ, orderZ)+0.0001)),
					 					  new VoxelInt(0, 0, 0)) {
			/**
			 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric#applyMask()
			 */
			@Override
			public ConvolutionBaseGeneric applyMask() {
				
				ImageCore inputImage = inImageSignPolicyEmbed.getImageRaw();
				ImageCore outputImage = outImageSignPolicyEmbed.getImageRaw();
				
				int [] latestValuesZ = new int[inputImage.getWidth()*inputImage.getHeight()];
				int [] nextValuesZ = new int[inputImage.getWidth()*inputImage.getHeight()];
				
				int zeroValueIn = inImageSignPolicyEmbed.getZero();
				int zeroValueOut = outImageSignPolicyEmbed.getZero();

				for (int i = 1 ; i <= orderZ ; i++){
					
					if (i >= 2 && i < orderZ && inputImage != outputImage){
						outImageSignPolicyEmbed.copyInto(inImageSignPolicyEmbed,
														 this.m_shiftOuputMargin,
														 false
														);
					}
					
					for (int j=0 ; j<skippingStepZ ; j++){
						
						for (int y=0 ; y<inputImage.getHeight() ; y++){
							for (int x=0 ; x<inputImage.getWidth() ; x++){
								latestValuesZ[y*inputImage.getWidth()+x] = zeroValueIn;
							}
						}
						
						for (int y=0 ; y<inputImage.getHeight() ; y++){
							for (int x=0 ; x<inputImage.getWidth() ; x++){
								if (skippingStepZ+j >= inputImage.getDepth()){
									nextValuesZ[y*inputImage.getWidth()+x] = zeroValueIn;
								}else{
									nextValuesZ[y*inputImage.getWidth()+x] = 
														inputImage.getVoxel(x, y, skippingStepZ+j);
								}
							}
						}
			
						for (int z=j ; z<inputImage.getDepth() ; z += skippingStepZ){

							for (int y=0 ; y<inputImage.getHeight() ; y++){
								for (int x=0 ; x<inputImage.getWidth() ; x++){
									
									
									int savePixel = inputImage.getVoxel(x, y, z);
								
									int outValue = zeroValueOut
													 + nextValuesZ[y*inputImage.getWidth()+x]
													 - latestValuesZ[y*inputImage.getWidth()+x];
														
									int xOut = x - this.m_shiftOuputMargin.getX();
									int yOut = y - this.m_shiftOuputMargin.getY();
									int zOut = z - this.m_shiftOuputMargin.getZ();
									if (xOut >= 0 && xOut < outputImage.getWidth() &&
										yOut >= 0 && yOut < outputImage.getHeight() &&
										zOut >= 0 && zOut < outputImage.getDepth()){
									
										if ((absoluteValue|| squared) && outValue < zeroValueOut){
											outValue = 2*zeroValueOut - outValue;
										}
										if (squared){
											outValue *= outValue;
											outValue /= this.getNormalizationDenominator();
										}
										if (addToOutput){
											outValue /= this.getNormalizationDenominator();
											outValue /= scaleDenominator;
											outValue += outputImage.getVoxel(xOut, yOut, zOut);
										}
										
										outputImage.setVoxel(xOut, yOut, zOut, outValue);
									}
									
									latestValuesZ[y*inputImage.getWidth()+x] = savePixel;
									
									nextValuesZ[y*inputImage.getWidth()+x] = 
											z+2*skippingStepZ < inputImage.getDepth() ?
														inputImage.getVoxel(x, y, z+2*skippingStepZ) : zeroValueIn;
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
	 * Allows to apply a finite difference to compute a partial differential along the Y axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepY.
	 * This process is iterated orderY times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepY)^orderY
	 * should be taken care of later if required.
	 * 
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param outImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link ImageSignPolicyEmbedGeneric#embedValues(true)} if necessary.
	 * @param skippingStepY The skipping step used for finite difference evaluation
	 * @param orderY cardinality of the support of the smoothing mask; second coordinate
	 * @param addToOutput If true, the differential is added to the output
	 * @param absoluteValue If true, the absolute value of the partial difference.
	 * @param squared If true, the finite difference should be squared (for instance to compute gradient norm)
	 * @param scaleDenominator A value by which to divide to ensure the result fits into the bit depth.
	 * 
	 * @note addToOutput in combination with order higher than one is unsupported.
	 * @note If squared is true, then the values are divided by the normalization denominator
	 *       one right away.
	 * @note If addToOutput, then the values normalized right before addition as it is applied.
	 * @note the method has been made protected because its multi-purpose use makes it obscure.
	 * 		 It might be duplicated for optimization (avoiding tests), and then ba added
	 *		 to the public interface.
	 * 
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	protected static ConvolutionBaseGeneric partialFiniteDifferenceY(
											final ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed,
											final int orderY, final int skippingStepY,
											final ImageSignPolicyEmbedGeneric outImageSignPolicyEmbed,
											final boolean addToOutput, final boolean absoluteValue,
											final boolean squared, final int scaleDenominator){
		
		if (orderY > 1 && addToOutput){
			throw new UnsupportedOperationException("Higher Order Differential cannot" + 
													" be added to output"
												   );
		}
		
		return new ConvolutionBaseGeneric(inImageSignPolicyEmbed, outImageSignPolicyEmbed,
					 					  ((int)(Math.pow(2*skippingStepY, orderY)+0.0001)),
					 					  new VoxelInt(0, 0, 0)) {
			/**
			 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric#applyMask()
			 */
			@Override
			public ConvolutionBaseGeneric applyMask() {
				
				ImageCore inputImage = inImageSignPolicyEmbed.getImageRaw();
				ImageCore outputImage = outImageSignPolicyEmbed.getImageRaw();
				int [] latestValuesY = new int[inputImage.getWidth()*inputImage.getDepth()];
				int [] nextValuesY = new int[inputImage.getWidth()*inputImage.getDepth()];
		
				int zeroValueIn = inImageSignPolicyEmbed.getZero();
				int zeroValueOut = outImageSignPolicyEmbed.getZero();
				
				for (int i = 1 ; i <= orderY ; i++){
					
					if (i >= 2 && i < orderY && inputImage != outputImage){
						outImageSignPolicyEmbed.copyInto(inImageSignPolicyEmbed,
														 this.m_shiftOuputMargin,
														 false
														);
					}
					
					for (int j=0 ; j<skippingStepY ; j++){
						
						for (int z=0 ; z<inputImage.getDepth() ; z++){
							for (int x=0 ; x<inputImage.getWidth() ; x++){
								latestValuesY[z*inputImage.getWidth()+x] = zeroValueIn;
							}
						}
						
						for (int z=0 ; z<inputImage.getDepth() ; z++){
							for (int x=0 ; x<inputImage.getWidth() ; x++){
								if (skippingStepY+j >= inputImage.getHeight()){
									nextValuesY[z*inputImage.getWidth()+x] = zeroValueIn;
								}else{
									nextValuesY[z*inputImage.getWidth()+x] = 
														inputImage.getVoxel(x, skippingStepY+j, z);
								}
							}
						}
			
						for (int y=j ; y<inputImage.getHeight() ; y += skippingStepY){
							for (int z=0 ; z<inputImage.getDepth() ; z++){
								for (int x=0 ; x<inputImage.getWidth() ; x++){
									
									int savePixel = inputImage.getVoxel(x, y, z);
								
									int outValue = zeroValueOut 
													 + nextValuesY[z*inputImage.getWidth()+x]
													 - latestValuesY[z*inputImage.getWidth()+x];
									
									int xOut = x - this.m_shiftOuputMargin.getX();
									int yOut = y - this.m_shiftOuputMargin.getY();
									int zOut = z - this.m_shiftOuputMargin.getZ();
									if (xOut >= 0 && xOut < outputImage.getWidth() &&
										yOut >= 0 && yOut < outputImage.getHeight() &&
										zOut >= 0 && zOut < outputImage.getDepth()){
									
										if ((absoluteValue|| squared) && outValue < zeroValueOut){
											outValue = 2*zeroValueOut - outValue;
										}
										if (squared){
											outValue *= outValue;
											outValue /= this.getNormalizationDenominator();
										}
										if (addToOutput){
											outValue /= this.getNormalizationDenominator();
											outValue /= scaleDenominator;
											outValue += outputImage.getVoxel(xOut, yOut, zOut);
										}
										
										outputImage.setVoxel(xOut, yOut, zOut, outValue);
									}
									
									latestValuesY[z*inputImage.getWidth()+x] = savePixel;
									
									nextValuesY[z*inputImage.getWidth()+x] = 
														y+2*skippingStepY < inputImage.getHeight() ?
														inputImage.getVoxel(x, y+2*skippingStepY, z) : zeroValueIn;
								
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
	 * Allows to apply a finite difference to compute a partial differential along the X axis.
	 * A centered symmetric difference is computed between point distant from 2*skippingStepX.
	 * This process is iterated orderX times.
	 * No normalization is performed, and therefore dividing by (2*skippingStepX)^orderX
	 * should be taken care of later if required.
	 * 
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param outImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link ImageSignPolicyEmbedGeneric#embedValues(true)} if necessary.
	 * @param orderX cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStepX  The skipping step used for finite difference evaluation
	 * @param addToOutput If true, the differential is added to the output
	 * @param absoluteValue If true, the absolute value of the partial difference.
	 * @param squared If true, the finite difference should be squared (for instance to compute gradient norm)
	 * @param scaleDenominator A value by which to divide to ensure the result fits into the bit depth.
	 * 
	 * @note addToOutput in combination with order higher than one is unsupported.
	 * @note If squared is true, then the values are divided by the normalization denominator
	 *       one right away.
	 * @note If addToOutput, then the values normalized right before addition as it is applied.
	 * @note the method has been made protected because its multi-purpose use makes it obscure.
	 * 		 It might be duplicated for optimization (avoiding tests), and then ba added
	 *		 to the public interface.
	 *
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	protected static ConvolutionBaseGeneric partialFiniteDifferenceX(
											final ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed,
			   								final int orderX, final int skippingStepX,
			   								final ImageSignPolicyEmbedGeneric outImageSignPolicyEmbed,
			   								final boolean addToOutput, final boolean absoluteValue,
			   								final boolean squared, final int scaleDenominator){
			
		if (orderX > 1 && addToOutput){
			throw new UnsupportedOperationException("Higher Order Differential cannot be added to output");
		}
		
		return new ConvolutionBaseGeneric(inImageSignPolicyEmbed, outImageSignPolicyEmbed,
					 					  ((int)(Math.pow(2*skippingStepX, orderX)+0.0001)),
					 					  new VoxelInt(0, 0, 0)) {
			/**
			 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric#applyMask()
			 */
			@Override
			public ConvolutionBaseGeneric applyMask() {
				
				ImageCore inputImage = inImageSignPolicyEmbed.getImageRaw();
				ImageCore outputImage = outImageSignPolicyEmbed.getImageRaw();
				
				int [] latestValuesX = new int[inputImage.getWidth()*inputImage.getDepth()];
				int [] nextValuesX = new int[inputImage.getWidth()*inputImage.getDepth()];
				
				int zeroValueIn = inImageSignPolicyEmbed.getZero();
				int zeroValueOut = outImageSignPolicyEmbed.getZero();
		
				for (int i = 1 ; i <= orderX ; i++){
					
					if (i >= 2 && i < orderX && inputImage != outputImage){
						outImageSignPolicyEmbed.copyInto(inImageSignPolicyEmbed,
														 this.m_shiftOuputMargin,
														 false
														);
					}
					
					for (int j=0 ; j<skippingStepX ; j++){
						
						for (int z=0 ; z<inputImage.getDepth() ; z++){
							for (int y=0 ; y<inputImage.getHeight() ; y++){
								latestValuesX[z*inputImage.getHeight()+y] = zeroValueIn;
							}
						}
						
						for (int z=0 ; z<inputImage.getDepth() ; z++){
							for (int y=0 ; y<inputImage.getHeight() ; y++){
								if (skippingStepX+j >= inputImage.getWidth()){
									nextValuesX[z*inputImage.getHeight()+y] = zeroValueIn;
								}else{
									nextValuesX[z*inputImage.getHeight()+y] = 
														inputImage.getVoxel(skippingStepX+j, y, z);
								}
							}
						}
			
						for (int x=j ; x<inputImage.getWidth() ; x += skippingStepX){
		
							for (int z=0 ; z<inputImage.getDepth() ; z++){
								for (int y=0 ; y<inputImage.getHeight() ; y++){
									
									int savePixel = inputImage.getVoxel(x, y, z);

									int outValue = zeroValueOut
													 + nextValuesX[z*inputImage.getHeight()+y]
													 - latestValuesX[z*inputImage.getHeight()+y];
																		
									int xOut = x - this.m_shiftOuputMargin.getX();
									int yOut = y - this.m_shiftOuputMargin.getY();
									int zOut = z - this.m_shiftOuputMargin.getZ();
									if (xOut >= 0 && xOut < outputImage.getWidth() &&
										yOut >= 0 && yOut < outputImage.getHeight() &&
										zOut >= 0 && zOut < outputImage.getDepth()){
									
										if ((absoluteValue|| squared) && outValue < zeroValueOut){
											outValue = 2*zeroValueOut - outValue;
										}
										if (squared){
											outValue *= outValue;
											outValue /= this.getNormalizationDenominator();
										}
										if (addToOutput){
											outValue /= this.getNormalizationDenominator();
											outValue /= scaleDenominator;
											outValue += outputImage.getVoxel(xOut, yOut, zOut);
										}
										
										outputImage.setVoxel(xOut, yOut, zOut, outValue);
									}

									latestValuesX[z*inputImage.getHeight()+y] = savePixel;
									
									nextValuesX[z*inputImage.getHeight()+y] = 
														x+2*skippingStepX < inputImage.getWidth() ?
														inputImage.getVoxel(x+2*skippingStepX, y, z) : zeroValueIn;

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
	 * Computes a Partial Differential of arbitrary order in each direction by (iterated) finite
	 * differences with skipping steps.
	 * A centered symmetric difference is computed between point distant from 2*skippingStep.
	 * This process is iterated order times for each direction.
	 * 
	 * The results can be normalized according to different policies.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted
	 * 				without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param orderX cardinality of the support of the smoothing mask; first coordinate
	 * @param orderY cardinality of the support of the smoothing mask; second coordinate
	 * @param orderZ cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStepX Skipping step in X
	 * @param skippingStepY Skipping step in Y
	 * @param skippingStepZ Skipping step in Z
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public static ConvolutionBase finiteDifference(
											ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
											int orderX, int orderY, int orderZ,
											int skippingStepX, int skippingStepY, int skippingStepZ){
		
		return partialFiniteDifferenceX(imageSignPolicyEmbed, orderX, skippingStepX)
			 .composeWith(partialFiniteDifferenceY(imageSignPolicyEmbed, orderY, skippingStepY)
			 .composeWith(partialFiniteDifferenceZ(imageSignPolicyEmbed, orderZ, skippingStepZ))
			 );
	}
	
	
	/**
	 * Computes a Partial Differential of the image by (iterated) finite differences
	 * with skipping steps.
	 * A centered symmetric difference is computed between point distant from 2*skippingStep.
	 * This process is iterated order times for each direction.
	 * 
	 * The results can be normalized according to different policies.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted
	 * 				without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
	 * @param orderX cardinality of the support of the smoothing mask; first coordinate
	 * @param orderY cardinality of the support of the smoothing mask; second coordinate
	 * @param orderZ cardinality of the support of the smoothing mask; second coordinate
	 * @param skippingStepX Skipping step in X
	 * @param skippingStepY Skipping step in Y
	 * @param skippingStepZ Skipping step in Z
	 * @param normalizationPolicy  The policy for normalizing the results.
	 * @return The image containing the result with the format corresponding to normalizationPolicy
	 */
	public static ImageCore applyFiniteDifference(
											ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
											int orderX, int orderY, int orderZ,
											int skippingStepX, int skippingStepY, int skippingStepZ,
											ConvolutionNormalizationPolicy normalizationPolicy){
		
		ConvolutionBase mask = finiteDifference(imageSignPolicyEmbed,
												orderX, orderY, orderZ, 
												skippingStepX, skippingStepY, skippingStepZ);
		mask.applyMask();		
		return mask.getImageConvolved(normalizationPolicy);
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
	 * {@link #finiteDifference(ImageSignPolicyEmbedGeneric, int, int, int, int, int, int)}
	 * except the skipping steps are given in (approximately) multiples of the calibration data
	 * (Voxels edges lengths in a given length unit) instead of number of voxels.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted
	 * 				without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * 				if necessary.
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
	public static ConvolutionBase finiteDifferenceCalibrated(
						ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
						ImageCalibration imageCalibration,
						int orderX, int orderY, int orderZ, 
						double realSkippingStepX, double realSkippingStepY, double realSkippingStepZ) {
		
		int skippingStepX = (int) (realSkippingStepX/imageCalibration.getVoxelWidth());
		if (skippingStepX <= 0){
			skippingStepX = 1;
		}
		int skippingStepY = (int)(realSkippingStepY/imageCalibration.getVoxelHeight());
		if (skippingStepY <= 0){
			skippingStepY = 1;
		}
		int skippingStepZ = (int) (realSkippingStepZ/imageCalibration.getVoxelDepth());
		if (skippingStepZ <= 0){
			skippingStepZ = 1;
		}
		
		return finiteDifference(imageSignPolicyEmbed,
								orderX, orderY, orderZ, 
								skippingStepX, skippingStepY, skippingStepZ);
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
	 * {@link #applyFiniteDifference(ImageSignPolicyEmbedGeneric, int, int, int, int, int, int, ConvolutionNormalizationPolicy)}
	 * except the skipping steps are given in (approximately) multiples of the calibration data
	 * (Voxels edges lengths in a given length unit) instead of number of voxels.
	 * 
	 * @param imageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted
	 * 				without overflow.
	 * 				Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)} if necessary.
	 * @param imageCalibration Image Calibration metadata (see {@link ImageCore#getImageCalibration()}).
	 * @param orderX cardinality of the support of the smoothing mask; first coordinate
	 * @param orderY cardinality of the support of the smoothing mask; second coordinate
	 * @param orderZ cardinality of the support of the smoothing mask; second coordinate
	 * @param realSkippingStepX Skipping step in X
	 * @param realSkippingStepY Skipping step in Y
	 * @param realSkippingStepZ Skipping step in Z
	 * @param normalizationPolicy  The policy for normalizing the results.
	 * @return The Convolution Kernel implementing the {@link ConvolutionBaseGeneric#applyMask()}
	 * 		   method allowing to apply a finite difference.
	 */
	public static ImageCore applyFiniteDifferenceCalibrated(
						ImageSignPolicyEmbedGeneric imageSignPolicyEmbed,
						ImageCalibration imageCalibration,
						int orderX, int orderY, int orderZ, 
						double realSkippingStepX, double realSkippingStepY, double realSkippingStepZ,
						ConvolutionNormalizationPolicy normalizationPolicy) {
		
		ConvolutionBase mask = finiteDifferenceCalibrated(
											imageSignPolicyEmbed, imageCalibration,
											orderX, orderY, orderZ, 
											realSkippingStepX, realSkippingStepY, realSkippingStepZ);
		mask.applyMask();		
		return mask.getImageConvolved(normalizationPolicy);
	}
	
}
