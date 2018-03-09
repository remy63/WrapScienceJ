/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: PixelShort.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.wrapImaJ.core;
/**
 *  
 * Represents a pixel with its integer coordinates in the two dimensions stored as shorts on 2 Bytes.
 * This class can be used for memory intensive storage of pixel's coordinates since
 * these coordinates generally don't take high values so that they fit in a short.
 *
 * @author Rémy Malgouyres
 */
public class PixelShort
{


	/**
	 * Integer valued coordinates of the pixel
	 */
	private short[] m_coordinates;


	/**
	 * Creates an uninitialized pixel
	 */
	public PixelShort() {
		this.m_coordinates = new short[2];
	}


	/**
	 * Constructor from given coordinates
	 * 
	 * @param x first coordinate of the pixel
	 * @param y second coordinate of the pixel
	 */
	public PixelShort(short x, short y) {
		this.m_coordinates = new short[2];
		this.m_coordinates[0] = x;
		this.m_coordinates[1] = y;
	}

	/**
	 * Constructor
	 *
	 * @param x first coordinate of the pixel
	 * @param y second coordinate of the pixel
	 */
	public void setCoordinates(short x, short y)
	{
		this.m_coordinates[0] = x;
		this.m_coordinates[1] = y;
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
	 * Increments the i^th coordinates of a pixel, with i = 0 or 1
	 * @param i the coordinate's index (0 or 1)
	 */
	public void incrementCoord(int i) {  
		this.m_coordinates[i]++;  
	}
	
	/**
	 * sets the i^th coordinates of a pixel, with i = 0 or 1
	 * @param i the coordinate's index (0 or 1)
	 * @param value the new value of the i^th coordinate of the pixel
	 */
	public void setCoord(int i, short value) {  
		this.m_coordinates[i] = value;  
	}
	
	/**
	 * sets the first coordinate of a pixel
	 * @param value the new value of the first coordinate of the pixel
	 */
	public void setX(short value) {  
		this.m_coordinates[0] = value;  
	}
	
	/**
	 * sets the second coordinate of a pixel
	 * @param value the new value of the first coordinate of the pixel
	 */
	public void setY(short value) {  
		this.m_coordinates[1] = value;  
	}
	

	/**
	 * Return the i^th coordinates of a pixel, with i = 0 or 1
	 * @param i the coordinate's index (0 or 1)
	 * @return the i^th coordinate of the pixel
	 */
	public short getCoord(int i) {  
		return this.m_coordinates[i];  
	}

	/**
	 * Return the x coordinates of a pixel
	 * @return the first coordinate of the pixel
	 */
	public short getX() {
		return this.m_coordinates[0];  
	}

	/**
	 * Return the y coordinate of a pixel
	 * @return the second coordinate of the pixel
	 */
	public short getY()  {  
		return this.m_coordinates[1]; 
	}

	/**
	 * Computes the coordinate by coordinate addition between two voxels
	 * @param pixel another pixel
	 */
	public void shiftCoordinates (PixelShort pixel) {   
		this.m_coordinates[0] += pixel.m_coordinates[0];
		this.m_coordinates[1] += pixel.m_coordinates[1];
	}

	/**
	 * Multiplies the coordinates of pixel with a different factor on each coordinates
	 * @param a factor on the first coordinate
	 * @param b factor on the second coordinate
	 */
	public void multipliy (short a, short b) { 
		this.m_coordinates[0] *= a;
		this.m_coordinates[1] *= b;
	}

	/**
	 * Multiplied the coordinates of pixel with a same factor for each coordinates
	 * @param a
	 */
	public void multiplie (short a){
		this.m_coordinates[0]*=a;
		this.m_coordinates[1]*=a;
	}

	/**
	 * Returns a human readable representation of a pixel as a string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.m_coordinates[0] + ", " + this.m_coordinates[1]+")";
	}
} // End of class