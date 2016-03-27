package com.taraxippus.go.game.gameobject;

import com.taraxippus.go.game.*;
import com.taraxippus.go.render.*;

public class GameObject implements Comparable<GameObject>
{
	public final World world;
	public Shape shape;
	private Pass pass = Pass.SCENE;
	
	public float depthOffset;
	
	public GameObject(World world)
	{
		this.world = world;
	}
	
	public void init()
	{
		this.shape = createShape();
	}
	
	public Shape createShape()
	{
		return null;
	}
	
	public void update()
	{
		
	}
	
	public void render(Renderer renderer)
	{
		if (shape != null)
			shape.render();
	}
	
	public Pass getPass()
	{
		return pass;
	}
	
	public GameObject setPass(Pass pass)
	{
		this.pass = pass;
		
		return this;
	}
	
	public GameObject setDepthOffset(float depthOffset)
	{
		this.depthOffset = depthOffset;
		
		return this;
	}
	
	public void delete()
	{
		if (shape != null)
			shape.delete();
	}

	public float getDepth()
	{
		return depthOffset;
	}
	
	@Override
	public int compareTo(GameObject o)
	{
		return (int) Math.signum(this.getDepth() - o.getDepth());
	}
}
