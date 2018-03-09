/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: BoxROI.java                                                        * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.core;

/**
 * @author remy
 *
 */
public class BoxROI {
	
	short m_xmin;
	short m_ymin;
	short m_zmin;
	short m_xmax;
	short m_ymax;
	short m_zmax;
	
	/**
     * Creates an empty Rectangle 3D Region of Interest (ROI) in the image. 
	 * The coordinate min are set to Integer.MAX_VALUE and the coordinates
	 * max are set to Integer.MIN_VALUE, as initialized to compute minima and maxima
	 * by going sequentially through values.
     */	
	public BoxROI(){
		this.m_xmin = Short.MAX_VALUE;
		this.m_ymin = Short.MAX_VALUE;
		this.m_zmin = Short.MAX_VALUE;
		this.m_xmax = Short.MIN_VALUE;
		this.m_ymax = Short.MIN_VALUE;
		this.m_zmax = Short.MIN_VALUE;
	}
	
    /**
     * Creates a Rectangle 3D Region of Interest (ROI) in the image. 
     * The coordinates bounding boxe's minimal coordinates and edges length (in number of pixels) are given as parameters. 
     * @param xmin minimal X coordinate of the ROI
     * @param ymin minimal Y coordinate of the ROI
     * @param zmin minimal Z coordinate of the ROI
     * @param xmax maximal X coordinate of the ROI plus one
     * @param ymax maximal Y coordinate of the ROI plus one
     * @param zmax maximal Z coordinate of the ROI plus one
     */	
	public BoxROI(short xmin, short ymin, short zmin, short xmax, short ymax, short zmax){
		this.m_xmin = xmin;
		this.m_ymin = ymin;
		this.m_zmin = zmin;
		this.m_xmax = xmax;
		this.m_ymax = ymax;
		this.m_zmax = zmax;
	}
	
	/**
	 * Update the boundaries of the BoxROI to take into account that a given voxel
	 * must be in the box.
	 * @param voxelInTheBox A voxel that should be tested to know whether it is contained in the box.
	 * @return true if the voxel is contained in the BoxROI, and false otherwise.
	 */
	public boolean contains(VoxelShort voxelInTheBox){
		
		return (voxelInTheBox.getX() >= this.m_xmin) && (voxelInTheBox.getX() < this.m_xmax) &&
			   (voxelInTheBox.getY() >= this.m_ymin) && (voxelInTheBox.getY() < this.m_ymax) &&
			   (voxelInTheBox.getZ() >= this.m_zmin) && (voxelInTheBox.getZ() < this.m_zmax);
	}
	
	/**
	 * @return The Geometric center of the BoxROI (iso-barycenter of the voxels contained in the BoxROI)
	 */
	public VoxelDouble getCenter(){
		return new VoxelDouble(0.5d*(this.m_xmax + this.m_xmin -1), 0.5d*(this.m_ymax + this.m_ymin-1),
								0.5d*(this.m_zmax + this.m_zmin-1)
								);
	}
	
	/**
	 * @return The size of the box in each dimension (in voxel coordinates)
	 */
	public VoxelShort getSize(){
		return new VoxelShort((short)(this.m_xmax - this.m_xmin), (short)(this.m_ymax - this.m_ymin),
							  (short)(this.m_zmax - this.m_zmin));
	}	
	
	/**
	 * Update the boundaries of the BoxROI to take into account that a given voxel
	 * must be in the box.
	 * @param voxelInTheBox A voxel that should be contained in the box.
	 */
	public void updateBox(VoxelShort voxelInTheBox){
		
		if (voxelInTheBox.getX() < this.m_xmin){
			this.m_xmin = voxelInTheBox.getX();
		}
		if (voxelInTheBox.getX() >= this.m_xmax){
			this.m_xmax = (short)(voxelInTheBox.getX()+1);
		}
		
		if (voxelInTheBox.getY() < this.m_ymin){
			this.m_ymin = voxelInTheBox.getY();
		}
		if (voxelInTheBox.getY() >= this.m_ymax){
			this.m_ymax = (short)(voxelInTheBox.getY()+1);
		}

		if (voxelInTheBox.getZ() < this.m_zmin){
			this.m_zmin = voxelInTheBox.getZ();
		}
		if (voxelInTheBox.getZ() >= this.m_zmax){
			this.m_zmax = (short)(voxelInTheBox.getZ()+1);
		}
	}
	
	/**
	 * @return A full copy of the BoxROI
	 */
	public BoxROI duplicate(){
		return new BoxROI(this.m_xmin, this.m_ymin, this.m_zmin, this.m_xmax, this.m_ymax, this.m_zmax);
	}
	
	/**
	 * Enlarge a BoxROI by some margin on both sides for each coordinate axis.
	 * The BoxROI can be shrunk if margins are negative.
	 * @param xMargin Width of the margin to add on both sides
	 * @param yMargin Height of the margin to add on both sides
	 * @param zMargin Depth of the margin to add on both sides
	 * @return An enlarged (or shrunk if margins are negative) version of the BoxROI.
	 */
	public BoxROI getEnlargedBox(short xMargin, short yMargin, short zMargin){
		
		BoxROI copy = duplicate();
		
		copy.m_xmin -= xMargin;
		copy.m_xmax += xMargin;
		
		copy.m_ymin -= yMargin;
		copy.m_ymax += yMargin;

		copy.m_zmin -= zMargin;
		copy.m_zmax += zMargin;
		
		return copy;
	}
	
	
	/**
	 * Allows to intersect (clip) the Box with a box-like region (ROI), in order to
	 * ensure that the resulting box is included in that region.
	 * 
	 * @param xMinClip Minimal X coordinate of the ROI to intersect with.
	 * @param yMinClip Minimal Y coordinate of the ROI to intersect with.
	 * @param zMinClip Minimal Z coordinate of the ROI to intersect with.
	 * @param xMaxClip Maximal X coordinate PLUS ONE of the ROI to intersect with.
	 * @param yMaxClip Maximal Y coordinate PLUS ONE of the ROI to intersect with.
	 * @param zMaxClip Maximal Z coordinate PLUS ONE of the ROI to intersect with.
	 */
	public void clip(int xMinClip, int yMinClip, int zMinClip, int xMaxClip, int yMaxClip, int zMaxClip){
		
		if (this.m_xmin < xMinClip){
			this.m_xmin = (short)xMinClip;
		}
		if (this.m_ymin < yMinClip){
			this.m_ymin = (short)yMinClip;
		}
		if (this.m_zmin < zMinClip){
			this.m_zmin = (short)zMinClip;
		}
		if (this.m_xmax > xMaxClip){
			this.m_xmax = (short)xMaxClip;
		}
		if (this.m_ymax > yMaxClip){
			this.m_ymax = (short)yMaxClip;
		}
		if (this.m_zmax > zMaxClip){
			this.m_zmax = (short)zMaxClip;
		}
	}
	

	/**
	 * @param axis The coordinate axis for which to retrieve the voxel's coordinate value
	 * @return The minimal coordinate value for this coordinate.
	 */
	public short getMin(CoordinateAxis axis){
		switch(axis){
			case X: return this.m_xmin;
			case Y: return this.m_ymin;
			case Z: return this.m_zmin;
			default:
				throw new IllegalArgumentException("Unknown coordinate axis");
		}
	}
	
	/**
	 * @param axis The coordinate axis for which to retrieve the voxel's coordinate value
	 * @return The maximal coordinate value for this coordinate plus one.
	 */
	public short getMax(CoordinateAxis axis){
		switch(axis){
			case X: return this.m_xmax;
			case Y: return this.m_ymax;
			case Z: return this.m_zmax;
			default:
				throw new IllegalArgumentException("Unknown coordinate axis");
		}
	}
	
	/**
	 * Allows to set the minimal coordinate value for a given coordinate.
	 * @param axis The coordinate axis for which to retrieve the voxel's coordinate value
	 * @param value The value to use for the new minimal coordinate.
	 */
	public void setMin(CoordinateAxis axis, short value){
		switch(axis){
			case X: this.m_xmin = value;
					break;
			case Y: this.m_ymin = value;
					break;
			case Z: this.m_zmin = value;
					break;
			default:
				throw new IllegalArgumentException("Unknown coordinate axis");
		}
	}
	
	/**
	 * Allows to set the maximal coordinate value, plus one, for a given coordinate.
	 * @param axis The coordinate axis for which to retrieve the voxel's coordinate value
	 * @param value The value to use for the new maximal coordinate plus one.
	 */
	public void setMax(CoordinateAxis axis, short value){
		switch(axis){
			case X: this.m_xmax = value;
					break;
			case Y: this.m_ymax = value;
					break;
			case Z: this.m_zmax = value;
					break;
			default:
				throw new IllegalArgumentException("Unknown coordinate axis");
		}
	}
	
	/**
	 * Allows to swap coordinates axis in the voxel's domain by modifying the attributes
	 * for minimal and (maximal plus one) coordinate values.
	 * @param axis1 The first axis to be swapped
	 * @param axis2 The second axis to be swapped
	 */
	public void getAxisSwapped(CoordinateAxis axis1, CoordinateAxis axis2){
		short min1 = getMin(axis1);
		setMin(axis1, getMin(axis2));
		setMin(axis2, min1);
		
		short max1 = getMax(axis1);
		setMax(axis1, getMax(axis2));
		setMax(axis2, max1);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "xMin=" + this.m_xmin + "; yMin=" + this.m_ymin + "; zMin=" + this.m_zmin
				+ "; xMax=" + this.m_xmax + "; yMax=" + this.m_ymax + "; zMax=" + this.m_zmax;
	}
}
