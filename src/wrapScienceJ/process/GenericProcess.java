/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GenericProcess.java                                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.process;

import wrapScienceJ.metaData.container.MetaDataContainer;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeType;
import wrapScienceJ.resource.ModelCore;


/**
 * Represents a hierarchical structure of processes, using the Composite and Visitor Pattern
 * Each concrete process is intended to execute a method runProcess(), can optionally 
 * use an Object as parameter, and can optionally return an Object.
 * 
 * The Generic Process is characterized by a String description, which should be human readable
 * and preferably unique to avoid confusion, especially since some operations use the
 * description as a unique key.
 * 
 * The Generic Process usually takes a set of parameters as part of its input,
 * which is generally accessible through an abstract method called getConfig() and the
 * GenericProcessConfig class.
 * @see MetaDataContainer
 * @see AttributeData
 * @see AttributeType
 * Since the GenericProcessConfig contains references to a number of AttributeData,
 * the instance returned by getConfig() can be modified to adapt to a specific
 * use of the Generic Process.
 * This modification can be set through access to a configuration file, which is contained
 * in a directory specified by a String parameter for getConfig().
 * The modification of the process's parameter might also be obtained by any kind of
 * request (e.g. network stream request of HTTPS API request), including Graphical User Interface
 * request.
 * The policy for managing and updating configs can be
 * <ul>
 * <li>"overwrite": An existing config is overwritten if a new config is obtained;</li>
 * <li>"Always ask": A new configuration is sought at each invocation without changing the default config
 * (the GenericProcessConfig.duplicate() method can be used)</li>
 * <li>"Always use default": No configuration input is sought at all and the configuration is taken as is,
 * assumed to be "one size fits all"</li>
 * </ul>
 */
public interface GenericProcess extends InputOutputPolicy {
	
	/**
	 * Allows to ge a set of parameters as input for the generic process
	 * @return The complete configuration for the generic process
	 */
	public ModelCore getConfig();
	
	/**
	 * @param arg a generic argument if the process requires additional data or methods
	 * @param option An optional string transmitted to the process (e.g. directory, path, etc.)
	 * @return a generic object in case the process is required to return an object
	 */
	public Object runProcess(Object arg, String option);
	
}
