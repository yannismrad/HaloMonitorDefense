package com.yannis.mrad.halo.graphicsentity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

/**
 * Class EndPoint
 * @author Yannis
 * 
 * Représente le point d'arrivée d'une Lane
 *
 */
public class EndPoint extends WayPoint {
	private String textureId, textureNormalId;

	/**
	 * Constructeur de EndPoint
	 * @param ctx
	 * @param world
	 */
	public EndPoint(Context ctx, World world)
	{
		super(ctx,world);
		this.block = new Object3D(12);
		this.block.setName(mContext.getResources().getString(R.string.end_point_name_generic));
		textureId = mContext.getResources().getString(R.string.tex_name_spawn);
		textureNormalId = mContext.getResources().getString(R.string.tex_name_spawn_normal);
		buildEndBlock();
	}

	/**
	 * Méthode de construction du bloc de fin
	 */
	public void buildEndBlock()
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
		this.setShaders(shadersDir+"lane/"+"vertexShader.glsl",shadersDir+"lane/"+"fragmentShader.glsl");
	}

	/**
	 * Méthode de liaison d'un objet avec un shader
	 * @param vertexPath
	 * @param fragmentPath
	 */
	public void setShaders(String vertexPath, String fragmentPath)
	{
		//Utilisation d'un shader pour la texture du sol
		InputStream fragShader;
		InputStream vertShader;
		try 
		{

			fragShader = mContext.getAssets().open(fragmentPath);
			vertShader = mContext.getAssets().open(vertexPath);

			String fragmentShader = Loader.loadTextFile(fragShader);
			String vertexShader = Loader.loadTextFile(vertShader);

			GLSLShader shader = new GLSLShader(vertexShader, fragmentShader);
			block.setShader(shader);

			shader.setStaticUniform("colorMap", 1);
			shader.setStaticUniform("normalMap", 1);
			shader.setStaticUniform("invRadius", 0.0005f);
		} 

		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			Log.d("TAG", "shaders not found");
			e.printStackTrace();
		} 

		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			Log.d("TAG", "shaders IO exception");
			e.printStackTrace();
		}

		block.setSpecularLighting(true);
		block.strip();
		block.build();
	}

	public Object3D getEndBlock() {
		return block;
	}

	public void setEndBlock(Object3D endBlock) {
		this.block = endBlock;
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
