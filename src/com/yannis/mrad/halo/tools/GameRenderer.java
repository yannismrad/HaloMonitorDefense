package com.yannis.mrad.halo.tools;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import yannis.mrad.halo.exceptions.GameException;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;
import com.threed.jpct.util.SkyBox;
import com.yannis.mrad.halo.GameActivity;
import com.yannis.mrad.halo.R;
import com.yannis.mrad.halo.gameentity.PlasmaTower;
import com.yannis.mrad.halo.gameentity.Tower;
import com.yannis.mrad.halo.graphicsentity.EndPoint;
import com.yannis.mrad.halo.graphicsentity.Lane;
import com.yannis.mrad.halo.graphicsentity.Map;
import com.yannis.mrad.halo.graphicsentity.PathBlock;
import com.yannis.mrad.halo.graphicsentity.SpawnPoint;
import com.yannis.mrad.halo.graphicsentity.TowerBlock;
import com.yannis.mrad.halo.interfaces.CameraListener;
import com.yannis.mrad.halo.interfaces.EnemyListener;
import com.yannis.mrad.halo.interfaces.LoaderListener;
import com.yannis.mrad.halo.interfaces.SelectionListener;
import com.yannis.mrad.halo.objects.enemy.*;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.Toast;

/**
 * Class GameRenderer
 * @author Yannis
 * 
 * Renderer utilisé pour l'affichage en 3D du jeu
 *
 */
public class GameRenderer implements GLSurfaceView.Renderer{
	private Context mContext;
	private FrameBuffer fb;
	private int fps;
	private long time;
	private TextureManager tm;
	private RGBColor back;
	private Map map;
	private CameraListener cameraListener;
	private SelectionListener selectionListener;
	private LoaderListener loaderListener;
	private float deltaX, deltaY;
	private TowerBlock currentlySelected;
	private ArrayList<Object3D> savedObjects;
	private boolean zoomEnabled, zoomIn, zoomOut;
	private float currentTime, startTime;
	private EnemyListener enemyListener;
	private CopyOnWriteArrayList<Enemy> displayedEnemies;
	private CopyOnWriteArrayList<Object3D> available3DEnemies;
	private CopyOnWriteArrayList<Particle> displayedShotParticles;
	private ArrayList<Tower> builtTowers;

	/**
	 * Constructeur de GameRenderer
	 * @param ctx
	 */
	public GameRenderer(Context ctx)
	{
		this.mContext = ctx;
		map = new Map(ctx);
		fps = 0;
		time = System.currentTimeMillis();
		startTime = System.nanoTime();
		displayedEnemies = new CopyOnWriteArrayList<Enemy>();
		displayedShotParticles = new CopyOnWriteArrayList<Particle>();
		builtTowers = new ArrayList<Tower>();
		available3DEnemies = new CopyOnWriteArrayList<Object3D>();
		savedObjects = new ArrayList<Object3D>();

		int backR = mContext.getResources().getInteger(R.integer.backR);
		int backG = mContext.getResources().getInteger(R.integer.backG);
		int backB = mContext.getResources().getInteger(R.integer.backB);
		back = new RGBColor(backR, backG, backB);	

		deltaX = 0;
		deltaY = 0;
		zoomEnabled = false;
		zoomIn = false;
		zoomOut = true;

		this.setCameraListener(new CameraListener(){

			@Override
			public void onCameraChanged(float xStart, float xEnd, float yStart,
					float yEnd) {
				Log.d("TAG", "Camera Listener reacted !");
				Log.d("TAG","RECEIVED Scroll from "+xStart+","+yStart+" to "+xEnd+","+yEnd);


				deltaX = xEnd - xStart;
				deltaY = yEnd - yStart;

				/*SimpleVector touchOrigin = Interact2D.reproject2D3D(map.getCam(), fb, (int)xStart, (int)yStart);
				SimpleVector touchEnd = Interact2D.reproject2D3D(map.getCam(), fb, (int)xEnd, (int)yEnd);
				SimpleVector moveDist = touchEnd.calcSub(touchOrigin);

				Log.d("TAG", "Vector origin : "+touchOrigin.toString());
				Log.d("TAG", "Vector end : "+touchEnd.toString());
				Log.d("TAG", "Vector distance : "+moveDist.toString()); */

			}

			@Override
			public void onCameraZoomed() {
				zoomEnabled = true;
				if(zoomOut)
				{
					zoomIn = true;
					zoomOut = false;
				}
				else if(zoomIn)
				{
					zoomOut = true;
					zoomIn = false;
				}

			}

		});

	}

	/**
	 * Méthode de démarrage du mouvement des ennemis récemment crées
	 * @param enemy
	 * @param spawnPoint
	 */
	public void startEnemyMovement(Enemy enemy, SpawnPoint spawnPoint)
	{
		float[] spawnBounds = Utils.getWorldSpaceBounds(spawnPoint.getBlock());
		float[] enemyBounds = Utils.getWorldSpaceBounds(enemy.getAppearance());

		enemy.getAppearance().setCenter(spawnPoint.getBlock().getTransformedCenter());
		SimpleVector translateZ = new SimpleVector(0,0,-10);
		enemy.getAppearance().translate(translateZ);
		//Déplacer l'ennemi à la position du spawnPoint
		SimpleVector translatePos = new SimpleVector(spawnBounds [0] - enemyBounds[0], spawnBounds[2] - enemyBounds[2],0);
		enemy.getAppearance().translate(translatePos);
		enemy.setxPosition(enemy.getAppearance().getTransformedCenter().x);
		enemy.setyPosition(enemy.getAppearance().getTransformedCenter().y);
		enemy.setOnSpawnPoint(true);
		enemy.setSpawnPoint(spawnPoint);
		displayedEnemies.add(enemy);
		//this.duplicateDisplayedEnemies.add(enemy);
		Log.d("TAG", "Added a new enemy in the displayed enemies list : "+enemy.getId());
	}

	/**
	 * Méthode de démarrage des particules créées
	 * @param particle
	 * @param tower
	 */
	public void startParticleMovement(Particle particle, Tower tower)
	{
		particle.getAppearance().setCenter(tower.getTop().getTransformedCenter());

		float[] particleBounds = Utils.getWorldSpaceBounds(particle.getAppearance());
		float[] towerBounds = Utils.getWorldSpaceBounds(tower.getTop());


		//Déplacer la particule vers l'ennemi visé
		SimpleVector translatePos = new SimpleVector(towerBounds [0] - particleBounds[0], towerBounds[2] - particleBounds[2], towerBounds[4]- particleBounds[4]);
		particle.getAppearance().translate(translatePos);
		displayedShotParticles.add(particle);
		Log.d("TAG","Particle starts moving");
	}

	/**
	 * Méthode de mouvement de particule vers sa cible
	 * @param particle
	 */
	public synchronized void moveParticleToEnemy(Particle particle, float distance)
	{
		SimpleVector enemyPosition = particle.getTarget().getAppearance().getTransformedCenter();
		SimpleVector particlePosition = particle.getAppearance().getTransformedCenter();
		SimpleVector moveVec = new SimpleVector(0,0,0);

		//Direction du mouvement

		//Ennemi à droite de la particule
		if(enemyPosition.x> particlePosition.x)
		{
			moveVec.x = distance;
		}

		//Ennemi à gauche de la particule
		if(enemyPosition.x < particlePosition.x)
		{
			moveVec.x = -distance;
		}

		//Eennemi en bas de la particule
		if(enemyPosition.y > particlePosition.y)
		{
			moveVec.y = distance;
		}

		//Ennemi en haut de la particule
		if(enemyPosition.y < particlePosition.y)
		{
			moveVec.y = -distance;
		}

		//Ennemi au dessus de la particule
		if(enemyPosition.z > particlePosition.z)
		{
			moveVec.z = distance;
		}

		//Ennemi en dessous de la particule
		if(enemyPosition.z < particlePosition.z)
		{
			moveVec.z = -distance;
		}

		particle.getAppearance().translate(moveVec);
		Log.d("TAG","Particle moving ");
		
		float[] enemyBounds = Utils.getWorldSpaceBounds(particle.getTarget().getAppearance());
		
		//TODO vérifier si la particule touche un ennemi (est au "centre" de l'ennemi)
		if(particlePosition.x >= enemyBounds[0] && particlePosition.x <= enemyBounds[1]
				&& particlePosition.y >= enemyBounds[2] && particlePosition.y <= enemyBounds[3])
		{
			enemyListener.onEnemyHurt(particle.getTarget(), particle);
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

		//Rechercher la lane dont le point de spawn est celui auquel l'ennemi a été associé
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
			enemyListener.onEnemyReachedEnd(enemy);
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

						enemyListener.onEnemyReachedBlock(enemy, block);
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
	}


	/**
	 * Méthode de réception des évènements de toucher
	 * @param x
	 * @param y
	 */
	public boolean processTouchEvent(float x, float y)
	{
		boolean hit = false;
		SimpleVector touchDir = Interact2D.reproject2D3DWS(map.getCam(), fb, (int)x, (int)y).normalize();
		Object[] res=map.getWorld().calcMinDistanceAndObject3D(map.getCam().getPosition(), touchDir, 10000);

		Object3D picked=(Object3D) res[1];
		String towerFound="";

		if(res[1] != null)
		{
			hit = true;
			Log.d("TAG", "Object : "+picked.getName()+" touched !");

			if(picked.getName().contains(mContext.getResources().getString(R.string.tower_block_name_generic)))
			{
				towerFound = picked.getName();
				Toast.makeText(mContext, "Tour : "+picked.getName(), Toast.LENGTH_SHORT).show();

				for(Lane l : map.getLanes())
				{
					for(TowerBlock towerBlock : l.getTowerBlocks())
					{
						//Cas de figure 1 : tour sélectionnée != tour déjà sélectionnée
						if(towerBlock.isSelected() && towerFound != towerBlock.getBlock().getName())
						{
							String tex = mContext.getResources().getString(R.string.tex_name_tower_block);
							towerBlock.unselect(tex);
						}

						if(towerBlock.getBlock().getName().equals(towerFound))
						{
							Log.d("TAG", "tower found");

							//Cas de figure 2 : Tour n'est pas déjà sélectionnée
							if(!towerBlock.isSelected())
							{
								String texSelection = mContext.getResources().getString(R.string.tex_name_tower_block_selected);
								towerBlock.select(texSelection);
								currentlySelected = towerBlock;
								this.selectionListener.onTowerBlockSelected();
								//int color = mContext.getResources().getInteger(R.integer.tower_blue);
								//buildTower(towerBlock.getBlock(),color);
							}

						}
					}
				}
			}
		}

		//Aucun objet sélectionnable touché
		else
		{
			Log.d("TAG", "No object touched");

			//Cas de figure 3 : Pas de tour touchée + une tour déjà sélectionnée
			for(Lane l : map.getLanes())
			{
				for(TowerBlock towerBlock : l.getTowerBlocks())
				{
					if(towerBlock.isSelected())
					{
						String tex = mContext.getResources().getString(R.string.tex_name_tower_block);
						towerBlock.unselect(tex);
						currentlySelected = null;
						this.selectionListener.onTowerBlockUnselected();
					}
				}
			}
		}

		return hit;
	}

	/**
	 * Méthode qui appelle la méthode de construction de tour pour un bloc donné
	 * @param color
	 */
	public void requestBuildTower(int color)
	{
		if(currentlySelected != null)
		{
			if(!currentlySelected.hasTower())
			{
				buildTower(currentlySelected, color);
			}

		}

	}


	/**
	 * Méthode de construction d'une tour
	 * @param position
	 * @param towerColor
	 * @return
	 */
	public Tower buildTower(TowerBlock block,int towerColor)
	{
		Object3D towerBody = Primitives.getCylinder(8,1);
		Object3D towerTop = Primitives.getSphere(1);
		Object3D position = block.getBlock();

		String bodyTexId = mContext.getResources().getString(R.string.tex_name_tower_block);
		String bodyTexNormId = mContext.getResources().getString(R.string.tex_name_tower_block_normal);

		String topTexId="", topTexNoiseId = "";

		switch(towerColor)
		{

		case StaticVars.COLOR_BLUE:
			topTexId = mContext.getResources().getString(R.string.tex_name_tower_blue);
			topTexNoiseId = mContext.getResources().getString(R.string.tex_name_tower_blue_noise);
			break;

		case StaticVars.COLOR_RED:
			topTexId = mContext.getResources().getString(R.string.tex_name_tower_blue);
			topTexNoiseId = mContext.getResources().getString(R.string.tex_name_tower_red_noise);

			break;

		case StaticVars.COLOR_GREEN:
			topTexId = mContext.getResources().getString(R.string.tex_name_tower_blue);
			topTexNoiseId = mContext.getResources().getString(R.string.tex_name_tower_green_noise);

			break;

		case StaticVars.COLOR_YELLOW:
			topTexId = mContext.getResources().getString(R.string.tex_name_tower_blue);
			topTexNoiseId = mContext.getResources().getString(R.string.tex_name_tower_yellow_noise);

			break;
		}


		//Texture de la base de la tour
		TextureInfo bodyTex = new TextureInfo(TextureManager.getInstance().getTextureID(bodyTexId));
		bodyTex.add(TextureManager.getInstance().getTextureID(bodyTexNormId), TextureInfo.MODE_MODULATE);

		//Texture du haut de la tour
		TextureInfo topTex = new TextureInfo(TextureManager.getInstance().getTextureID(topTexId));
		topTex.add(TextureManager.getInstance().getTextureID(topTexNoiseId), TextureInfo.MODE_MODULATE);

		towerBody.setTexture(bodyTex);
		towerBody.strip();
		//towerBody.calcTextureWrap();

		towerBody.build();


		String shadersDir = mContext.getResources().getString(R.string.shadersDir);

		ArrayList<ShaderUniform> uniformsBody = new ArrayList<ShaderUniform>();
		uniformsBody.add(new ShaderUniform("colorMap", 1));
		uniformsBody.add(new ShaderUniform("normalMap", 1));
		uniformsBody.add(new ShaderUniform("invRadius", 0.0005f));

		ShaderUtils.setShader(mContext, towerBody, shadersDir+"blocks/"+"vertexShader.glsl", shadersDir+"blocks/"+"fragmentShader.glsl", uniformsBody);
		//towerBody.setSpecularLighting(true);

		towerTop.setTexture(topTex);
		towerTop.calcTextureWrapSpherical();
		//towerTop.setSpecularLighting(true);
		towerTop.build();

		String uniformTime = mContext.getResources().getString(R.string.global_uniform_time);

		ArrayList<ShaderUniform> uniformsTop = new ArrayList<ShaderUniform>();
		uniformsTop.add(new ShaderUniform("colorMap", 1));
		uniformsTop.add(new ShaderUniform("noiseMap", 1));
		uniformsTop.add(new ShaderUniform(uniformTime, currentTime));
		uniformsTop.add(new ShaderUniform("alpha", 1.0));
		uniformsTop.add(new ShaderUniform("baseSpeed", 0.005));
		uniformsTop.add(new ShaderUniform("noiseScale", 0.1337));
		uniformsTop.add(new ShaderUniform("invRadius", 0.0005f));


		ShaderUtils.setShader(mContext, towerTop, shadersDir+"towers/"+"vertexShader.glsl", shadersDir+"towers/"+"fragmentShader.glsl", uniformsTop);

		towerBody.addChild(towerTop);
		position.addChild(towerBody);

		towerBody.rotateAxis(towerBody.getXAxis(), (float) 1.55);
		SimpleVector bodyPos = position.getZAxis();
		SimpleVector topPos = towerBody.getZAxis();
		bodyPos.scalarMul((float) -1.5);
		topPos.scalarMul((float) 2);

		towerBody.translate(bodyPos);
		towerTop.translate(topPos);

		Tower tow = getTowerType(towerColor,towerBody,towerTop);
		String towId = mContext.getResources().getString(R.string.tower_name_generic)+block.getBlock().getName();
		tow.setTowerId(towId);
		block.setTower(tow);
		tow.setTowerBlock(block);
		block.setHasTower(true);
		tow.setBlockAttackRadius(2);
		builtTowers.add(tow);

		map.getWorld().addObject(towerBody);
		map.getWorld().addObject(towerTop);

		return tow;
	}

	/**
	 * Méthode retournant un type de tour selon la couleur donnée
	 * @param color
	 * @param model
	 * @param top
	 * @return tow
	 */
	public Tower getTowerType(int color, Object3D model, Object3D top)
	{
		Tower tow = null;
		int shotSpeed = 1, towerAttackSpeed = 1;
		switch(color)
		{

		case StaticVars.COLOR_BLUE:
			shotSpeed = mContext.getResources().getInteger(R.integer.part_blue_speed);
			towerAttackSpeed = mContext.getResources().getInteger(R.integer.tower_blue_speed);
			tow = new PlasmaTower(model,top,0,0);
			
			break;

		case StaticVars.COLOR_RED:
			
			shotSpeed = mContext.getResources().getInteger(R.integer.part_red_speed);
			towerAttackSpeed = mContext.getResources().getInteger(R.integer.tower_red_speed);
			tow = new Tower(model,top,0,0);
			break;

		case StaticVars.COLOR_GREEN:
			tow = new Tower(model,top,0,0);
			shotSpeed = mContext.getResources().getInteger(R.integer.part_green_speed);
			towerAttackSpeed = mContext.getResources().getInteger(R.integer.tower_green_speed);
			tow = new Tower(model,top,0,0);
			break;

		case StaticVars.COLOR_YELLOW:
			tow = new Tower(model,top,0,0);
			shotSpeed = mContext.getResources().getInteger(R.integer.part_yellow_speed);
			towerAttackSpeed = mContext.getResources().getInteger(R.integer.tower_yellow_speed);
			tow = new Tower(model,top,0,0);
			break;
		}
		
		tow.setAttackSpeed(towerAttackSpeed);
		tow.setShotSpeed(shotSpeed);

		return tow;
	}

	/**
	 * Méthode de création des particules de tir des tours
	 * @param enemy
	 */
	public void createShotParticle(Tower tower) throws GameException
	{
		//TODO ADAPTER LES PARTICULES AU TYPE DE TOUR
		String texture ="";
		Particle particle = new Particle(tower, tower.getCurrentTarget());
		//TODO vérification classe de la tour ici et choix de la texture
		texture = mContext.getResources().getString(R.string.tex_name_particle_blue);
		Log.d("TAG", "Creating new particle ");


		Object3D particleModel = ObjectCreator.getSimpleTexturedParticlePlane(texture);
		particleModel.setTransparency(50);
		map.getWorld().addObject(particleModel);
		particle.setAppearance(particleModel);
		startParticleMovement(particle,tower);

	}

	/**
	 * Méthode d'apparition des ennemis sur la map
	 * @param enemy
	 */
	public void spawnEnemy(Enemy enemy) throws GameException
	{
		String texture="";
		Enemy enemyInstance = null; //Copie du type d'ennemi à créer

		//Choix de la texture à utiliser selon le type d'ennemi
		if(enemy.getClass().equals(InfectionForm.class))
		{
			enemyInstance = new InfectionForm();
			texture = mContext.getResources().getString(R.string.tex_name_flood_infection);
			Log.d("TAG","Creating plane enemy of type infection form");
		}

		if(enemy.getClass().equals(CombatForm.class))
		{
			enemyInstance = new CombatForm();
			texture = mContext.getResources().getString(R.string.tex_name_flood_combat1);
			Log.d("TAG","Creating plane enemy of type combat form");
		}

		if(enemy.getClass().equals(FastCombatForm.class))
		{
			enemyInstance = new FastCombatForm();
			texture = mContext.getResources().getString(R.string.tex_name_flood_combat2);
			Log.d("TAG","Creating plane enemy of type fast combat form");
		}

		if(enemy.getClass().equals(CarrierForm.class))
		{
			enemyInstance = new CarrierForm();
			texture = mContext.getResources().getString(R.string.tex_name_flood_carrier);
			Log.d("TAG","Creating plane enemy of type carrier form");
		}

		if(enemyInstance == null)
		{
			throw new GameException("Error while spawning enemy type : "+enemy.getClass().getSimpleName());
		}
		enemyInstance.setId(enemy.getId());
		enemyInstance.setSpeedFactor(enemy.getSpeedFactor());

		Object3D floodModel = ObjectCreator.getSimpleTexturedEnemyPlane(texture);
		map.getWorld().addObject(floodModel);
		enemy.setAppearance(floodModel);

		//TODO : Changer cela pour qu'on parcours toutes les lanes de la map, et qu'on fasse spawner l'ennemi sur l'une d'elles si plusieurs lanes
		this.getEnemyListener().onEnemyCreated(enemy, map.getLanes().get(0).getSpawnPoint());

	}

	/**
	 * Méthode d'attribution de CameraListener
	 * @param camListener
	 */
	public void setCameraListener(CameraListener camListener)
	{
		this.cameraListener = camListener;
	}


	public CameraListener getCameraListener() {
		return cameraListener;
	}


	/**
	 * Méthode d'attribution de SelectionListener
	 * @param selectionListener
	 */
	public void setSelectionListener(SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}


	public SelectionListener getSelectionListener() {
		return selectionListener;
	}	

	/**
	 * Méthode onDrawFrame 
	 * (boucle principale d'affichage 3D)
	 */
	@Override
	public void onDrawFrame(GL10 gl) {

		if(deltaX != 0)
		{
			SimpleVector xAxisVec = map.getCam().getXAxis();

			map.getCam().moveCamera(xAxisVec, deltaX/50f);

			deltaX = 0;
		}

		if(deltaY != 0)
		{
			SimpleVector yAxisVec = map.getCam().getYAxis();

			map.getCam().moveCamera(yAxisVec, deltaY/50f);

			deltaY = 0;
		}

		/*if(cameraChanged)
		{
			SimpleVector xAxisVec = map.getCam().getXAxis();
			SimpleVector yAxisVec = map.getCam().getYAxis();

			map.getCam().moveCamera(moveX, 2);
			map.getCam().moveCamera(moveY, 2);

			cameraChanged = false;
			Log.d("TAG", "Camera moved by : "+cameraMovement.toString());
		} */


		if(zoomEnabled && zoomIn)
		{
			map.getCam().moveCamera(Camera.CAMERA_MOVEIN, 200);
			zoomEnabled = false;
		}

		else if(zoomEnabled && zoomOut)
		{
			map.getCam().moveCamera(Camera.CAMERA_MOVEOUT, 200);
			zoomEnabled = false;
		}

		//Log.d("TAG","Number of displayed enemies : "+displayedEnemies.size());

		//A chaque frame faire avancer les ennemis
		for(int i=0; i< displayedEnemies.size();i++)
		{
			moveToNextBlock(displayedEnemies.get(i), displayedEnemies.get(i).getSpeedFactor());
		}

		//A chaque frame dépalcer les particules de tir
		for(int i=0; i< displayedShotParticles.size();i++)
		{
			moveParticleToEnemy(displayedShotParticles.get(i), displayedShotParticles.get(i).getOrigin().getShotSpeed());
		}




		fb.clear(back);
		map.getWorld().renderScene(fb);
		map.getWorld().draw(fb);
		fb.display();

		if (System.currentTimeMillis() - time >= 1000) {
			Logger.log(fps + "fps");
			fps = 0;
			time = System.currentTimeMillis();
			currentTime = System.nanoTime() - startTime;
			animateTextures();
		}
		fps++;
	}

	/**
	 * Méthode qui anime les textures (tours, eau) via la variable currentTime
	 */
	public void animateTextures()
	{
		ArrayList<Lane> lanes = map.getLanes();

		for(Lane l : lanes)
		{
			ArrayList<TowerBlock> towBlocks = l.getTowerBlocks();
			String uniformTime = mContext.getResources().getString(R.string.global_uniform_time);
			for(TowerBlock b : towBlocks)
			{
				Tower t = b.getTower();
				if(t != null)
				{
					t.getTop().getShader().setUniform(uniformTime, currentTime); //haut de la tour
					Log.d("TAG", "animating tower top , time = "+currentTime);

				}
			}
		}

	}

	/**
	 * Méthode onSurfaceChanged
	 * (agit durant une modification de la scène)
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		if (fb != null) 
		{
			fb.dispose(); //Vider le framebuffer
		}

		fb = new FrameBuffer(width, height);

		map.initWorld();
		map.createFloor();
		map.createCamera();
		//map.createBlocks();
		//Object3D test = map.createDummyBase();
		Lane l = map.createLane("centralLane");
		map.generateWalls();
		//map.createLaneBlocks(3, l.getSpawnPoint().getSpawnBlock());

		Light sun = new Light(map.getWorld());
		sun.setIntensity(250, 250, 250);
		map.addLight(sun,0,200,1000);
		//cameraPosition = map.getCam().getPosition();
		map.getWorld().compileAllObjects();


		//Enlever l'écran de chargement au premier affichage des éléments
		loaderListener.onLoadingFinished();
	}

	/**
	 * Méthode onSurfaceCreated
	 * (création de la scène)
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		//Chargement des textures utilisées (seulement si non chargées)
		try
		{
			this.tm = TextureManager.getInstance();
			map.setTextureManager(tm);

			//Texture fbBackground = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.bg_test)), 1024, 1024));
			//tm.addTexture(mContext.getResources().getString(R.string.tex_name_background),fbBackground);

			Texture texWall = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.wall_tex)), 512, 512));
			Texture texWallNormal = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.wall_tex_norm)), 512, 512));
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_wall),texWall);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_wall_normal), texWallNormal);

			Texture texFloor= new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.floor_tex2)), 512, 512));
			Texture texFloorNormal = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.floor_tex2_norm)), 256, 256));
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_floor), texFloor);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_floor_normal), texFloorNormal);

			Texture texBlock = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.tower_block)), 256, 256));
			Texture texBlockNormal = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.tower_block_norm)), 256, 256));
			Texture texBlockSelected = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.tower_block_selected)), 256, 256));
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_tower_block), texBlock);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_tower_block_normal), texBlockNormal);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_tower_block_selected), texBlockSelected);


			Texture texLane = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.floor_flood)), 256, 256));
			Texture texLaneNormal = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.floor_tex2_norm)), 256, 256));
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_lane), texLane);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_lane_normal), texLaneNormal);

			Texture texSpawn = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.spawn_tex)), 256, 256));
			Texture texSpawnNormal = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.spawn_tex_norm)), 256, 256));
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_spawn), texSpawn);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_spawn_normal), texSpawnNormal);

			Texture texTowerBlue = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.blue_tower)), 1024, 1024));
			Texture texTowerBlueNoise = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.blue_tower_noise)), 512, 512));
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_tower_blue), texTowerBlue);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_tower_blue_noise), texTowerBlueNoise);

			Texture texTowerRedNoise = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.red_tower_noise)), 512, 512));
			//tm.addTexture(mContext.getResources().getString(R.string.tex_name_tower_red), texTowerBlue);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_tower_red_noise), texTowerRedNoise);

			Texture texTowerGreenNoise = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.green_tower_noise)), 512, 512));
			//tm.addTexture(mContext.getResources().getString(R.string.tex_name_tower_red), texTowerBlue);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_tower_green_noise), texTowerGreenNoise);

			Texture texTowerYellowNoise = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.yellow_tower_noise)), 512, 512));
			//tm.addTexture(mContext.getResources().getString(R.string.tex_name_tower_red), texTowerBlue);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_tower_yellow_noise), texTowerYellowNoise);

			Texture floodInfection = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.flood_1)), 512, 512), true);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_flood_infection), floodInfection);

			Texture floodCombat1 = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.flood_combat1)), 512, 512), true);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_flood_combat1), floodCombat1);

			Texture floodCombat2 = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.flood_combat2)), 512, 512), true);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_flood_combat2), floodCombat2);

			Texture floodCarrier = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.flood_carrier)), 512, 512), true);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_flood_carrier), floodCarrier);

			Texture particleBlue = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.particle_blue)), 512, 512), true);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_particle_blue), particleBlue);

			Texture particleRed = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.particle_red)), 512, 512), true);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_particle_red), particleRed);

			Texture particleGreen = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.particle_green)), 512, 512), true);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_particle_green), particleGreen);

			Texture particleYellow = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.particle_yellow)), 512, 512), true);
			tm.addTexture(mContext.getResources().getString(R.string.tex_name_particle_yellow), particleYellow);

		}

		catch(Exception e)
		{
			Log.d("TAG","Texture already loaded");
		}

	}

	/**
	 * Méthode de restauration des éléments graphiques après un onPause / onResume
	 * @return
	 */
	public boolean restoreRendererContext()
	{
		boolean done = false;
		restoreSavedObjects();
		restoreCurrentlySelected();
		return done;
	}

	/**
	 * Méthode de restauration des Object3D
	 */
	public void restoreSavedObjects()
	{
		//TODO
	}

	/**
	 * Méthode de restauration de selection de l'objet currentlySelected
	 */
	public void restoreCurrentlySelected()
	{
		if(currentlySelected != null)
		{
			//TODO
		}
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public FrameBuffer getFb() {
		return fb;
	}

	public void setFb(FrameBuffer fb) {
		this.fb = fb;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public TextureManager getTm() {
		return tm;
	}

	public void setTm(TextureManager tm) {
		this.tm = tm;
	}

	public RGBColor getBack() {
		return back;
	}

	public void setBack(RGBColor back) {
		this.back = back;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public float getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(float deltaX) {
		this.deltaX = deltaX;
	}

	public float getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(float deltaY) {
		this.deltaY = deltaY;
	}

	public TowerBlock getCurrentlySelected() {
		return currentlySelected;
	}

	public void setCurrentlySelected(TowerBlock currentlySelected) {
		this.currentlySelected = currentlySelected;
	}

	public ArrayList<Object3D> getSavedObjects() {
		return savedObjects;
	}

	public void setSavedObjects(ArrayList<Object3D> savedObjects) {
		this.savedObjects = savedObjects;
	}

	public LoaderListener getLoaderListener() {
		return loaderListener;
	}

	public void setLoaderListener(LoaderListener loaderListener) {
		this.loaderListener = loaderListener;
	}

	public boolean isZoomEnabled() {
		return zoomEnabled;
	}

	public void setZoomEnabled(boolean zoomEnabled) {
		this.zoomEnabled = zoomEnabled;
	}

	public boolean isZoomIn() {
		return zoomIn;
	}

	public void setZoomIn(boolean zoomIn) {
		this.zoomIn = zoomIn;
	}

	public boolean isZoomOut() {
		return zoomOut;
	}

	public void setZoomOut(boolean zoomOut) {
		this.zoomOut = zoomOut;
	}

	public EnemyListener getEnemyListener() {
		return enemyListener;
	}

	public void setEnemyListener(EnemyListener enemyListener) {
		this.enemyListener = enemyListener;
	}

	public CopyOnWriteArrayList<Enemy> getDisplayedEnemies() {
		return displayedEnemies;
	}

	public void setDisplayedEnemies(CopyOnWriteArrayList<Enemy> displayedEnemies) {
		this.displayedEnemies = displayedEnemies;
	}

	public ArrayList<Tower> getBuiltTowers() {
		return builtTowers;
	}

	public void setBuiltTowers(ArrayList<Tower> builtTowers) {
		this.builtTowers = builtTowers;
	}

	public CopyOnWriteArrayList<Particle> getDisplayedShotParticles() {
		return displayedShotParticles;
	}

	public void setDisplayedShotParticles(
			CopyOnWriteArrayList<Particle> displayedShotParticles) {
		this.displayedShotParticles = displayedShotParticles;
	}
	
	







}
