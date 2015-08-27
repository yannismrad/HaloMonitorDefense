package com.yannis.mrad.halo.tools;

/**
 * Class StaticUniform
 * @author Yannis
 * 
 * Repr�sente une variable uniform pass�e en param�tre � un shader
 *
 */
public class ShaderUniform<T> {
	private String name;
	private T value;
	
	/**
	 * Constructeur par d�faut de StaticUniform
	 * @param name
	 */
	public ShaderUniform(String name, T value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
	
}
