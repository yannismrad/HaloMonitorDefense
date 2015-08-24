package com.yannis.mrad.halo.graphicsentity;

import com.threed.jpct.Object3D;

/**
 * Class PathBlock
 * @author Yannis
 * 
 * Représente un bloc de chemin sur une Lane
 *
 */
public class PathBlock {
	
	private PathBlock nextBlock;
	private PathBlock previousBlock;
	private Object3D block;
	private boolean isLastBlock;
	private String blockId;
	
	/**
	 * Constructeur de PathBlock
	 * @param appearance
	 * @param nextBlock
	 */
	public PathBlock(Object3D block)
	{
		this.block = block;
	}

	public PathBlock getNextBlock() {
		return nextBlock;
	}

	public void setNextBlock(PathBlock nextBlock) {
		this.nextBlock = nextBlock;
	}

	public Object3D getBlock() {
		return block;
	}

	public void setBlock(Object3D block) {
		this.block = block;
	}

	public boolean isLastBlock() {
		return isLastBlock;
	}

	public void setLastBlock(boolean isLastBlock) {
		this.isLastBlock = isLastBlock;
	}

	public PathBlock getPreviousBlock() {
		return previousBlock;
	}

	public void setPreviousBlock(PathBlock previousBlock) {
		this.previousBlock = previousBlock;
	}

	public String getBlockId() {
		return blockId;
	}

	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}

}
