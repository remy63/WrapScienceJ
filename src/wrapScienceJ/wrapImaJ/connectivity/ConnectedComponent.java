/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ConnectedComponent.java                                            * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.connectivity;


import java.util.ArrayList;

import wrapScienceJ.wrapImaJ.connectivity.filtering.ComponentRemovalPredicate;
import wrapScienceJ.wrapImaJ.connectivity.filtering.predefined.ComponentRemovalNone;
import wrapScienceJ.wrapImaJ.core.CoordinateAxis;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.VoxelShort;


/**
 * Allows connected components labeling in a binary image.
 * This abstract class has derived classes to label components slice by slice in 2D
 * or by considering components in 3D images.
 * Some filtering criteria for filtering the connected components are supported :
 * <ul>
 * 	<li>Minimal volume for the components (taking into account the calibration)</li>
 * 	<li>Remove connected components that touch the boundary of the image.</li>
 * </ul>
 * An option in the filtering method allows to modify the input image to set random grey
 * levels on each component.
 *
 * RESTRICTIONS
 * The total number of connected components labeled must remain smaller
 * than the number of labels available which is less than 2* Short.MAX_VALUE -2.
 * This can be guaranteed by imposing the connected components considered
 * have at least (total number of voxels)/(2* Short.MAX_VALUE -2) voxels.
 * 
 */
public abstract class ConnectedComponent {
	/**
	 * Input Image. 
	 * The image can be modified by the filtering in the filterComponents() method
	 */
	protected ImageCore m_inputImage;
	
	/**
	 * Color of the foreground in the input binary image
	 */
	protected int m_foregroundColor;

	/**
	 * Components Information (cardinality, label, voxel representative, etc.)
	 */
	protected ArrayList<ComponentInfo> m_compInfo;
	
	/**
	 * Array containing the label of each voxel
	 */
	private short[][] m_labels;
	
	/**
	 * Image width for labels array access withour method call
	 */
	private int m_width;
	
	/**
	 * Once components are labeled, their memory footprint might be reduced to a byte.
	 * In that process, they will be stored in an ImageCore. The {@link #getLabel(int, int, int)}
	 * and {@link #setLabel(int, int, int, short)} methods then use that image.
	 * 
	 */
	private ImageCore m_labelsImage;
	
	/**
	 * Volume of a voxel (used for component size thresholding)
	 */
	protected double m_voxelVolume;

	/**
	 * Policy to label the connected components, which can be 3D components (in which
	 * adjacency is considered in 3D) labeling, or slice by slice against a given axis.
	 */
	private LabelingPolicy m_labelingPolicy;
	
	
	/**
	 * @return The image which is used as input for labeling
	 */
	public ImageCore getImage(){
		return this.m_inputImage;
	}
	
	
	/**
	 * retrieves the label of a voxel (after calling doComponents)
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 * @return the label of the input voxel (0 if not in any connected component)
	 */
	public short getLabel(int x, int y, int z){
		if (this.m_labelsImage != null){
			return (short)this.m_labelsImage.getVoxel(x, y, z);
		}
		return this.m_labels[z][y*this.m_width+x];
	}

	/**
	 * Returns the short label value corresponding to an unlabeled voxel.
	 * This "undefined" label is smaller than all the used labels which are contiguous.
	 * @return Short.MIN_VALUE
	 */
	protected static short undefinedLabel(){
		return Short.MIN_VALUE;
	}
	
	/**
	 * Returns a non negative index to map the set of used labels (greater than
	 * {@link #undefinedLabel()}) to consecutive non-negative unsigned shorts,
	 * to be used as keys in an indexed list (e.g. {@link ArrayList}).
	 * 
	 * @param label A used connected component label (grater than {@link Short.MIN_VALUE})
	 * @return A non negative integer to be used as index
	 */
	protected static int indexFromLabel(short label){
		return -Short.MIN_VALUE+label-1;
	}
	/**
	 * retrieves the label of a voxel (after calling doComponents)
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 * @param label the label of the input voxel (0 if not in any connected component)
	 */
	protected void setLabel(int x, int y, int z, short label){
		this.m_labels[z][y*this.m_width+x] = label;
	}
	
	/**
	 * Initializes the fields of this instance (to be called in derived classes constructors)
	 * The input image must have type Gray8.
	 * @param inputImage : input (probably binary) image, the components of which to compute.
	 * @param foregroundColor label of the 1's in the input image ip
	 * @throws IllegalStateException if the image doesn't have type GRAY8.
	 */
	protected ConnectedComponent(ImageCore inputImage, int foregroundColor) throws IllegalStateException {
		if (inputImage.getBitDepth() != 8){
			throw new IllegalStateException("Images must have type GRAY8 (typically binary)" +
											" for their connected components to be labeled.");
		}
		
		this.m_inputImage = inputImage;

		this.m_width = this.m_inputImage.getWidth();
		
		this.m_voxelVolume =  this.m_inputImage.getImageCalibration().getVolume();

		this.m_foregroundColor = foregroundColor;
		this.m_labels = new short[this.m_inputImage.getDepth()][this.m_inputImage.getWidth()*this.m_inputImage.getHeight()];
		for (int k=0 ; k<this.m_inputImage.getDepth() ; k++){
			for (int j=0 ; j<this.m_inputImage.getHeight() ; j++){
				for (int i=0 ; i<this.m_inputImage.getWidth() ; i++){
					this.m_labels[k][j*this.m_inputImage.getWidth()+i] = Short.MIN_VALUE;
				}
			}
		}
		
		this.m_compInfo = new ArrayList<ComponentInfo>();    
	}

	
	
	/**
	 * Constructs a ConnectedComponent derived class instance with relevant 
	 * dimension (2D or 3D) and labels the components.
	 * The input image must have type Gray8.
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 * </ul>
	 * @param inputImage : input (probably binary) image, the components of which to compute.
	 * @param labelingPolicy Can be 3D components labeling, or slice by slice against a given axis.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by filtering
	 * @param thresholdComponentVolume minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @param setRandomColors true if the colors of the original image should be set according to the components labels.
	 * @return an instance of a concrete derived class for ConnectedComponent
	 * @throws IllegalStateException in case the number of connected components exceeds the Short.MAX_VALUE (32767)
	 * 	 							 or if the image doesn't have type GRAY8.
	 */
	public static ConnectedComponent getLabeledComponents(
												ImageCore inputImage,
												LabelingPolicy labelingPolicy,
												int foregroundColor,
												boolean removeBorderComponent,
												double thresholdComponentVolume,
												boolean setRandomColors
												) throws IllegalStateException {
		
		return getLabeledComponents(
								inputImage,
								labelingPolicy,
								foregroundColor,
								removeBorderComponent,
								thresholdComponentVolume,
								new ComponentRemovalNone(), // Don't remove components
								true, // keep predicate (Don't remove components)
								setRandomColors
								);	
	}
	
	/**
	 * Allows to swap back the axis on the labels and the input image after 2D slice by slice
	 * components labeling against an axis other than Z.
	 * @param inputImage The input image for the components labeling process
	 * @param swappedImage The image with axis swapped for labeling against another axis than Z
	 * 					   and which should be copied back to the input image in case the colors changed.
	 * @param labelingPolicy Can be 3D components labeling, or slice by slice against a given axis.
	 * 
	 */
	private void swapAxisWithZ(LabelingPolicy labelingPolicy, ImageCore inputImage, ImageCore swappedImage){
		CoordinateAxis axisToSwap = null;
		if (labelingPolicy == LabelingPolicy.X_2D){
			axisToSwap = CoordinateAxis.X;
		}
		if (labelingPolicy == LabelingPolicy.Y_2D){
			axisToSwap = CoordinateAxis.Y;
		}
		if (axisToSwap != null){
			ImageCore labelsSwapped = this.getLabelsAsImage()
										 .getImageDomainTransform()
										 .getAxisSwapped(axisToSwap, CoordinateAxis.Z);
			this.m_labelsImage = labelsSwapped;
			
			swappedImage.getImageDomainTransform().copyAxisSwapped(inputImage, axisToSwap, CoordinateAxis.Z);
			this.m_inputImage = inputImage;
			
			this.m_width = inputImage.getWidth();
			
			for (ComponentInfo compInfo: this.m_compInfo){
				compInfo.getAxisSwapped(axisToSwap, CoordinateAxis.Z);
			}
		}
	}
	
	/**
	 * Constructs a ConnectedComponent derived class instance with relevant dimension
	 * (2D or 3D) and labels the components.
	 * The input image must have type Gray8.
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 * </ul>
	 * @param inputImage : input (probably binary) image, the components of which to compute.
	 * @param labelingPolicy Can be 3D components labeling, or slice by slice against a given axis.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by filtering
	 * @param thresholdComponentVolume minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @param removalPredicate a predicate according to which components should be filtered out
	 * @param keepPredicate true if we should keep the components with a voxel satisfying removalPredicate, and false if we should remove the components with a voxel satisfying removalPredicate 
	 * @param setRandomColors true if the colors of the original image should be set according to the components labels.
	 * @return an instance of a concrete derived class for ConnectedComponent
	 * @throws IllegalStateException  in case the number of connected components exceeds the Short.MAX_VALUE (32767)
	 * 	 							 or if the image doesn't have type GRAY8.
	 */
	public static ConnectedComponent getLabeledComponents(
												ImageCore inputImage,
												LabelingPolicy labelingPolicy,
												int foregroundColor,
												boolean removeBorderComponent, 
												double thresholdComponentVolume,
												ComponentRemovalPredicate removalPredicate,
												boolean keepPredicate,
												boolean setRandomColors
												) throws IllegalStateException {
		
		ConnectedComponent cc;
		ImageCore swappedImage = null;
		switch(labelingPolicy){
			case Full3D:
				cc = new ConnectedComponent3D(inputImage, foregroundColor);
				break;
			case Full3D_noSecureSize:
				cc = new ConnectedComponent3D(inputImage, foregroundColor);
				break;
			case X_2D :
				swappedImage = inputImage.getImageDomainTransform()
										 .getAxisSwapped(CoordinateAxis.X, CoordinateAxis.Z);
				cc = new ConnectedComponent2D(swappedImage, foregroundColor);
				break;
			case Y_2D :
				swappedImage = inputImage.getImageDomainTransform()
										 .getAxisSwapped(CoordinateAxis.Y, CoordinateAxis.Z);
				cc = new ConnectedComponent2D(swappedImage, foregroundColor);
				break;
			case Z_2D :
				cc = new ConnectedComponent2D(inputImage, foregroundColor);
				break;
			default:
				throw new IllegalArgumentException("Unknown labeling policy");
		}
		
		cc.m_labelingPolicy = labelingPolicy;
		
		// Limit lowest volume of components so that labels can fit into a short
		int lowThresholdNbPoints;
		if (labelingPolicy == LabelingPolicy.Full3D){
			// The total number of connected components labeled must remain smaller
			// than the number of labels available which is less than 2* Short.MAX_VALUE -2.
			// This can be guaranteed by imposing the connected components considered
			// have at least (total number of voxels)/(2* Short.MAX_VALUE -2) voxels.
			lowThresholdNbPoints = (inputImage.getWidth()*inputImage.getHeight()
									*inputImage.getDepth())/(2* Short.MAX_VALUE -2);
		}else{
			if (labelingPolicy == LabelingPolicy.Full3D_noSecureSize){
				lowThresholdNbPoints = 0;
			}else{
				lowThresholdNbPoints = (inputImage.getWidth()*inputImage.getHeight())/(2*Short.MAX_VALUE-2);
			}
		}

		cc.doLabelConnectedComponent(lowThresholdNbPoints);

		cc.filterComponents(removeBorderComponent, thresholdComponentVolume, 
				removalPredicate,
				keepPredicate,
				setRandomColors);
		
		cc.swapAxisWithZ(labelingPolicy, inputImage, swappedImage);
		
		return cc;
	}	
	
	/**
	 * retrieves the number of connected components (as constructed by doComponents)
	 * @return the number of components detected.
	 */
	public int getNumberOfComponents(){
		return this.m_compInfo.size();
	}


	/**
	 * Retrieves the informations about a component for its label
	 * may return null if the component does not exist or has been erased because below volume threshold or on the border.
	 * @param label the component label (Can be negative and can be shifted to access a list index)
	 * @return the ComponentInfo instance of the component with the considered label. returns null if the component info is undefined
	 * @see #indexFromLabel(short)
	 */
	public ComponentInfo getComponentInfo(short label){
		try{
			if (label == Short.MIN_VALUE){
				return null;
			}
			ComponentInfo ci = this.m_compInfo.get(indexFromLabel(label));
			if (ci.getnumberOfPoints() ==0){
				return null;
			}
			return ci;
		}catch (IllegalStateException e){
			return null;
		}
	}
	
	/**
	 * Retrieves the informations about a component for its label index.
	 * may return null if the component does not exist or has been erased because below volume threshold or on the border.
	 * @param labelIndex the component label (non negative integer)
	 * @return the ComponentInfo instance of the component with the considered label. returns null if the component info is undefined
	 */
	public ComponentInfo getComponentInfo(int labelIndex){
		try{
			if (labelIndex < 0){
				return null;
			}			
			ComponentInfo ci = this.m_compInfo.get(labelIndex);
			if (ci.getnumberOfPoints() ==0){
				return null;
			}
			return ci;
		}catch (IllegalStateException e){
			return null;
		}
	}

	/**
	 * Retrieve a collection of voxels, with one voxel in each (possibly filtered) connected component.
	 * @return the array of voxel representatives of components
	 */
	public ArrayList<VoxelInt> getVoxelRepresentants(){
		ArrayList<VoxelInt> tabVoxels = new ArrayList<VoxelInt>();
		for (ComponentInfo ci : this.m_compInfo){
			if (ci.getnumberOfPoints() > 0){
				tabVoxels.add(ci.getRepresentant());
			}
		}
		return tabVoxels;
	}

	/**
	 * labels the connected components of the input image (attribute this.m_ip)
	 * @param lowThresholdNbPoints Lower limit of number of points below which
	 * 							   connected components will be dropped
	 * @throws IllegalStateException in case the number of connected components exceeds the Short.MAX_VALUE (32767)
	 */ 	
	abstract void doLabelConnectedComponent(int lowThresholdNbPoints) throws IllegalStateException;

	/**
	 * Converts the labels of the voxels to gray levels in a regular image
	 * for further processing.
	 * If the largest label fits into a Byte, the output image has type GRAY8.
	 * Otherwise, the output image has type GRAY16.
	 * 
	 * @return An image with gray level values giving the connected component label
	 *         for each voxel.
	 */
	public ImageCore getLabelsAsImage(){
		
		if (this.m_labelsImage != null){
			return this.m_labelsImage;
		}

		// Note that in the case of a slice by slice component labeling,
		// the labels are re-used from slice to slice, so that the
		// maximal label value can be smaller than this.m_compInfo.size()
		int maxLabel = undefinedLabel();
		for (int i = 0 ; i < this.m_compInfo.size() ; ++i){
			if (this.m_compInfo.get(i).getLabel() > maxLabel){
				maxLabel = this.m_compInfo.get(i).getLabel();
			}
		}
		
		// If the number of components fits into a byte, we can save memory
		// which might be worthwhile.
		int bitDepth = 16;
		if (maxLabel - undefinedLabel() < 256){
			bitDepth = 8;
		}
		
		this.m_labelsImage = this.m_inputImage.getPreferedFactory()
											  .createFromRegularArray(this.m_labels,
																	  this.m_inputImage.getWidth(),
																	  bitDepth);
		this.m_labelsImage.mergeMetaData(this.m_inputImage)
						  .setTitle("Image composed of Labels");

		this.m_labels = null;
		System.gc();

		return this.m_labelsImage;
	}		

	/**
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 *    <li>Possibly keep (or remove) the components with a voxel satisfying a predicate</li>
	 * </ul>
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by filtering
	 * @param thresholdComponentVolume minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @param setRandomColors true if the colors of the original image should be set according to the components labels.
	 */
	protected void filterComponents(
			boolean removeBorderComponent, 
			double thresholdComponentVolume, 
			boolean setRandomColors) {
		
		filterComponents(removeBorderComponent, 
						 thresholdComponentVolume, 
						 new ComponentRemovalNone(),
						 true,
						 setRandomColors);
	}

	/**
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 *    <li>Possibly keep (or remove) the components with a voxel satisfying a predicate</li>
	 * </ul>
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by filtering
	 * @param thresholdComponentVolume minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @param removalPredicate a predicate according to which components should be filtered out
	 * @param keepPredicate true if we should keep only the components with at least one voxel satisfying removalPredicate, and false if we should remove the components with at least one voxel satisfying removalPredicate 
	 * @param setRandomColors true if the colors of the original image should be set according to the components labels.
	 */
	protected void filterComponents(boolean removeBorderComponent, 
									double thresholdComponentVolume, 
									ComponentRemovalPredicate removalPredicate,
									boolean keepPredicate,
									boolean setRandomColors) {
		

		ArrayList<Boolean> existsVoxelSatisfyingPredicate = new ArrayList<Boolean>();
		for (int i = 0 ; i < this.m_compInfo.size() ; ++i){
			existsVoxelSatisfyingPredicate.add(Boolean.valueOf(false));
		}
		
		// Check the predicate
		VoxelShort voxelToTest = new VoxelShort();
		for (voxelToTest.setZ((short)0); voxelToTest.getZ() < this.m_inputImage.getDepth(); voxelToTest.incrementCoord(2)){
			for (voxelToTest.setY((short)0); voxelToTest.getY() < this.m_inputImage.getHeight(); voxelToTest.incrementCoord(1)){
				for (voxelToTest.setX((short)0); voxelToTest.getX() < this.m_inputImage.getWidth(); voxelToTest.incrementCoord(0)){
					// get the voxel's label
					short label = getLabel(voxelToTest.getX(), voxelToTest.getY(), voxelToTest.getZ());
					if (label > undefinedLabel()){ // if not a background voxel
						ComponentInfo ci = this.m_compInfo.get(indexFromLabel(label));
						// test the predicate
						if(removalPredicate.keepVoxelComponent(voxelToTest, ci)){
							existsVoxelSatisfyingPredicate.set(indexFromLabel(label),
															   Boolean.valueOf(true)
															  );
						}
					}
				}
			}
		}
		
		// if the keep predicate is true for at least one voxel
		// and we should remove
		// the components with a voxel satisfying removalPredicate 
		// or
		// if the keep predicate is false for all the voxels
		// and we should keep only
		// the components with a voxel satisfying removalPredicate 
		for (int i = 0 ; i < this.m_compInfo.size() ; ++i){
			if (((!existsVoxelSatisfyingPredicate.get(i).booleanValue()) && keepPredicate) ||
				(existsVoxelSatisfyingPredicate.get(i).booleanValue() && !keepPredicate)){
				// remove the component
				this.m_compInfo.get(i).setNumberOfPoints(0);
			}
		}

		
		
		int thresholdNVoxel = (int)(thresholdComponentVolume/this.m_voxelVolume);
		ArrayList<Short> newLabels = new ArrayList<Short>(this.m_compInfo.size());
		ArrayList<ComponentInfo> newTabComponents = new ArrayList<ComponentInfo>();

		short componentsCount = undefinedLabel();

		// For each label
		for (short label=1 ; label<=this.m_compInfo.size() ; label++){
			ComponentInfo ci = this.m_compInfo.get(label-1);

			// If the component survives the filtering criteria
			if (ci != null && ci.getnumberOfPoints() > 0 &&
					ci.getnumberOfPoints() >= thresholdNVoxel && 
					((!removeBorderComponent) || !ci.isOnTheeBorder())){
				componentsCount++;
				// old label/new label correspondence
				newLabels.add(new Short(componentsCount));
				// register the component in the final array
				newTabComponents.add(ci);
			}else{
				ci.setNumberOfPoints(0);
				newLabels.add(new Short(undefinedLabel()));
			}
		}

		ArrayList<Integer> componentsColors = new ArrayList<Integer>(newTabComponents.size());

		for (int i=0 ; i<newTabComponents.size() ; i++){
			componentsColors.add(new Integer((int)(100 + Math.random()*(255 - 100))));
		}

		for (int k = 0; k < this.m_inputImage.getDepth(); ++k){
			int component2DcolorShift = (int)(Math.random()*(255-100));
			for (int j = 0; j < this.m_inputImage.getHeight(); ++j){
				for (int i = 0; i < this.m_inputImage.getWidth(); ++i ){
					
					short label = getLabel(i, j, k);
					
					// if not a background voxel and component not removed
					if (label > undefinedLabel() && 
							newLabels.get(indexFromLabel(label)).intValue() > undefinedLabel()){
						short newLabel = newLabels.get(indexFromLabel(label)).shortValue(); // get new label from old label
						ComponentInfo ci = newTabComponents.get(indexFromLabel(newLabel));
						ci.setLabel(newLabel); // Set new label for the component
						setLabel(i, j, k, newLabel); // Set new label for the voxel
						// Possibly change the color on the whole component
						if (setRandomColors){
							int color = componentsColors.get(indexFromLabel(newLabel)).intValue();
							if (this.m_labelingPolicy != LabelingPolicy.Full3D){
								color = (((color-100) + component2DcolorShift)%(255-100)) + 100;
							}
							this.m_inputImage.setVoxel(i, j, k, color);
						}
					}else{
						setLabel(i, j, k, undefinedLabel()); // Set label to background (remove the voxel)
						this.m_inputImage.setVoxel(i, j, k, 0);
					}
				}
			}
		}
		this.m_compInfo = newTabComponents;
	}

	
	/**
	 * @return a human readable string representation of this instance
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Connected components of the image "+ this.m_inputImage.getTitle()
				      +"(" + this.m_compInfo.size() + " Components)\n");
		//for (ComponentInfo compInfo : this.m_compInfo){
		//	builder.append(compInfo + "\n");
		//}
		return builder.toString();
	}
} // end of class 