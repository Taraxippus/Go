package com.taraxippus.go.game.gameobject;

import android.opengl.*;
import com.taraxippus.go.game.*;
import com.taraxippus.go.game.gameobject.*;
import com.taraxippus.go.render.*;
import com.taraxippus.go.util.*;

public class Box extends SceneObject
{
	final boolean inverted;
	
	public Box(World world)
	{
		this(world, false);
	}
	
	public Box(World world, boolean inverted)
	{
		super(world);

		this.inverted = inverted;
	}
	
	public float getRadius()
	{
		return (float) Math.sqrt(scale.x * scale.x * 0.5 * 0.5 + scale.y * scale.y * 0.5 * 0.5 + scale.z * scale.z * 0.5 * 0.5);
	}
	
	public static final float[] vertices = new float[]
	{
		-0.5F, -0.5F, -0.5F,
		0, -1, 0,
		
		0.5F, -0.5F, -0.5F,
		0, -1, 0,
		
		-0.5F, -0.5F, 0.5F,
		0, -1, 0,
		
		0.5F, -0.5F, 0.5F,
		0, -1, 0,
		
		
		-0.5F, 0.5F, -0.5F,
		0, 1, 0,
		
		0.5F, 0.5F, -0.5F,
		0, 1, 0,
		
		-0.5F, 0.5F, 0.5F,
		0, 1, 0,
		
		0.5F, 0.5F, 0.5F,
		0, 1, 0,
		
		
		-0.5F, -0.5F, -0.5F,
		-1, 0, 0,

		-0.5F, 0.5F, -0.5F,
		-1, 0, 0,

		-0.5F, -0.5F, 0.5F,
		-1, 0, 0,

		-0.5F, 0.5F, 0.5F,
		-1, 0, 0,


		0.5F, -0.5F, -0.5F,
		1, 0, 0,

		0.5F, 0.5F, -0.5F,
		1, 0, 0,

		0.5F, -0.5F, 0.5F,
		1, 0, 0,

		0.5F, 0.5F, 0.5F,
		1, 0, 0,
		
		
		-0.5F, -0.5F, -0.5F,
		0, 0, -1,

		0.5F, -0.5F, -0.5F,
		0, 0, -1,

		-0.5F, 0.5F, -0.5F,
		0, 0, -1,

		0.5F, 0.5F, -0.5F,
		0, 0, -1,


		-0.5F, -0.5F, 0.5F,
		0, 0, 1,

		0.5F, -0.5F, 0.5F,
		0, 0, 1,

		-0.5F, 0.5F, 0.5F,
		0, 0, 1,

		0.5F, 0.5F, 0.5F,
		0, 0, 1,
		
	};
	
	public static final float[] vertices_inverted = new float[]
	{
		-0.5F, -0.5F, -0.5F,
		0, 1, 0,

		0.5F, -0.5F, -0.5F,
		0, 1, 0,

		-0.5F, -0.5F, 0.5F,
		0, 1, 0,

		0.5F, -0.5F, 0.5F,
		0, 1, 0,


		-0.5F, 0.5F, -0.5F,
		0, -1, 0,

		0.5F, 0.5F, -0.5F,
		0, -1, 0,

		-0.5F, 0.5F, 0.5F,
		0, -1, 0,

		0.5F, 0.5F, 0.5F,
		0, -1, 0,


		-0.5F, -0.5F, -0.5F,
		1, 0, 0,

		-0.5F, 0.5F, -0.5F,
		1, 0, 0,

		-0.5F, -0.5F, 0.5F,
		1, 0, 0,

		-0.5F, 0.5F, 0.5F,
		1, 0, 0,


		0.5F, -0.5F, -0.5F,
		-1, 0, 0,

		0.5F, 0.5F, -0.5F,
		-1, 0, 0,

		0.5F, -0.5F, 0.5F,
		-1, 0, 0,

		0.5F, 0.5F, 0.5F,
		-1, 0, 0,


		-0.5F, -0.5F, -0.5F,
		0, 0, 1,

		0.5F, -0.5F, -0.5F,
		0, 0, 1,

		-0.5F, 0.5F, -0.5F,
		0, 0, 1,

		0.5F, 0.5F, -0.5F,
		0, 0, 1,


		-0.5F, -0.5F, 0.5F,
		0, 0, -1,

		0.5F, -0.5F, 0.5F,
		0, 0, -1,

		-0.5F, 0.5F, 0.5F,
		0, 0, -1,

		0.5F, 0.5F, 0.5F,
		0, 0, -1,

	};
	
	public static final short[] indices = new short[]
	{
		0, 1, 2,
		1, 3, 2,
		
		4, 6, 5,
		5, 6, 7,
		
		8, 10, 9,
		9, 10, 11,
		
		12, 13, 14,
		13, 15, 14,
		
		16, 18, 17,
		17, 18, 19,
		
		20, 21, 22,
		21, 23, 22
	};
	
	public static final short[] indices_inverted = new short[]
	{
		0, 2, 1,
		1, 2, 3,

		4, 5, 6,
		5, 7, 6,

		8, 9, 10,
		9, 11, 10,

		12, 14, 13,
		13, 14, 15,

		16, 17, 18,
		17, 19, 18,

		20, 22, 21,
		21, 22, 23
	};
	
	@Override
	public Shape createShape()
	{
		Shape shape = new Shape();
		
		shape.init(GLES20.GL_TRIANGLES, inverted ? vertices_inverted : vertices, inverted ? indices_inverted : indices, getPass().getAttributes());
		
		return shape;
	}
}
