/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: RenderToolIJ3D.java                                                * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package wrapScienceJ.wrapImaJ.wrappers.imagej.gui.render;


import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

import ij3d.Content;
import ij3d.Image3DUniverse;
import wrapScienceJ.wrapImaJ.core.ImageCore;
import wrapScienceJ.wrapImaJ.gui.render.RenderTool;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;

/**
 * This class implements a 3D Render Tool for ImageJ wrapper and ImageCore implementer.
 * 
 * Note that the 3D Viewer is NOT CURRENTLY OFFICIALLY SUPPORTED because its
 * existing implementation is based on a 3D Viewer Plugin for ImageJ, the maintenance
 * of which cannot be ensured. It is provided here for convenience.
 * 
 * Therefore, the interface for 3D Viewing should remain, but the implementation
 * and features are liable to change or become DEPRECATED in the future.
 * Main utility to manage Graphical User Interface implemented on the ImageJ Framework
 * Allows to include GUI elements in ImageJ plugins generated through WrapImaJ
 * 
 * Follows the Singleton Design Pattern
 */
public class RenderToolIJ3D implements RenderTool{

	/** Unique instance of this class as in the Singleton Design Pattern */
	private static RenderToolIJ3D m_instance = null;
	
	
	protected RenderToolIJ3D(){
	}
	
	/**
	 * @return The unique instance of GuiFrameworkIJ
	 */
	public static RenderToolIJ3D getInstance(){
		if (m_instance == null){
			m_instance = new RenderToolIJ3D();
		}
		return m_instance;
	}
	
	/**
	 * Opens a Graphical Human Interface window and displays the surface.
	 */
	@Override
	public void display(ImageCore image) {
		if (image instanceof ImageCoreIJ){
			Image3DUniverse univ = new Image3DUniverse();
			univ.setAutoAdjustView(true);
			univ.addVoltex(((ImageCoreIJ)image).getImp(), null, "WrapImaJ", 50, new boolean[] {true, true, true}, 2);

			Content content = univ.getContent("WrapImaJ");

			//double calibrationAverage= (image.getImageCalibration().getCalibration().getX()+
			//							image.getImageCalibration().getCalibration().getY()+
			//							image.getImageCalibration().getCalibration().getZ())
			//							/3.0;

			//Transform3D t3d = new Transform3D(new double[]{
			//		image.getImageCalibration().getVoxelLength().getX(), 0.0, 0.0, -0.5*image.getWidth()*image.getImageCalibration().getVoxelLength().getX(),
			//		0.0, image.getImageCalibration().getVoxelLength().getY(), 0.0, -0.5*image.getHeight()*image.getImageCalibration().getVoxelLength().getY(),
			//		0.0, 0.0, image.getImageCalibration().getVoxelLength().getZ(), -0.5*image.getDepth()*image.getImageCalibration().getVoxelLength().getZ(),
			//		0.0, 0.0, 0.0, 1.0
			//});
			
			Transform3D t3d = new Transform3D();
			t3d.setTranslation(new Vector3d(image.getWidth()*0.5d,
											image.getHeight()*0.5d,
											image.getDepth()*0.5d));
			t3d.setScale(new Vector3d(image.getImageCalibration().getVoxelLength().getX(),
					  image.getImageCalibration().getVoxelLength().getY(),
					  image.getImageCalibration().getVoxelLength().getZ()));
			
			content.applyTransform(t3d);
			univ.recalculateGlobalMinMax();
			univ.adjustView(content);
			univ.centerSelected(content);
			univ.show();

		}
	}

}
