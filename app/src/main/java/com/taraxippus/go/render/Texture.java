package com.taraxippus.go.render;
import android.graphics.*;
import android.net.*;
import android.opengl.*;

public class Texture
{
	final int[] texture = new int[1];
	int target = GLES20.GL_TEXTURE_2D;
	
	public Texture()
	{
		
	}
	
	public void init(Bitmap bitmap, int minFilter, int magFilter, int wrapping)
	{
		init(bitmap, 0, 0, 0, 0, 0, minFilter, magFilter, wrapping);
	}
		
	public void init(int width, int height, int format, int type, int pixelFormat, int minFilter, int magFilter, int wrapping)
	{
		init(null, width, height, format, type, pixelFormat, minFilter, magFilter, wrapping);
	}
	
	public void init(Bitmap bitmap, int width, int height, int format, int type, int pixelFormat, int minFilter, int magFilter, int wrapping)
	{
		if (this.initialized())
			delete();
		
		GLES20.glGenTextures(1, texture, 0);
		
		this.bind(0);
		
		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MAG_FILTER, magFilter);
		
		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S, wrapping);
		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T, wrapping);
		
		if (bitmap == null)
		{
			GLES20.glTexImage2D(target, 0, format, width, height, 0, pixelFormat, type, null);
		}
		else
		{
			GLUtils.texImage2D(target, 0, bitmap, 0);

			bitmap.recycle();
		}
		
		if (minFilter == GLES20.GL_LINEAR_MIPMAP_LINEAR
			|| minFilter == GLES20.GL_LINEAR_MIPMAP_NEAREST
			|| minFilter == GLES20.GL_NEAREST_MIPMAP_LINEAR
			|| minFilter == GLES20.GL_NEAREST_MIPMAP_NEAREST)
			GLES20.glGenerateMipmap(target);
		
		if (!initialized())
		{
			delete();
			throw new RuntimeException("Error creating texture");
		}
	}
	
	public void initCubemap(Bitmap[] bitmap, int minFilter, int magFilter, int wrapping)
	{
		initCubemap(bitmap, 0, 0, 0, 0, 0, minFilter, magFilter, wrapping);
	}
	
	public void initCubemap(Bitmap[] bitmap, int width, int height, int format, int type, int pixelFormat, int minFilter, int magFilter, int wrapping)
	{
		if (this.initialized())
			delete();

		target = GLES20.GL_TEXTURE_CUBE_MAP;
			
		GLES20.glGenTextures(1, texture, 0);

		this.bind(0);

		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MAG_FILTER, magFilter);

		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S, wrapping);
		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T, wrapping);
		
		for (int side = 0; side < 6; ++side)
		{
			if (bitmap == null || bitmap.length <= side || bitmap[side] == null)
			{
				GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + side, 0, format, width, height, 0, pixelFormat, type, null);
			}
			else
			{
				GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + side, 0, bitmap[side], 0);

				bitmap[side].recycle();
			}
			
		}
		
		if (minFilter == GLES20.GL_LINEAR_MIPMAP_LINEAR
			|| minFilter == GLES20.GL_LINEAR_MIPMAP_NEAREST
			|| minFilter == GLES20.GL_NEAREST_MIPMAP_LINEAR
			|| minFilter == GLES20.GL_NEAREST_MIPMAP_NEAREST)
			GLES20.glGenerateMipmap(target);

		if (!initialized())
		{
			delete();
			throw new RuntimeException("Error creating texture");
		}
	}
	
	public boolean initialized()
	{
		return texture[0] != 0;
	}
	
	public void bind(int active)
	{
		if (!initialized())
			throw new RuntimeException("Tried to bind an uninitialized texture");
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + active);
		GLES20.glBindTexture(target, texture[0]);
	}
	
	public void delete()
	{
		if (!initialized())
			throw new RuntimeException("Tried to delete an uninitialized texture");
		
		
		GLES20.glDeleteTextures(1, texture, 0);

		texture[0] = 0;
		
	}
}
