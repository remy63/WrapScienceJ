/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ConnectedComponent3D.java                                          * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.connectivity;


import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.VoxelShort;

import java.util.LinkedList;



/**
 * Class dedicated to connected components labeling in 3D images
 * 
 * @author Remy Malgouyres
 */
public class ConnectedComponent3D extends ConnectedComponent {

	/**
	 * Constructor from an ImageWrapper representing a binary image and the foreground color in this image
	 * @param inputImage input (probably binary) image, the components of which to compute.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 */
	protected ConnectedComponent3D(ImageCore inputImage, int foregroundColor) {
		super(inputImage, foregroundColor);
	}


	/**
	 * Performs a breadth first search of the connected component for labeling
	 * The method goes over all the voxels in the connected component of the object
	 * of the initial voxel.
	 * The method sets the fields of the ComponentInfo parameter to record the status of the component.
	 * 
	 * @param voxelShort initial voxel of the connected component
	 * @param labelToSet label to set for the voxels of the component
	 * @throws IllegalStateException if the FIFO's size exceeds the number of preallocated voxels
	 */
	protected void breadthFirstSearch(VoxelShort voxelShort,
									  short labelToSet, 
									  short foregroundLabel,
									  ComponentInfo componentInfo) throws IllegalStateException {

		// FIFO for the Breadth First Search algorithm
		// LinkedList is more efficient than ArrayList 
		// because poll() (alias Remove(0)) is constant time !!!
		LinkedList<VoxelShort> voxelFifo =  new LinkedList<VoxelShort>();
		
		// add initial voxel to the FIFO
		voxelFifo.add(voxelShort);

		while (!voxelFifo.isEmpty())
		{
			// Retrieve and remove the head of the FIFO
			VoxelShort polledVoxelShort = voxelFifo.poll();

			short iV = polledVoxelShort.getX();
			short jV = polledVoxelShort.getY();
			short kV = polledVoxelShort.getZ();

			// Determine the neighborhood taking into account the image's boundaries
			short imin, imax, jmin, jmax, kmin, kmax;
			if (iV-1 >= 0){
				imin = (short)(iV-1);
			}else{
				imin = 0;
				componentInfo.incrementNVoxelsOnTheeBorder();
			}
			if (jV-1 >= 0){
				jmin = (short)(jV-1);
			}else{
				jmin = 0;
				componentInfo.incrementNVoxelsOnTheeBorder();
			}
			if (kV-1 >= 0){
				kmin = (short)(kV-1);
			}else{
				kmin = 0;
				componentInfo.incrementNVoxelsOnTheeBorder();
			}

			if (iV+1 < this.m_inputImage.getWidth()){
				imax = (short)(iV+1);
			}else{
				imax = (short)(this.m_inputImage.getWidth()-1);
				componentInfo.incrementNVoxelsOnTheeBorder();
			}
			if (jV+1 < this.m_inputImage.getHeight()){
				jmax = (short)(jV+1);
			}else{
				jmax = (short)(this.m_inputImage.getHeight()-1);
				componentInfo.incrementNVoxelsOnTheeBorder();
			}
			if (kV+1 < this.m_inputImage.getDepth()){
				kmax = (short)(kV+1);
			}else{
				kmax = (short)(this.m_inputImage.getDepth()-1);
				componentInfo.incrementNVoxelsOnTheeBorder();
			}
			
			// For each neighbor :
			for (short kk = kmin ; kk <= kmax; kk++){
				//this.m_inputImage.setCurrentSlice(kk);
				for (short ii = imin ; ii <= imax ; ii++){
					for (short jj = jmin ; jj <= jmax; jj++){
						// If the neighbor (different from VoxelRecordShort) is a 1 and not labeled
						if ((getLabel(ii, jj, kk) == foregroundLabel) && 
							(this.m_inputImage.getVoxel(ii,jj,kk) == this.m_foregroundColor)	
							)
						{
							// Set the voxel's label
							setLabel(ii, jj, kk, labelToSet); 
							componentInfo.incrementNumberOfPoints(); // increment component's cardinality	
							VoxelShort fifoVoxel = new VoxelShort(ii, jj, kk);
							componentInfo.updateBox(fifoVoxel);
							voxelFifo.add(fifoVoxel); // add to FIFO
							if (labelToSet == 0){
								this.m_inputImage.setVoxel(ii,jj,kk, 0);
							}
							// check for minimal depth representative and update if necessary
							if (kk < componentInfo.getRepresentant().getZ()){
								componentInfo.getRepresentant().setX(ii);
								componentInfo.getRepresentant().setY(jj);
								componentInfo.getRepresentant().setZ(kk);
							}
						}
					}
				}
			}
		}
	}



	/**
	 * Labels the connected components of the input image
	 * @param lowThresholdNbPoints Lower limit of number of points below which
	 * 							   connected components will be dropped
	 * @throws IllegalStateException 
	 */ 
	@Override
	public void doLabelConnectedComponent(int lowThresholdNbPoints) throws IllegalStateException {

		short currentLabel = undefinedLabel();
		short unsetLabel = undefinedLabel();
		
		boolean incrementLabel = true;

		for (short k = 0; k < this.m_inputImage.getDepth(); k++){	
			System.err.print("z = " + k + ", ");
			if (k%10 == 0){
				System.err.print("\nCurrent Label: " + (currentLabel-Short.MIN_VALUE) + ". " );
			}
			for (short j = 0; j < this.m_inputImage.getHeight(); j++){
				for (short i = 0; i < this.m_inputImage.getWidth(); i++){
					if (this.m_inputImage.getVoxel(i, j, k) == this.m_foregroundColor
							&& getLabel(i, j, k) == unsetLabel){
						if (incrementLabel){
							currentLabel++;	
						}
						incrementLabel = true;
						if (currentLabel == Short.MAX_VALUE){
							throw new IllegalStateException("Too many connected components.");
						}
						setLabel(i, j, k, currentLabel);
						
						ComponentInfo componentInfo = new ComponentInfo(
								currentLabel, 
								1, // Number of points
								new VoxelInt(i, j, k), 
								0 // Number of voxels on the image's edge
								);

						breadthFirstSearch(
								new VoxelShort(i, j, k),
								currentLabel,
								unsetLabel,
								componentInfo);
						if (componentInfo.getnumberOfPoints() < lowThresholdNbPoints){
							breadthFirstSearch(
								new VoxelShort(i, j, k),
								unsetLabel,
								currentLabel,
								componentInfo);
								incrementLabel = false;
						}else{
							this.m_compInfo.add(componentInfo);
						}
					}
				}
			}
			System.gc();
		}
	}
	
} // end of class

