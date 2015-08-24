package com.yannis.mrad.halo.objects.enemy;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.yannis.mrad.halo.gameentity.Tower;

/**
 * Class Particle
 * @author Yannis
 * 
 * Représente une particule (tir de tour)
 *
 */
public class Particle {
	private Tower origin;
	private Enemy target;
	private Object3D appearance;
	
	/**
	 * Constructeur de Particle
	 * @param origin
	 * @param destination
	 */
	public Particle(Tower origin, Enemy destination)
	{
		this.origin = origin;
		this.target = destination;
	}
	
	/**
	 * Constructeur de Particle
	 * @param appearance
	 * @param origin
	 * @param destination
	 */
	public Particle(Object3D appearance, Tower origin, Enemy destination)
	{
		this.appearance = appearance;
		this.origin = origin;
		this.target = destination;
	}

	public Tower getOrigin() {
		return origin;
	}

	public void setOrigin(Tower origin) {
		this.origin = origin;
	}

	public Enemy getTarget() {
		return target;
	}

	public void setTarget(Enemy target) {
		this.target = target;
	}

	public Object3D getAppearance() {
		return appearance;
	}

	public void setAppearance(Object3D appearance) {
		this.appearance = appearance;
	}
	
	

}
