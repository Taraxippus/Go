package com.taraxippus.go.game.gameobject;

import android.opengl.*;
import com.taraxippus.go.*;
import com.taraxippus.go.game.*;
import com.taraxippus.go.render.*;
import com.taraxippus.go.util.*;

public class Grid extends SceneObject
{
	public static final float MAX_ALPHA = 0.95F;
	public static final float ANIMATION_DURATION = 0.5F;
	
	public static final float WIDTH = 0.0025F;
	public static final float EPSILON = 0.0025F;
	
	final VectorF size = new VectorF(1, 1, 1);

	public Grid(World world, VectorF size)
	{
		super(world);

		this.size.set(size);
		this.scale(this.size.x, this.size.y, this.size.z);
		
		this.specularityExponent = 20F;
		this.specularityFactor = 0.5F;
		
		this.alpha = 0.85F;
	}

	public float getRadius()
	{
		return (float) Math.sqrt(scale.x * scale.x * 0.5 * 0.5 + scale.y * scale.y * 0.5 * 0.5 + scale.z * scale.z * 0.5 * 0.5);
	}

	@Override
	public Shape createShape()
	{
		final float[] vertices = new float[(3 + 3) * 8 * (int)(size.x + size.z)];
		int offset = 0;
		
		int x, z;
		
		for (x = 0; x < size.x; ++x)
		{
			offset = addLine(vertices, offset,
							 (x + 0.5F) / size.x * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -(0.5F - EPSILON),
							 -(0.5F - 0.5F / size.z),
							 (x + 0.5F) / size.x * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -(0.5F - EPSILON),
							 0.5F - 0.5F / size.z,
							 0, 1, 0, -1, 0, 0);
		}
		
		for (z = 0; z < size.z; ++z)
		{
			offset = addLine(vertices, offset,
							 -(0.5F - 0.5F / size.x),
							 -(0.5F - EPSILON),
							 (z + 0.5F) / size.z * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 0.5F - 0.5F / size.x,
							 -(0.5F - EPSILON),
							 (z + 0.5F) / size.z * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 0, 1, 0, 0, 0, 1);
		}
		
		Shape shape = new Shape();
		shape.init(GLES20.GL_TRIANGLES, vertices, vertices.length / (3 + 3), getPass().getAttributes());

		return shape;
	}
	
	public int addLine(float[] vertices, int offset, float x1, float y1, float z1, float x2, float y2, float z2, float normalX, float normalY, float normalZ, float widthFactor, float heightFactor, float lengthFactor)
	{
		vertices[offset++] = x1 - WIDTH * widthFactor;
		vertices[offset++] = y1 - WIDTH * heightFactor;
		vertices[offset++] = z1 - WIDTH * lengthFactor;

		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;

		vertices[offset++] = x1 + WIDTH * widthFactor;
		vertices[offset++] = y1 + WIDTH * heightFactor;
		vertices[offset++] = z1 + WIDTH * lengthFactor;
		
		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;

		vertices[offset++] = x2 - WIDTH * widthFactor;
		vertices[offset++] = y2 - WIDTH * heightFactor;
		vertices[offset++] = z2 - WIDTH * lengthFactor;
		
		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;
		
		
		
		vertices[offset++] = x2 - WIDTH * widthFactor;
		vertices[offset++] = y2 - WIDTH * heightFactor;
		vertices[offset++] = z2 - WIDTH * lengthFactor;
		
		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;
		
		vertices[offset++] = x1 + WIDTH * widthFactor;
		vertices[offset++] = y1 + WIDTH * heightFactor;
		vertices[offset++] = z1 + WIDTH * lengthFactor;
		
		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;
		
		vertices[offset++] = x2 + WIDTH * widthFactor;
		vertices[offset++] = y2 + WIDTH * heightFactor;
		vertices[offset++] = z2 + WIDTH * lengthFactor;
		
		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;
		
		return offset;
	}
}
