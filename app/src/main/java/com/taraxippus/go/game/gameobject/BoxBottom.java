package com.taraxippus.go.game.gameobject;

import android.opengl.*;
import com.taraxippus.go.game.*;
import com.taraxippus.go.game.gameobject.*;
import com.taraxippus.go.render.*;
import com.taraxippus.go.util.*;

public class BoxBottom extends SceneObject
{
	public BoxBottom(World world)
	{
		super(world);
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

	public static final short[] indices = new short[]
	{
		0, 1, 2,
		1, 3, 2,

		4, 6, 5,
		5, 6, 7,

		8, 9, 10,
		9, 11, 10,

		12, 14, 13,
		13, 14, 15,

		16, 17, 18,
		17, 19, 18,
	};

	@Override
	public Shape createShape()
	{
		Shape shape = new Shape();

		shape.init(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());

		return shape;
	}
}
