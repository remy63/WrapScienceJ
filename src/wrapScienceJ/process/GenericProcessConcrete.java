/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GenericProcessConcrete.java                                        * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.process;

import wrapScienceJ.resource.generic.ModelCoreGeneric;

/**
 * Represents a leaf in a hierarchical structure of processes, using the Composite and Visitor Pattern.
 * A leaf runProcess() method implements an actual series of operations and contains two concrete
 * configuration as attributes: a default configuration an a current configuration.
 * @see GenericProcess
 * @see GenericProcessNode
 * 
 * The runProcess uses the <strong>current</strong> configuration in the dedicated attribute.
 * The current configuration is initialized as a duplicate (full copy) of
 * the current configuration.
 * 
 * The "Update Policy" for the concrete configuration is handled as follows
 * in the getConfig() method:
 * <ul>
 * <li>"overwrite" policy: The current configuration has same reference as the default configuration
 * and the concrete mutable instance of the single configuration instance is returned;</li>
 * <li>"Always ask" policy: The instance of the current is re-initialized as a duplicate
 * of the default, and the current configuration is returned;</li>
 * <li>"Always use default" policy: An empty configuration set of attributes is returned for update.</li>
 * </ul>
 * 
 */
public abstract class GenericProcessConcrete implements GenericProcess {
	
	
	/**
	 * @see GenericProcess#getConfig()
	 * 
	 * The "Update Policy" for the concrete configuration is handled as follows:
	 * <ul>
	 * <li>"overwrite" policy: The current configuration has same reference as the default configuration
	 * and the concrete mutable instance of the single configuration instance is returned;</li>
	 * <li>"Always ask" policy: The instance of the current is re-initialized as a duplicate
	 * of the default, and the current configuration is returned;</li>
	 * <li>"Always use default" policy: An empty configuration set of attributes is returned for update.</li>
	 * </ul>
 	 */
	public abstract ModelCoreGeneric getConfig();
	
	/**
	 * @param arg a generic argument if the process requires additional data or methods
	 * @param option An optional string transmitted to the process (e.g. directory, path, etc.)
	 * @return a generic object in case the process is required to return an object
	 */
	public abstract Object runProcess(Object arg, String option);
	
}
