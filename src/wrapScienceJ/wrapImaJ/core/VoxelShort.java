/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: VoxelShort.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core;
/**
 *  
 * Represents a voxel with its integer coordinates in the three dimensions stored as shorts on 2 Bytes.
 * This class can be used for memory intensive storage of voxel's coordinates since
 * these coordinates generally don't take high values so that they fit in a short.
 *
 * @author Rémy Malgouyres
 */
public class VoxelShort {


	/**
	 * Integer valued coordinates of the voxel
	 */
	private short[] m_coordinates;


	/**
	 * Creates an uninitialized voxel
	 */
	public VoxelShort() {
		this.m_coordinates = new short[3];
	}


	/**
	 * Constructor from given coordinates
	 * 
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 */
	public VoxelShort(short x, short y, short z) {
		this.m_coordinates = new short[3];
		this.m_coordinates[0] = x;
		this.m_coordinates[1] = y;
		this.m_coordinates[2] = z;
	}

	/**
	 * Constructor
	 *
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 */
	public void setCoordinates(short x, short y, short z)
	{
		this.m_coordinates[0] = x;
		this.m_coordinates[1] = y;
		this.m_coordinates[2] = z;
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
	 * Increments the i^th coordinates of a voxel, with i = 0, 1 or 2
	 * @param i the coordinate's index (0, 1 or 2)
	 */
	public void incrementCoord(int i) {  
		this.m_coordinates[i]++;  
	}

	/**
	 * Return the i^th coordinates of a voxel, with i = 0, 1 or 2
	 * @param i the coordinate's index (0, 1 or 2)
	 * @return the i^th coordinate of the voxel
	 */
	public short getCoord(int i) {  
		return this.m_coordinates[i];  
	}
	
	/**
	 * @param axis The coordinate axis for which to retrieve the voxel's coordinate value
	 * @return The voxel's coordinate value
	 */
	public short getCoord(CoordinateAxis axis){
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
	public short getX() {
		return this.m_coordinates[0];  
	}

	/**
	 * Return the y coordinate of a voxel
	 * @return the second coordinate of the voxel
	 */
	public short getY()  {  
		return this.m_coordinates[1]; 
	}

	/**
	 * Return the z coordinate of a voxel
	 * @return the third coordinate of a voxel
	 */
	public short getZ() {  
		return this.m_coordinates[2];  
	}

	/**
	 * sets the i^th coordinates of a voxel, with i = 0, 1 or 2
	 * @param i the coordinate's index (0, 1 or 2)
	 * @param value the new value of the i^th coordinate of the voxel
	 */
	public void setCoord(int i, short value) {  
		this.m_coordinates[i] = value;  
	}
	
	/**
	 * @param axis The coordinate axis for which to set the voxel's coordinate value
	 * @param value the new value of the i^th coordinate of the voxel
	 */
	public void setCoord(CoordinateAxis axis, short value){
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
	 * sets the first coordinate of a voxel
	 * @param value the new value of the first coordinate of the voxel
	 */
	public void setX(short value) {  
		this.m_coordinates[0] = value;  
	}
	
	/**
	 * sets the second coordinate of a voxel
	 * @param value the new value of the first coordinate of the voxel
	 */
	public void setY(short value) {  
		this.m_coordinates[1] = value;  
	}
	
	/**
	 * sets the third coordinate of a voxel
	 * @param value the new value of the first coordinate of the voxel
	 */
	public void setZ(short value) {  
		this.m_coordinates[2] = value;  
	}
	
	/**
	 * Allows to swap coordinates axis in the voxel's domain by modifying the attributes.
	 * @param axis1 The first axis to be swapped
	 * @param axis2 The second axis to be swapped
	 */
	public void getAxisSwapped(CoordinateAxis axis1, CoordinateAxis axis2){
		short coord1 = getCoord(axis1);
		setCoord(axis1, getCoord(axis2));
		setCoord(axis2, coord1);
	}

	/**
	 * Computes the coordinate by coordinate addition between two voxels
	 * @param voxel another voxel
	 */
	public void shiftCoordinates (VoxelShort voxel) {   
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
	public void multipliy (short a, short b, short c) { 
		this.m_coordinates[0] *= a;
		this.m_coordinates[1] *= b;
		this.m_coordinates[2] *= c; 
	}

	/**
	 * Multiplied the coordinates of voxel with a same factor for each coordinates
	 * @param a
	 */
	public void multiplie (short a){
		this.m_coordinates[0]*=a;
		this.m_coordinates[1]*=a;
		this.m_coordinates[2]*=a; 
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