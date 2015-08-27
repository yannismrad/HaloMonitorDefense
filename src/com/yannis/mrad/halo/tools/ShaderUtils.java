package com.yannis.mrad.halo.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.threed.jpct.GLSLShader;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.yannis.mrad.halo.R;

/**
 * Class ShaderUtils
 * @author Yannis
 * 
 * Classe fournissant des fonctions pour utiliser des shaders
 *
 */
public class ShaderUtils {
	
	/**
	 * Méthode setShader
	 * @param mContext
	 * @param obj
	 * @param vertPath
	 * @param fragPath
	 */
	@SuppressWarnings("rawtypes")
	public static void setShader(Context mContext, Object3D obj, String vertPath, String fragPath, ArrayList<ShaderUniform> uniforms)
	{

		InputStream fragShader;
		InputStream vertShader;
		try 
		{
			fragShader = mContext.getAssets().open(fragPath);
			vertShader = mContext.getAssets().open(vertPath);

			String fragmentShader = Loader.loadTextFile(fragShader);
			String vertexShader = Loader.loadTextFile(vertShader);

			GLSLShader shader = new GLSLShader(vertexShader, fragmentShader);
			obj.setShader(shader);
			
			for(ShaderUniform s : uniforms)
			{
				if (s.getValue() instanceof Integer)
				{
					Log.d("TAG", "UNIFORM INTEGER : "+s.getName());
					shader.setStaticUniform(s.getName(), (Integer)s.getValue());
				}
				
				else if (s.getValue() instanceof Float)
				{
					Log.d("TAG", "UNIFORM FLOAT: "+s.getName());
					shader.setStaticUniform(s.getName(), (Float)s.getValue());
				}
				
				else if (s.getValue() instanceof Float[])
				{
					shader.setStaticUniform(s.getName(), (float[])s.getValue());
				}
				
				else if (s.getValue() instanceof SimpleVector)
				{
					shader.setStaticUniform(s.getName(), (SimpleVector)s.getValue());
				}
				
				else if (s.getValue() instanceof SimpleVector[])
				{
					shader.setStaticUniform(s.getName(), (SimpleVector[])s.getValue());
				}
				
				
				else if (s.getValue() instanceof Matrix)
				{
					shader.setStaticUniform(s.getName(), (Matrix)s.getValue());
				}
			}
			
		
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

	}
}
