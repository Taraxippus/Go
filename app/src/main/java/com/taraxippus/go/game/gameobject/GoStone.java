package com.taraxippus.go.game.gameobject;

import android.opengl.*;
import com.taraxippus.go.*;
import com.taraxippus.go.game.*;
import com.taraxippus.go.render.*;
import com.taraxippus.go.util.*;

public class GoStone extends SceneObject
{
	public static final float MAX_MOVE_TICK = 0.5F;
	public static final float yOffset = - (float)Math.sin(Math.PI / 4F) * 0.5F;
	
	public final boolean black;
	public int x = -1, z = -1;
	
	public GoStone(World world, boolean black)
	{
		super(world);
		
		this.black = black;
		
		this.specularityExponent = 30;
		this.specularityFactor = 0.25F;
		
		this.setColor(black ? 0x444444 : 0xDDDDDD);
		
		this.translate(0, yOffset, 0);
		this.scale(0.5F, 0.5F, 0.5F);
	}

	private float moveTick;
	private final VectorF prevPosition = new VectorF();
	
	public GoStone setPositionWithDelay(int x, int z, int delay)
	{
		this.x = x;
		this.z = z;

		this.prevPosition.set(position);

		moveTick = -MAX_MOVE_TICK * (0.5F + delay * 0.5F);
		
		return this;
	}
	
	public GoStone setPosition(int x, int z)
	{
		this.x = x;
		this.z = z;
		
		this.prevPosition.set(position);
		
		moveTick = MAX_MOVE_TICK;
		
		return this;
	}

	@Override
	public void update()
	{
		if (moveTick < 0)
		{
			moveTick += Main.FIXED_DELTA;
			
			if (moveTick >= 0)
				moveTick = MAX_MOVE_TICK;
		}
		else if (moveTick > 0)
		{
			moveTick -= Main.FIXED_DELTA;
			
			float delta = 1 - moveTick / MAX_MOVE_TICK;
			
			position.set(x * delta + prevPosition.x * (1 - delta), 
						 yOffset * delta + prevPosition.y * (1 - delta) + Math.sin(delta * Math.PI) * 3, 
						 z * delta + prevPosition.z * (1 - delta));
			
			if (moveTick <= 0)
			{
				moveTick = 0;
				
				position.set(x, yOffset, z);
			}
			
			updateMatrix();
		}
		
		super.update();
	}
	
	public float getRadius()
	{
		return (float) Math.sqrt(scale.x * scale.x * 0.5 * 0.5 + scale.y * scale.y * 0.5 * 0.5 + scale.z * scale.z * 0.5 * 0.5);
	}

	public static final int rings = 61;
	public static final int sectors = 60;
	
	final static Shape shape = new Shape();
	static int instances = 0;
	
	@Override
	public Shape createShape()
	{
		if (!shape.initialized())
		{
			final float[] vertices = new float[rings * sectors * 6];

			final float R = 0.5F / (float) (rings - 1);
			final float S = 1.0F / (float) (sectors - 1);
			int r, s, offset = 0;
			float x, y, z;

			for (r = 0; r < rings; r++) 
				for (s = 0; s < sectors; s++) 
				{
					y = (float) (Math.sin(-Math.PI / 2 + Math.PI * r * R + (r > rings / 2 ? Math.PI / 2F : 0)));
					x = (float) (Math.cos(2 * Math.PI * s * S) * 2 * Math.sin(Math.PI * r * R + (r > rings / 2 ? Math.PI / 2F : 0)));
					z = (float) (Math.sin(2 * Math.PI * s * S) * 2 * Math.sin(Math.PI * r * R + (r > rings / 2 ? Math.PI / 2F : 0)));

					if (r > rings / 2)
						y = (y - (float)Math.sin(Math.PI / 4F)) * 2;
					else
						y = (y + (float)Math.sin(Math.PI / 4F)) * 2;

					vertices[offset++] = x * 0.5F;
					vertices[offset++] = y * 0.5F;
					vertices[offset++] = z * 0.5F;

					vertices[offset++] = x;
					vertices[offset++] = y;
					vertices[offset++] = z;
				}

			offset = 0;

			final short[] indices = new short[rings * sectors * 6];

			for (r = 0; r < rings; r++)
				for (s = 0; s < sectors; s++)
				{
					indices[offset++] = (short) (r * sectors + s);
					indices[offset++] = (short) ((r + 1) * sectors + (s + 1));
					indices[offset++] = (short) (r * sectors + (s + 1));

					indices[offset++] = (short) (r * sectors + s);
					indices[offset++] = (short) ((r + 1) * sectors + s);
					indices[offset++] = (short) ((r + 1) * sectors + (s + 1));
				}

			shape.init(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());
		}
		instances++;
		
		return shape;
	}

	@Override
	public void delete()
	{
		if (hasReflection)
			for (ReflectionObject reflectionObject : reflection)
				world.remove(reflectionObject);
		
		instances--;
		
		if (instances == 0 && shape.initialized())
			shape.delete();
	}

}
