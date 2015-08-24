package com.yannis.mrad.thread;

import java.util.TimerTask;

import yannis.mrad.halo.exceptions.GameException;

import android.util.Log;

import com.yannis.mrad.halo.gameentity.Tower;
import com.yannis.mrad.halo.interfaces.TowerListener;
import com.yannis.mrad.halo.tools.GameRenderer;

/**
 * Class TowerTask
 * @author Yannis M'RAD
 * 
 *Gestion des attaques de tours
 */
public class TowerTask extends TimerTask{
	private Tower tower;
	private GameRenderer renderer; 
	private TowerListener towerListener;
	
	/**
	 * Constructeur de TowerTask
	 * @param tower
	 */
	public TowerTask(Tower tower, GameRenderer renderer)
	{
		this.tower = tower;
		this.renderer = renderer;
	}

	@Override
	public void run() {
		if(tower.getCurrentTarget() != null)
		{
			Log.d("TAG", "I'm shooting !");
			try 
			{
				renderer.createShotParticle(tower);
			} 
			
			catch (GameException e) 
			{
				e.printStackTrace();
			}
		}
		
		else if(tower.getCurrentTarget() == null)
		{
			Log.d("TAG","I've lost the target, I stop shooting");
			this.towerListener.onShootPhaseFinished();
		}
		
		
	}

	public Tower getTower() {
		return tower;
	}

	public void setTower(Tower tower) {
		this.tower = tower;
	}

	public GameRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}

	public TowerListener getTowerListener() {
		return towerListener;
	}

	public void setTowerListener(TowerListener towerListener) {
		this.towerListener = towerListener;
	}
	
	

}
