package com.yannis.mrad.halo.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yannis.mrad.halo.R;
import android.content.Context;
import android.util.Log;

import com.yannis.mrad.halo.gameentity.Wave;
import com.yannis.mrad.halo.objects.enemy.CarrierForm;
import com.yannis.mrad.halo.objects.enemy.CombatForm;
import com.yannis.mrad.halo.objects.enemy.Enemy;
import com.yannis.mrad.halo.objects.enemy.FastCombatForm;
import com.yannis.mrad.halo.objects.enemy.InfectionForm;

/**
 * Class XmlWaveParser
 * @author Yannis
 *
 * Utilisée pour le parsing du fichier XML waves.xml
 */
public class XmlWaveParser {
	
	/**
	 * Méthode de parsing du fichier XML waves.xml
	 * @param is
	 * @return waves
	 */
	public static ArrayList<Wave> parseXmlInputStream(InputStream is, Context ctx)
	{
		ArrayList<Wave> waves = null;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();	
			Document dom = builder.parse(is);
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName("wave");
			
			waves = new ArrayList<Wave>();
			
			for(int i=0;i<items.getLength();i++)
			{
				Node item = items.item(i);
				NamedNodeMap attrs = item.getAttributes();
				NodeList childNodes = item.getChildNodes();
				
				int waveNumber = Integer.parseInt(attrs.getNamedItem("number").getTextContent());
				Log.d("TAG", "WAVE NUMBER :"+waveNumber); 
				Wave wave = new Wave(waveNumber);
				
				for(int j=0;j<childNodes.getLength();j++)
				{
					if(childNodes.item(j) instanceof Element)
					{
						Element child = (Element)childNodes.item(j);
						Log.d("TAG", "child node : "+child.getNodeName()+" type: "+child.getAttribute("type"));
						String enemyType = child.getAttribute("type");
						int count = Integer.parseInt(child.getAttribute("count"));
						Enemy enemy = createEnemyFromString(enemyType, ctx);
						wave.getEnemies().put(enemy, count);
					}
		
				}
				
				waves.add(wave);
			}
			
			
		}
		
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch(ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e) 
		{
			e.printStackTrace();
		}
		
		return waves;
		
		
	}
	
	/**
	 * Méthode qui retourne un ennemi selon le type récupéré dans la méthode de parsing XML
	 * @param enemyType
	 * @param ctx
	 * @return
	 */
	public static Enemy createEnemyFromString(String enemyType, Context ctx)
	{
		Enemy enemy = null;
		
		if(enemyType.equals(ctx.getResources().getString(R.string.flood_infection)))
		{
			enemy = new InfectionForm();
			
		}
		
		if(enemyType.equals(ctx.getResources().getString(R.string.flood_combat1)))
		{
			enemy = new CombatForm();
		}
		
		if(enemyType.equals(ctx.getResources().getString(R.string.flood_combat2)))
		{
			enemy = new FastCombatForm();
		}
		
		if(enemyType.equals(ctx.getResources().getString(R.string.flood_carrier)))
		{
			enemy = new CarrierForm();
		}
		
		return enemy;
	}

}
