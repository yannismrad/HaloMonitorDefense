package com.yannis.mrad.halo.interfaces;

/**
 * Interface CameraListener
 * @author Yannis
 * 
 * Utilisée pour les évènements liés au déplacement de la caméra de la scène
 *
 */
public interface CameraListener {

	/**
	 * Méthode onCameraChanged (changement de position de la caméra)
	 * @param xStart
	 * @param xEnd
	 * @param yStart
	 * @param yEnd
	 */
	public void onCameraChanged(float xStart, float xEnd, float yStart, float yEnd);
	
	/**
	 * Méthode onCameraZoomed (zoom de camera)
	 */
	public void onCameraZoomed();

}
