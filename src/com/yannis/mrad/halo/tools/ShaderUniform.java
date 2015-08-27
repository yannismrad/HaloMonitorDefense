package com.yannis.mrad.halo.tools;

/**
 * Class StaticUniform
 * @author Yannis
 * 
 * Représente une variable uniform passée en paramètre à un shader
 *
 */
public class ShaderUniform<T> {
	private String name;
	private T value;
	
	/**
	 * Constructeur par défaut de StaticUniform
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
