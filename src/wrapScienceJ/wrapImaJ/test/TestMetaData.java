/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: TestMetaData.java                                                  * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.test;


import java.io.IOException;

import wrapScienceJ.factory.image.ImageCoreFactoryIJ;
import wrapScienceJ.factory.render.RenderToolFactoryIJ3D;
import wrapScienceJ.metaData.container.MetaDataRetriever.RetrievalPolicy;
import wrapScienceJ.metaData.sets.DoubleSetSingle;
import wrapScienceJ.utils.ResourcesMonitor;
import wrapScienceJ.wrapImaJ.connectivity.ConnectedComponent;
import wrapScienceJ.wrapImaJ.connectivity.LabelingPolicy;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.ThresholdingOption;

/**
 * Allows to test loading and saving meta data along with images and resources.
 * 
 * TODO FURTHER DOCUMENTATION ABOUT GENERIC PROCESSES AND METADATA IS TO BE WRITTEN
 * 
 */
public class TestMetaData {
	
	ImageCore m_image;

	/** Metadata type that contains a single value with type double */
	DoubleSetSingle m_nucleusVolumeThreshold;

	
	/**
	 * @param inputImagePath Path to the TIF image file, possibly with metaData
	 * @param policy Policy to retrieve the meta data ("meta data files only", "force dialog")
	 * @throws IOException In case of read error.
	 */	
	TestMetaData(String inputImagePath, RetrievalPolicy policy) throws IOException{
		
		this.m_image = ImageCoreFactoryIJ.getInstance()
										 .getImageCore(inputImagePath, 
													false, // Convert to 8 bits
													false, // Maximize values range
													policy);
		
		
		this.m_nucleusVolumeThreshold = new DoubleSetSingle("NucleusVolumeThreshold", 
															"Nucleus lowest volume",
															policy, 1.0);
		this.m_image.addMetaData(this.m_nucleusVolumeThreshold);
		this.m_image.retrieveMetaData();
	}


	/**
	 * @throws IllegalStateException In case of overflow of the number of connected components.
	 */
	public void testImageMetaData() throws IllegalStateException{
		
		System.err.println("Blurring image...");
		this.m_image.getImageBlur().getBinomialBlur(6, 6, 1);
		System.err.println("Blurr done. Thresholding...");
		this.m_image.getImageThresholding().thresholdImageAndBinarize(ThresholdingOption.Otsu, true);
		System.err.print("Thresholding done. ");
        ConnectedComponent cc;

		System.err.println("Labelling Components...");
		cc = ConnectedComponent.getLabeledComponents(
				this.m_image, 
				LabelingPolicy.Full3D,
				255, true, this.m_nucleusVolumeThreshold.getValue(), true);
        // print connected components informations :
        System.err.println(cc);
		System.err.println("Ressource performance:\n" 
				+ ResourcesMonitor.getRessourceInfo());
		
		this.m_image.getPreferedRenderTool().display(this.m_image);
		RenderToolFactoryIJ3D.getInstance().getRenderTool().display(this.m_image);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TestMetaData instance = new TestMetaData(TestImageThresholding.getSampleImageGray8(1), 
													 RetrievalPolicy.TryConfFileThenDialog);
			//TestMetaData instance = new TestMetaData(TestCoreMethods.getSampleImageGray8(1), 
			//		 								RetrievalPolicy.ForceDialog);			
			instance.testImageMetaData();
		} catch (IllegalStateException e) {
			System.err.println("Problème de construction des composantes connexes");
			e.printStackTrace();
		} catch (IOException e){
			System.err.println("Problème de chargement de fichier");
			e.printStackTrace();
		}
	}

}
