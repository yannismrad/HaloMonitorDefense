package com.yannis.mrad.halo.graphicsentity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.GLSLShader;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.SkyBox;
import com.yannis.mrad.halo.R;
import com.yannis.mrad.halo.interfaces.CameraListener;
import com.yannis.mrad.halo.tools.ShaderUtils;
import com.yannis.mrad.halo.tools.ShaderUniform;
import com.yannis.mrad.halo.tools.Utils;

/**
 * Class Map
 * @author Yannis
 * 
 * Représente la carte du jeu
 *
 */
public class Map {
	private World world;
	private Camera cam;
	private TextureManager tm;
	private Object3D floor; //Sol
	private ArrayList<TowerBlock> towerBlocks; //Blocs pour les tours
	private ArrayList<Lane> lanes; //Blocs pour le chemin des ennemis
	private Context mContext;
	private boolean cameraHasChanged;
	private CameraListener camListener;

	/**
	 * Constructeur de map
	 * @param ctx
	 */
	public Map(Context ctx)
	{
		this.mContext = ctx;
		this.towerBlocks = new ArrayList<TowerBlock>();
		this.lanes= new ArrayList<Lane>();
		this.cameraHasChanged= false;
	}

	/**
	 * Méthode d'initialiation de la map du jeu
	 */
	public void initWorld()
	{
		this.world = new World();

		/* Illumination ambiante */
		int ambientR = mContext.getResources().getInteger(R.integer.ambientR);
		int ambientG = mContext.getResources().getInteger(R.integer.ambientG);
		int ambientB = mContext.getResources().getInteger(R.integer.ambientB);
		world.setAmbientLight(ambientR, ambientG, ambientB);
		world.setClippingPlanes(0, 1500);

	}

	/**
	 * Méthode de génération d'une skybox
	 * @param size
	 */
	public SkyBox createSkybox(float size)
	{
		String sTex = mContext.getResources().getString(R.string.tex_name_tower_block);
		SkyBox skybox = new SkyBox(sTex, sTex, sTex, sTex, sTex, sTex, size);
		return skybox;
	}

	/**
	 * Création d'un plan de sol texturé
	 * @param textureInfo
	 */
	public void createFloor()
	{

		/* Création du sol (sans texture)*/
		int nbQuads = mContext.getResources().getInteger(R.integer.plane_quads);
		int scale = mContext.getResources().getInteger(R.integer.plane_scale);
		floor = Primitives.getPlane(nbQuads, scale);

		String floorTexId = mContext.getResources().getString(R.string.tex_name_floor);
		String floorTexNormId = mContext.getResources().getString(R.string.tex_name_floor_normal);

		Log.d("TAG", " tex : "+floorTexId+ " normal : "+floorTexNormId);
		TextureInfo floorTex = new TextureInfo(tm.getTextureID(floorTexId));
		floorTex.add(tm.getTextureID(floorTexNormId), TextureInfo.MODE_MODULATE);

		floor.setTexture(floorTex);
		Log.d("TAG", floorTex.toString());

		floor.setSpecularLighting(true);
		floor.strip();
		floor.build();
		floor.setName(mContext.getResources().getString(R.string.map_floor_name));
		world.addObject(floor);
		
		String shadersDir = mContext.getResources().getString(R.string.shadersDir);

		ArrayList<ShaderUniform> uniforms = new ArrayList<ShaderUniform>();
		uniforms.add(new ShaderUniform("colorMap", 1));
		uniforms.add(new ShaderUniform("normalMap", 1));
		uniforms.add(new ShaderUniform("invRadius", 0.0005f));
		
		ShaderUtils.setShader(mContext, floor, shadersDir+"lane/"+"vertexShader.glsl", shadersDir+"lane/"+"fragmentShader.glsl", uniforms);

		Log.d("TAG", "added : "+floor.getName());


	}

	/**
	 * Méthode qui crée une caméra
	 */
	public void createCamera()
	{
		this.cam = world.getCamera();
		cam.setFOVLimits(1, 10);
		cam.moveCamera(Camera.CAMERA_MOVEOUT, 800);
		//cam.moveCamera(Camera.CAMERA_MOVERIGHT, 200);
		cam.lookAt(floor.getTransformedCenter());
	}


	/**
	 * Méthode de mise à jour de la position de la caméra
	 * @param xStart
	 * @param yStart
	 * @param xEnd
	 * @param yEnd
	 */
	public void updateCameraPosition(SimpleVector newPos) {
		SimpleVector currentPos = cam.getPosition();
		SimpleVector dir = newPos.calcSub(currentPos);
		//this.cam.setPosition(updatedPos);
		this.cam.moveCamera(dir, 2);

	}

	/**
	 * /**
	 * Méthode d'ajout d'un objet Light
	 * @param light
	 * @param xTranslate
	 * @param yTranslate
	 * @param zTranslate
	 */
	public void addLight(Light light, float xTranslate, float yTranslate, float zTranslate)
	{
		SimpleVector sv = new SimpleVector();
		sv.set(this.getFloor().getTransformedCenter());
		sv.x -= xTranslate;
		sv.y -= yTranslate;
		sv.z -= zTranslate;
		light.setPosition(sv);
	}

	/**
	 * Méthode de création de blocs (manuellement pour des raisons de texturing)
	 */
	public void createBlocks()
	{
		Object3D box=new Object3D(12);

		SimpleVector upperLeftFront=new SimpleVector(-1,-1,-1);
		SimpleVector upperRightFront=new SimpleVector(1,-1,-1);
		SimpleVector lowerLeftFront=new SimpleVector(-1,1,-1);
		SimpleVector lowerRightFront=new SimpleVector(1,1,-1);

		SimpleVector upperLeftBack = new SimpleVector( -1, -1, 1);
		SimpleVector upperRightBack = new SimpleVector(1, -1, 1);
		SimpleVector lowerLeftBack = new SimpleVector( -1, 1, 1);
		SimpleVector lowerRightBack = new SimpleVector(1, 1, 1);

		// Front
		box.addTriangle(upperLeftFront,0,0, lowerLeftFront,0,1, upperRightFront,1,0);
		box.addTriangle(upperRightFront,1,0, lowerLeftFront,0,1, lowerRightFront,1,1);

		// Back
		box.addTriangle(upperLeftBack,0,0, upperRightBack,1,0, lowerLeftBack,0,1);
		box.addTriangle(upperRightBack,1,0, lowerRightBack,1,1, lowerLeftBack,0,1);

		// Upper
		box.addTriangle(upperLeftBack,0,0, upperLeftFront,0,1, upperRightBack,1,0);
		box.addTriangle(upperRightBack,1,0, upperLeftFront,0,1, upperRightFront,1,1);

		// Lower
		box.addTriangle(lowerLeftBack,0,0, lowerRightBack,1,0, lowerLeftFront,0,1);
		box.addTriangle(lowerRightBack,1,0, lowerRightFront,1,1, lowerLeftFront,0,1);

		// Left
		box.addTriangle(upperLeftFront,0,0, upperLeftBack,1,0, lowerLeftFront,0,1);
		box.addTriangle(upperLeftBack,1,0, lowerLeftBack,1,1, lowerLeftFront,0,1);

		// Right
		box.addTriangle(upperRightFront,0,0, lowerRightFront,0,1, upperRightBack,1,0);
		box.addTriangle(upperRightBack,1,0, lowerRightFront, 0,1, lowerRightBack,1,1);

		box.setScale(30);

		String blockTexId = mContext.getResources().getString(R.string.tex_name_tower_block);
		String blockTexNormId = mContext.getResources().getString(R.string.tex_name_tower_block_normal);

		Log.d("TAG", " tex block : "+blockTexId+ " normal : "+blockTexNormId);
		TextureInfo blockTex = new TextureInfo(tm.getTextureID(blockTexId));
		blockTex.add(tm.getTextureID(blockTexNormId), TextureInfo.MODE_MODULATE);

		box.setTexture(blockTex);
		Log.d("TAG", blockTex.toString());

		box.setName(mContext.getResources().getString(R.string.map_floor_name));
		world.addObject(box);

		SimpleVector sv = new SimpleVector();
		sv.set(this.getFloor().getTransformedCenter());
		SimpleVector floorCenter = floor.getTransformedCenter();
		//box.translate(50,0,0);

		float[] floorBounds = floor.getMesh().getBoundingBox();
		for(float i : floorBounds)
		{
			Log.d("TAG", "Bound : "+i);
		}
		box.translate(floorBounds[0],(floorBounds[2]+floorBounds[3])/2,floorBounds[5]);

		//Utilisation d'un shader pour la texture du sol
		InputStream fragShader;
		InputStream vertShader;
		try 
		{
			String shadersDir = mContext.getResources().getString(R.string.shadersDir);

			fragShader = mContext.getAssets().open(shadersDir+"blocks/"+"fragmentShader.glsl");
			vertShader = mContext.getAssets().open(shadersDir+"blocks/"+"vertexShader.glsl");

			String fragmentShader = Loader.loadTextFile(fragShader);
			String vertexShader = Loader.loadTextFile(vertShader);

			GLSLShader shader = new GLSLShader(vertexShader, fragmentShader);
			box.setShader(shader);

			shader.setStaticUniform("colorMap", 1);
			shader.setStaticUniform("normalMap", 1);
			shader.setStaticUniform("invRadius", 0.0005f);
		} 

		catch (FileNotFoundException e) 
		{
			Log.d("TAG", "shaders not found");
			e.printStackTrace();
		} 

		catch (IOException e) 
		{
			Log.d("TAG", "shaders IO exception");
			e.printStackTrace();
		}

		box.setSpecularLighting(true);
		box.strip();
		box.build();
	}

	/**
	 * Méthode qui crée une Lane pour les ennemis
	 */
	public Lane createLane(String id)
	{
		Lane lane = new Lane(id,mContext, world);
		Object3D spawnBlock = lane.getSpawnPoint().getSpawnBlock();
		Object3D endBlock = lane.getEndPoint().getEndBlock();
		float[] floorBounds = Utils.getWorldSpaceBounds(floor);
		float[] spawnBlockBounds = Utils.getWorldSpaceBounds(spawnBlock); //Bornes du point de départ
		float[] endBlockBounds = Utils.getWorldSpaceBounds(endBlock); // Bornes du point d'arrivée

		lane.getSpawnPoint().moveBlock((floorBounds[0]+floorBounds[1])/2,floorBounds[2] - spawnBlockBounds[2] + (spawnBlockBounds[3]-spawnBlockBounds[2]) ,floorBounds[4]);
		lane.getEndPoint().moveBlock((floorBounds[0]+floorBounds[1])/2,floorBounds[3] - endBlockBounds[3] - (endBlockBounds[3]-endBlockBounds[2]),floorBounds[4]);

		lane.createLaneBlocks(lane.getSpawnPoint().getSpawnBlock(), lane.getEndPoint().getEndBlock(),0);
		lane.createTowers();
		lanes.add(lane);

		return lane;
	}

	/**
	 * Méthode de création récursive d'un chemin de blocs à partir d'un bloc donné
	 * @param nbBlocks
	 * @param base
	 */
	public void createLaneBlocks(int nbBlocks, Object3D base)
	{
		Object3D pathBlock = base.cloneObject();
		float[] baseBounds = Utils.getWorldSpaceBounds(base);
		float[]floorBounds = Utils.getWorldSpaceBounds(floor);

		if(baseBounds[3] < floorBounds[3]) //reste de la place pour un bloc
		{
			world.addObject(pathBlock);
			pathBlock.translate(0, baseBounds[3]-baseBounds[2], 0);
			createLaneBlocks(nbBlocks-1, pathBlock);
			Log.d("TAG", "Block path created");
		}


	}

	/**
	 * Méthode de création d'un bloc de mur de base
	 * @return
	 */
	public Object3D createWallBase()
	{
		Object3D box=new Object3D(12);

		SimpleVector upperLeftFront=new SimpleVector(-1,-1,-2);
		SimpleVector upperRightFront=new SimpleVector(1,-1,-2);
		SimpleVector lowerLeftFront=new SimpleVector(-1,1,-2);
		SimpleVector lowerRightFront=new SimpleVector(1,1,-2);

		SimpleVector upperLeftBack = new SimpleVector( -1, -1, 1);
		SimpleVector upperRightBack = new SimpleVector(1, -1, 1);
		SimpleVector lowerLeftBack = new SimpleVector( -1, 1, 1);
		SimpleVector lowerRightBack = new SimpleVector(1, 1, 1);

		// Front
		box.addTriangle(upperLeftFront,0,0, lowerLeftFront,0,1, upperRightFront,1,0);
		box.addTriangle(upperRightFront,1,0, lowerLeftFront,0,1, lowerRightFront,1,1);


		// Right
		box.addTriangle(upperRightFront,0,0, lowerRightFront,0,1, upperRightBack,1,0);
		box.addTriangle(upperRightBack,1,0, lowerRightFront, 0,1, lowerRightBack,1,1);

		box.setScale(20);

		String blockTexId = mContext.getResources().getString(R.string.tex_name_wall);
		String blockTexNormId = mContext.getResources().getString(R.string.tex_name_wall_normal);

		Log.d("TAG", " tex wall : "+blockTexId+ " normal : "+blockTexNormId);
		TextureInfo blockTex = new TextureInfo(tm.getTextureID(blockTexId));
		blockTex.add(tm.getTextureID(blockTexNormId), TextureInfo.MODE_MODULATE);

		box.setTexture(blockTex);
		Log.d("TAG", blockTex.toString());

		box.setName(mContext.getResources().getString(R.string.map_floor_name));

		//Utilisation d'un shader pour la texture du sol
		InputStream fragShader;
		InputStream vertShader;
		try 
		{
			String shadersDir = mContext.getResources().getString(R.string.shadersDir);

			fragShader = mContext.getAssets().open(shadersDir+"blocks/"+"fragmentShader.glsl");
			vertShader = mContext.getAssets().open(shadersDir+"blocks/"+"vertexShader.glsl");

			String fragmentShader = Loader.loadTextFile(fragShader);
			String vertexShader = Loader.loadTextFile(vertShader);

			GLSLShader shader = new GLSLShader(vertexShader, fragmentShader);
			box.setShader(shader);

			shader.setStaticUniform("colorMap", 1);
			shader.setStaticUniform("normalMap", 1);
			shader.setStaticUniform("invRadius", 0.0005f);
		} 

		catch (FileNotFoundException e) 
		{
			Log.d("TAG", "shaders not found");
			e.printStackTrace();
		} 

		catch (IOException e) 
		{
			Log.d("TAG", "shaders IO exception");
			e.printStackTrace();
		}

		box.setSpecularLighting(true);
		box.build();

		return box;
	}

	/**
	 * Méthode de génération des murs autour du plan de la map
	 */
	public void generateWalls()
	{
		Object3D dummyBlockBase = createWallBase(); //Objet temporaire cloné par la suite (gain de performances)
		Object3D blockUpLeft = dummyBlockBase.cloneObject();
		Object3D blockUpRight = dummyBlockBase.cloneObject();
		Object3D blockBottomLeft = dummyBlockBase.cloneObject();
		Object3D blockBottomRight = blockUpLeft.cloneObject();

		world.addObject(blockUpLeft);
		world.addObject(blockUpRight);
		world.addObject(blockBottomLeft);
		world.addObject(blockBottomRight);

		float[] floorBounds = Utils.getWorldSpaceBounds(floor);
		float[] blockULBounds = Utils.getWorldSpaceBounds(blockUpLeft),
				blockURBounds = Utils.getWorldSpaceBounds(blockUpRight),
				blockBLBounds = Utils.getWorldSpaceBounds(blockBottomLeft),
				blockBRBounds = Utils.getWorldSpaceBounds(blockBottomRight);

		SimpleVector upLeftPos = new SimpleVector(floorBounds[0] - blockULBounds[0], floorBounds[2] - blockULBounds[2],(floorBounds[4] - blockULBounds[3]));
		SimpleVector upRightPos = new SimpleVector(-(floorBounds[0] - blockURBounds[0]), floorBounds[2] - blockURBounds[2],(floorBounds[4] - blockURBounds[3]));
		SimpleVector botLeftPos = new SimpleVector(floorBounds[0] - blockBLBounds[0], -(floorBounds[2] - blockBLBounds[2]),(floorBounds[4] - blockBLBounds[3]));
		SimpleVector botRightPos = new SimpleVector(-(floorBounds[0] - blockBRBounds[0]), -(floorBounds[2] - blockBRBounds[2]),(floorBounds[4] - blockBRBounds[3]));

		/*SimpleVector upLeftPosOffset = new SimpleVector(-(blockBounds[0]+blockBounds[1]), -(blockBounds[2]+blockBounds[3]),0);
		SimpleVector upRightPosOffset = new SimpleVector(floorBounds[1], floorBounds[2],0);
		SimpleVector botLeftPosOffset = new SimpleVector(floorBounds[0], floorBounds[3],0);
		SimpleVector botRightPosOffset = new SimpleVector(floorBounds[1], floorBounds[3],0);
		 */


		blockUpLeft.translate(upLeftPos);
		//blockUpLeft.translate(upLeftPosOffset);
		blockUpRight.translate(upRightPos);
		blockBottomLeft.translate(botLeftPos);
		blockBottomRight.translate(botRightPos);

		//Rotations des murs
		double rotateAngle = Math.toRadians(-90);
		blockBottomLeft.rotateZ(-(float) rotateAngle);	
		blockBottomRight.rotateZ(-2*(float) rotateAngle);
		blockUpRight.rotateZ(-3*(float) rotateAngle);

		//Duplication des blocs de base pour construire les murs
		generateWallLine(blockUpLeft, blockBottomLeft);
		generateWallLine(blockBottomLeft, blockBottomRight);
		generateWallLine(blockBottomRight, blockUpRight);
		generateWallLine(blockUpRight, blockUpLeft);

	}



	/**
	 * Méthode de création récursive de blocs de murs à partir d'une base
	 * @param start
	 * @param end
	 */
	public void generateWallLine(Object3D start, Object3D end)
	{
		float[] startBounds = Utils.getWorldSpaceBounds(start);
		float[] endBounds = Utils.getWorldSpaceBounds(end);
		Object3D newWall = start.cloneObject();

		/*Calcul de la position de end par rapport à start pour le sens de duplication */

		//start.x < end.x  : -->
		if((startBounds[1] - startBounds[0]) <= (endBounds[0]- startBounds[1]))
		{
			world.addObject(newWall);
			newWall.translate(startBounds[1]-startBounds[0],0, 0);
			generateWallLine(newWall, end);
			Log.d("TAG", "New vertical wall created (direction : right)");
		}

		//end.x < start.x : <--
		else if ((startBounds[1] - startBounds[0]) <= -(endBounds[1]- startBounds[0]))
		{
			world.addObject(newWall);
			newWall.translate(-(startBounds[1]-startBounds[0]),0, 0);
			generateWallLine(newWall, end);
			Log.d("TAG", "New vertical wall created (direction : left)");
		}

		//start.y > end.y : up
		else if ((startBounds[3] - startBounds[2]) <= -(endBounds[3] - startBounds[2]))
		{
			world.addObject(newWall);
			newWall.translate(0, -(startBounds[3]-startBounds[2]), 0);
			generateWallLine(newWall, end);
			Log.d("TAG", "New vertical wall created (direction : up)");
		}

		//start.y < end.y : down
		else if ((startBounds[3] - startBounds[2]) <= (endBounds[2] - startBounds[3]))
		{
			world.addObject(newWall);
			newWall.translate(0, startBounds[3]-startBounds[2], 0);
			generateWallLine(newWall, end);
			Log.d("TAG", "New vertical wall created (direction : down)");
		}

	}


	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Object3D getFloor() {
		return floor;
	}

	public void setFloor(Object3D floor) {
		this.floor = floor;
	}

	public ArrayList<TowerBlock> getTowerBlocks() {
		return towerBlocks;
	}

	public void setTowerBlocks(ArrayList<TowerBlock> towerBlocks) {
		this.towerBlocks = towerBlocks;
	}

	public ArrayList<Lane> getLanes() {
		return lanes;
	}

	public void setLanes(ArrayList<Lane> lanes) {
		this.lanes= lanes;
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public Camera getCam() {
		return cam;
	}

	public void setCam(Camera cam) {
		this.cam = cam;
	}

	public TextureManager getTextureManager() {
		return tm;
	}

	public void setTextureManager(TextureManager tm) {
		this.tm = tm;
	}

	public TextureManager getTm() {
		return tm;
	}

	public void setTm(TextureManager tm) {
		this.tm = tm;
	}

	public boolean cameraHasChanged() {
		return cameraHasChanged;
	}

	public void setCameraHasChanged(boolean cameraHasChanged) {
		this.cameraHasChanged = cameraHasChanged;
	}

	public CameraListener getCamListener() {
		return camListener;
	}

	public void setCamListener(CameraListener camListener) {
		this.camListener = camListener;
	}








}
