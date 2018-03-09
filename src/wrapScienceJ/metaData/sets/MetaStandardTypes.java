/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaStandardTypes.java                                             * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.sets;

/**
 * Generic interface to manage sets of metadata containers with values having standard types
 * (double, int, boolean) or String.
 *
 */
public interface MetaStandardTypes {
	/**
	 * @param metaValue An instance of MetaValue to add to the set
	 */
	public void addMetaValue(MetaValue metaValue);
}
