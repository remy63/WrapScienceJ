/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ResourceCore.java                                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.resource;

import java.io.IOException;

import wrapScienceJ.io.stream.FileHelper;
import wrapScienceJ.metaData.container.MetaDataRetriever;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;

/**
 * GeneralInterface for resources such as images, videos, DNA sequence, signal, etc.
 *
 */
public interface ResourceCore {
	
    /**
     * Duplicates the image (invocation of the operation)
     * @return a reference to a clone copy of this instance.
     */   
    public ResourceCore duplicate();
    

	/**
	 * returns the path on disk to the image source file.
	 * @return path to the image source file
	 */
	public String getPath();

	
	/**
	 * @return the title of the image
	 */
	public String getTitle();

	/**
	 * Sets the image's title in the ImagePlus data.
	 * @param title the title to set for the image
	 * @return This instance to allow for use of the cascade design pattern
	 */
	public ResourceCore setTitle(String title);

	/**
	 * Save the image to a file on the disk.
	 * 
	 * @param destinationFilePath the path on disk to the file where to save the image.
	 * @return This instance to allow for use of the cascade design pattern
	 * @throws UnsupportedEncodingException in case of unsupported output file format.
	 * @throws IOException 
	 */
	public ResourceCore writeToFile(String destinationFilePath) throws IOException;

	/**
	 * Opens a Graphical Human Interface tool to displays the surface.
	 * @return A Rendering tool allowing to display the resource.
	 */
	public RenderTool getPreferedRenderTool();
	
	/**
	 * Provides a facility to retrieve the last open directory, or a default directory,
	 * {@link wrapScienceJ.config.GlobalOptions#getDefaultInputDir()}, first attempting a specified directory.
	 * @return The file helper's implementer on the platform.
	 */
	public FileHelper getFileHelper();
	
	/**
	 * Provides a facility to store, retrieve and access the metadata of this image.
	 * @return The instance containing the metadata associated with this image.
	 */
	public ModelCore getMetaData();
	
	/**
	 * Adds some container to store, retrieve and access the metadata of this image.
	 * If a container with the same title exists in the image's metadata, it is overwritten.
	 * @param config The configuration instance with meat data to add or replace
	 * @return This instance to allow for use of the cascade design pattern
	 */
	public ResourceCore addMetaData(MetaDataRetriever config);
	
	/**
	 * Allows to retrieve the resource's Metadata, either from a config file or from a dialog box,
	 * depending on the retrieval policy for each metadata set.
	 * @return This instance to allow for use of the cascade design pattern
	 * @throws IOException
	 */
	public ResourceCore retrieveMetaData() throws IOException;
	
	/**
	 * Adds some metadata to a resource by copying it from another resource.
	 * In case some metadata with the same title already exists in this resource,
	 * that part of the metadata is updated, not copied.
	 * @param resource A resource from which to copy the metadata.
	 * @return This instance to allow for use of the cascade design pattern
	 */
	public ResourceCore mergeMetaData(ResourceCore resource);
	
	/**
	 * Allows to retrieve the directory for default global metadata configuration file
	 * related to instances of the implementer class.
	 * @return The default global directory to search metadata files.
	 */
	public String getDefaultMetaDataDir();
	
	/**
	 * Writes the image to a file, including the metadata
	 * @param directory The directory where to save the image
	 * @param basename The basename of the image file
	 * @param extension The image file extension
	 * @return This instance to allow for use of the cascade design pattern
	 * @throws IOException 
	 * @see wrapScienceJ.resource.ModelCore#writeToFile(java.lang.String, java.lang.String)
	 */
	public ResourceCore writeToFileWithMetaData(String directory, String basename, String extension) throws IOException;
	

}
