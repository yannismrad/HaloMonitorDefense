package com.yannis.mrad.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;

import yannis.mrad.halo.exceptions.GameException;

import android.util.Log;

import com.yannis.mrad.halo.R;
import com.yannis.mrad.halo.gameentity.Wave;
import com.yannis.mrad.halo.interfaces.SpawnListener;
import com.yannis.mrad.halo.objects.enemy.CarrierForm;
import com.yannis.mrad.halo.objects.enemy.CombatForm;
import com.yannis.mrad.halo.objects.enemy.Enemy;
import com.yannis.mrad.halo.objects.enemy.FastCombatForm;
import com.yannis.mrad.halo.objects.enemy.InfectionForm;
import com.yannis.mrad.halo.tools.GameRenderer;

/**
 * Class SpawnTimerTask
 * @author Yannis
 * 
 * Gère l'apparition des ennemis
 *
 */
public class SpawnTimerTask extends TimerTask{
	private Wave wave;
	private boolean chooseRandomEnemyType;
	private static int enemiesToSpawn; //Nombre d'ennemis de la vague restant à spawner
	private GameRenderer renderer; //Référence au renderer pour l'affichage des ennemis
	private SpawnListener spawnListener;

	/**
	 * Constructeur de SpawnTimerTask
	 * @param runningWave
	 * @param renderer
	 * @param chooseRandomEnemyType
	 */
	public SpawnTimerTask(Wave runningWave, GameRenderer renderer) {
		super();
		this.wave = runningWave;
		this.renderer = renderer;
		enemiesToSpawn = wave.getTotalEnemyCount();
	}

	@Override
	public void run() {

		Enemy enemy = null;
		boolean chooseRandomEnemyType = false;

		//Si plusieurs types d'ennemis, en choisir un différent à chaque cycle
		if(wave.getEnemies().size() > 1)
		{
			chooseRandomEnemyType = true;
		}

		//Vérifier le nombre d'ennemis restants à spawner
		//S'il reste des ennemis on les spawne
		if(enemiesToSpawn > 0)
		{
			//Si plusieurs types dispos => choix aléatoire d'un ennemi parmi les types disponibles
			if(chooseRandomEnemyType)
			{
				//Type d'ennemi aléatoire parmi ceux disponibles
				int typeChosenIndex = new Random().nextInt(wave.getEnemies().size());
				ArrayList<Enemy> types= new ArrayList<Enemy>(wave.getEnemies().keySet());
				Class chosenEnemyType = null;
				int typeCount = Integer.MIN_VALUE;
				
				//Recherche du type d'ennemi à spawner
				for (int i = 0; i < types.size(); i++) 
				{
					if(i == typeChosenIndex)
					{
						chosenEnemyType = types.get(i).getClass(); //Récupération du type d'ennemi répertorié (sa classe)
					}
				}
				
				if(chosenEnemyType != null)
				{
					try
					{
						enemy = getEnemyInstance(chosenEnemyType);
						Log.d("TAG", "Got new enemy intance : "+enemy.getClass());
					}
					
					catch(GameException e)
					{
						e.printStackTrace();
					}
					
					
					//Récupérer le compteur associé au type d'ennemi dans la HashMap
					for (Map.Entry<Enemy,Integer> entry : wave.getEnemies().entrySet()) 
					{
						Enemy enemyType = entry.getKey();
						if(enemyType.getClass().equals(enemy.getClass()))
						{
							typeCount = entry.getValue();
						}
					}
					
					//S'il reste des ennemis du type choisi à spawner, on les spawne
					if(typeCount>0)
					{	
						Log.d("TAG", "Spawning random enemy type : "+enemy.getClass().getSimpleName()+", count : "+typeCount);
						//Diminution du nombre d'ennemis restants
						try 
						{
							renderer.spawnEnemy(enemy);
							typeCount--;
							enemiesToSpawn--;
							
							//Mise à jour du compteur associé au type d'ennemi dans la HashMap
							for (Map.Entry<Enemy,Integer> entry : wave.getEnemies().entrySet()) 
							{
								Enemy enemyType = entry.getKey();
								if(enemyType.getClass().equals(enemy.getClass()))
								{
									entry.setValue(typeCount);
								}
							}

							Log.d("TAG","Remaining enemies of type "+enemy.getClass().getSimpleName()+" : "+typeCount);

						} 
						
						catch (GameException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}   

					//Il n'y a plus d'ennemis à spawner pour le type donné mais il reste des ennemis d'autres types
					else if (typeCount == 0)
					{
						Log.d("TAG", "No more enemies of type "+enemy.getClass().getSimpleName());
						boolean otherSpawnDone = false;
						Log.d("TAG", "Choosing another type");

						while(!otherSpawnDone)
						{
							for (Map.Entry<Enemy,Integer> entry : wave.getEnemies().entrySet()) 
							{
								if(entry.getValue() > 0)
								{
									chosenEnemyType= entry.getKey().getClass();

									try
									{
										enemy = getEnemyInstance(chosenEnemyType);
									}
									
									catch(GameException e)
									{
										e.printStackTrace();
									}

									Log.d("TAG", "Other type chosen :  "+enemy.getClass().getSimpleName() + ", count : "+entry.getValue());
									int count = entry.getValue();

									try {
										renderer.spawnEnemy(enemy);
										entry.setValue(count--);
										enemiesToSpawn--;
										otherSpawnDone = true;
										Log.d("TAG","Remaining enemies of type "+enemy.getClass().getSimpleName()+" : "+count);

									} catch (GameException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}


								}

							}
						}


					}
				}
			}

			//Un seul type d'ennemi : spawn linéaire
			else
			{
				for (Map.Entry<Enemy,Integer> entry : wave.getEnemies().entrySet()) 
				{
					Enemy enemyType = entry.getKey();
					int count = entry.getValue();

					try
					{
						enemy = getEnemyInstance(enemyType.getClass());
					}
					
					catch(GameException e)
					{
						e.printStackTrace();
					}
					
					try {
						renderer.spawnEnemy(enemy);
						count--;
						Log.d("TAG","Linear spawn : "+enemy.getClass());
						enemiesToSpawn--;
					} 
					
					catch (GameException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			Log.d("TAG", "Remaining enemies to spawn : "+enemiesToSpawn);
		}

		//Si plus d'ennemi, signaler au GameEngine que la vague des spawn est terminée
		else
		{
			Log.d("TAG","No more enemy to spawn for the current wave, calling onSpawnFinished()");
			spawnListener.onSpawnFinished();
		}

	}
	
	/**
	 * Méthode qui retourne une instance d'ennemi selon un type choisi
	 * @param chosenEnemyType
	 * @return
	 */
	public Enemy getEnemyInstance(Class chosenEnemyType) throws GameException
	{
		Enemy enemy = null;
		
		//Selon le type d'ennemi choisi, on définit sa vitesse, ses points de vie ...
		if(chosenEnemyType.equals(InfectionForm.class))
		{
			enemy = new InfectionForm();
			enemy.setId(renderer.getmContext().getResources().getString(R.string.flood_infection));
			enemy.setSpeedFactor(renderer.getmContext().getResources().getInteger(R.integer.speed_infection));
			enemy.setHealthPoints(renderer.getmContext().getResources().getInteger(R.integer.hp_infection));
			Log.d("TAG", "Created new InfectionForm, hashCode = "+System.identityHashCode(enemy));
		}
		
		if(chosenEnemyType.equals(CombatForm.class))
		{
			enemy = new CombatForm();
			enemy.setId(renderer.getmContext().getResources().getString(R.string.flood_combat1));
			enemy.setSpeedFactor(renderer.getmContext().getResources().getInteger(R.integer.speed_combat1));
			enemy.setHealthPoints(renderer.getmContext().getResources().getInteger(R.integer.hp_combat1));
			Log.d("TAG", "Created new CombatForm, hashCode = "+System.identityHashCode(enemy));
		}
		
		if(chosenEnemyType.equals(FastCombatForm.class))
		{
			enemy = new FastCombatForm();
			enemy.setId(renderer.getmContext().getResources().getString(R.string.flood_combat2));
			enemy.setSpeedFactor(renderer.getmContext().getResources().getInteger(R.integer.speed_combat2));
			enemy.setHealthPoints(renderer.getmContext().getResources().getInteger(R.integer.hp_combat2));
			Log.d("TAG", "Created new FastCombatForm, hashCode = "+System.identityHashCode(enemy));
		}
		
		if(chosenEnemyType.equals(CarrierForm.class))
		{
			enemy = new CarrierForm();
			enemy.setId(renderer.getmContext().getResources().getString(R.string.flood_carrier));
			enemy.setSpeedFactor(renderer.getmContext().getResources().getInteger(R.integer.speed_carrier));
			enemy.setHealthPoints(renderer.getmContext().getResources().getInteger(R.integer.hp_carrier));
			Log.d("TAG", "Created new CarrierForm, hashCode = "+System.identityHashCode(enemy));
		}
		
		if(enemy == null)
		{
			throw new GameException("Could not create a new enemy instance : NULL");
		}
		
		return enemy;
		
	}

	public Wave getWave() {
		return wave;
	}

	public void setWave(Wave wave) {
		this.wave = wave;
	}

	public boolean isChooseRandomEnemyType() {
		return chooseRandomEnemyType;
	}

	public void setChooseRandomEnemyType(boolean chooseRandomEnemyType) {
		this.chooseRandomEnemyType = chooseRandomEnemyType;
	}

	public static int getEnemiesToSpawn() {
		return enemiesToSpawn;
	}

	public static void setEnemiesToSpawn(int enemiesToSpawn) {
		SpawnTimerTask.enemiesToSpawn = enemiesToSpawn;
	}

	public GameRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}

	public SpawnListener getSpawnListener() {
		return spawnListener;
	}

	public void setSpawnListener(SpawnListener spawnListener) {
		this.spawnListener = spawnListener;
	}



}
