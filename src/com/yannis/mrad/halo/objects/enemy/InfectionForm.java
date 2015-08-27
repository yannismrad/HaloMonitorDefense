package com.yannis.mrad.halo.objects.enemy;
import android.util.Log;

import com.threed.jpct.Object3D;

/**
 * Classe InfectionForm
 * @author Yannis
 * 
 * Flood d'infection
 *
 */
public class InfectionForm extends Enemy {
	
	public InfectionForm()
	{
		super();
	}

	public InfectionForm(float xPos, float yPos, int health, float speed, Object3D appearance) {
		super(xPos, yPos, health, speed, appearance);
	}

	public InfectionForm(float xPos, float yPos, Object3D appearance) {
		super(xPos, yPos, appearance);
	}
	
	public InfectionForm clone()
	{
		InfectionForm enemy = null;
		Enemy e = super.clone();
		enemy = (InfectionForm)e;
		Log.d("TAG", "Cloning infection form");
		return enemy;
	}
	

}
