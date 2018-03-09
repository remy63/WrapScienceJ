/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ResourcesMonitor.java                                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 


package wrapScienceJ.utils;

/**
 * This utility class allows to monitor the used/available resources (e.g. memory)
 * for the program at runtime.
 * 
 * @author Rémy Malgouyres
 *
 */
public class ResourcesMonitor {

	/**
	 * Returns the available resources and memory use informations.
	 * 
	 * @return a string with a human readable description of resources.
	 */
	public static String getRessourceInfo(){
		StringBuilder stb = new StringBuilder();
		stb.append("Number of Cores: " + Runtime.getRuntime().availableProcessors() + "\n");
		stb.append("Free memory (bytes): " + Runtime.getRuntime().freeMemory() + "\n");
		stb.append("Maximal amount of memory (bytes): " + Runtime.getRuntime().maxMemory() + "\n");
		stb.append("Memory currently used (bytes): " + Runtime.getRuntime().totalMemory() + "\n");
		
		return stb.toString();
	}
}
