package com.yannis.mrad.halo.objects.enemy;

import com.threed.jpct.Object3D;
import com.yannis.mrad.halo.graphicsentity.PathBlock;
import com.yannis.mrad.halo.graphicsentity.SpawnPoint;
import com.yannis.mrad.halo.graphicsentity.WayPoint;

/**
 * Class Enemy
 * @author Yannis
 * 
 * Représente un ennemi
 *
 */
public class Enemy implements Cloneable{
	protected int healthPoints;
	protected float speedFactor;
	protected float xPosition, yPosition;
	protected Object3D appearance;
	protected String id;
	protected WayPoint currentPoint;
	protected SpawnPoint spawnPoint;
	protected PathBlock currentPathBlock;
	protected boolean isOnSpawnPoint;
	protected boolean hasReachedEnd;
	
	/**
	 * Constructeur d'Enemy
	 * @param xPos
	 * @param yPos
	 */
	public Enemy(float xPos, float yPos, Object3D appearance)
	{
		this.xPosition = xPos;
		this.yPosition = yPos;
		this.appearance = appearance;
	}
	
	/**
	 * Constructeur d'Enemy
	 * @param xPos
	 * @param yPos
	 * @param health
	 * @param speed
	 */
	public Enemy(float xPos, float yPos, int health, float speed, Object3D appearance)
	{
		this.xPosition = xPos;
		this.yPosition = yPos;
		this.healthPoints = health;
		this.speedFactor = speed;
		this.appearance = appearance;
	}
	
	/**
	 * Constructeur par défaut d'Enemy
	 */
	public Enemy()
	{
		this.xPosition = 0;
		this.yPosition = 0;
		this.healthPoints = 0;
		this.speedFactor = 0;
	}
	
	public Enemy clone()
	{
		Enemy enemy = null;
		try
		{
			enemy = (Enemy) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		
		return enemy;
	}

	public int getHealthPoints() {
		return healthPoints;
	}

	public void setHealthPoints(int healthPoints) {
		this.healthPoints = healthPoints;
	}

	public float getSpeedFactor() {
		return speedFactor;
	}

	public void setSpeedFactor(float speedFactor) {
		this.speedFactor = speedFactor;
	}

	public float getxPosition() {
		return xPosition;
	}

	public void setxPosition(float xPosition) {
		this.xPosition = xPosition;
	}

	public float getyPosition() {
		return yPosition;
	}

	public void setyPosition(float yPosition) {
		this.yPosition = yPosition;
	}

	public Object3D getAppearance() {
		return appearance;
	}

	public void setAppearance(Object3D appearance) {
		this.appearance = appearance;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public WayPoint getCurrentPoint() {
		return currentPoint;
	}

	public void setCurrentPoint(WayPoint wayPoint) {
		this.currentPoint = wayPoint;
	}

	public PathBlock getCurrentPathBlock() {
		return currentPathBlock;
	}

	public void setCurrentPathBlock(PathBlock currentPathBlock) {
		this.currentPathBlock = currentPathBlock;
	}

	public SpawnPoint getSpawnPoint() {
		return spawnPoint;
	}

	public void setSpawnPoint(SpawnPoint spawnPoint) {
		this.spawnPoint = spawnPoint;
	}

	public boolean isOnSpawnPoint() {
		return isOnSpawnPoint;
	}

	public void setOnSpawnPoint(boolean isOnSpawnPoint) {
		this.isOnSpawnPoint = isOnSpawnPoint;
	}

	public boolean hasReachedEnd() {
		return hasReachedEnd;
	}

	public void setHasReachedEnd(boolean hasReachedEnd) {
		this.hasReachedEnd = hasReachedEnd;
	}

}
