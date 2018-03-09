/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ProcessInputOutput.java                                            * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.process;

import wrapScienceJ.resource.ModelCore;


/**
 * @author remy
 *
 */
public abstract class ProcessInputOutput implements InputOutputPolicy {
	
	
	
	/**
	 * Output object supposed to be an instance of ImageCoreIJ
	 */
	protected Object m_outputObject;
	
	/**
	 * Enumeration of different possible cases for the output and processing of
	 * a PluginFilterGeneric.
	 * Only in the case of {@link #EqualsInput} is the input image assumed to be changed.
	 * In all other cases, the process must leave the image unchanged.
	 * In case of {@link #CreatedFromInputCopy}, the input image has to be duplicated to
	 * initialize the current image before running the process.
	 */
	public enum OutputDataKind {
		
		/**
		 * The output object has same reference as the input image
		 */
		EqualsInput(1),
		/**
		 * Implements ImageCore that is created by initial copy of the input image.
		 */
		CreatedFromInputCopy(2),
		/**
		 * Implements ImageCore but is different from the input image. Unlikely to come from a duplicate of input.
		 */
		ImageOtherThanInput(3),
		/**
		 * A List<ImageCore> of a few instances that Implement ImageCore. Those images can be simultaneously displayed.
		 */
		FewImages(4),
		/**
		 * A List<ImageCore> of instances that Implement ImageCore. Those images can be simultaneously
		 * displayed by default.
		 */		
		ImageCollection(5),
		/**
		 * The output can be anything and the {@link #getOutputDataKind()} method must be overridden
		 * to use and process the output data.
		 */		
		OtherUnspecified(6);	
		
		private final int m_outputkind;

		/**
		 * sets the kind ou output of the process
		 * @param value
		 */
		private OutputDataKind(int outputKind) throws IllegalArgumentException {
			if (outputKind < 1 || outputKind > 6){
				throw new IllegalArgumentException("Undefined Display and Rendering Tool.");
			}
			this.m_outputkind = outputKind;
		}
	
		/**
		 * @return the integer representation for the wrapper's ID
		 */
		public int getValue() {
			return this.m_outputkind;
		}
	
		/** 
		 * @return a human readable description of the wrapper.
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			switch(this.m_outputkind){
				case 1: 
					return "The output object has same reference as the input image, which is changed";
				case 2:
					return "Implements ImageCore that is created by initial copy of the input image";
				case 3:
					return "Implements ImageCore but is different from the input image";
				case 4:
					return "A List<ImageCore> of a few instances that Implement ImageCore";
				case 5:
					return "A List<ImageCore> of instances that Implement ImageCore and output processing must be defined";
				case 6:
					return "The output can be anything and output processing must be defined";
				default:
					throw new IllegalArgumentException("Unknown Wrapper ID");
			}
		}
	} // End of enum OutputDataKind
	
	/** 
	 * Defines the kind of output of the process for generic post-processing
	 * By default, it is {@link OutputDataKind#EqualsInput}
	 */
	protected OutputDataKind m_outputDataKind = OutputDataKind.EqualsInput;
	

	
	/**
	 * @return The output object of the process
	 */
	@Override
	public Object getOutputObject(){
		return this.m_outputObject;
	}




	/**
	 * @see wrapScienceJ.process.InputOutputPolicy#getInputResourcePath()
	 */
	@Override
	public abstract String getInputResourcePath();





	/**
	 * @see wrapScienceJ.process.InputOutputPolicy#getInputResourceMetaData()
	 */
	@Override
	public abstract ModelCore getInputResourceMetaData();




	/**
	 * @see wrapScienceJ.process.InputOutputPolicy#getOutputDataKind()
	 */
	@Override
	public OutputDataKind getOutputDataKind() {
		return this.m_outputDataKind;
	}





	/**
	 * @see wrapScienceJ.process.InputOutputPolicy#setOutputDataKind(wrapScienceJ.process.ProcessInputOutput.OutputDataKind)
	 */
	@Override
	public void setOutputDataKind(OutputDataKind outputDataKind) {
		this.m_outputDataKind = outputDataKind;
	}
	

}
