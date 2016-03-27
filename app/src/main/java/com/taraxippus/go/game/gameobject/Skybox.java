package com.taraxippus.go.game.gameobject;

import android.graphics.*;
import android.opengl.*;
import com.taraxippus.go.game.*;
import com.taraxippus.go.render.*;

import android.opengl.Matrix;
import com.taraxippus.go.game.Camera;
import com.taraxippus.go.*;

public class Skybox extends SceneObject
{
	public final float scale;
	public final Texture texture = new Texture();
	
	public Skybox(World world)
	{
		super(world);
		
		this.scale = (float)Math.sqrt(Camera.Z_FAR * Camera.Z_FAR / 3F * 4) - 20;
		this.scale(scale, scale, scale);
	}

	public float getRadius()
	{
		return (float) Math.sqrt(scale * scale * 0.5 * 0.5 + scale * scale * 0.5 * 0.5 + scale * scale * 0.5 * 0.5);
	}

	@Override
	public float getDepth()
	{
		return 0;
	}
	
	@Override
	public void update()
	{
		this.position.set(world.main.camera.position);
		this.updateMatrix();
		
		super.update();
	}

	@Override
	public void init()
	{
		super.init();
		
		texture.initCubemap(new Bitmap[] 
		{
			world.main.resourceHelper.getBitmap(R.drawable.posx),
			world.main.resourceHelper.getBitmap(R.drawable.negx),
			world.main.resourceHelper.getBitmap(R.drawable.posy),
			world.main.resourceHelper.getBitmap(R.drawable.negy),
			world.main.resourceHelper.getBitmap(R.drawable.posz),
			world.main.resourceHelper.getBitmap(R.drawable.negz),
		}, GLES20.GL_NEAREST, GLES20.GL_NEAREST, GLES20.GL_CLAMP_TO_EDGE);
	}

	@Override
	public void render(Renderer renderer)
	{
		texture.bind(0);
		
		super.render(renderer);
	}
	
	@Override
	public void delete()
	{
		super.delete();
		
		if (texture.initialized())
			texture.delete();
	}
	
	
	@Override
	public Pass getPass()
	{
		return Pass.SKYBOX;
	}
	
	public static final float[] vertices = new float[]
	{
		-0.5F, -0.5F, -0.5F,
		0.5F, -0.5F, -0.5F,
		-0.5F, -0.5F, 0.5F,
		0.5F, -0.5F, 0.5F,
		
		-0.5F, 0.5F, -0.5F,
		0.5F, 0.5F, -0.5F,
		-0.5F, 0.5F, 0.5F,
		0.5F, 0.5F, 0.5F,

		-0.5F, -0.5F, -0.5F,
		-0.5F, 0.5F, -0.5F,
		-0.5F, -0.5F, 0.5F,
		-0.5F, 0.5F, 0.5F,

		0.5F, -0.5F, -0.5F,
		0.5F, 0.5F, -0.5F,
		0.5F, -0.5F, 0.5F,
		0.5F, 0.5F, 0.5F,

		-0.5F, -0.5F, -0.5F,
		0.5F, -0.5F, -0.5F,
		-0.5F, 0.5F, -0.5F,
		0.5F, 0.5F, -0.5F,

		-0.5F, -0.5F, 0.5F,
		0.5F, -0.5F, 0.5F,
		-0.5F, 0.5F, 0.5F,
		0.5F, 0.5F, 0.5F,
	};

	public static final short[] indices = new short[]
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

		shape.init(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());

		return shape;
	}
}
