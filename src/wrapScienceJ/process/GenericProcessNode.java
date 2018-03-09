/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GenericProcessNode.java                                            * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.process;

import java.util.ArrayList;

import wrapScienceJ.resource.ModelCore;


/**
 * Represents a node in a hierarchical structure of processes, using the Composite and Visitor Pattern.
 * A node contains an ordered collection of subprocesses.
 * the runProcess() method recursively calls the runProcess() methods for children processes.
 * @see GenericProcess
 * @see GenericProcessConcrete
 *  
 * This implementation is a sequential ordered implementation of children processes.
 * The same Object is transmitted to all children and no object is returned from the runProcess() method.
 * The configuration is the merging of the children processes configurations, implementing
 * the update policy is the responsibility of leaves (@link{GenericProcessConcrete}).
 * 
 * Use another node class for parallel (e.g. run children processes in parallel threads)
 * processing of the children.
 * TODO Implement a parallel multi-threaded version of this class. 
 */
public abstract class GenericProcessNode implements GenericProcess {
	
	ArrayList<GenericProcess> m_childrenProcesses;
	
	/**
	 * Allows to create a Generic Process Node with no children
	 */
	public GenericProcessNode(){
		this.m_childrenProcesses = new ArrayList<GenericProcess>();
	}
	
	/**
	 * Allows to create a Generic Process Node with a given collection of children
	 * @param children a given collection of children to create in the node of proceesses
	 */
	public GenericProcessNode(GenericProcess[] children){
		this.m_childrenProcesses = new ArrayList<GenericProcess>();
		for (GenericProcess child: children){
			this.getInputResourceMetaData().merge(child.getConfig());
			this.m_childrenProcesses.add(child);
		}
	}
	
	
	/**
	 * Allows to append a child Generic Process Node to the collection of children processes
	 * @param child a given process to append to the node of processes
	 */
	public void addChild(GenericProcess child){
		this.m_childrenProcesses.add(child);
	}
	
	/**
	 * @see GenericProcess#getConfig()
	 */
	@Override
	public ModelCore getConfig(){
		return getInputResourceMetaData();
	}
	
	/**
	 * @param arg a generic argument if the process requires additional data or methods
	 * @param option An optional string transmitted to the process (e.g. directory, path, etc.)
	 * @return a generic object in case the process is required to return an object
	 */
	@Override
	public Object runProcess(Object arg, String option){
		for (GenericProcess process: this.m_childrenProcesses){
			process.runProcess(arg, option);
		}
		return null;
	}

}
