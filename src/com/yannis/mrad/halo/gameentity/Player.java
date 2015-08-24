package com.yannis.mrad.halo.gameentity;

/**
 * Classe Player
 * @author Yannis
 * 
 * Repr�sente un joueur
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
	 * Constructeur par d�faut
	 */
	public Player()
	{
		this.score = 0;
		this.money = 0;
	}

	/**
	 * M�thode getScore
	 * @return score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * M�thode setScore
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
	}

	
	/**
	 * M�thode getMoney
	 * @return money
	 */
	public int getMoney() {
		return money;
	}

	/**
	 * M�thode setMoney
	 * @param money
	 */
	public void setMoney(int money) {
		this.money = money;
	}




}
