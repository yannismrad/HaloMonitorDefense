package com.yannis.mrad.halo.tools;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

/**
 * Class Utils
 * @author Yannis M'RAD
 *
 *Méthodes utilitaires
 */
public class Utils {
	
	/**
	 * Méthode qui retourne les coordonnées des bornes d'un objet
	 * (espace objet) en coordonnées dans le world space
	 * @param obj
	 * @return worldSpaceBounds
	 */
	public static float[] getWorldSpaceBounds(Object3D obj) {
		float[] objectSpaceBounds = obj.getMesh().getBoundingBox();
		SimpleVector mins = new SimpleVector(objectSpaceBounds[0], objectSpaceBounds[2], objectSpaceBounds[4]);
		SimpleVector maxs = new SimpleVector(objectSpaceBounds[1], objectSpaceBounds[3], objectSpaceBounds[5]);
		SimpleVector[] p = new SimpleVector[8];
		p[0] = new SimpleVector(mins.x, mins.y, maxs.z); p[1] = new SimpleVector(mins.x, mins.y, mins.z); p[2] = new SimpleVector(maxs.x, mins.y, mins.z);
		p[3] = new SimpleVector(maxs.x, mins.y, maxs.z); p[4] = new SimpleVector(maxs.x, maxs.y, mins.z);
		p[5] = new SimpleVector(maxs.x, maxs.y, maxs.z); p[6] = new SimpleVector(mins.x, maxs.y, mins.z); p[7] = new SimpleVector(mins.x, maxs.y, maxs.z);
		float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
		for (int i = 0; i < 8; i++) {
			p[i].matMul(obj.getWorldTransformation());
			if (p[i].x < minX)
				minX = p[i].x;
			if (p[i].y < minY)
				minY = p[i].y;
			if (p[i].z < minZ)
				minZ = p[i].z;
			if (p[i].x > maxX)
				maxX = p[i].x;
			if (p[i].y > maxY)
				maxY = p[i].y;
			if (p[i].z > maxZ)
				maxZ = p[i].z;
		}
		float[] worldSpaceBounds = new float[6];
		worldSpaceBounds[0] = minX;
		worldSpaceBounds[1] = maxX;
		worldSpaceBounds[2] = minY;
		worldSpaceBounds[3] = maxY;
		worldSpaceBounds[4] = minZ;
		worldSpaceBounds[5] = maxZ;
		
		return worldSpaceBounds;
	}

}
