/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ImageCoreIJ.java                                                   * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.core;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.io.stream.FileHelper;
import wrapScienceJ.metaData.container.MetaDataRetriever;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.resource.BufferEnlargementPolicy;
import wrapScienceJ.resource.ResourceCore;
import wrapScienceJ.resource.generic.ModelCoreImageGeneric;
import wrapScienceJ.resource.generic.ResourceCoreGeneric;
import wrapScienceJ.wrapImaJ.core.*;
import wrapScienceJ.wrapImaJ.core.operation.ImageContrast;
import wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ImageDifferentialOperatorGeneric;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageConnectedComponentsGeneric;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageDomainProjectionGeneric;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageDrawAwt;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation.BlurFactoryIJ;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation.ImageContrastIJ;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation.ImageConvertIJ;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation.ImageDomainOperationIJ;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation.ImageDomainTransformIJ;
import wrapScienceJ.wrapImaJ.wrappers.imagej.gui.GuiFrameworkIJ;
import wrapScienceJ.wrapImaJ.wrappers.imagej.gui.render.RenderToolIJ;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.OpenDialog;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.File;
import java.io.IOException;



/**
 * The ImageCore class is a wrapper for the ImageJ library's image data and its basic operations.
 * The class does not expose all functionalities of ImagePlus, but rather a selection
 * of fundamental operations.
 * 
 * @author Remy Malgoures
 */
public class ImageCoreIJ extends ResourceCoreGeneric implements ImageCore {

	// ////////////////////////////////////////////////////////
	//  ATTRIBUTES AND CLASS DATA
	// ////////////////////////////////////////////////////////

	/** Denotes the last stack accessed */
	int m_currentZ;
	
	/**
	 * Image Data as an ImagePlus instance using ImageJ
	 */
	protected ImagePlus m_imp;

	/**
	 * Path on dusk to the Image data file
	 */
	protected String m_path;
	
	private ImageThresholdingIJ m_imageThresholding = null;
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getPreferedFactory()
	 */
	@Override
	public ImageCoreFactoryIJ getPreferedFactory(){
		return ImageCoreFactoryIJ.getInstance();
	}
	
	/**
	 * Constructs an instance by loading the image from a source file with default calibration value.
	 * The voxel's edges lengths are all set to 1.0.
	 * The predefined metadata for the resource of type ImageCore is created with the
	 * {@link RetrievalPolicy#TryConfFileNoDialog} policy.
	 * @param path path to the image source file on disk.
	 * @param format_8bits true if the image should be converted () if necessary to 8 bits per pixels and memory should be optimized for this.
	 * @param maximizeValuesRange If true, then the gray levels will be scaled so as to occupy
	 * 							  the broadest possible range in the image representation.
	 * @throws IOException  IOException in case of failure to load the image from file
	 */
	public ImageCoreIJ(String path, boolean format_8bits, boolean maximizeValuesRange) throws IOException {
		this(path, format_8bits, maximizeValuesRange, RetrievalPolicy.TryConfFileNoDialog);
	}
	
	/**
	 * Constructs an instance by loading the image from a source file with default calibration value.
	 * The voxel's edges lengths are all set to 1.0.
	 * @param path path to the image source file on disk.
	 * @param format_8bits true if the image should be converted (if necessary) to 8 bits per pixels and memory should be optimized for this.
	 * @param maximizeValuesRange If true, then the gray levels will be scaled so as to occupy
	 * 							  the broadest possible range in the image representation.
	 * @param retrievalPolicy The policy to retrieve predefined metadata for the resource of type ImageCore.
	 * @see RetrievalPolicy
	 * @see ImageContrast#maximizeValuesRange()
	 * @throws IOException  IOException in case of failure to load the image from file
	 */
	public ImageCoreIJ(String path, boolean format_8bits, boolean maximizeValuesRange,
			RetrievalPolicy retrievalPolicy) throws IOException {
		if (format_8bits){
			loadImage_8bits(path, maximizeValuesRange);
		}else{
			loadImage(path);
			if (maximizeValuesRange){
					getImageContrast().maximizeValuesRange();
			}
		}
		
		setCurrentZ(0);
		setPath(path);
		this.m_modelCoreImage = new ModelCoreImageGeneric(this, retrievalPolicy);
		this.m_imp.killRoi();
	}
	
	/**
	 * Constructs a full copy of this instance.
	 * @param image the image to copy.
	 * @throws IOException  IOException in case of failure to load the image from file
	 */
	public ImageCoreIJ(ImageCoreIJ image) {
		this.m_imp = image.getImp();
		this.m_path = image.getPath();

		this.m_currentZ = 1;
		this.m_modelCoreImage = new ModelCoreImageGeneric(this, image);
    	this.m_imp.killRoi();
	}
	

	/**
	 * The method is provided to make compatibility with legacy code using ImageJ easier.
	 * Note that the purpose of this class is to wrap and hide the ImageJ library.
	 * The use of this method should therefore always be considered transitional, unless intended for private use.
	 * @param imp Image data as an ImageJ image Instance.
	 * @throws IOException  IOException in case of failure to load the image from file
	 */
	public ImageCoreIJ(ImagePlus imp) {
		this.m_imp = imp;
		this.m_path = OpenDialog.getLastDirectory()+File.separator
						+ OpenDialog.getLastName();		

		this.m_currentZ = 1;
		this.m_modelCoreImage = new ModelCoreImageGeneric(this, RetrievalPolicy.TryConfFileThenDialog);
	}
	
	/**
	 * Allocates and returns a black image with given dimensions and type (GRAY8 or GRAY16).
	 * The gray levels are uninitialized, the image has a default title and no meatdata
	 * such as calibration data.
	 * @see ImageCore#getImageCalibration()
	 * @see ModelCoreImageGeneric#addMetaData(wrapScienceJ.metaData.container.MetaDataRetriever)
	 * 
	 * @param width Width of the image in the first voxel coordinate
	 * @param height Height of the image in the second voxel coordinate
	 * @param depth Depth  of the image in the third voxel coordinate
	 * @param bitDepth Number of bits per voxel
	 * @return An instance of ImageCore implementer
	 */
	public static ImageCoreIJ getEmptyImageCore(int width, int height, int depth, int bitDepth){
		
		ImagePlus imp = IJ.createImage("Black Image", width, height, depth, bitDepth);
		ImageCoreIJ blackImage = new ImageCoreIJ(imp);
		return blackImage;
	}
	

    /**
     * @see wrapScienceJ.resource.generic.ResourceCoreGeneric#duplicate()
     */
    public ImageCoreIJ duplicate(){
    	ImageCoreIJ copy = this.getImageDomainOperation()
    						   .crop(0, 0, 0, getWidth(), getHeight(), getDepth());
    	copy.m_path = getPath();
    	this.m_imp.killRoi();
    	return copy;
    }
    
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#deleteMemory()
	 */
	@Override
	public void deleteMemory() {
		int nSlices = this.getDepth();
		int bitDepth = this.getBitDepth();
		if (this.m_imp.getWindow() != null){
			this.m_imp.getWindow().close();
		}
		ImageStack stack = this.m_imp.getStack();
		for (int i=0 ; i< nSlices ; i++){
			stack.deleteLastSlice();
		}
		stack = new ImageStack(1, 1);
		ImageProcessor processor = (bitDepth == 8) ? new ByteProcessor(1, 1) : new ShortProcessor(1, 1);
		stack.addSlice(processor);
		// Old Style imageJ doesn't support empty stacks
		this.m_imp.setStack(stack);
	}

	/**
	 * Allows access to the image data as an ImageJ image instance.
	 * The method is provided to make compatibility with legacy code using ImageJ easier.
	 * Note that the purpose of this class is to wrap and hide the ImageJ library.
	 * The use of this method should therefore always be considered transitional.
	 * @return the ImagePlus data
	 */
	public ImagePlus getImp() {
		return this.m_imp;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageContrast()
	 */
	@Override
	public ImageContrastIJ getImageContrast() {
		return new ImageContrastIJ(this);
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageBlur()
	 */
	public BlurFactoryIJ getImageBlur() {
		return new BlurFactoryIJ(this);
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageConnectedComponents()
	 */
	@Override
	public ImageConnectedComponentsGeneric getImageConnectedComponents() {
		return new ImageConnectedComponentsGeneric(this);
	}

	
    /**
     * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageThresholding()
     */
    public ImageThresholdingIJ getImageThresholding(){
    	if (this.m_imageThresholding == null){
    		this.m_imageThresholding = new ImageThresholdingIJ(this);
    	}
    	return this.m_imageThresholding;
    }

    
	/**
	 * @see wrapScienceJ.resource.generic.ResourceCoreGeneric#getPath()
	 */
	@Override
	public String getPath() {
		return this.m_path;
	}

	/**
	 * @see wrapScienceJ.resource.generic.ResourceCoreGeneric#getTitle()
	 */
	@Override
	public String getTitle() {
		return this.m_imp.getTitle();
	}


	/**
	 * @see wrapScienceJ.resource.generic.ResourceCoreGeneric#setTitle(java.lang.String)
	 */
	@Override
	public ImageCore setTitle(String title) {
		this.m_imp.setTitle(title);
		return this;
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getWidth()
	 */
	@Override
	public int getWidth() {
		return this.m_imp.getWidth();
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getHeight()
	 */
	@Override
	public int getHeight() {
		return this.m_imp.getHeight();
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getDepth()
	 */
	@Override
	public int getDepth() {
		return this.m_imp.getStackSize();
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getSize(wrapScienceJ.wrapImaJ.core.CoordinateAxis)
	 */
	@Override
	public int getSize(CoordinateAxis axis) {
		switch(axis){
		case X: return this.getWidth();
		case Y: return this.getHeight();
		case Z: return this.getDepth();
		default:
			throw new IllegalArgumentException("Unknown coordinate axis");
		}
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getBitDepth()
	 */
	@Override
	public int getBitDepth() {
		return this.m_imp.getBitDepth();
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getWhiteValue()
	 */
	public int getWhiteValue(){
		switch(getBitDepth()){
		case 8 :
			return 255;
		case 16 :
			return 65535;
		default:
			throw new IllegalArgumentException("Maximal gray level cannot be determined" +
											   " for the type of image.");
		}
	}
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#setCurrentZ(int)
	 */
	@Override
	public void setCurrentZ(int zCoord) {
		this.m_currentZ = zCoord;
		this.m_imp.setZ(this.m_currentZ +1);

	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getCurrentZ()
	 */
	@Override
	public int getCurrentZ() {
		return this.m_currentZ;
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getPixel(int, int)
	 */
	@Override
	public int getPixel(int x, int y) {
		return this.m_imp.getProcessor().get(x,y);
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#setPixel(int, int, int)
	 */
	@Override
	public void setPixel(int x, int y, int value) {
		this.m_imp.getProcessor().set(x, y, value);
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#setVoxel(int, int, int, int)
	 */
	@Override
	public void setVoxel(int x, int y, int z, int value) { 
		if (z != this.m_currentZ){
			setCurrentZ(z);
		}
		this.setPixel(x, y, value);
	}	
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#setVoxelFloat(int, int, int, float)
	 */
	public void setVoxelFloat(int x, int y, int z, float floatValue) { 
		if (z != this.m_currentZ){
			setCurrentZ(z);
		}
		this.m_imp.getImageStack().setVoxel(x, y, z, floatValue);
	}	


	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getVoxel(int, int, int)
	 */
	@Override
	public int getVoxel(int x, int y, int z) {
		if (z != this.m_currentZ){
			setCurrentZ(z);
		}		
		return this.getPixel(x, y);
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getVoxelFloat(int, int, int)
	 */
	@Override
	public float getVoxelFloat(int x, int y, int z) {
		if (z != this.m_currentZ){
			setCurrentZ(z);
		}		
		return this.m_imp.getProcessor().getPixelValue(x, y);
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#setVoxel(wrapScienceJ.wrapImaJ.core.VoxelShort, int)
	 */
	@Override
	public void setVoxel(VoxelShort voxel, int value) {
		if (voxel.getZ() != this.m_currentZ){
			setCurrentZ(voxel.getZ());
		}				
		this.m_imp.getProcessor().set(voxel.getX(), voxel.getY(), value);		
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getVoxel(wrapScienceJ.wrapImaJ.core.VoxelShort)
	 */
	@Override
	public int getVoxel(VoxelShort voxel) {
		if (voxel.getZ() != this.m_currentZ){
			setCurrentZ(voxel.getZ());
		}	
		return this.m_imp.getProcessor().get(voxel.getX(), voxel.getY());
	}


	/**
	 * Sets the path to the image source file.
	 * @param path
	 */
	private void setPath(String path) { 
		this.m_path = path;
		if (getMetaData() != null){
			// Emulate "Last Opened Image" properties in an actual imagej plugin.
			String defaultDir = GlobalOptions.getDefaultInputDir();
			String pathString;
			if (getPath() == null){
				pathString = GlobalOptions.getDefaultInputDir();	
			}else{
				pathString = getPath();
			}
			getMetaData().setMetaDataFileName(pathString, "Calibration3D", defaultDir);
		}
	}

	/**
	 * Initializes this instance by loading an image from a file.
	 *
	 * @param path path on disk to an image
	 * @param voxelWidth floating point coordinates representing the X edge length
	 * @param VoxelHeight floating point coordinates representing the Y edge length
	 * @param voxelDepth floating point coordinates representing the Z edge length
	 * @throws IOException in case of failure to load the image from file
	 */
	protected void loadImage(String path) throws IOException {
		
		this.m_imageThresholding = null;
		try {
			int count = 1;	
			ImagePlus currentSlice = IJ.openImage(path, count);
			

			this.m_imp = IJ.createImage(path, 
					currentSlice.getWidth(),
					currentSlice.getHeight(),
					0,
					currentSlice.getBitDepth());
			
			this.m_imp.setCalibration(currentSlice.getCalibration());
			ImageStack stack = this.m_imp.getStack();

			while (currentSlice != null){
				if (count == 1){
					stack.setProcessor(currentSlice.getProcessor().duplicate(), count);
					stack.setSliceLabel("Slice"+1, 1);
				}else{
					stack.addSlice("Slice"+count, currentSlice.getProcessor().duplicate());
				}

				this.m_imp.setZ(count);
				
				currentSlice.flush();
				count++;
				try{
					currentSlice = IJ.openImage(path, count);
				}catch (Exception e){
					this.m_imp.setStack(stack);
					break;
				}
				if (count%10 == 0){
					System.gc();
				}
			}
			
			if (this.m_imp == null) {
				throw new IOException("Load image: could not load image from file.");
			}

		} catch (Exception e) {
			throw new IOException("Load image: could not load image from file. "
					+ e.getMessage() + "(path: " + path +")");
		}
		
		setPath(path);
	}

	/**
	 * Initializes this instance by loading an image from a file.
	 *
	 * @param path path on disk to an image
	 * @param maximizeValuesRange If true, then the gray levels will be scaled so as to occupy
	 * 							  the broadest possible range in the image representation.
	 * @throws IOException in case of failure to load the image from file
	 */
	protected void loadImage_8bits(String path, boolean maximizeValuesRange) throws IOException {
		this.m_imageThresholding = null;
		int minValue = Integer.MAX_VALUE;
		int maxValue = 0;
		int bitDepthInput = 8;
		
		try {
			int count=1;
			boolean stopLoop = false;
			while (!stopLoop){
				try{
					ImagePlus slice = IJ.openImage(path, count);
					count++;
					bitDepthInput = slice.getBitDepth();
					slice.setZ(1);
					ImageProcessor processor = slice.getProcessor();
					for (int y=0 ; y<slice.getHeight() ; y++){
						for (int x=0 ; x<slice.getWidth() ; x++){
							int value = processor.getPixel(x, y);
							if (value < minValue){
								minValue = value;
							}
							if (value > maxValue){
								maxValue = value;
							}
						}
					}
				}catch (Exception e){
					stopLoop = true;
					break;
				}
			}
			
			System.err.println("ImageCoreIJ maximizeValuesRange, minRange: " 
								+ minValue + ", maxRange: " + maxValue);


		} catch (Exception e) {
			throw new IOException("Load image: could not load image from file. "
					+ e.getMessage());
		}
		
		int tableSize = 65536 ; 
		int inverseFactor = (bitDepthInput == 16) ? 256 : 1;
		System.err.println("inverseFactor = " + inverseFactor);
		int[] table = new int[tableSize];

		for (int i=0; i<tableSize; i++) {
			if (i <= minValue){
				table[i] = 0;
			}else{
				if (i >= maxValue){
					table[i] = tableSize-1;
				}else{
					table[i] = (int)(((double)(i-minValue)/(maxValue-minValue))*tableSize)/inverseFactor;
				}
			}
		}		
			
	//	try {
			
			int count = 1;				
			ImagePlus currentSlice = IJ.openImage(path, count);
			

			this.m_imp = IJ.createImage(path, 
					currentSlice.getWidth(),
					currentSlice.getHeight(),
					0,
					8);
			
			this.m_imp.setCalibration(currentSlice.getCalibration());
			ImageStack stack = this.m_imp.getStack();

			while (currentSlice != null){
				this.m_imp.getProcessor().setMinAndMax(minValue, maxValue);
				ByteProcessor bp = new ByteProcessor(currentSlice.getWidth(),
						 										 currentSlice.getHeight());
				ImageProcessor currentSliceProcessor;
				if (maximizeValuesRange){
					currentSliceProcessor = currentSlice.getProcessor();
					if (currentSlice.getType() == ImagePlus.GRAY16){
						for (int j=0 ; j < currentSlice.getHeight() ; j++){
							for (int i=0 ; i < currentSlice.getWidth() ; i++){
								currentSliceProcessor.set(i, j, table[currentSliceProcessor.get(i, j)]);
							}
						}
					}
				}else{
					currentSliceProcessor = currentSlice.getProcessor();
					if (currentSlice.getType() == ImagePlus.GRAY16){
						for (int j=0 ; j < currentSlice.getHeight() ; j++){
							for (int i=0 ; i < currentSlice.getWidth() ; i++){
								currentSliceProcessor.set(i, j, currentSliceProcessor.get(i, j)/256);
							}
						}
					}
				}
				   
				for (int j=0 ; j < currentSlice.getHeight() ; j++){
					for (int i=0 ; i < currentSlice.getWidth() ; i++){
						if (currentSlice.getType() == ImagePlus.GRAY16){
							bp.set(i, j, currentSliceProcessor.get(i, j));	
						}else{
							bp.set(i, j, currentSliceProcessor.get(i, j));	
						}
					}
				}
				if (count == 1){
					stack.setProcessor(bp, count);
					stack.setSliceLabel("Slice"+1, 1);
				}else{
						stack.addSlice("Slice"+count, bp);	
				}
				
				currentSlice.flush();
				count++;
				try{
					currentSlice = IJ.openImage(path, count);
				}catch (Exception e){
					this.m_imp.setStack(stack);
					break;
				}
				if (count%10 == 0){
					System.gc();
				}
			}
			
			if (this.m_imp == null) {
				throw new IOException("Load image: could not load image from file.");
			}

		/*} catch (Exception e) {
			throw new IOException("Load image: could not load image from file. "
					+ e.getMessage());
		}*/
		setPath(path);
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageCalibration()
	 */
	public ImageCalibration getImageCalibration(){
		return new ImageCalibrationIJ(this);
	}
	

	/**
	 * Opens a Graphical Human Interface tool to displays the surface.
	 * @return A Rendering tool allowing to display te image.
	 */
	@Override
	public RenderTool getPreferedRenderTool() {
		return RenderToolIJ.getInstance();
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getFileHelper()
	 */
	@Override
	public FileHelper getFileHelper() {
		return GuiFrameworkIJ.getInstance().getFileHelper();
	}


	/**
	 * Allows to delete the Image Histogram in case it needs to be rebuilt after
	 * changes in colors.
	 */
	public void deleteHistogram(){
		// This is meant to update the data in derived classes (e.g. histogram data) 
		// when colors have changed.
		this.m_imageThresholding = null;
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageConvert()
	 */
	public ImageConvertIJ getImageConvert(){
		return new ImageConvertIJ(this);
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageDomainOperation()
	 */
	@Override
	public ImageDomainOperationIJ getImageDomainOperation() {
		return new ImageDomainOperationIJ(this);
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageDomainProjection()
	 */
	@Override
	public ImageDomainProjectionGeneric getImageDomainProjection() {
		return new ImageDomainProjectionGeneric(this);
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageDomainTransform()
	 */
	@Override
	public ImageDomainTransformIJ getImageDomainTransform() {
		return new ImageDomainTransformIJ(this);
	}
	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageSignPolicyEmbed(boolean)
	 */
	public ImageSignPolicyEmbedGeneric getImageSignPolicyEmbed(boolean allowSignedValues){
		return ImageSignPolicyEmbedGeneric.linearCombinationFactory(this, allowSignedValues);
	}

	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageSignPolicyEmbed()
	 */
	public ImageSignPolicyEmbedGeneric getImageSignPolicyEmbed(){
		return ImageSignPolicyEmbedGeneric.linearCombinationFactory(this, false);
	}


	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageDifferential(boolean, int, int, int, wrapScienceJ.resource.BufferEnlargementPolicy, int)
	 */
	@Override
	public ImageDifferentialOperatorGeneric getImageDifferential(boolean autoAllowSignedValues, 
													int inputImageMarginX, int inputImageMarginY, int inputImageMarginZ,
													BufferEnlargementPolicy enlargementPolicy,
													int embedBitDepth) {
		return new ImageDifferentialOperatorGeneric(this, autoAllowSignedValues, 
													inputImageMarginX, inputImageMarginY, inputImageMarginZ,
													enlargementPolicy,
													embedBitDepth
												   );
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageDifferential()
	 */
	@Override
	public ImageDifferentialOperatorGeneric getImageDifferential() {
		return new ImageDifferentialOperatorGeneric(this, true, 0, 0, 0, BufferEnlargementPolicy.Mirror, getBitDepth());
	}
	
	
	/**
	 * @see wrapScienceJ.resource.generic.ResourceCoreGeneric#writeToFile(java.lang.String)
	 */
	@Override
	public ImageCoreIJ writeToFile(String destinationFilePath) throws IOException {
		if (!destinationFilePath.toLowerCase().endsWith(".tif")){
			throw new IOException(
					"Sorry, only .tif file format is supported for saving an image.");
		}
		IJ.saveAs(this.m_imp, "TIFF", destinationFilePath);
		return this;
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.ImageCore#getImageDraw()
	 */
	@Override
	public ImageDrawAwt getImageDraw() {
		return new ImageDrawAwt(this);
	}
	

	////////////////////////////////////////////////////////////////////////
    // Overrides from ResourceCore
    ////////////////////////////////////////////////////////////////////////
    
	

	/**
	 * @see wrapScienceJ.resource.ResourceCore#addMetaData(wrapScienceJ.metaData.container.MetaDataRetriever)
	 */
	@Override
	public ImageCoreIJ addMetaData(MetaDataRetriever config){
		super.addMetaData(config);
		return this;
	}
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#retrieveMetaData()
	 */
	@Override
	public ImageCoreIJ retrieveMetaData() throws IOException {
		super.retrieveMetaData();
		return this;
	}
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#mergeMetaData(wrapScienceJ.resource.ResourceCore)
	 */
	@Override
	public ImageCoreIJ mergeMetaData(ResourceCore resource){
		super.mergeMetaData(resource);
		return this;
	}
	
	/**
	 * @see wrapScienceJ.resource.ResourceCore#writeToFileWithMetaData(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ImageCoreIJ writeToFileWithMetaData(String directory, String basename, String extension) throws IOException {
		super.writeToFileWithMetaData(directory, basename, extension);
		return this;
	}



} // End of class
