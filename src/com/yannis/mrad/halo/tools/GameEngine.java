package com.yannis.mrad.halo.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import yannis.mrad.halo.exceptions.GameException;

import com.threed.jpct.SimpleVector;
import com.yannis.mrad.halo.GameActivity;
import com.yannis.mrad.halo.R;
import com.yannis.mrad.halo.gameentity.Tower;
import com.yannis.mrad.halo.gameentity.Wave;
import com.yannis.mrad.halo.graphicsentity.PathBlock;
import com.yannis.mrad.halo.graphicsentity.SpawnPoint;
import com.yannis.mrad.halo.interfaces.EnemyListener;
import com.yannis.mrad.halo.interfaces.SpawnListener;
import com.yannis.mrad.halo.interfaces.TowerListener;
import com.yannis.mrad.halo.interfaces.UpdateMenuListener;
import com.yannis.mrad.halo.interfaces.WaveListener;
import com.yannis.mrad.halo.objects.enemy.Enemy;
import com.yannis.mrad.halo.objects.enemy.Particle;
import com.yannis.mrad.thread.SpawnTimerTask;
import com.yannis.mrad.thread.TowerTask;

import android.R.*;
import android.content.Context;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

/**
 * Class GameEngine
 * @author Yannis
 * 
 * Gère le déroulement du jeu
 *
 */
public class GameEngine {

	private ArrayList<Wave> waves; //Vagues d'ennemis
	private int lifePoints; //Points de vie de l'objet à défendre
	private int currentWave; //vague actuelle
	private int numberOfWaves, startWave; //Nombre de vagues, vague de départ
	private int currentLifePoints; //Points de vie actuels restants
	private int remainingEnemies; //nombre d'ennemis restants pendant une vague
	private CountDownTimer waveTimer; //Pause de quelques secondes entre deux vagues
	private boolean waveTimerOver;
	private Context mContext;
	private UpdateMenuListener menuListener;
	private WaveListener waveListener;
	private GameRenderer renderer;
	private boolean gameOver;

	/**
	 * Constructeur de GameEngine
	 * @param ctx
	 */
	public GameEngine(Context ctx, GameRenderer renderer)
	{
		this.mContext = ctx;
		this.renderer = renderer;
		Resources res = mContext.getResources();
		this.numberOfWaves = res.getInteger(R.integer.number_of_waves);
		this.startWave = res.getInteger(R.integer.start_wave);
		this.currentWave = this.startWave;
		this.lifePoints= res.getInteger(R.integer.life_points);
		waveTimerOver = false;
		this.currentLifePoints = this.lifePoints;
		this.remainingEnemies = 0;
		loadWaves();

		setWaveTimer();

		this.setWaveListener(new WaveListener(){

			@Override
			public void onWaveStart() {
				try 
				{
					runWave(currentWave);
				} 
				catch (GameException e) 
				{
					e.printStackTrace();
					e.getMessage();
				}

			}

			@Override
			public void onWaveEnd() {
				currentWave++;
				startWaveTimer();
			}

		});

		displayWaves();
		
		configureEnemyListener();

	}

	/**
	 * Méthode qui remplit l'arrayList de vagues depuis des données XML
	 * @return loadComplete
	 */
	public boolean loadWaves()
	{
		boolean loadComplete = false;
		String waveFilePath = mContext.getResources().getString(R.string.gamedataDir)+"waves.xml";
		InputStream waveFile;

		try 
		{
			waveFile = mContext.getAssets().open(waveFilePath);
			this.waves = XmlWaveParser.parseXmlInputStream(waveFile, mContext);
			loadComplete = true;
		} 

		catch (IOException e) 
		{
			e.printStackTrace();
		}

		return loadComplete;

	}


	/**
	 * Méthode de déroulement d'une vague
	 * @param number
	 * @throws GameException
	 */
	public void runWave(int number) throws GameException
	{
		Wave wave = null;
		//Rechercher la vague dans l'ArrayList et charger ses données
		for(Wave w : waves)
		{
			if(w.getNumber() == number)
			{
				wave = w;
			}
		}

		if(wave == null)
		{
			throw new GameException("Error while loading wave nb "+this.currentWave);
		}

		Log.d("TAG", "Wave "+currentWave+" is now starting ");
		
		remainingEnemies = wave.getTotalEnemyCount();
		
		//Phase de Spawn des ennemis
		spawnEnemies(wave);
		
	}

	/**
	 * Méthode de création des ennemis d'une vague
	 * @param wave
	 */
	public void spawnEnemies(Wave wave)
	{
		final Timer spawnTimer = new Timer();
		((GameActivity)mContext).getTimers().add(spawnTimer);
		
		//Thread qui crée des ennemis toutes les secondes
		final SpawnTimerTask spawnTask = new SpawnTimerTask(wave,renderer);
		
		//Listener associé au thread, quand il n'y a plus d'ennemis à spawner on arrête le spawn
		spawnTask.setSpawnListener(new SpawnListener(){

			@Override
			public void onSpawnFinished() {
				spawnTimer.cancel(); //Arrêt du timer de spawn
				Log.d("TAG","Wave spawning finished !");
			}	
		});
		
		spawnTimer.schedule(spawnTask, 0, 1000);

	}



	/**
	 * Méthode d'affichage des vagues
	 */
	public void displayWaves()
	{
		Log.d("TAG", "WAVES : ");
		for(Wave w : waves)
		{
			Log.d("TAG","Wave n°"+w.getNumber()+" Enemies : "+w.getEnemies().toString() + "Total count : "+w.getTotalEnemyCount());
		}
	}

	/**
	 * Méthode d'initialisation du timer entre chaque vague
	 */
	public void setWaveTimer()
	{
		long cooldownTime = mContext.getResources().getInteger(R.integer.cooldownTime);
		long cooldownInterval = mContext.getResources().getInteger(R.integer.cooldownInterval);
		waveTimer = new CountDownTimer(cooldownTime, cooldownInterval)
		{

			@Override
			public void onFinish() {
				Log.d("TAG", "Wave timer finished !");
				GameActivity parent = (GameActivity)mContext;
				TextView timerTv = (TextView) parent.findViewById(R.id.nextWaveTime);
				timerTv.setText(""+0);
				waveTimerOver = true;
				//parent.displayWaveMessage(currentWave);
				waveListener.onWaveStart(); //Fin du timer = début d'une vague

			}

			@Override
			public void onTick(long millisUntilFinished) {
				Log.d("TAG", "Wave timer remaining time : "+millisUntilFinished /1000+"s");
				GameActivity parent = (GameActivity)mContext;
				TextView timerTv = (TextView) parent.findViewById(R.id.nextWaveTime);
				timerTv.setText(""+millisUntilFinished/1000);

			}

		};
		((GameActivity)mContext).getCountDowntimers().add(waveTimer);
	}

	/**
	 * Méthode de démarrage du timer cooldown
	 */
	public void startWaveTimer()
	{
		if(waveTimerOver){
			waveTimerOver = false;
		}
		waveTimer.start();
	}
	
	/**
	 * Méthode de configuration du listener lié au comportement des ennemis
	 */
	public void configureEnemyListener()
	{
		renderer.setEnemyListener(new EnemyListener(){

			/**
			 * A la création d'un ennemi (par la méthode spawnEnemy())
			 * Positionner l'ennemi sur un spawnPoint, le faire avancer
			 */
			@Override
			public void onEnemyCreated(Enemy enemy, SpawnPoint spawnPoint) {
				renderer.startEnemyMovement(enemy, spawnPoint);

			}

			/**
			 * A la mort d'un ennemi (tué par une tour)
			 * Détruire l'entité Enemy
			 */
			@Override
			public void onEnemyKilled(Enemy enemy) {
				
				//Chercher la tour qui a verrouillé l'ennemi, la faire déverrouiller
				for(int i=0; i < renderer.getBuiltTowers().size(); i++)
				{
					Tower tower = renderer.getBuiltTowers().get(i);
					if(tower.getCurrentTarget() != null)
					{
						if(tower.getCurrentTarget() == enemy)
						{
							tower.setCurrentTarget(null);
							Log.d("TAG", "Tower "+tower.getTowerId()+" has lost enemy "+enemy.getId() + " cause : dead");
							
						}
					}
							
				}
				this.onEnemyDeath(enemy); //"tuer" l'ennemi (disparition de l'écran)
			}

			/**
			 * Un ennemi a atteint le point d'arrivée
			 * Diminution des points de vie
			 */
			@Override
			public void onEnemyReachedEnd(Enemy enemy) {
				Log.d("TAG",enemy.getClass().getSimpleName()+"has reached the end point !");
				
				this.onEnemyDeath(enemy); //"tuer" l'ennemi
				
				if(currentLifePoints > 0)
				{
					currentLifePoints--;
					Log.d("TAG", "Player Hitpoints remaining : "+currentLifePoints);
					final GameActivity parent = (GameActivity)mContext;
					
					//A ce niveau le code est éxecuté dans le thread GL (renderer)
					//Pour modifier un élement de l'UI il faut éxecuter
					//Le code dans le thread UI via un Runnable
					parent.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							parent.updateLifePointsCounter(currentLifePoints);	
						}
						
					});
					
				}
				
				if(currentLifePoints == 0)
				{
					//TODO : mettre un Dialog avec message et bouton retour au menu principal
					Log.d("TAG", "Game Over !");
					gameOver = true;
				}
				

			}
			
			/**
			 * A la mort d'un ennemi
			 * Détruire l'entité Enemy
			 */
			@Override
			public void onEnemyDeath(Enemy enemy) {

				if(enemy.getAppearance() != null)
				{
					Log.d("TAG", "removing enemy");
					
					//"Recyclage" du modèle : on stocke l'apparence de l'ennemi en réserve pour
					//l'attribuer à un ennemi qui sera crée prochainement
					//ObjectCreator.storeAvailableObject(enemy.getAppearance());
					//Log.d("TAG", "Saved enemy model for further use, currently stored models : "+ObjectCreator.getAvailableModels().size());
					renderer.getMap().getWorld().removeObject(enemy.getAppearance());
					Log.d("TAG", "removed enemy");
				}
				
				renderer.getDisplayedEnemies().remove(enemy);
				Log.d("TAG", enemy.getClass().getSimpleName()+" removed from the world");
				Log.d("TAG", "remaining displayed enemies : "+ renderer.getDisplayedEnemies().size());
				
				//Diminution du nombre d'ennemis restants de la vague
				if(remainingEnemies > 0)
				{
					remainingEnemies--;
					Log.d("TAG","Remaining enemies alive : "+remainingEnemies);
				}
				
				//S'il ne reste plus d'ennemis (et s'il reste des points de vie au joueur)
				// passer à la vague suivante 
				if(remainingEnemies == 0 && !gameOver)
				{
					Log.d("TAG","Last enemy killed ! Wave "+currentWave+" is over !");
					waveListener.onWaveEnd();
				}			
			}

			@Override
			public void onEnemyReachedBlock(Enemy enemy, PathBlock block) {
				
				//Verifier quelles tours ont le bloc dans leur portée de tir
				for(int i=0; i < renderer.getBuiltTowers().size(); i++)
				{
						Tower tower = renderer.getBuiltTowers().get(i);
						
						//Si une tour a le bloc dans son rayon, on lui fait verrouiller l'ennemi si possible
						if(tower.getRadiusBlocks().contains(block))
						{
							Log.d("TAG", "Tower "+tower.getTowerId()+" has an enemy in its range");
							
							//Si pas de cible déjà verrouillée, verrouiller celle-ci
							if(tower.getCurrentTarget() == null)
							{
								tower.setCurrentTarget(enemy);
								startShootCycle(tower);
								Log.d("TAG", "Tower "+tower.getTowerId()+" has locked enemy "+enemy.getId());
							}
						}
						
						//Si ennemi hors de portée
						else
						{
							//Si l'ennemi était verrouillé, le déverouiller
							if(tower.getCurrentTarget() == enemy)
							{
								tower.setCurrentTarget(null);
								Log.d("TAG", "Tower "+tower.getTowerId()+" has lost enemy "+enemy.getId() + " cause : out of range");
								
							}
						}
				}

			}

			/**
			 * Méthode déclenchée lorsqu'un ennemi est touché par un tir
			 */
			@Override
			public void onEnemyHurt(Enemy enemy, Particle particle) {
				Log.d("TAG", "Enemy "+enemy.getClass().getSimpleName()+" hit !");
				
				renderer.getMap().getWorld().removeObject(particle.getAppearance());
				renderer.getDisplayedShotParticles().remove(particle);
				
				//TODO un effet d'explosion ??
				
				//Diminuer la vie
				enemy.setHealthPoints(enemy.getHealthPoints()-1);
				Log.d("TAG", "Enemy HP remaining : "+enemy.getHealthPoints()+" hp");
				
				//Plus de vie : tuer l'ennemi
				if(enemy.getHealthPoints() == 0)
				{
					this.onEnemyKilled(enemy);
				}
				
			}

		});
	}
	
	/**
	 * Méthode déclenchant la phase de tir d'une tour
	 * @param tower
	 * @param enemy
	 */
	public void startShootCycle(final Tower tower)
	{
		if(tower.getCurrentTarget() != null)
		{
			final Timer shootTimer = new Timer();
			TowerTask towerTask = new TowerTask(tower, renderer);
			((GameActivity)mContext).getTimers().add(shootTimer);
			Log.d("TAG", "Tower shoot cycle starts ");
			
			//Listener associé au thread, quand l'ennemi est hors de portée on arrête la phase de tir
			towerTask.setTowerListener(new TowerListener(){

				@Override
				public void onShootPhaseFinished() {
					shootTimer.cancel(); //Arrêt du timer de spawn
					//shootTimer.purge();
					Log.d("TAG","Tower "+tower.getTowerId()+" has stopped shooting");
					
				}	
			});
			
			//Démarrage du thread de gestion du comportement de la tour
			shootTimer.schedule(towerTask, 0, 1000);
		}
		
		
	}

	public ArrayList<Wave> getWaves() {
		return waves;
	}

	public void setWaves(ArrayList<Wave> waves) {
		this.waves = waves;
	}

	public int getLifePoints() {
		return lifePoints;
	}

	public void setLifePoints(int lifePoints) {
		this.lifePoints = lifePoints;
	}

	public int getCurrentWave() {
		return currentWave;
	}

	public void setCurrentWave(int currentWave) {
		this.currentWave = currentWave;
	}

	public int getNumberOfWaves() {
		return numberOfWaves;
	}

	public void setNumberOfWaves(int numberOfWaves) {
		this.numberOfWaves = numberOfWaves;
	}

	public int getStartWave() {
		return startWave;
	}

	public void setStartWave(int startWave) {
		this.startWave = startWave;
	}

	public int getCurrentLifePoints() {
		return currentLifePoints;
	}

	public void setCurrentLifePoints(int currentLifePoints) {
		this.currentLifePoints = currentLifePoints;
	}

	public boolean isWaveTimerOver() {
		return waveTimerOver;
	}

	public void setWaveTimerOver(boolean waveTimerOver) {
		this.waveTimerOver = waveTimerOver;
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public UpdateMenuListener getMenuListener() {
		return menuListener;
	}

	public void setMenuListener(UpdateMenuListener menuListener) {
		this.menuListener = menuListener;
	}

	public CountDownTimer getWaveTimer() {
		return waveTimer;
	}

	public void setWaveTimer(CountDownTimer waveTimer) {
		this.waveTimer = waveTimer;
	}

	public WaveListener getWaveListener() {
		return waveListener;
	}

	public void setWaveListener(WaveListener waveListener) {
		this.waveListener = waveListener;
	}

	public GameRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public int getRemainingEnemies() {
		return remainingEnemies;
	}

	public void setRemainingEnemies(int remainingEnemies) {
		this.remainingEnemies = remainingEnemies;
	}
	
	
	
	





}
