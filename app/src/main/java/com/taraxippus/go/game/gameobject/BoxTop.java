package com.taraxippus.go.game.gameobject;

import android.opengl.*;
import com.taraxippus.go.game.*;
import com.taraxippus.go.game.gameobject.*;
import com.taraxippus.go.render.*;
import com.taraxippus.go.util.*;

public class BoxTop extends SceneObject
{
	public BoxTop(World world)
	{
		super(world);
	}

	public float getRadius()
	{
		return (float) Math.sqrt(scale.x * scale.x * 0.5 * 0.5 + scale.y * scale.y * 0.5 * 0.5 + scale.z * scale.z * 0.5 * 0.5);
	}

	public static final float[] vertices = new float[]
	{
		-0.5F, 0.5F, -0.5F,
		0, 1, 0,

		0.5F, 0.5F, -0.5F,
		0, 1, 0,

		-0.5F, 0.5F, 0.5F,
		0, 1, 0,

		0.5F, 0.5F, 0.5F,
		0, 1, 0,
	};

	
	public static final short[] indices = new short[]
	{
		0, 2, 1,
		1, 2, 3
	};

	@Override
	public Shape createShape()
	{
		Shape shape = new Shape();

		shape.init(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());

		return shape;
	}
}
