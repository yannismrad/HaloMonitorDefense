package com.yannis.mrad.halo.interfaces;

import com.yannis.mrad.halo.graphicsentity.TowerBlock;

/**
 * Interface SelectionListener
 * @author Yannis
 * 
 * Utilisée pour les évènements de sélection d'objets
 *
 */
public interface SelectionListener {
	
	public void onTowerBlockSelected();
	public void onTowerBlockUnselected();

}
