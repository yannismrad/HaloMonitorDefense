package com.yannis.mrad.halo.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import android.util.Log;

import com.threed.jpct.SimpleVector;
import com.yannis.mrad.halo.gameentity.Wave;
import com.yannis.mrad.halo.graphicsentity.Lane;
import com.yannis.mrad.halo.graphicsentity.Map;
import com.yannis.mrad.halo.graphicsentity.PathBlock;
import com.yannis.mrad.halo.interfaces.MoveListener;
import com.yannis.mrad.halo.interfaces.SpawnListener;
import com.yannis.mrad.halo.objects.enemy.Enemy;

/**
 * Class MoveEnemyTask
 * @author Yannis
 * 
 * Gère l'apparition des ennemis
 *
 */
public class MoveEnemyTimerTask extends TimerTask{
	private ArrayList<Enemy> displayedEnemies;
	private GameRenderer renderer;
	private Map map;
	private MoveListener moveListener;

	/**
	 * Constructeur de SpawnTimerTask
	 * @param runningWave
	 * @param renderer
	 * @param chooseRandomEnemyType
	 */
	public MoveEnemyTimerTask(ArrayList<Enemy> displayedEnemies, Map map, GameRenderer renderer) {
		super();
		this.displayedEnemies = displayedEnemies;
		this.map = map;
		this.renderer = renderer;
	}

	@Override
	public void run() {
		for(Enemy e : displayedEnemies)
		{
			moveToNextBlock(e, e.getSpeedFactor());
		}
	}
	
	/**
	 * Méthode de déplacement d'un ennemy jusqu'à un bloc donné
	 * @param enemy
	 * @param distance
	 */
	public synchronized void moveToNextBlock(Enemy enemy, float distance)
	{
		PathBlock nextBlock = null;
		SimpleVector enemyPosition = enemy.getAppearance().getTransformedCenter();
		SimpleVector moveVec = new SimpleVector(0,0,0);
		SimpleVector nextCoordinates = new SimpleVector(0,0,0);
		Lane currentLane = null;

		//Rechercher la lane dont le point de spawn est celui auquel l'ennemy a été associé
		for(Lane l : map.getLanes())
		{
			if(l.getSpawnPoint().equals(enemy.getSpawnPoint())) 
			{
				currentLane = l;
			}
		}
		
		float[] endBounds = Utils.getWorldSpaceBounds(currentLane.getEndPoint().getBlock());


		Log.d("TAG", "moveToNextBlock for enemy : "+enemy.getClass().getSimpleName());

		//Ennemi sur le point de spawn -> prochain bloc = spawnPoint.getFirstPathBlock()
		if(enemy.isOnSpawnPoint())
		{
			nextBlock = enemy.getSpawnPoint().getFirstPathBlock();
			Log.d("TAG", enemy.getClass().getSimpleName()+" on spawn block ");
			moveVec = new SimpleVector(0,0,0);
			nextCoordinates = nextBlock.getBlock().getTransformedCenter();
			enemy.setOnSpawnPoint(false); //Dès qu'il se déplace, il n'est plus considéré comme étant sur le spawn point

		}
		
		//Position de l'ennemi dans les bornes du point d'arrivée => fin du parcours
		else if (enemyPosition.x >= endBounds[0] && enemyPosition.x <= endBounds[1]
				&& enemyPosition.y >= endBounds[2] && enemyPosition.y <= endBounds[3])
		{
			enemy.setHasReachedEnd(true);
			renderer.getEnemyListener().onEnemyReachedEnd(enemy);
		}
				

		//Ennemi sur un Path Block => calcul de la distance jusqu'à sa destination
		else
		{
			for(PathBlock block : currentLane.getLaneBlocks())
			{
				float[] blockBounds = Utils.getWorldSpaceBounds(block.getBlock());

				//Si l'ennemi est à l'intérieur des bornes du bloc actuellement lu
				if(enemyPosition.x >= blockBounds[0] && enemyPosition.x <= blockBounds[1]
						&& enemyPosition.y >= blockBounds[2] && enemyPosition.y <= blockBounds[3])
				{
					Log.d("TAG","Enemy found on block n° "+currentLane.getLaneBlocks().indexOf(block));


					//Mise à jour du bloc actuel de l'ennemi s'il n'était pas arrivé jusqu'à celui ci
					if(enemy.getCurrentPathBlock() == null || !enemy.getCurrentPathBlock().equals(block))
					{
						enemy.setCurrentPathBlock(block);
						Log.d("TAG", enemy.getClass().getSimpleName()+" has reached a new block");
					}


					//Si le bloc actuel est le dernier bloc de la lane
					if(block.isLastBlock())
					{
						Log.d("TAG", enemy.getClass().getSimpleName()+" on the last path block");
						nextCoordinates = currentLane.getEndPoint().getBlock().getTransformedCenter();
					}

					//S'il n'est pas le dernier, l'ennemi avance vers le prochain bloc
					else if(!block.isLastBlock())
					{
						Log.d("TAG", enemy.getClass().getSimpleName()+" moves to the next block");
						nextCoordinates = block.getNextBlock().getBlock().getTransformedCenter();	
					}

				}

			}
		}

		//Direction du mouvement

		//Bloc situé à droite de l'ennemi
		if(nextCoordinates.x > enemyPosition.x)
		{
			moveVec.x = distance;
		}

		//Bloc situé à gauche de l'ennemi
		if(nextCoordinates.x < enemyPosition.x)
		{
			moveVec.x = -distance;
		}

		//Bloc situé en dessous de l'ennemi
		if(nextCoordinates.y > enemyPosition.y)
		{
			moveVec.y = distance;
		}

		//Bloc situé au dessus de l'ennemi
		if(nextCoordinates.y < enemyPosition.y)
		{
			moveVec.y = -distance;
		}

		//Application du mouvement
		enemy.getAppearance().translate(moveVec);


		moveListener.onMoveFinished();
	}


	public GameRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}

	public MoveListener getMoveListener() {
		return moveListener;
	}

	public void setMoveListener(MoveListener moveListener) {
		this.moveListener = moveListener;
	}
	
	
}
