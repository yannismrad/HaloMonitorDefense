package com.yannis.mrad.halo.tools;

import java.util.ArrayList;

import yannis.mrad.halo.exceptions.GameException;

import android.util.Log;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.yannis.mrad.halo.objects.enemy.Enemy;
import com.yannis.mrad.halo.objects.enemy.InfectionForm;

/**
 * Class ObjectCreator
 * @author Yannis
 * 
 * Contient des fonctions qui retournent des copies d'Object3D (sans re-création de Mesh)
 *
 */
public class ObjectCreator {
	private static Object3D simpleEnemyPlane, simpleParticlePlane;
	private static ArrayList<Object3D> availableModels;

	static
	{
		availableModels = new ArrayList<Object3D>();
	}

	/**
	 * Méthode de clonage de l'objet simpleEnemyPlane (avec texture)
	 * @param textureId
	 * @return clonedPlaned
	 */
	public static Object3D getSimpleTexturedEnemyPlane(String texId)
	{
		if(simpleEnemyPlane == null)
		{
			simpleEnemyPlane = Primitives.getPlane(10, 4);
			simpleEnemyPlane.strip();
			simpleEnemyPlane.build();
		}
		Object3D clonedPlane = simpleEnemyPlane.cloneObject();
		TextureInfo tex = new TextureInfo(TextureManager.getInstance().getTextureID(texId));
		clonedPlane.setTexture(tex);

		return clonedPlane;
	}
	
	/**
	 * Méthode de clonage de l'objet simpleParticlePlane (avec texture)
	 * @param textureId
	 * @return clonedPlaned
	 */
	public static Object3D getSimpleTexturedParticlePlane(String texId)
	{
		if(simpleParticlePlane == null)
		{
			simpleParticlePlane = Primitives.getPlane(10, 2);
			simpleParticlePlane.strip();
			simpleParticlePlane.build();
		}
		Object3D clonedPlane = simpleParticlePlane.cloneObject();
		TextureInfo tex = new TextureInfo(TextureManager.getInstance().getTextureID(texId));
		clonedPlane.setTexture(tex);

		return clonedPlane;
	}

	/**
	 * Méthode permettant d'assigner un modèle (object3D) à un ennemi, si disponible
	 * @param enemy
	 * @return success
	 */
	public static Object3D assignModelToEnemy(Enemy enemy, String enemyName) throws GameException
	{
		Object3D assignedObject =getAvailableObject3D(enemyName);
		
		if(assignedObject == null)
		{
			throw new GameException("No 3D model available for enemy "+enemyName+")");
		}

		return assignedObject;
	}

	/**
	 * Méthode qui retourne un objet du tableau d'objets correspondant à un paramètre donné
	 * @param objectName
	 * @return result
	 */
	public static Object3D getAvailableObject3D(String objectName)
	{
		Object3D result = null;

		for(int i=0; i< availableModels.size();i++)
		{
			//Récupérer le premier modèle disponible correspondant au nom cherché
			if(availableModels.get(i).getName().equals(objectName))
			{
				result = availableModels.get(i);
				break;
			}
		}

		return result;
	}

	/**
	 * Méthode d'ajout d'un object3D au tableau d'objets disponibles
	 * @param obj
	 */
	public static void storeAvailableObject(Object3D obj)
	{
		availableModels.add(obj);
	}

	public static ArrayList<Object3D> getAvailableModels() {
		return availableModels;
	}

	public static void setAvailableModels(ArrayList<Object3D> availableModels) {
		ObjectCreator.availableModels = availableModels;
	}

	
	
}
