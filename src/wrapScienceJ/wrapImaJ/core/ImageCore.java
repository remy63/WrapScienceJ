/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageCore.java                                                     * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core;

import java.io.IOException;

import wrapScienceJ.factory.image.ImageCoreFactory;
import wrapScienceJ.metaData.container.MetaDataRetriever;
import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.resource.ResourceCore;
import wrapScienceJ.resource.generic.ModelCoreImageGeneric;
import wrapScienceJ.wrapImaJ.core.operation.ImageConnectedComponents;
import wrapScienceJ.wrapImaJ.core.operation.ImageContrast;
import wrapScienceJ.wrapImaJ.core.operation.ImageConvert;
import wrapScienceJ.wrapImaJ.core.operation.ImageDomainOperation;
import wrapScienceJ.wrapImaJ.core.operation.ImageDomainProjection;
import wrapScienceJ.wrapImaJ.core.operation.ImageDomainTransform;
import wrapScienceJ.wrapImaJ.core.operation.ImageDraw;
import wrapScienceJ.wrapImaJ.core.operation.ImageThresholding;
import wrapScienceJ.wrapImaJ.core.operation.convolve.generic.BlurFactoryGeneric;
import wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ImageDifferentialOperatorGeneric;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric;



/**
 * The ImageCore class is a wrapper for the ImageJ library's image data and its basic operations.
 * The class does not expose all functionalities of ImagePlus, but rather a selection
 * of fundamental operations.
 * 
 * @author Ngoungoure Nsapngue and Remy Malgoures
 */
public interface ImageCore extends ResourceCore {

	/**
	 * @return The concrete Factory for ImageCore of the type of this instance.
	 * @see wrapScienceJ.factory.image.ImageCoreFactory
	 */
	public ImageCoreFactory getPreferedFactory();
	
	/**
	 * Retrieves the width (first coordinate's dimension) of the image in pixels
	 * @return the width of the image
	 */
	public int getWidth();

	/**
	 * Retrieves the height (second coordinate's dimension) of the image in pixels
	 * @return the height of the image
	 */
	public int getHeight();

	/**
	 * Retrieves the depth (number of slices, i.e. third coordinate's dimension) of the image in pixels
	 * @return the depth (number of slices) of the image
	 */
	public int getDepth();
	
	/**
	 * Retrieves the size (number voxels in one coordinate's dimension) of the image in pixels,
	 * for a given coordinate axis.
	 * @param axis The axis for which the edge length of the image (in voxels) should be returned
	 * @return the number of slices of the image
	 */
	public int getSize(CoordinateAxis axis);
	
	/**
	 * @return the number of bits of each color value (8, 16, 24 (RGB) or 32).
	 */
	public int getBitDepth();
	
	/**
	 * Allows to retrieve the maximage gray level given an image representation.
	 * This is 255 for a one byte per component (e.g.GRAY8) and 65535 for a 2 bytes
	 * per component (e.g. GRAY16)
	 * @return The maximal gray level value, given the bit depth of this image
	 */
	public int getWhiteValue();
	
	/**
	 * Sets the z-value of the current slice (for use with {@link #getPixel(int, int)}) and 
	 * {@link #setPixel(int, int, int)}.
	 * For cache optimization reasons, z-values should not be access randomly, but the slices
	 * should better be processed one by one. For the reason, changing the z value, which is set at each
	 * new slice accessed, is a relatively expensive operationand can be made explicit.
	 * @param zCoord the id (z-value) of the new current slice, between zero and (getDepth()-1).
	 */
	public void setCurrentZ(int zCoord);


	/**
	 * @return the z-value of the current slice, between zero and (getDepth()-1).
	 */  
	public int getCurrentZ();


	/**
	 * allows to release the memory of the slices of the image, making
	 * the image unusable.
	 */
	public void deleteMemory();
	
    
	/**
	 * Returns the gray level value of a pixel on the current stack of the image,
	 * previously selected by the #setCurrentSlice(int) method
	 * @param x the (integer valued) x coordinate of the pixel
	 * @param y the (integer valued) y coordinate of the pixel
	 * @return the gray level value of a pixel on the current stack of the image
	 * @see #setCurrentZ(int)
	 */
	public int getPixel(int x, int y);

	/**
	 * Sets the grey level or RGB value of a pixel on the current stack of the image,
	 * previously selected by the {@link #setCurrentZ(int)}) method.
	 * @param x the (integer valued) x coordinate of the pixel
	 * @param y the (integer valued) y coordinate of the pixel
	 * @param value the grey level value to set for the pixel.
	 * @see #setCurrentZ(int)
	 */
	public void setPixel(int x, int y, int value);

	/**
	 * Sets the gray level value of a voxel on the current stack of the image.
	 * The method checks of the z-value (slice id) has changed with respect to the previous access,
	 * and in that case updates the z-value ({@link #setCurrentZ(int)}.
	 * Note that accessing z-values (different slices) randomly is expensive and that slices should
	 * rather be processed one by one.
	 * @param x the (integer valued) x coordinate of the pixel
	 * @param y the (integer valued) y coordinate of the pixel
	 * @param z the (integer valued) z coordinate of the pixel
	 * @param value the gray level value to set for the pixel.
	 */
	public void setVoxel(int x, int y, int z, int value);

	/**
	 * Sets the gray level value of a voxel on the current stack of the image.
	 * The method checks of the z-value (slice id) has changed with respect to the previous access,
	 * and in that case updates the z-value ({@link #setCurrentZ(int)}.
	 * Note that accessing z-values (different slices) randomly is expensive and that slices should
	 * rather be processed one by one.
	 * This method should be used exclusively for methods intended to work with image with floating
	 * point representation of gray levels.
	 * @param x the (integer valued) x coordinate of the pixel
	 * @param y the (integer valued) y coordinate of the pixel
	 * @param z the (integer valued) z coordinate of the pixel
	 * @param floatValue the gray level value to set for the pixel.
	 */
	public void setVoxelFloat(int x, int y, int z, float floatValue);
	
	
	/**
	 * Retrieves the gray level value of a voxel on the current stack of the image.
	 * Note that accessing z-values (different slices) randomly is expensive and that slices should
	 * rather be processed one by one.
	 * This method should be used exclusively for methods intended to work with image with floating
	 * point representation of gray levels.
	 * @param x the (integer valued) x coordinate of the pixel
	 * @param y the (integer valued) y coordinate of the pixel
	 * @param z the (integer valued) z coordinate of the pixel
	 * @return the gray level value of the voxel.
	 */
	public float getVoxelFloat(int x, int y, int z);
	
	
	/**
	 * Retrieves the gray level value of a voxel on the current stack of the image.
	 * Note that accessing z-values (different slices) randomly is expensive and that slices should
	 * rather be processed one by one.
	 * @param x the (integer valued) x coordinate of the pixel
	 * @param y the (integer valued) y coordinate of the pixel
	 * @param z the (integer valued) z coordinate of the pixel
	 * @return the gray level value of the voxel.
	 */
	public int getVoxel(int x, int y, int z);

	
	/**
	 * Sets the gray level value of a voxel on the current stack of the image.
	 * The method checks of the z-value (slice id) has changed with respect to the previous access,
	 * and in that case updates the z-value ({@link #setCurrentZ(int)}.
	 * Note that accessing z-values (different slices) randomly is expensive and that slices should
	 * rather be processed one by one.
	 * @param voxel the voxel at which to set the gray level.
	 * @param value the gray level value to set for the pixel.
	 */
	public void setVoxel(VoxelShort voxel, int value);

	/**
	 * Retrieves the gray level value of a voxel on the current stack of the image.
	 * <strong>Warning:</strong>  the internal use of a double for the grey level value should be removed in the future.
	 * Note that accessing z-values (different slices) randomly is expensive and that slices should
	 * rather be processed one by one.
	 * @param voxel the voxel at which to retrive the gray level.
	 * @return the gray level value of the voxel.
	 */
	public int getVoxel(VoxelShort voxel);

	
	/**
	 * Retrieves the Calibration Data for non isotropic images provided with a length unit
	 * @return the Calibration Data for the Image. Returns (1.0, 1.0, 1.0) if not set.
	 */	
	public ImageCalibration getImageCalibration();
	
	/**
	 * Retrieves an instance of the image provided with format conversion operations.
	 * @return an instance of ImageConvert with the same image data as this instance.
	 */
	public ImageConvert getImageConvert();
	
    /**
     * Retrieves an instance of the class implementing image domains operations
     * such as union, enlargement, cropping, slices extraction or addition, etc.
     * @return an instance of the class implementing the Image Domain Operations
     * 		   involving this instance.
     */
    public ImageDomainOperation getImageDomainOperation();

    /**
     * Retrieves an instance of the class implementing image domains one to one
     * isometric transformations such as symmetries and rotations.
     *
     * @return an instance of the class implementing the Image Domain Operations
     * 		   involving this instance.
     */
    public ImageDomainTransform getImageDomainTransform();

    
	/**
	 * Factory to generate an instance allowing to perform linear combinations
	 * with other images, or similar operations such as set constant values.
	 * 
	 * There are mainly two policies to perform these operations: with signed
	 * values or with unsigned values. Due to unsupported unsigned values in java,
	 * the implementation of some operations differs depending on the policy,
	 * thus leading to two derived classes.
	 * 
	 * Note that no change in the values is made at instantiation. Only some
	 * operations such as absolute values and subtraction have a different
	 * implementation with the unsigned and signed policies.
	 * 
	 * THE USER IS RESPONSIBLE FOR RELEVANT INPUT IMAGE FORMAT FOR SIGNED/UNSIGNED VALUES.
	 * Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * if necessary to ensure this precondition.
	 * 
	 * @see wrapScienceJ.wrapImaJ.core.operation.generic.ImageEmbedSigned
	 * @see wrapScienceJ.wrapImaJ.core.operation.generic.ImageEmbedUnsigned
	 * 
	 * Note that the input image is assumed to hold values allowing, to some extent,
	 * the linear operations to be performed without incurring overflows.
	 * Use {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}
	 * if necessary to ensure this precondition.
	 * 
	 * @param allowSignedValues If true, the gray levels MUST BE arranged to start in the middle of
	 * 							the range of a short value, in order to support signed operations. 
	 * 
	 * @return The instance of the relevant linear combinations implementer
	 */
    public ImageSignPolicyEmbedGeneric getImageSignPolicyEmbed(boolean allowSignedValues);

	/**
	 * Factory to generate an instance allowing to perform linear combinations
	 * with other images, or similar operations such as set constant values.
	 * 
	 * This method is the default for {@link #getImageSignPolicyEmbed(boolean)}
	 * which provides the UNSIGNED POLICY (allowSignedValues = false)
	 * 
	 * There are mainly two policies to perform these operations: with signed
	 * values or with unsigned values. Due to unsupported unsigned values in java,
	 * the implementation of some operations differs depending on the policy,
	 * thus leading to two derived classes.
	 * @see wrapScienceJ.wrapImaJ.core.operation.generic.ImageEmbedSigned
	 * @see wrapScienceJ.wrapImaJ.core.operation.generic.ImageEmbedUnsigned
	 * 	 * 
	 * @return The instance of the relevant linear combinations implementer
	 */
    public ImageSignPolicyEmbedGeneric getImageSignPolicyEmbed();
    
    
    /**
     * Retrieves an instance of the class implementing image domains projection,
     * such as tomographic (gray levels averaging) or volume rendering (maximal gray level)
     * along a projection direction.
     * @return an instance of the class implementing the Image Domain Operations
     * 		   involving this instance.
     */
    public ImageDomainProjection getImageDomainProjection();
    /**
     * Retrieves an instance of the class implementing Contrast and Brightness Adjustment for this image.
     * @return an instance of the class implementing the ImageContrast interface for this image
     */
    public ImageContrast getImageContrast();
    
    /**
     * Retrieves an instance of the class implementing blurring (e.g. Gaussian, Binomial...) for this image.
     * @return an instance of the factory to create instances of convolution masks for this image.
     */
    public BlurFactoryGeneric getImageBlur();

    /**
     * Retrieves an instance of the class implementing connected components labeling,.
     * @return an instance of the class implementing connected components labeling
     * 		   involving this instance.
     */
    public ImageConnectedComponents getImageConnectedComponents();
    
    
    /**
     * Retrieves an instance of the class implementing general convolution Operators
     * (e.g. Blur, Gradient, Laplacian, Partial Differentials...) for this image.
     * The operators can be composed using the Cascade Design Pattern.
	 * @param autoAllowSignedValues If true, the gray levels are shifted to start in the middle of
	 * 							the range of a short value. 
	 * @param inputImageMarginX Margin breadth to enlarge the input image in X
	 * @param inputImageMarginY Margin breadth to enlarge the input image in Y
	 * @param inputImageMarginZ Margin breadth to enlarge the input image in Z
	 * @param enlargementPolicy Policy to fill the new margins with color
 	 * @param embedBitDepth The target bit depth for the image to be embedded using the method
	 * 					{@link ImageSignPolicyEmbedGeneric#getImageEmbedding(boolean, int)}
	 * 					thus allowing values to be added AND subtracted without overflow.
	 * 					Otherwise, the image is assumed to already allow
	 * 					values to be added AND subtracted without overflow.
	 * 
     * @return an instance of the class implementing the ImageDifferential interface for this image
     */
    public ImageDifferentialOperatorGeneric getImageDifferential(
    							boolean autoAllowSignedValues,
								int inputImageMarginX, int inputImageMarginY, int inputImageMarginZ,
								BufferEnlargementPolicy enlargementPolicy,
					    		int embedBitDepth);


    /**
     * Retrieves an instance of the class implementing general convolution Operators
     * (e.g. Blur, Gradient, Laplacian, Partial Differentials...) for this image.
     * The operators can be composed using the Cascade Design Pattern.
     * 
     * The output image is automatically embedded using
     * {@link wrapScienceJ.wrapImaJ.core.operation.ImageSignPolicyEmbed#getImageEmbedding(boolean, int)}.
     * 
     * The gray levels are shifted to start in the middle of the range of a short value
     * (same as {@link #getImageDifferential(boolean, int, int, int, BufferEnlargementPolicy, int)}
     * with autoAllowSignedValues=true). 
     * 
     * @return an instance of the class implementing the ImageDifferential interface for this image
     */
    public ImageDifferentialOperatorGeneric getImageDifferential();

    
    /**
     * Retrieves an instance of the class implementing thresholding (e.g. Otsu) for this image.
     * @return an instance of the class implementing the ImageThresholdinginterface for this image
     */
    public ImageThresholding getImageThresholding();

    /**
     * Retrieves an instance of the class implementing drawing (e.g. text drawing) within the image.
     * @return an instance of the class implementing the ImageDraw for this image
     */
   public ImageDraw getImageDraw();
    
    
    ////////////////////////////////////////////////////////////////////////
    // Overrides from ResourceCore
    ////////////////////////////////////////////////////////////////////////
    
	
    /**
     * @see wrapScienceJ.resource.ResourceCore#setTitle(java.lang.String)
     */
    @Override
	public ImageCore  setTitle(String title);
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#duplicate()
	 */
	@Override
	public ImageCore duplicate();
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#writeToFile(java.lang.String)
	 */
	@Override
	public ImageCore writeToFile(String destinationFilePath) throws IOException;	
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#getMetaData()
	 */
	@Override
	public ModelCoreImageGeneric getMetaData();
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#addMetaData(wrapScienceJ.metaData.container.MetaDataRetriever)
	 */
	@Override
	public ImageCore addMetaData(MetaDataRetriever config);
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#retrieveMetaData()
	 */
	@Override
	public ImageCore retrieveMetaData() throws IOException;
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#mergeMetaData(wrapScienceJ.resource.ResourceCore)
	 */
	@Override
	public ImageCore mergeMetaData(ResourceCore resource);
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#writeToFileWithMetaData(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ImageCore writeToFileWithMetaData(String directory, String basename, String extension) throws IOException;
	

	
} // End of interface
