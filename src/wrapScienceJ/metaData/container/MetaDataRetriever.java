/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: MetaDataRetriever.java                                             * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.metaData.container;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import wrapScienceJ.config.GlobalOptions;
import wrapScienceJ.gui.GuiFramework;

/**
 * Allows to retrieve metadata from several sources according to a policy.
 * Policies ({@link RetrievalPolicy}) give possibility or priority to
 * known/initial values, reading from a configuration file or prompting the
 * user with a dialog box.
 *
 */
public abstract class MetaDataRetriever extends MetaData{
	
	/** The Graphical User Interface tool used to generate dialogs */
	public static GuiFramework m_guiFramework = GlobalOptions.getDefaultGuiFramework();
	
	/**
	 * Enumeration of all possible options ID for wrapImaJ implementers.
	 *(currently, only one implementation is provided, based on ImageJ)
	 */
	public enum RetrievalPolicy {
		
		/**
		 * The policy has to remain unspecified at this moment, so that a default behavior
		 * (which is force dialog) is used, unless a specific policy is defined later.
		 * This option is also used to prevent overriding when invoking a process construction
		 * within which other processes are constructed, each with a specialized policy.
		 */
		Unspecified(1),
		/**
		 * Force to prompt the user for input with a Dialog Box.
		 * The default values are retrieved from a conf file, if available.
		 * Otherwise, initial (previously known) values are used as default values.
		 */
		ForceDialog(2),
		/**
		 * Use the initial (previously known) values without trying to retrieve data
		 * either from a conf file or by prompting the user.
		 */
		UseKnownValues(3),
		/**
		 * Try to retrieve the values from a conf file to override initial (previously known) values.
		 * Don't prompt the user for input in any case.
		 */
		TryConfFileNoDialog(4),
		/**
		 * Try to retrieve the values from a conf file to override initial (previously known) values.
		 * If no file is found, prompt the user for input with a Dialog Box..
		 */
		TryConfFileThenDialog(5);
		
		private final int m_retrievalPolicy;

		/**
		 * sets the wrapper's ID
		 * @param value
		 */
		private RetrievalPolicy(int retrievalPolicy) throws IllegalArgumentException {
			if (retrievalPolicy < 1 || retrievalPolicy > 5){
				throw new IllegalArgumentException("Undefined RetrievalPolicy.");
			}
			this.m_retrievalPolicy = retrievalPolicy;
		}
	
		/**
		 * @return the integer representation for the wrapper's ID
		 */
		public int getValue() {
			return this.m_retrievalPolicy;
		}
	
		/** 
		 * @return a human readable description of the wrapper.
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			switch(this.m_retrievalPolicy){
				case 1:
					return "Unspecified policy (specify later or default will be used)";					
				case 2: 
					return "Force to prompt the user for input with a Dialog Box. Try to recover default values from a conf file";
				case 3:
					return "Use the initial (previously known) values without trying to retrieve data";
				case 4:
					return "Try to retrieve the values from a conf file to override initial (previously known) values but don't bother the user";
				case 5:
					return "Try to retrieve the values from a conf file to override initial (previously known) values and ask the user for values not found";
				default:
					throw new IllegalArgumentException("Unknown Parameters Retrieval Policy");
			}
		}
	} // End of enum RetrievalPolicy
	
	/** The policy to retrieve the parameters (known values, config file, user input...) */
	protected RetrievalPolicy m_retrievalPolicy;
	
	protected String m_subdir;
	
	
	/**
	 * @return The policy to retrieve the metadata ("from config file only", "prompt the user")
	 */
	public RetrievalPolicy getPolicy(){
		return this.m_retrievalPolicy;
	}
	
	/**
	 * @param retrievalPolicy The policy to retrieve the metadata ("from config file only", "prompt the user")
	 * @see RetrievalPolicy
	 */
	public void setPolicy(RetrievalPolicy retrievalPolicy){
		this.m_retrievalPolicy = retrievalPolicy;
	}
	
	/**
	 * Creates a DoubleValueConfig with initial value zero.
	 * @param fileNamePostfix The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
	 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
	 * @param subdir Sub-directory of the default gobal directory in which the default global metadata
	 * 				file should be looked for.
	 * @see MetaData#MetaData(String)
	 */
	public MetaDataRetriever(String fileNamePostfix, RetrievalPolicy retrievalPolicy,
							 String subdir) {
		super(fileNamePostfix);
		this.m_retrievalPolicy = retrievalPolicy;
		this.m_subdir = subdir + (subdir.endsWith(File.separator) ? "" : File.separator);
	}
	
	/**
	 * Creates a DoubleValueConfig with initial value zero.
	 * @param fileNamePostfix The short human readable title, used as a postfix in conf files (e.g. for use as dialog box title)
	 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
	 * @see MetaData#MetaData(String)
	 */
	public MetaDataRetriever(String fileNamePostfix, RetrievalPolicy retrievalPolicy) {
		super(fileNamePostfix);
		this.m_retrievalPolicy = retrievalPolicy;
		this.m_subdir = "";
	}
	
	/**
	 * Converts an ordinary metadata container into a metadat retriever
	 * @param config an ordinary metadata container 
	 * @param retrievalPolicy The policy to retrieve the parameters (known values, config file, user input...)
	 */
	public MetaDataRetriever(MetaDataContainer config, RetrievalPolicy retrievalPolicy){
		super(config.getTitle());
		this.m_retrievalPolicy = retrievalPolicy;
		merge(config);
	}
	
	
	/**
	 * Updates the model's value from the config metadata
	 */
	public abstract void updateModelFromConfig();
	
	
	/**
	 * Updates the model's value from the config metadata
	 */
	public abstract void retrieveConfigFromModel();
	
	/**
	 * @return The postfix of file name corresponding to the configuration files for 3D calibration
	 */
	public String getFileNamePostfix(){
		return getTitle();
	}
	
	/**
	 * @return The postfix of file name corresponding to the configuration files for 3D calibration
	 */
	public String getDefaultConfigFileName(){
		return "default__" + this.getTitle() + "." + MetaData.getFileExtension();
	}	
	
	/**
	 * Allows to set the GUI framework for dialogs
	 * @param guiFramework
	 */
	public static void setGuiFramework(GuiFramework guiFramework){
		m_guiFramework = guiFramework;
	}
	
	/**
	 * Allows to retrieve the GUI framework for dialogs
	 * @return the framework for dialogs
	 */
	protected static GuiFramework getGuiFramework(){
		return m_guiFramework;
	}
	
	/**
	 * @return a full copy of this instance.
	 */
	public abstract MetaDataRetriever duplicate();
	
	protected void addDuplicate(MetaDataRetriever config){
		this.merge(config);
	}
	
	/**
	 * Prints a message on the standard error stream suggesting to save calibration data.
	 * Pops a message up in case the image is NOT clibrated (failure...)
	 */
	public void debug(){
		System.err.println("You might want to save the parameters in a file "+getDefaultConfigFileName()+":");
		System.err.println(super.toString());
	}
	
	/**
	 * Allows to drop data from a ConfigRetriever by definitely determining some parameters from
	 * config files, in accordance with some policy.
	 * The method successively tries different directory names and default file names
	 * to read the parameters.
	 * @param guessDir A temptative directory where to seek first for the metadata
	 * @return null if no data is to be prompted from the user, and the parameters to ask for otherwise.
	 */
	protected MetaDataContainer dropRetrivableFromFile(String guessDir){
				
		if (this.m_retrievalPolicy == RetrievalPolicy.UseKnownValues){
			return null;
		}
		
		MetaDataParserFile configReader = new MetaDataParserFile();
		String directory = guessDir;
		System.err.println("directory 1 = " + directory+",search file: "+getConfigFileName());
		
		MetaDataContainer simplifiedConfig = this;

		if (MetaDataParserFile.fileExists(directory)){
			simplifiedConfig = configReader.readConfig(simplifiedConfig, directory);
		}
		
		if (simplifiedConfig.isEmpty()){
			return simplifiedConfig;
		}

		boolean changed = simplifiedConfig.setFileNameToDefault();


		if (changed && MetaDataParserFile.fileExists(directory)){
			simplifiedConfig = configReader.readConfig(simplifiedConfig, directory);
		}
		
		if (simplifiedConfig.isEmpty()){
			return simplifiedConfig;
		}
		
		directory = getGuiFramework().getFileHelper().retrieveLastDirectory(guessDir);
		if (directory != guessDir && MetaDataParserFile.fileExists(directory)){
			System.err.println("directory 2 = " + directory);
			simplifiedConfig = configReader.readConfig(simplifiedConfig, directory);
			if (simplifiedConfig.isEmpty()){
				return simplifiedConfig;
			}	
		}			
		
		String globalDefaultDir = GlobalOptions.getGlobalMetaDataDir() + 
					(GlobalOptions.getGlobalMetaDataDir().endsWith(File.separator) ? "" : File.separator) + 
					this.m_subdir;
		if (directory != globalDefaultDir){
			directory = globalDefaultDir;
			System.err.println("directory 3 = " + directory);
			
			if (MetaDataParserFile.fileExists(directory)){
				simplifiedConfig = configReader.readConfig(simplifiedConfig, directory);
			}
		}
		
		return simplifiedConfig;
	}

	/**
	 * Retrieves the scale for the voxels.
	 * either from a configuration file, or from a dialog box. If the image
	 * is already calibrated and force is false, the image is left unchanged.
	 * Otherwise, a configuration file is sought, and if that fails, a dialog asks the user for data.
	 * 
	 * @param dialogTitle Title for the dialog box
	 * @param config the configuration data to ask the user
	 */
	public static void promptUserDialog(String dialogTitle, MetaDataContainer config){
		

		if (!config.isEmpty()){
			getGuiFramework().getGenericDialog().showDialog(dialogTitle, config);
			System.err.println("Metadata provided by the user: \n" + config);
		}
	}
	
	/**
	 * Allows to retrieve all parameters in a collection of metadata contents, each according to its policy.
	 * @param configRetrievers The input collection of Config Retrievers
	 * @param guessDir A temptative directory where to seek first for the metadata
	 * @param dialogTitle Title for the dialog box, if any, that prompts the user for data
	 * @throws IOException In case of fire read error.
	 */
	public static void retrieveConfigsUsingPolicies(ArrayList<MetaDataRetriever> configRetrievers, 
											  String guessDir, String dialogTitle) throws IOException {
		
		MetaDataContainer simplifiedConfig = new MetaDataContainer(dialogTitle, dialogTitle);
		for (MetaDataRetriever config: configRetrievers){
			simplifiedConfig.merge(config.dropRetrivableFromFile(guessDir));
		}
		for (MetaDataRetriever config: configRetrievers){
			if (config.m_retrievalPolicy == RetrievalPolicy.ForceDialog ||
				config.m_retrievalPolicy == RetrievalPolicy.Unspecified){
				simplifiedConfig.merge(config);
			}
		}
				
		promptUserDialog(dialogTitle, simplifiedConfig);
		
		for (MetaDataRetriever config: configRetrievers){
			config.updateModelFromConfig();
			config.m_retrievalPolicy = RetrievalPolicy.UseKnownValues;
		}
	}
}
