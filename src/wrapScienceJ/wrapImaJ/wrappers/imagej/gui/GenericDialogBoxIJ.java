/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: GenericDialogBoxIJ.java                                            * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Panel;

import ij.gui.GenericDialog;
import wrapScienceJ.gui.GenericDialogBox;
import wrapScienceJ.metaData.container.MetaDataContainer;
import wrapScienceJ.metaData.container.attribute.MetaChoiceInList;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeChoiceList;
import wrapScienceJ.metaData.container.attribute.baseTypes.AttributeData;


/**
 * Automatically generated dialog box fir ImageJ plugin or use of the ImageJ based wrapper.
 *
 */
public class GenericDialogBoxIJ implements GenericDialogBox {


	/**
	 * @see wrapScienceJ.gui.GenericDialogBox#showDialog(java.lang.String, wrapScienceJ.metaData.container.MetaDataContainer)
	 */
	@Override
	public void showDialog(String dialogTitle, MetaDataContainer configData) {
		GenericDialog gd = new GenericDialog(dialogTitle);
		for (int a = 0 ; a < configData.getSize() ; a++){
			AttributeData attrib = configData.getAttribute(a);
			switch(attrib.getType()){
			case StringAttrib :
				if (!attrib.getShortDescription().equals(MetaDataContainer
						.m_keyShortDescriptionForConfigFileName)){
					if (attrib.getShortDescription().equals(MetaDataContainer
													.m_keyShortDescriptionForTitle)){
					// Insets determines the padding
						gd.addPanel(new Panel(), GridBagConstraints.EAST, new Insets(5, 5, 5, 5));
						gd.addMessage((String)attrib.getAttributeValue());
					}else{
						gd.addStringField(attrib.getShortDescription()+": ", (String)attrib.getAttributeValue());
					}
				}
				break;
			case FloatAttrib :
				gd.addNumericField(attrib.getShortDescription()+": ", 
								   ((Float)attrib.getAttributeValue()).doubleValue(), 4);
				break;
			case DoubleAttrib :				
				gd.addNumericField(attrib.getShortDescription()+": ", 
						   ((Double)attrib.getAttributeValue()).doubleValue(), 4);
				break;
			case IntAttrib :				
				gd.addNumericField(attrib.getShortDescription()+": ", 
						   ((Integer)attrib.getAttributeValue()).doubleValue(), 0);
				break;
			case BooleanAttrib :				
				gd.addCheckbox(attrib.getShortDescription()+": ", 
						   ((Boolean)attrib.getAttributeValue()).booleanValue());
				break;
			case StringNoAttrib :
				gd.addMessage((String)attrib.getAttributeValue());
				break;
			case ChoiceInListAttrib :
				gd.addChoice(attrib.getShortDescription()+": ",
						((AttributeChoiceList)attrib.getAttributeValue()).getChoices(),
						((AttributeChoiceList)attrib.getAttributeValue()).getValue());
				break;
			default:
				throw new IllegalArgumentException("Sorry... Type of field not handled in Dialog Boxes.");
			}
			
		}
		
	    gd.showDialog();
	    if (gd.wasCanceled()){
	    	return;
	    }		
	    
	    for (int a = 0 ; a < configData.getSize() ; a++){
			AttributeData attrib = configData.getAttribute(a);
			switch(attrib.getType()){
			case StringAttrib :
				if (!attrib.getShortDescription().equals(MetaDataContainer
						.m_keyShortDescriptionForConfigFileName)){
					if (attrib.getShortDescription().equals(MetaDataContainer
													.m_keyShortDescriptionForTitle)){
						// Nothing to retrieve
					}else{
						attrib.setAttributeValue(gd.getNextString());
					}
				}
				break;
			case FloatAttrib :
				attrib.setAttributeValue(new Float(gd.getNextNumber()));
				break;
			case DoubleAttrib :				
				attrib.setAttributeValue(new Double(gd.getNextNumber()));
				break;
			case IntAttrib :				
				attrib.setAttributeValue(new Integer((int)gd.getNextNumber()));
				break;
			case BooleanAttrib :				
				attrib.setAttributeValue(new Boolean(gd.getNextBoolean()));
				break;
			case StringNoAttrib :				
				// Nothing to do
				break;
			case ChoiceInListAttrib :
				((MetaChoiceInList)attrib).setValue(gd.getNextChoice());
				break;
			default:
				throw new IllegalArgumentException("Sorry... Type of field not handled in Dialog Boxes.");
			}
	    }

	}

}
