package com.taraxippus.go.render;

import android.opengl.*;
import java.nio.*;

public class Shape
{
	final int[] vbo = new int[2];
	int[] attributes;
	
	int indexCount;
	int type;
	
	boolean hasIndices = true;
	
	public Shape()
	{
		
	}
	
	public void init(int type, float[] vertices, short[] indices,  int... attributes)
	{
		init(type, FloatBuffer.wrap(vertices), ShortBuffer.wrap(indices), 0, false, attributes);
	}
	
	public void init(int type, FloatBuffer vertices, ShortBuffer indices, boolean dynamic, int... attributes)
	{
		init(type, vertices, indices, 0, dynamic, attributes);
	}
	
	public void init(int type, float[] vertices, int indexCount, int... attributes)
	{
		init(type, FloatBuffer.wrap(vertices), null, indexCount, false, attributes);
	}
	
	public void init(int type, FloatBuffer vertices, int indexCount, boolean dynamic, int... attributes)
	{
		init(type, vertices, null, indexCount, dynamic, attributes);
	}
	
	private void init(int type, FloatBuffer vertices, ShortBuffer indices, int indexCount, boolean dynamic, int... attributes)
	{
		if (initialized())
			delete();
		
		this.type = type;
		this.hasIndices = indices != null;
		this.attributes = attributes;
		
		if (hasIndices)
		{
			this.indexCount = indices.capacity();

			GLES20.glGenBuffers(2, vbo, 0);

			if (initialized()) 
			{
				vertices.position(0);
				indices.position(0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.capacity() * 4, vertices, dynamic ? GLES20.GL_DYNAMIC_DRAW : GLES20.GL_STATIC_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo[1]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indices.capacity() * 2, indices, dynamic ? GLES20.GL_DYNAMIC_DRAW : GLES20.GL_STATIC_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

				if (!dynamic)
				{
					vertices.limit(0);
					indices.limit(0);
				}
			}
			else 
			{
				throw new RuntimeException("Error creating VBO");
			}
		}
		else
		{
			this.indexCount = indexCount;

			GLES20.glGenBuffers(1, vbo, 0);

			if (initialized()) 
			{
				vertices.position(0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.capacity() * 4, vertices, dynamic ? GLES20.GL_DYNAMIC_DRAW : GLES20.GL_STATIC_DRAW);
				
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			
				if (!dynamic)
					vertices.limit(0);
			}
			else 
				throw new RuntimeException("Error creating VBO");
		}
	}
	
	public void buffer(FloatBuffer vertices, ShortBuffer indices)
	{
		if (initialized())
		{
			if (hasIndices)
			{
				if (vertices != null)
				{
					vertices.position(0);
					
					GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
					GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertices.capacity() * 4, vertices);

					GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				}
					
				if (indices != null)
				{
					indices.position(0);
					
					GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo[1]);
					GLES20.glBufferSubData(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0, indices.capacity() * 2, indices);
					
					GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
				}
			}
			else
			{
				vertices.position(0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertices.capacity() * 4, vertices);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			}
		}
		else 
			throw new RuntimeException("Tried to buffer unitialized shape");
	}
	
	public boolean initialized()
	{
		return vbo[0] != 0 && (!hasIndices || vbo[1] != 0);
	}
	
	int stride;
	int offset;
	
	public void render()
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
		
		stride = offset = 0;
		
		for (int i = 0; i < attributes.length; ++i)
		{
			stride += attributes[i] * 4;
		}
		
		for (int i = 0; i < attributes.length; ++i)
		{
			GLES20.glVertexAttribPointer(i, attributes[i], GLES20.GL_FLOAT, false, stride, offset);
			GLES20.glEnableVertexAttribArray(i);
			
			offset += attributes[i] * 4;
		}

		if (hasIndices)
		{
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo[1]);
			GLES20.glDrawElements(type, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		else
		{
			GLES20.glDrawArrays(type, 0, indexCount);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		}
	}
	
	public void delete()
	{
		if (!initialized())
		{
			System.err.println("Tried to delete uninitialized vbo");
			return;
		}
		
		GLES20.glDeleteBuffers(hasIndices ? 2 : 1, vbo, 0);
		vbo[0] = vbo[1] = 0;
	}
}
