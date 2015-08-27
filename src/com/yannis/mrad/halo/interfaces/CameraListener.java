package com.yannis.mrad.halo.interfaces;

/**
 * Interface CameraListener
 * @author Yannis
 * 
 * Utilis�e pour les �v�nements li�s au d�placement de la cam�ra de la sc�ne
 *
 */
public interface CameraListener {

	/**
	 * M�thode onCameraChanged (changement de position de la cam�ra)
	 * @param xStart
	 * @param xEnd
	 * @param yStart
	 * @param yEnd
	 */
	public void onCameraChanged(float xStart, float xEnd, float yStart, float yEnd);
	
	/**
	 * M�thode onCameraZoomed (zoom de camera)
	 */
	public void onCameraZoomed();

}
