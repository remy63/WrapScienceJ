/******************************************************************************\
*     Copyright (C) 2017 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File:  GaussianBlurIJ.java                                               * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 
package wrapScienceJ.wrapImaJ.wrappers.imagej.core.operation;


import wrapScienceJ.wrapImaJ.core.VoxelDouble;
import wrapScienceJ.wrapImaJ.core.VoxelInt;
import wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric;
import wrapScienceJ.wrapImaJ.wrappers.imagej.core.ImageCoreIJ;
import wrapScienceJ.wrapImaJ.core.operation.convolve.generic.GaussianBlurGeneric;
import wrapScienceJ.wrapImaJ.core.operation.generic.ImageSignPolicyEmbedGeneric;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.GaussianBlur;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;


/**
 * @author remy
 *
 */
public class GaussianBlurIJ extends GaussianBlurGeneric {

	/**
	 * Applies a Gaussian Blur on the image, with a given standard deviation in
	 * each coordinate.
	 * 
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param outImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link ImageSignPolicyEmbedGeneric#getImageEmbedding(boolean, int)} if necessary.
	 * @param sigmaX standard deviation of the Gaussian on the x coordinate
	 * @param sigmaY standard deviation of the Gaussian on the y coordinate
	 * @param sigmaZ standard deviation of the Gaussian on the z coordinate
	 * @param shiftOuputMargin Margins that were added through enlargement of input buffer.
	 */
	public GaussianBlurIJ(ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed, 
			   			  ImageSignPolicyEmbedGeneric outImageSignPolicyEmbed,
						  double sigmaX, double sigmaY, double sigmaZ,
						  VoxelInt shiftOuputMargin
						 ){
		super(inImageSignPolicyEmbed, outImageSignPolicyEmbed, sigmaX, sigmaY, sigmaZ, shiftOuputMargin);
	}
	
	
	/**
	 * Applies a Gaussian Blur on the image, with a given standard deviation in
	 * each coordinate.
	 * This method does the same as 
	 * {@link #GaussianBlurIJ(ImageSignPolicyEmbedGeneric, ImageSignPolicyEmbedGeneric, double, double, double, VoxelInt)}
	 * except that the standard deviations are given in (approximately) multiples of the calibration
	 * data (Voxels edges lengths in a given length unit) instead of number of voxels.
	 * @param inImageSignPolicyEmbed An original image on which to apply the mask.
	 * @param outImageSignPolicyEmbed An original image on which to apply the mask.
	 * 				The image must allow values to be added AND subtracted without overflow.
	 * 				Use {@link ImageSignPolicyEmbedGeneric#getImageEmbedding(boolean, int)} if necessary.
	 * 
	 * @param realSigmaX standard deviation of the Gaussian on the x coordinate
	 * @param realSigmaY standard deviation of the Gaussian on the y coordinate
	 * @param realSigmaZ standard deviation of the Gaussian on the z coordinate
	 * @param voxelEdgesLength The voxel's scale (edges' length) used as a factor to estimate
	 * 						   actual skipping steps in each direction.
	 * @param shiftOuputMargin Margins that were added through enlargement of input buffer.
	 */
	public GaussianBlurIJ(ImageSignPolicyEmbedGeneric inImageSignPolicyEmbed, 
 						  ImageSignPolicyEmbedGeneric outImageSignPolicyEmbed,
						  double realSigmaX, double realSigmaY, double realSigmaZ,
						  VoxelDouble voxelEdgesLength,
						  VoxelInt shiftOuputMargin){
		super(inImageSignPolicyEmbed, outImageSignPolicyEmbed,
				realSigmaX, realSigmaY, realSigmaZ, voxelEdgesLength, shiftOuputMargin);
	}
	
	
	/**
	 * @see wrapScienceJ.wrapImaJ.core.operation.convolve.generic.ConvolutionBaseGeneric#applyMask()
	 */
	@Override
	public ConvolutionBaseGeneric applyMask(){
		
		ImagePlus imp = ((ImageCoreIJ)getInputImageRaw()).getImp();
		
		imp.deleteRoi();
		ImageStack stack = imp.getStack();
		if (this.m_sigmaX > 0.0 || this.m_sigmaY > 0.0) {
			GaussianBlur gb = new GaussianBlur();
			gb.setNPasses(imp.getStackSize());
			for (int i = 1; i <= imp.getStackSize(); i++) {
				ImageProcessor ip = stack.getProcessor(i);
				double accuracy = (imp.getBitDepth() == 8 || 
								   imp.getBitDepth() == 24) ? 0.002 : 0.0002;
				gb.blurGaussian(ip, this.m_sigmaX, this.m_sigmaY, accuracy);
				if (i%10==9){
					System.gc();
				}
			}
		}
		if (this.m_sigmaZ > 0.0) {
			blurZ(stack, this.m_sigmaZ);
		}
		imp.setStack(stack);
		
		getOutImageSignPolicyEmbed().copyFrom(getInImageSignPolicyEmbed(), this.m_shiftOuputMargin, true);
		
		return getIdentityMask();
	}
	

	/**
	 * Method for internal use of the method {@link #gaussianBlur(double, double, double)}.
	 */
	private static void blurZ(ImageStack stack, double sigmaZ) {
		GaussianBlur gb = new GaussianBlur();
		double accuracy = (stack.getBitDepth() == 8 || stack.getBitDepth() == 24) ? 0.002 : 0.0002;
		int w = stack.getWidth(), h = stack.getHeight(), d = stack.getSize();
		float[] zpixels = null;
		FloatProcessor fp = null;
		int channels = stack.getProcessor(1).getNChannels();
		for (int y = 0; y < h; y++) {
			for (int channel = 0; channel < channels; channel++) {
				zpixels = stack.getVoxels(0, y, 0, w, 1, d, zpixels, channel);
				if (fp == null) {
					fp = new FloatProcessor(w, d, zpixels);
				}
				gb.blur1Direction(fp, sigmaZ, accuracy, false, 0);
				stack.setVoxels(0, y, 0, w, 1, d, zpixels, channel);
			}
		}
	}

}
