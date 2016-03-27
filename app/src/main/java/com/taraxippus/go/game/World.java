package com.taraxippus.go.game;

import com.taraxippus.go.*;
import com.taraxippus.go.game.gameobject.*;
import com.taraxippus.go.render.*;
import java.util.*;

public class World
{
	public final Main main;
	public final ArrayList<GameObject>[] gameObjects = new ArrayList[Pass.values().length];
	public final ArrayList<SceneObject> sceneObjects = new ArrayList<>();

	public final List<GameObject> gameObjects_add = Collections.synchronizedList(new ArrayList<GameObject>());
	public final List<GameObject> gameObjects_remove = Collections.synchronizedList(new ArrayList<GameObject>());
	
	public float time = 0;
	
	public World(Main main)
	{
		this.main = main;
		
		for (int i = 0; i < gameObjects.length; ++i)
			gameObjects[i] = new ArrayList<>();
	}
	
	public void addLater(GameObject gameObject)
	{
		gameObjects_add.add(gameObject);
	}
	
	public void add(GameObject gameObject)
	{	
		if (gameObject == null || isDestroying)
			return;
			
		gameObject.init();
		gameObjects[gameObject.getPass().getParent().ordinal()].add(gameObject);
		
		if (gameObject instanceof SceneObject)
			sceneObjects.add((SceneObject) gameObject);
			
	}
	
	public void removeLater(GameObject gameObject)
	{
		gameObjects_remove.add(gameObject);
	}
	
	public void remove(GameObject gameObject)
	{
		if (gameObject == null || isDestroying)
			return;
		
		gameObject.delete();
		gameObjects[gameObject.getPass().getParent().ordinal()].remove(gameObject);
		
		if (gameObject instanceof SceneObject)
			sceneObjects.remove(gameObject);
	}
	
	public void update()
	{
		time += Main.FIXED_DELTA;
		
		for (ArrayList<GameObject> list : gameObjects)
			for (GameObject gameObject : list)
				gameObject.update();
	}

	public void render(Renderer renderer, Pass pass)
	{
		for (int i = 0; i < gameObjects_add.size();)
		{
			this.add(gameObjects_add.get(0));
			gameObjects_add.remove(0);
		}
		
		for (int i = 0; i < gameObjects_remove.size();)
		{
			this.remove(gameObjects_remove.get(0));
			gameObjects_remove.remove(0);
		}
		
		Collections.sort(gameObjects[pass.ordinal()]);
		
		for (GameObject gameObject : gameObjects[pass.ordinal()])
			gameObject.render(renderer);
	}

	public boolean isDestroying = false;
	
	public void delete()
	{
		isDestroying = true;
		
		for (ArrayList<GameObject> list : gameObjects)
			for (GameObject gameObject : list)
				gameObject.delete();
			
		isDestroying = false;
	}
}
