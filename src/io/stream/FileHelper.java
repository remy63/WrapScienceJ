/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: FileHelper.java                                                    * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.io.stream;

import java.io.File;

import wrapScienceJ.resource.ResourceCore;

/**
 * Provides utilities, which might be related to the framework, to locate and open files.
 *
 */
public abstract class FileHelper {
	
	/**
	 * If guessDir is neither null nor "default", and the directory guessDir exists, then guessDir is used.
	 * If guessDir is "default", then an attempt is made to obtain the image directory from metadata.
	 * If that fails or guessDir is null, an attempt is made to retrieve the last used directory in the framework.
	 * If that also fails, null is returned.
	 * @param resource A resource assumed to be opened in The Resource Processing Framework
	 * 				   (e.g. an ImageCore in WrapImaJ)
	 * @param guessDir 
	 * @return a directory that might be null.
	 */
	public abstract String retrieveResourceDirectory(ResourceCore resource, String guessDir);
	
	/**
	 * Checks is the input directory path does correspond to a directory.
	 * Attempts to retrieve the last used directory from the framework, or a default directory
	 * is that fails too.
	 * 
	 * @param guessDir If guessDir is neither null nor "default", and the directory guessDir exists,
	 * then guessDir is used.
	 * If guessDir is "default", then an attempt is made to obtain the image directory from metadata.
	 * If that fails or guessDir is null, an attempt is made to retrieve the last used directory in the framework.
	 * If that also fails, null is returned.
	 * @return a directory that might be null.
	 */
	public abstract String retrieveLastDirectory(String guessDir);
	
	/**
	 * Appends a postfix to the file basename and changes its extension
	 * e.g. a file path "/home/Images/myImage.tif" is changed to "/home/Images/myImage_silce16.png"
	 * with postfix = "_slice16" and extension = "png"
	 * @param filePath Original path to some file.
	 * @param postfix the postfix to append to the basename of the file
	 * @param newExtension  the new file extension for a possibly new file format.
	 * @param removeDirectory If true, the leading directory path is removed from the resulting file name.
	 * @return The modified file path.
	 */
	public static String appendPostfixAndSetExtension(String filePath, String postfix,
											   String newExtension, boolean removeDirectory) {

		String basename = removeDirectory ? getBaseName(filePath)
										  : filePath.substring(0, filePath.lastIndexOf("."));
		
		return basename + postfix + "." + newExtension;

	}
	
	/**
	 * Appends a postfix to the file basename and changes its extension
	 * e.g. a file path "/home/Images/myImage.tif" is changed to "/home/Images/myImage_silce16.png"
	 * with postfix = "_slice16" and extension = "png"
	 * @param filePath Original path to some file.
	 * @param newDirectory The new directory where to locate the new file.
	 * @param postfix the postfix to append to the basename of the file
	 * @param newExtension  the new file extension for a possibly new file format.
	 * @return The modified file path.
	 */
	public static String changeDirectoryPostfixAndExtension(String filePath,
												String newDirectory, String postfix,
												String newExtension) {

		String newName = appendPostfixAndSetExtension(filePath, postfix,
				   									 newExtension, true);
		
		return newDirectory + (newDirectory.endsWith(File.separator) ? "" : File.separator) + newName;

	}
	
	/**
	 * Retrieves the basename of a file by its path, by removing the directory and extention.
	 * e.g. a file path "/home/Images/myImage.tif" yields "myImage"
	 * 
	 * @param filePath Original path to some file.
	 * @return The obtained basename.
	 */
	public static String getBaseName(String filePath) {
		
		String basename;
		int index =  filePath.lastIndexOf(File.separator);
		if (index < 0){
			basename = filePath;
		}else{
			basename = filePath.substring(index+1);
		}
		index = basename.lastIndexOf(".");
		if (index < 0){
			return basename;
		}
		return basename.substring(0, basename.lastIndexOf("."));
	}
}
