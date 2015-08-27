package com.yannis.mrad.halo.objects.enemy;
import android.util.Log;

import com.threed.jpct.Object3D;
import com.yannis.mrad.halo.R;

/**
 * Classe CombatForm
 * @author Yannis
 * 
 * Flood de combat
 *
 */
public class CombatForm extends Enemy{

	public CombatForm() {
		super();
	}

	public CombatForm(float xPos, float yPos, int health, float speed, Object3D appearance) {
		super(xPos, yPos, health, speed, appearance);
	}

	public CombatForm(float xPos, float yPos, Object3D appearance) {
		super(xPos, yPos, appearance);
	}
	
	public CombatForm clone()
	{
		CombatForm enemy = null;
		Enemy e = super.clone();
		enemy = (CombatForm)e;
		Log.d("TAG", "Cloning Combat Form");
		return enemy;
	}
	
	

}
