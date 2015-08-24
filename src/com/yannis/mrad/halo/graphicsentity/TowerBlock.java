package com.yannis.mrad.halo.graphicsentity;

import android.util.Log;

import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.yannis.mrad.halo.gameentity.Tower;

/**
 * Class TowerBlock
 * @author Yannis
 * 
 * Représente un bloc pouvant accueillir une tour
 *
 */
public class TowerBlock {
	private Object3D block;
	private boolean hasTower;
	private boolean selected;
	private Tower tower;
	private PathBlock adjacentBlock;

	/**
	 * Constructeur de TowerBlock
	 * @param block
	 */
	public TowerBlock(Object3D block)
	{
		this.block = block;
		this.tower = null;
		this.hasTower = false;
		this.selected = false;
	}
	
	/**
	 * Méthode de selection du bloc de tour
	 */
	public void select(String textureSelection)
	{
		if(!this.selected)
		{
			this.selected = true;
			RGBColor highlight = new RGBColor(0,0,150);
			this.block.setTexture(textureSelection);
			Log.d("TAG", "tower selected");
		}
		
	}
	
	/**
	 * Méthode de déselection du bloc de tour
	 */
	public void unselect(String texture)
	{
		if(this.selected)
		{
			this.selected = false;
			this.block.setTexture(texture);
			Log.d("TAG", "tower unselected");
			//this.block.setTransparency(100);
		}
		
	}

	public Object3D getBlock() {
		return block;
	}
	public void setBlock(Object3D block) {
		this.block = block;
	}
	public boolean hasTower() {
		return hasTower;
	}
	public void setHasTower(boolean hasTower) {
		this.hasTower = hasTower;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean touched) {
		this.selected = touched;
	}

	public Tower getTower() {
		return tower;
	}

	public void setTower(Tower tower) {
		this.tower = tower;
	}

	public PathBlock getAdjacentBlock() {
		return adjacentBlock;
	}

	public void setAdjacentBlock(PathBlock adjacentBlock) {
		this.adjacentBlock = adjacentBlock;
	}

	public boolean isHasTower() {
		return hasTower;
	}

}
