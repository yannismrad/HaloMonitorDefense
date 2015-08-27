package com.yannis.mrad.halo.graphicsentity;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.yannis.mrad.halo.R;
import com.yannis.mrad.halo.tools.ShaderUniform;
import com.yannis.mrad.halo.tools.ShaderUtils;
import com.yannis.mrad.halo.tools.Utils;

/**
 * Class Lane
 * @author Yannis
 *
 * Représente une lane (voie) parcourue par les ennemis
 */
public class Lane {
	private String laneId;
	private Context mContext;
	private World world;
	private SpawnPoint spawnPoint;
	private EndPoint endPoint;
	private ArrayList<PathBlock> laneBlocks;
	private ArrayList<TowerBlock> towerBlocks;
	
	/**
	 * Constructeur de Lane
	 * @param ctx
	 * @param world
	 */
	public Lane(String id, Context ctx, World world)
	{
		this.laneId = id;
		this.mContext = ctx;
		this.world = world;
		this.laneBlocks = new ArrayList<PathBlock>();
		this.towerBlocks = new ArrayList<TowerBlock>();
		this.spawnPoint = new SpawnPoint(ctx, world);
		this.endPoint = new EndPoint(ctx, world);
	}
	
	/**
	 * Méthode qui retourne le dernier bloc de la lane
	 * @return dernier bloc
	 */
	public PathBlock getLastPathBlock()
	{
		return laneBlocks.get(laneBlocks.size()-1);
	}
	
	/**
	 * Méthode de création récursive d'un chemin de blocs à partir d'un bloc donné
	 * @param nbBlocks
	 * @param base
	 */
	public void createLaneBlocks(Object3D base, Object3D end, int blockNumber)
	{
		
		Object3D clonedBlock = base.cloneObject();
		PathBlock pathBlock = new PathBlock(clonedBlock);
		
		//Si le bloc courant est le point de départ, la première copie sera le premier path block
		if(base.getName().equals(mContext.getResources().getString(R.string.spawn_point_name_generic)))
		{
			this.spawnPoint.setFirstPathBlock(pathBlock);
			Log.d("TAG", "SpawnPoint passed in");
		}
		
		//Si l'on a copié un PathBlock on désigne cette copie comme le bloc suivant
		else
		{
			PathBlock previous = getLastPathBlock();
			if(previous.getNextBlock() == null)
			{
				Log.d("TAG", "Previous block has no next block, setting current as next");
				previous.setNextBlock(pathBlock);
				pathBlock.setPreviousBlock(previous);
			}
			
			
		}
		
		float[] baseBounds = Utils.getWorldSpaceBounds(base); //Bornes du point de départ
		float[] endBounds = Utils.getWorldSpaceBounds(end); // Bornes du point d'arrivée
		
		//Si place dispo entre courant et arrivée > taille du bloc courant, on peut placer un bloc
		if( (endBounds[2] - baseBounds[3] )> (baseBounds[3] - baseBounds[2])) //S'il reste de la place entre base et end
		{
			String newTextureId = mContext.getResources().getString(R.string.tex_name_lane);
			String newTextureNormalId = mContext.getResources().getString(R.string.tex_name_lane_normal);
			TextureInfo texInfo = new TextureInfo(TextureManager.getInstance().getTextureID(newTextureId));
			texInfo.add(TextureManager.getInstance().getTextureID(newTextureNormalId), TextureInfo.MODE_MODULATE);
			
			clonedBlock.setTexture(texInfo);
			clonedBlock.strip();
			clonedBlock.build();
			world.addObject(clonedBlock);
			pathBlock.setBlockId(this.laneId+"_block_"+blockNumber);
			laneBlocks.add(pathBlock);
			clonedBlock.translate(0, baseBounds[3]-baseBounds[2], 0);
			blockNumber++;
			createLaneBlocks(clonedBlock, end, blockNumber);
			Log.d("TAG", "Path block created, id = "+pathBlock.getBlockId());
		}
		
		//Pas assez de place, on ne clone pas, et on décale le end point s'il reste un espace vide entre le dernier bloc et lui
		else
		{
			getLastPathBlock().setLastBlock(true); //dernier bloc de la lane = true
			getLastPathBlock().setNextBlock(null);
			float gap = endBounds[2] - baseBounds[3];
			if(gap != 0)
			{
				SimpleVector adjustTranslate = new SimpleVector(0,-gap,0);
				end.translate(adjustTranslate);
			}
			
			for(int i=0; i<laneBlocks.size();i++)
			{
				Log.d("TAG","successeur : "+laneBlocks.get(i).getNextBlock()+" "+i);
			}
		}
		

	}
	
	/**
	 * Méthode de création de tours (VERTICALE) le long de la Lane
	 */
	public void createTowers()
	{
		int nbTowers=0;
		
		/*Nombre de blocs de tours = nombre de bloc de chemins (si pair) */
		if (laneBlocks.size() % 2 == 0)
		{
			nbTowers = laneBlocks.size();
		}
		
		/*Nombre de blocs de tours = nombre de bloc de chemins - 1 (si impair) */
		else if (laneBlocks.size() % 2 == 1)
		{
			nbTowers = laneBlocks.size()-1;
		}
		
		for(int i=0;i<nbTowers;i+=2)
		{
			TowerBlock lBlock = createTowerBlock();
			TowerBlock rBlock = createTowerBlock();
			Object3D leftBlock = lBlock.getBlock();
			Object3D rightBlock = rBlock.getBlock();
			String towerNameGen = mContext.getResources().getString(R.string.tower_block_name_generic);
			
			float[] currentPathBounds = Utils.getWorldSpaceBounds(laneBlocks.get(i).getBlock());
			float[] leftBlockBounds = Utils.getWorldSpaceBounds(leftBlock);
			float[] rightBlockBounds = Utils.getWorldSpaceBounds(rightBlock);
			
			SimpleVector leftPos = new SimpleVector(currentPathBounds[0] - leftBlockBounds[0] - (leftBlockBounds[1] - leftBlockBounds[0]), currentPathBounds[2] - leftBlockBounds[2],-(leftBlockBounds[3]- currentPathBounds[4]));
			leftBlock.translate(leftPos);
			leftBlock.setName(this.laneId+towerNameGen+i);
			lBlock.setAdjacentBlock(laneBlocks.get(i));
			
			SimpleVector rightPos = new SimpleVector(currentPathBounds[1] - rightBlockBounds[0], currentPathBounds[2] - rightBlockBounds[2],-(rightBlockBounds[3]- currentPathBounds[4]));
			rightBlock.translate(rightPos);
			rightBlock.setName(this.laneId+towerNameGen+(i+1));
			rBlock.setAdjacentBlock(laneBlocks.get(i));
			
		}
	}
	
	/**
	 * Méthode de création de blocs de tour
	 * @return towerBlock
	 */
	public TowerBlock createTowerBlock()
	{
		Object3D towerBlock = new Object3D(16);
		
		SimpleVector upperLeftFront=new SimpleVector(-1,-1,-1);
		SimpleVector upperRightFront=new SimpleVector(1,-1,-1);
		SimpleVector lowerLeftFront=new SimpleVector(-1,1,-1);
		SimpleVector lowerRightFront=new SimpleVector(1,1,-1);

		SimpleVector upperLeftBack = new SimpleVector( -1, -1, 1);
		SimpleVector upperRightBack = new SimpleVector(1, -1, 1);
		SimpleVector lowerLeftBack = new SimpleVector( -1, 1, 1);
		SimpleVector lowerRightBack = new SimpleVector(1, 1, 1);

		// Front
		towerBlock.addTriangle(upperLeftFront,0,0, lowerLeftFront,0,1, upperRightFront,1,0);
		towerBlock.addTriangle(upperRightFront,1,0, lowerLeftFront,0,1, lowerRightFront,1,1);

		// Upper
		towerBlock.addTriangle(upperLeftBack,0,0, upperLeftFront,0,1, upperRightBack,1,0);
		towerBlock.addTriangle(upperRightBack,1,0, upperLeftFront,0,1, upperRightFront,1,1);

		// Lower
		towerBlock.addTriangle(lowerLeftBack,0,0, lowerRightBack,1,0, lowerLeftFront,0,1);
		towerBlock.addTriangle(lowerRightBack,1,0, lowerRightFront,1,1, lowerLeftFront,0,1);

		// Left
		towerBlock.addTriangle(upperLeftFront,0,0, upperLeftBack,1,0, lowerLeftFront,0,1);
		towerBlock.addTriangle(upperLeftBack,1,0, lowerLeftBack,1,1, lowerLeftFront,0,1);

		// Right
		towerBlock.addTriangle(upperRightFront,0,0, lowerRightFront,0,1, upperRightBack,1,0);
		towerBlock.addTriangle(upperRightBack,1,0, lowerRightFront, 0,1, lowerRightBack,1,1);
		
		String blockTexId = mContext.getResources().getString(R.string.tex_name_tower_block);
		String blockTexNormId = mContext.getResources().getString(R.string.tex_name_tower_block_normal);

		Log.d("TAG", " tex block : "+blockTexId+ " normal : "+blockTexNormId);
		TextureInfo blockTex = new TextureInfo(TextureManager.getInstance().getTextureID(blockTexId));
		blockTex.add(TextureManager.getInstance().getTextureID(blockTexNormId), TextureInfo.MODE_MODULATE);

		towerBlock.setScale(15);
		towerBlock.setTexture(blockTex);
		towerBlock.calcTextureWrap();
		Log.d("TAG", blockTex.toString());

		towerBlock.setSpecularLighting(true);
		towerBlock.build();
		towerBlock.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		
		String shadersDir = mContext.getResources().getString(R.string.shadersDir);

		ArrayList<ShaderUniform> uniforms = new ArrayList<ShaderUniform>();
		uniforms.add(new ShaderUniform("colorMap", 1));
		uniforms.add(new ShaderUniform("normalMap", 1));
		//uniforms.add(new ShaderUniform("invRadius", 0.0005f));
		
		ShaderUtils.setShader(mContext, towerBlock, shadersDir+"lane/"+"vertexShader.glsl", shadersDir+"lane/"+"fragmentShader.glsl", uniforms);
		
		TowerBlock tb = new TowerBlock(towerBlock);
		towerBlocks.add(tb);
		
		world.addObject(towerBlock);
		
		return tb;
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

	public SpawnPoint getSpawnPoint() {
		return spawnPoint;
	}

	public void setSpawnPoint(SpawnPoint spawnPoint) {
		this.spawnPoint = spawnPoint;
	}

	public EndPoint getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(EndPoint endPoint) {
		this.endPoint = endPoint;
	}

	public ArrayList<PathBlock> getLaneBlocks() {
		return laneBlocks;
	}

	public void setLaneBlocks(ArrayList<PathBlock> laneBlocks) {
		this.laneBlocks = laneBlocks;
	}

	public ArrayList<TowerBlock> getTowerBlocks() {
		return towerBlocks;
	}

	public void setTowerBlocks(ArrayList<TowerBlock> towerBlocks) {
		this.towerBlocks = towerBlocks;
	}
	
	
	
	
	

}
