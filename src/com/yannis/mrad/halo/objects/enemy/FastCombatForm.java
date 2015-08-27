package com.yannis.mrad.halo.objects.enemy;
import android.util.Log;

import com.threed.jpct.Object3D;
import com.yannis.mrad.halo.R;

/**
 * Classe FastCombatForm
 * @author Yannis
 * 
 * Flood de combat rapide
 *
 */
public class FastCombatForm extends CombatForm{

	public FastCombatForm() {
		super();
	}

	public FastCombatForm(float xPos, float yPos, int health, float speed,Object3D appearance) {
		super(xPos, yPos, health, speed, appearance);
	}

	public FastCombatForm(float xPos, float yPos, Object3D appearance) {
		super(xPos, yPos, appearance);
	}
	
	public FastCombatForm clone()
	{
		FastCombatForm enemy = null;
		Enemy e = super.clone();
		enemy = (FastCombatForm)e;
		Log.d("TAG", "Cloning Fast Combat Form");
		return enemy;
	}
	
	

}
