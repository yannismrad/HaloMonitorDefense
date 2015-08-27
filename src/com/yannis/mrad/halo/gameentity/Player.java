package com.yannis.mrad.halo.gameentity;

/**
 * Classe Player
 * @author Yannis
 * 
 * Représente un joueur
 *
 */
public class Player {
	private int score;
	private int money;

	/**
	 * Constructeur de Player
	 * @param score
	 * @param money
	 */
	public Player(int score, int money) {
		super();
		this.score = score;
		this.money = money;
	}

	/**
	 * Constructeur par défaut
	 */
	public Player()
	{
		this.score = 0;
		this.money = 0;
	}

	/**
	 * Méthode getScore
	 * @return score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Méthode setScore
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
	}

	
	/**
	 * Méthode getMoney
	 * @return money
	 */
	public int getMoney() {
		return money;
	}

	/**
	 * Méthode setMoney
	 * @param money
	 */
	public void setMoney(int money) {
		this.money = money;
	}




}
