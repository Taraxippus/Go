package com.taraxippus.go.render;

import android.opengl.*;
import com.taraxippus.go.*;
import android.graphics.*;
import java.util.*;

public enum Pass
{
	SCENE_REFLECTION,
	SKYBOX_REFLECTION,
	
	SKYBOX,
	SCENE_MIRROR,
	SCENE,
	POST;
	
	public static final float REFLECTION_ALPHA_START = 0.25F;
	
	private static final Program[] programs = new Program[Pass.values().length];
	private static final Framebuffer[] framebuffers = new Framebuffer[Pass.values().length];
	
	private static final int[][] attributes = new int[Pass.values().length][];
	
	public static final Texture dither = new Texture();
	
	static
	{
		for (int i = 0; i < programs.length; ++i)
			programs[i] = new Program();
			
		for (int i = 0; i < framebuffers.length; ++i)
			framebuffers[i] = new Framebuffer();
			
		attributes[SKYBOX.ordinal()] = new int[] {3};
		attributes[SCENE_MIRROR.ordinal()] = new int[] {3, 3};
		attributes[SCENE_REFLECTION.ordinal()] = new int[] {3, 3};
		attributes[SKYBOX_REFLECTION.ordinal()] = new int[] {3};
		attributes[SCENE.ordinal()] = new int[] {3, 3};
		attributes[POST.ordinal()] = new int[] {2};
	}
	
	public static void init(Main main)
	{
		programs[SKYBOX.ordinal()].init(main, R.raw.vertex_skybox, R.raw.fragment_skybox, "a_Position");
		programs[SCENE_MIRROR.ordinal()].init(main, R.raw.vertex_scene, R.raw.fragment_scene_mirror, "a_Position", "a_Normal");
		programs[SCENE_REFLECTION.ordinal()].init(main, R.raw.vertex_reflection, R.raw.fragment_reflection, "a_Position", "a_Normal");
		programs[SKYBOX_REFLECTION.ordinal()].init(main, R.raw.vertex_skybox, R.raw.fragment_skybox_reflection, "a_Position");
		programs[SCENE.ordinal()].init(main, R.raw.vertex_scene, R.raw.fragment_scene, "a_Position", "a_Normal");
		
		programs[POST.ordinal()].init(main, R.raw.vertex_post, R.raw.fragment_post, "a_Position");
		
		framebuffers[SCENE.ordinal()].init(main, true, main.renderer.width, main.renderer.height);
		framebuffers[SCENE_REFLECTION.ordinal()].init(main, true, main.renderer.width / 2, main.renderer.height / 2);
		
		SKYBOX.getProgram().use();
		GLES20.glUniform1i(SKYBOX.getProgram().getUniform("u_Texture"), 0);
		
		SCENE_MIRROR.getProgram().use();
		GLES20.glUniform1i(SKYBOX.getProgram().getUniform("u_Texture"), 0);
		
		SCENE_REFLECTION.getProgram().use();
		GLES20.glUniform1f(SCENE_REFLECTION.getProgram().getUniform("u_AlphaStart"), REFLECTION_ALPHA_START);
		
		SKYBOX_REFLECTION.getProgram().use();
		GLES20.glUniform1f(SKYBOX_REFLECTION.getProgram().getUniform("u_AlphaStart"), REFLECTION_ALPHA_START);
		GLES20.glUniform1i(SKYBOX.getProgram().getUniform("u_Texture"), 0);
		
		POST.getProgram().use();
		GLES20.glUniform1i(POST.getProgram().getUniform("u_Texture"), 0);
		GLES20.glUniform1i(POST.getProgram().getUniform("u_Dither"), 1);
		
		final int[] colors = new int[main.renderer.width * main.renderer.height];
		final int gray;
		final Random random = new Random();
		
		for (int i = 0; i < colors.length; ++i)
		{
			gray = random.nextInt(256);
			colors[i] = Color.rgb(gray, gray, gray);
		}
			
		dither.init(Bitmap.createBitmap(main.getResources().getDisplayMetrics(), colors, main.renderer.width, main.renderer.height, Bitmap.Config.RGB_565), GLES20.GL_NEAREST, GLES20.GL_NEAREST, GLES20.GL_CLAMP_TO_EDGE);
	}
	
	public static void delete()
	{
		for (Program program : programs)
			if (program.initialized())
				program.delete();
				
		for (Framebuffer framebuffer : framebuffers)
			if (framebuffer.initialized())
				framebuffer.delete();
				
		if (dither.initialized())
			dither.delete();
	}
	
	public Program getProgram()
	{
		return programs[this.ordinal()];
	}
	
	public int[] getAttributes()
	{
		return attributes[this.ordinal()];
	}
	
	public Framebuffer getFramebuffer()
	{
		return framebuffers[this.ordinal()];
	}
	
	public boolean inOrder()
	{
		return true;
	}
	
	public Pass getParent()
	{
		switch (this)
		{
			default:
				return this;
		}
	}
	
	public static void onRenderFrame(Renderer renderer)
	{
		SCENE_REFLECTION.getFramebuffer().bind();
	}
	
	public void onRender(Renderer renderer)
	{
		this.getProgram().use();
		
		switch (this)
		{
			case SKYBOX_REFLECTION:
				GLES20.glDepthMask(true);
				break;
				
			case SCENE_REFLECTION:
				GLES20.glDepthMask(true);
				break;
				
			case SKYBOX:
				SCENE.getFramebuffer().bind();
				GLES20.glDepthMask(false);
				GLES20.glCullFace(GLES20.GL_BACK);
				
				break;
				
			case SCENE_MIRROR:
				SCENE_REFLECTION.getFramebuffer().bindTexture(0);
				GLES20.glUniform2f(getProgram().getUniform("u_InvResolution"), 1F / SCENE.getFramebuffer().width, 1F / SCENE.getFramebuffer().height);
				
				GLES20.glDepthMask(true);
				GLES20.glCullFace(GLES20.GL_BACK);
				
				GLES20.glUniform3fv(getProgram().getUniform("u_Eye"), 1, renderer.main.camera.eye.getVec40(), 0);
				GLES20.glUniform3fv(getProgram().getUniform("u_Light"), 1, renderer.main.game.light.getVec40(), 0);
				GLES20.glUniform3fv(getProgram().getUniform("u_Light2"), 1, renderer.main.game.light2.getVec40(), 0);
				
				break;
				
			case SCENE:
				GLES20.glDepthMask(true);
				GLES20.glCullFace(GLES20.GL_BACK);
				
				GLES20.glUniform3fv(getProgram().getUniform("u_Eye"), 1, renderer.main.camera.eye.getVec40(), 0);
				GLES20.glUniform3fv(getProgram().getUniform("u_Light"), 1, renderer.main.game.light.getVec40(), 0);
				GLES20.glUniform3fv(getProgram().getUniform("u_Light2"), 1, renderer.main.game.light2.getVec40(), 0);
				
				break;
				
			case POST:
				GLES20.glDepthMask(true);
				GLES20.glCullFace(GLES20.GL_BACK);
				Framebuffer.release(renderer);
				
				SCENE.getFramebuffer().bindTexture(0);
				dither.bind(1);
				
				GLES20.glUniform2f(getProgram().getUniform("u_InvResolution"), 1F / SCENE.getFramebuffer().width, 1F / SCENE.getFramebuffer().height);
				GLES20.glUniform1f(getProgram().getUniform("u_VignetteFactor"), 0.6F + 0.2F / renderer.main.timeFactor);
				
				break;
		}
	}
}
