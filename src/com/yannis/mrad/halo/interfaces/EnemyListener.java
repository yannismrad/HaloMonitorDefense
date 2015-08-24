package com.yannis.mrad.halo.interfaces;

import com.yannis.mrad.halo.graphicsentity.PathBlock;
import com.yannis.mrad.halo.graphicsentity.SpawnPoint;
import com.yannis.mrad.halo.objects.enemy.Enemy;
import com.yannis.mrad.halo.objects.enemy.Particle;

/**
 * Interface EnemyListener
 * @author Yannis
 * 
 * Gestion du cycle de vie d'un ennemi
 *
 */
public interface EnemyListener {
	public void onEnemyCreated(Enemy enemy, SpawnPoint spawnPoint);
	public void onEnemyKilled(Enemy enemy);
	public void onEnemyDeath(Enemy enemy);
	public void onEnemyReachedEnd(Enemy enemy);
	public void onEnemyReachedBlock(Enemy enemy, PathBlock block);
	public void onEnemyHurt(Enemy enemy, Particle particle);

}
