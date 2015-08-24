package com.yannis.mrad.halo.utils;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;

/**
 * Class ConcurrentArrayList
 * @author Yannis
 * @param <T>
 * 
 * Implémentation d'ArrayList gérant la concurrence d'accès
 * 
 */
public class ConcurrentArrayList<T> extends ArrayList<T> {
	private Lock operationLock;
	
	public ConcurrentArrayList()
	{
		this.operationLock = new ReentrantLock();
	}
	
	/**
	 * Méthode d'ajout d'élément
	 * @param <T> element
	 */
	public synchronized boolean syncAdd(T element)
	{
		Log.d("TAG","Lock requested in syncAdd ");
		operationLock.lock();
		Log.d("TAG","Lock acquired in syncAdd ");
		this.add(element);
		operationLock.unlock();
		Log.d("TAG","Lock freed in syncAdd ");
		return true;
	}
	
	/**
	 * Méthode de suppression d'élément
	 * @param element
	 */
	public synchronized void syncRemove(T element)
	{
		Log.d("TAG","Lock requested in syncRemove ");
		operationLock.lock();
		Log.d("TAG","Lock acquired in syncRemove ");
		this.remove(element);
		operationLock.unlock();
		Log.d("TAG","Lock freed in syncRemove ");
	}
	
	/**
	 * Méthode de suppression d'élément
	 * @param index
	 */
	public synchronized void syncRemove(int index)
	{
		Log.d("TAG","Lock requested in syncRemove ");
		operationLock.lock();
		Log.d("TAG","Lock acquired in syncRemove ");
		this.remove(index);
		operationLock.unlock();
		Log.d("TAG","Lock freed in syncRemove ");
	}

}
