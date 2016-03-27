package com.taraxippus.go.game.gameobject;

import android.opengl.*;
import com.taraxippus.go.game.*;
import com.taraxippus.go.render.*;

public class FullscreenQuad extends GameObject
{
	public FullscreenQuad(World world, Pass pass)
	{
		super(world);
		
		this.setPass(pass);
	}

	public static final float[] vertices = new float[] 
	{
		-1, -1,
		1, -1,
		-1, 1,
		1, 1
	};
	
	@Override
	public Shape createShape()
	{
		Shape shape = new Shape();
		shape.init(GLES20.GL_TRIANGLE_STRIP, vertices, 4, getPass().getAttributes());
		return shape;
	}
	
}
