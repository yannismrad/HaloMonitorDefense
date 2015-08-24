package com.yannis.mrad.halo.graphicsentity;

import android.content.Context;

import com.threed.jpct.Object3D;
import com.threed.jpct.World;

/**
 * Class WayPoint
 * @author Yannis
 *
 *	Représente un point de passage sur une lane
 */
public class WayPoint {
	protected Object3D block;
	protected Context mContext;
	protected World world;
	protected String textureId, textureNormalId;
	
	/**
	 * Constructeur de WayPoint
	 * @param ctx
	 * @param world
	 */
	public WayPoint(Context ctx, World world)
	{
		this.mContext = ctx;
		this.world = world;
	}
	
	/**
	 * Méthode de déplacement du bloc à un emplacement donné
	 * @param x
	 * @param y
	 */
	public void moveBlock(float dX, float dY, float dZ)
	{
		block.translate(dX,dY,dZ);
	}

	public Object3D getBlock() {
		return block;
	}

	public void setBlock(Object3D block) {
		this.block = block;
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public String getTextureId() {
		return textureId;
	}

	public void setTextureId(String textureId) {
		this.textureId = textureId;
	}

	public String getTextureNormalId() {
		return textureNormalId;
	}

	public void setTextureNormalId(String textureNormalId) {
		this.textureNormalId = textureNormalId;
	}
	
	

}
