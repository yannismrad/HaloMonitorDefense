package com.yannis.mrad.halo.gameentity;

import com.threed.jpct.Object3D;

/**
 * Class PlasmaTower
 * @author Yannis
 *
 *Représente un type de tour (de base)
 */
public class PlasmaTower extends Tower{
	
	/**
	 * Constructeur de PlasmaTower
	 * @param model
	 * @param attack
	 * @param attackSpeed
	 */
	public PlasmaTower(Object3D model, Object3D top, float attack, long attackSpeed)
	{
		super(model,top,attack,attackSpeed);
	}

}
