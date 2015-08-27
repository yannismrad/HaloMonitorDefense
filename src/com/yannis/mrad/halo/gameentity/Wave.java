package com.yannis.mrad.halo.gameentity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.yannis.mrad.halo.objects.enemy.Enemy;

/**
 * Class Wave
 * @author Yannis
 *
 *Représente une vague d'ennemis
 */
public class Wave {
	private int number;
	private HashMap<Enemy,Integer> enemies; //Type-nombre
	
	/**
	 * Constructeur de Wave
	 * @param number
	 */
	public Wave(int number) {
		this.number = number;
		this.enemies = new HashMap<Enemy,Integer>();
	}
	
	/**
	 * Méthode qui retourne le nombre de total d'ennemis de la vague
	 * @return count
	 */
	public int getTotalEnemyCount()
	{
		int count = 0;
		Iterator<Map.Entry<Enemy,Integer>> it = enemies.entrySet().iterator();
		while(it.hasNext())
		{
			count += it.next().getValue();
		}
		return count;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public HashMap<Enemy, Integer> getEnemies() {
		return enemies;
	}

	public void setEnemies(HashMap<Enemy, Integer> enemies) {
		this.enemies = enemies;
	}
	
	
	
	
	
	
	

}
