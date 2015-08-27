package com.yannis.mrad.halo.gameentity;

import java.util.ArrayList;

import android.util.Log;

import com.threed.jpct.Object3D;
import com.yannis.mrad.halo.graphicsentity.PathBlock;
import com.yannis.mrad.halo.graphicsentity.TowerBlock;
import com.yannis.mrad.halo.objects.enemy.Enemy;

/**
 * Class Tower
 * @author Yannis M'RAD
 * 
 * Représente une tour associée à un bloc de tour
 *
 */
public class Tower {
	protected String towerId;
	protected Object3D towerModel, top; //apparence (corps + sommet)
	protected float attack; //puissance d'attaque
	protected long attackSpeed; //vitesse d'attaque
	protected int shotSpeed; //vitesse d'un projectile de cette tour
	protected int status; //statut  = attaque, cooldown, idle
	protected TowerBlock towerBlock;
	protected Enemy currentTarget; //cible visée
	protected ArrayList<PathBlock> radiusBlocks; //liste des blocs à portée de tir
	
	
	protected final static int STATUS_IDLE = 0, STATUS_ATTACK = 1, STATUS_CDOWN = 2;

	/**
	 * Constructeur de Tower
	 * @param model
	 * @param attack
	 * @param attackSpeed
	 */
	public Tower(Object3D model, Object3D top, float attack, long attackSpeed)
	{
		this.towerModel = model;
		this.top = top;
		this.attack = attack;
		this.attackSpeed = attackSpeed;
		this.status = STATUS_IDLE;
		this.radiusBlocks = new ArrayList<PathBlock>();
	}
	
	/**
	 * Méthode permettant de choisir les blocs atteignables par les tirs de la tour
	 * @param radius
	 */
	public void setBlockAttackRadius(int radius)
	{
		PathBlock adjacentBlock = towerBlock.getAdjacentBlock();
		
		//Rayon 1 : seul le bloc adjacent à la tour est atteignable
		if( radius == 1)
		{
			radiusBlocks.add(adjacentBlock);
			Log.d("TAG","Radius init : radius = 1, only reachable block : "+adjacentBlock.getBlockId());
			
		}
		
		/*
		 * Rayon > 1 :
		 * Si c'est 2 : on aura le bloc adjacent + les 2 blocs adjacents (previous et next)  à ce bloc
		 * 3 : Le bloc adjacent A , les deux blocs adjacents A+1 A-1 à ce bloc, les deux blocs adjacents A+2 A-2
		 * etc ....
		 */
		else if (radius > 1)
		{
			radiusBlocks.add(adjacentBlock);
			PathBlock previous = adjacentBlock.getPreviousBlock();
			PathBlock next = adjacentBlock.getNextBlock();
			
			//Tant que le nombre de blocs à ajouter dans le rayon est > 0
			while(radius > 0)
			{
				if(previous !=null) //previous = null <=> c'est le 1er pathBlock de la lane
				{
					Log.d("TAG","Radius init : previous block found : "+previous.getBlockId());
					radiusBlocks.add(previous);
					previous = previous.getPreviousBlock(); //prendre le précédent du précédent
				}
				
				else
				{
					Log.d("TAG","Radius init : no previous block found for block : "+adjacentBlock.getBlockId());
				}
				
				if(next != null) //next = null <=> c'est le dernier pathBlock de la lane
				{
					Log.d("TAG","Radius init : next block found : "+next.getBlockId());
					radiusBlocks.add(next);
					next = next.getNextBlock(); //prendre le suivant du suivant
				}
				
				else
				{
					Log.d("TAG","Radius init : no next block found for block : "+adjacentBlock.getBlockId());
				}
				
				radius--;
				
				if(radius == 0)
				{
					Log.d("TAG", "Radius initialization done for tower");
				}
			}
		}
	}

	public Object3D getTowerModel() {
		return towerModel;
	}

	public void setTowerModel(Object3D towerModel) {
		this.towerModel = towerModel;
	}
	

	public Object3D getTop() {
		return top;
	}

	public void setTop(Object3D top) {
		this.top = top;
	}

	public float getAttack() {
		return attack;
	}

	public void setAttack(float attack) {
		this.attack = attack;
	}

	public long getAttackSpeed() {
		return attackSpeed;
	}

	public void setAttackSpeed(long attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Enemy getCurrentTarget() {
		return currentTarget;
	}

	public void setCurrentTarget(Enemy currentTarget) {
		this.currentTarget = currentTarget;
	}

	public ArrayList<PathBlock> getRadiusBlocks() {
		return radiusBlocks;
	}

	public void setRadiusBlocks(ArrayList<PathBlock> radiusBlocks) {
		this.radiusBlocks = radiusBlocks;
	}

	public TowerBlock getTowerBlock() {
		return towerBlock;
	}

	public void setTowerBlock(TowerBlock towerBlock) {
		this.towerBlock = towerBlock;
	}

	public String getTowerId() {
		return towerId;
	}

	public void setTowerId(String towerId) {
		this.towerId = towerId;
	}

	public int getShotSpeed() {
		return shotSpeed;
	}

	public void setShotSpeed(int shotSpeed) {
		this.shotSpeed = shotSpeed;
	}
	
	

}
