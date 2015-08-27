package com.yannis.mrad.halo.graphicsentity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.threed.jpct.GLSLShader;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.yannis.mrad.halo.R;
import com.yannis.mrad.halo.tools.ShaderUtils;
import com.yannis.mrad.halo.tools.ShaderUniform;

/**
 * Class SpawnPoint
 * @author Yannis
 * 
 * Représente un spawn point (point d'apparition) des ennemis d'une Lane
 *
 */
public class SpawnPoint extends WayPoint{
	private String textureId, textureNormalId;
	private PathBlock firstPathBlock;

	/**
	 * Constructeur de SpawnPoint
	 * @param ctx
	 * @param world
	 */
	public SpawnPoint(Context ctx, World world)
	{
		super(ctx,world);
		this.block = new Object3D(12);
		this.block.setName(mContext.getResources().getString(R.string.spawn_point_name_generic));
		textureId = mContext.getResources().getString(R.string.tex_name_spawn);
		textureNormalId = mContext.getResources().getString(R.string.tex_name_spawn_normal);
		buildSpawnBlock();
	}

	/**
	 * Méthode de construction du bloc de spawn
	 */
	public void buildSpawnBlock()
	{
		/* Vecteurs représentant les sommets du plan servant de zone de spawn */
		SimpleVector upperLeftFront=new SimpleVector(-1,-1,-1);
		SimpleVector upperRightFront=new SimpleVector(1,-1,-1);
		SimpleVector lowerLeftFront=new SimpleVector(-1,1,-1);
		SimpleVector lowerRightFront=new SimpleVector(1,1,-1);
		SimpleVector upperLeftBack = new SimpleVector(-1,-1,1);
		SimpleVector upperRightBack = new SimpleVector(1,-1,1);
		
		 // Avant
	    block.addTriangle(upperLeftFront,0,0, lowerLeftFront,0,1, upperRightFront,1,0);
	    block.addTriangle(upperRightFront,1,0, lowerLeftFront,0,1, lowerRightFront,1,1);
	    
	    // Dessus
	    block.addTriangle(upperLeftBack,0,0, upperLeftFront,0,1, upperRightBack,1,0);
	    block.addTriangle(upperRightBack,1,0, upperLeftFront,0,1, upperRightFront,1,1);
	    
	    block.setScale(20);
	    
	    //TextureInfo contenant la texture + la texture de normale
	    TextureInfo texInfo = new TextureInfo(TextureManager.getInstance().getTextureID(textureId));
		texInfo.add(TextureManager.getInstance().getTextureID(textureNormalId), TextureInfo.MODE_MODULATE);
		block.setTexture(texInfo);
		world.addObject(block);
		
		String shadersDir = mContext.getResources().getString(R.string.shadersDir);

		ArrayList<ShaderUniform> uniforms = new ArrayList<ShaderUniform>();
		uniforms.add(new ShaderUniform("colorMap", 1));
		uniforms.add(new ShaderUniform("normalMap", 1));
		uniforms.add(new ShaderUniform("invRadius", 0.0005f));
		
		//ShaderUtils.setShader(mContext, block, shadersDir+"lane/"+"vertexShader.glsl", shadersDir+"lane/"+"fragmentShader.glsl", uniforms);
		block.setSpecularLighting(true);
		block.strip();
		block.build();
	}

	public PathBlock getFirstPathBlock() {
		return firstPathBlock;
	}

	public void setFirstPathBlock(PathBlock firstPathBlock) {
		this.firstPathBlock = firstPathBlock;
	}

	public Object3D getSpawnBlock() {
		return block;
	}

	public void setSpawnBlock(Object3D spawnBlock) {
		this.block = spawnBlock;
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
