/******************************************************************************\
*     Copyright (C) 2018 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  ImageConnectedComponentsGeneric.java                              * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.core.operation.generic;

import wrapScienceJ.wrapImaJ.connectivity.ConnectedComponent;
import wrapScienceJ.wrapImaJ.connectivity.LabelingPolicy;
import wrapScienceJ.wrapImaJ.connectivity.filtering.ComponentRemovalPredicate;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.core.operation.ImageConnectedComponents;

/**
 * @author remy
 *
 */
public class ImageConnectedComponentsGeneric implements ImageConnectedComponents {

	/**
	 * Underlying image on which operations are performed
	 */
	ImageCore m_image;
	
	/**
	 * @param image The image to process
	 */
	public ImageConnectedComponentsGeneric(ImageCore image){

		this.m_image = image;
	}	

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConnectedComponents#getLabeledComponents(wrapScienceJ.wrapImaJ.connectivity.LabelingPolicy, int, boolean, double, boolean)
	 */
	@Override
	public ConnectedComponent getLabeledComponents(LabelingPolicy labelingPolicy, 
												   int foregroundColor,
												   boolean removeBorderComponent, 
												   double thresholdComponentVolume,
												   boolean setRandomColors
												  ) throws IllegalStateException {
		
		return ConnectedComponent.getLabeledComponents(this.m_image,
													   labelingPolicy,
													   foregroundColor,
													   removeBorderComponent,
													   thresholdComponentVolume,
													   setRandomColors
													  );
	}

	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.ImageConnectedComponents#getLabeledComponents(wrapScienceJ.wrapImaJ.connectivity.LabelingPolicy, int, boolean, double, wrapScienceJ.wrapImaJ.connectivity.filtering.ComponentRemovalPredicate, boolean, boolean)
	 */
	@Override
	public ConnectedComponent getLabeledComponents(LabelingPolicy labelingPolicy,
												   int foregroundColor,
												   boolean removeBorderComponent,
												   double thresholdComponentVolume,
												   ComponentRemovalPredicate removalPredicate,
												   boolean keepPredicate,
												   boolean setRandomColors
												  ) throws IllegalStateException {
		
		return ConnectedComponent.getLabeledComponents(this.m_image,
													   labelingPolicy,
													   foregroundColor,
													   removeBorderComponent,
													   thresholdComponentVolume,
													   removalPredicate,
													   keepPredicate,
													   setRandomColors
													  );
	}

}
