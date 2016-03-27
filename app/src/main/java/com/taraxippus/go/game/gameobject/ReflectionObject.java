package com.taraxippus.go.game.gameobject;

import android.opengl.*;
import com.taraxippus.go.render.*;
import com.taraxippus.go.util.*;

public class ReflectionObject extends GameObject
{
	public final SceneObject parent;
	public final VectorF side;
	
	public final float[] modelMatrix = new float[16];
	public final VectorF position = new VectorF();
	
	public ReflectionObject(SceneObject parent, VectorF side)
	{
		super(parent.world);
		this.parent = parent;
		this.side = side;
	}

	@Override
	public GameObject setPass(Pass pass)
	{
		if (pass == Pass.SKYBOX)
			super.setPass(Pass.SKYBOX_REFLECTION);
			
		else
			super.setPass(Pass.SCENE_REFLECTION);
			
		return this;
	}
	
	@Override
	public void render(Renderer renderer)
	{
		if (!parent.enabled || !world.main.camera.insideFrustum(position.set(getX(parent.position.x), getY(parent.position.y), getZ(parent.position.z)), parent.radius))
			return;
		
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z);
		Matrix.scaleM(modelMatrix, 0, parent.scale.x * (side.x == 0 ? 1 : -1), parent.scale.y * (side.y == 0 ? 1 : -1), parent.scale.z * (side.z == 0 ? 1 : -1));
		
		Matrix.rotateM(modelMatrix, 0, parent.rotation.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix, 0, parent.rotation.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, parent.rotation.z, 0, 0, 1);
		
		if (renderer.currentPass != getPass())
			getPass().onRender(renderer);
		
		renderer.uniform(modelMatrix, getPass());
		
		if (getPass() == Pass.SCENE_REFLECTION)
		{
			GLES20.glUniform4f(getPass().getProgram().getUniform("u_Color"), parent.color.x, parent.color.y, parent.color.z, parent.alpha);
			GLES20.glUniform2f(getPass().getProgram().getUniform("u_Specularity"), parent.specularityExponent, parent.specularityFactor);
			GLES20.glUniform3f(getPass().getProgram().getUniform("u_Eye"), getX(world.main.camera.eye.x), getY(world.main.camera.eye.y), getZ(world.main.camera.eye.z));
			GLES20.glUniform3f(getPass().getProgram().getUniform("u_Light"), getX(world.main.game.light.x), getY(world.main.game.light.y), getZ(world.main.game.light.z));
			GLES20.glUniform3f(getPass().getProgram().getUniform("u_Light2"), world.main.game.light2.x, -world.main.game.light2.y, world.main.game.light2.z);
			
			GLES20.glDepthMask(this.parent.alpha == 1);
		}
		else if (getPass() == Pass.SKYBOX_REFLECTION && parent instanceof Skybox)
			((Skybox) parent).texture.bind(0);
			
		GLES20.glCullFace((side.dot(side) % 2 == 1) ? GLES20.GL_FRONT : GLES20.GL_BACK);

		if (parent.shape != null)
			parent.shape.render();
			
		if (renderer.currentPass != getPass())
			getPass().getParent().onRender(renderer);
	}
	
	@Override
	public float getDepth()
	{
		return parent.depthOffset + 1000 * side.dot(side) + position.set(getX(parent.position.x), getY(parent.position.y), getZ(parent.position.z)).subtract(world.main.camera.eye).length();
	}
	
	public float getX(float x)
	{
		if (side.x == 1)
			return -x + 2 * (world.main.game.getWidth() - 0.5F);
		else if (side.x == -1)
			return -x + 2 * (- 0.5F);
		
		return x;
	}
	
	public float getY(float y)
	{
		if (side.y == 1)
			return -y + 2 * (world.main.game.getHeight() - 0.5F);
		else if (side.y == -1)
			return -y + 2 * (- 0.5F);
				
		return y;
	}
	
	public float getZ(float z)
	{
		if (side.z == 1)
			return -z + 2 * (world.main.game.getLength() - 0.5F);
		else if (side.z == -1)
			return -z + 2 * (- 0.5F);
		
		
		return z;
	}
	
}
