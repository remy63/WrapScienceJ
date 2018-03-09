/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: VoxelInt.java                                                         * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core;


/**
 * Represents a voxel with its integer coordinates in the three dimensions
 * 
 * @author Philippe Andrey, Poulet Axel and Remy Malgouyres
 */

public class VoxelInt {
	/**
	 * Integer valued coordinates of the voxel
	 */
	private int[] m_coordinates;

	
	/**
	 * Creates an uninitialized voxel
	 */
	public VoxelInt() {
		this.m_coordinates = new int[3];
	}


	/**
	 * Constructor
	 * 
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 */
	public VoxelInt(int x, int y, int z) {
		this.m_coordinates = new int[3];
		this.m_coordinates[0] = x;
		this.m_coordinates[1] = y;
		this.m_coordinates[2] = z;
	}
	
	/**
	 * Constructor from given coordinates
	 *
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 */
	public void setCordinates(int x, int y, int z)
	{
		this.m_coordinates[0] = x;
		this.m_coordinates[1] = y;
		this.m_coordinates[2] = z;
	}

	
    
	/**
	 * Return the i^th coordinates of a voxel, with i = 0, 1 or 2
	 * @param i the coordinate's index (0, 1 or 2)
	 * @return the i^th coordinate of the voxel
	 */
	public int getCoord(int i) {
		return this.m_coordinates[i];
	}

	/**
	 * @param axis The coordinate axis for which to retrieve the voxel's coordinate value
	 * @return The voxel's coordinate value
	 */
	public int getCoord(CoordinateAxis axis){
		switch(axis){
			case X: return getX();
			case Y: return getY();
			case Z: return getZ();
			default:
				throw new IllegalArgumentException("Unknown coordinate axis");
		}
	}
	
	/**
	 * Return the x coordinates of a voxel
	 * @return the first coordinate of the voxel
	 */
	public int getX() {
		return this.m_coordinates[0];
	}

	/**
	 * Return the y coordinate of a voxel
	 * @return the second coordinate of the voxel
	 */
	public int getY() {
		return this.m_coordinates[1];
	}

	/**
	 * Return the z coordinate of a voxel
	 * @return the third coordinate of a voxel
	 */
	public int getZ() {
		return this.m_coordinates[2];
	}

	/**
	 * Increments the i^th coordinates of a voxel, with i = 0, 1 or 2
	 * @param i the coordinate's index (0, 1 or 2)
	 */
	public void incrementCoord(int i) {  
		this.m_coordinates[i]++;  
	}
	
	
	/**
	 * Sets the i^th coordinates of a voxel, with i = 0, 1 or 2
	 * @param i the coordinate's index (0, 1 or 2)
	 * @param value the new value of the i^th coordinate of the voxel
	 */
	public void setCoord(int i, int value) {  
		this.m_coordinates[i] = value;  
	}
	
	
	/**
	 * @param axis The coordinate axis for which to set the voxel's coordinate value
	 * @param value the new value of the i^th coordinate of the voxel
	 */
	public void setCoord(CoordinateAxis axis, int value){
		switch(axis){
			case X: setX(value);
					break;
			case Y: setY(value);
					break;
			case Z: setZ(value);
					break;
			default:
				throw new IllegalArgumentException("Unknown coordinate axis");
		}
	}
	
	/**
	 * Sets the first coordinate of a voxel
	 * @param value the new value of the first coordinate of the voxel
	 */
	public void setX(int value) {  
		this.m_coordinates[0] = value;  
	}
	
	/**
	 * Sets the second coordinate of a voxel
	 * @param value the new value of the first coordinate of the voxel
	 */
	public void setY(int value) {  
		this.m_coordinates[1] = value;  
	}
	
	/**
	 * sets the third coordinate of a voxel
	 * @param value the new value of the first coordinate of the voxel
	 */
	public void setZ(int value) {  
		this.m_coordinates[2] = value;  
	}
	
	/**
	 * Allows to swap coordinates axis in the voxel's domain by modifying the attributes.
	 * @param axis1 The first axis to be swapped
	 * @param axis2 The second axis to be swapped
	 */
	public void getAxisSwapped(CoordinateAxis axis1, CoordinateAxis axis2){
		int coord1 = getCoord(axis1);
		setCoord(axis1, getCoord(axis2));
		setCoord(axis2, coord1);
	}

	/**
     * Override of the clone operation
     * @return a reference to a clone copy of this instance.
     */
    @Override
    protected Object clone() throws UnsupportedOperationException {
    	throw new UnsupportedOperationException();
    }
 
	/**
	 * Computes the coordinate by coordinate addition between two voxels
	 * @param voxel another voxel
	 */
	public void shiftCoordinates (VoxelInt voxel) {   
		this.m_coordinates[0] += voxel.m_coordinates[0];
		this.m_coordinates[1] += voxel.m_coordinates[1];
		this.m_coordinates[2] += voxel.m_coordinates[2]; 
	}

	/**
	 * Multiplies the coordinates of voxel with a different factor on each coordinates
	 * @param a factor on the first coordinate
	 * @param b factor on the second coordinate
	 * @param c factor on the third coordinate
	 */
	public void multiply (int a, int b, int c) { 
		this.m_coordinates[0] *= a;
		this.m_coordinates[1] *= b;
		this.m_coordinates[2] *= c; 
	}

	/**
	 * Multiplied the coordinates of voxel with a same factor for each coordinates
	 * @param a
	 */
	public void multiplie (int a){
		this.m_coordinates[0] *= a;
		this.m_coordinates[1] *= a;
		this.m_coordinates[2] *= a; 
		}

	/**
	 * Returns a human readable representation of a voxel as a string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.m_coordinates[0] + ", " + this.m_coordinates[1] + ", " +this.m_coordinates[2] +")";
	}
} // End of class