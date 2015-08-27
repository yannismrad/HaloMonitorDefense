package com.yannis.mrad.halo.objects.enemy;
import android.util.Log;

import com.threed.jpct.Object3D;
import com.yannis.mrad.halo.R;

/**
 * Class CarrierForm
 * @author Yannis
 *
 *Transporteur
 */
public class CarrierForm extends Enemy{

	public CarrierForm() {
		super();
	}

	public CarrierForm(float xPos, float yPos, int health, float speed,Object3D appearance) {
		super(xPos, yPos, health, speed, appearance);
		// TODO Auto-generated constructor stub
	}

	public CarrierForm(float xPos, float yPos, Object3D appearance) {
		super(xPos, yPos, appearance);
	}
	
	public CarrierForm clone()
	{
		CarrierForm enemy = null;
		Enemy e = super.clone();
		enemy = (CarrierForm)e;
		Log.d("TAG", "Cloning Carrier Form");
		return enemy;
	}
	
	

}
