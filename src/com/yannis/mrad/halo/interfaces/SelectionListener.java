package com.yannis.mrad.halo.interfaces;

import com.yannis.mrad.halo.graphicsentity.TowerBlock;

/**
 * Interface SelectionListener
 * @author Yannis
 * 
 * Utilis�e pour les �v�nements de s�lection d'objets
 *
 */
public interface SelectionListener {
	
	public void onTowerBlockSelected();
	public void onTowerBlockUnselected();

}
