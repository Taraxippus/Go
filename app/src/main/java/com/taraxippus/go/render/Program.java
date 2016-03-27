package com.taraxippus.go.render;

import android.opengl.*;
import com.taraxippus.go.*;
import java.util.*;
import android.app.AlertDialog;

public class Program
{
	int program;
	
	public Program()
	{
		
	}
	
	public void init(Main main, int vertexShader, int fragmentShader, String... attributes)
	{
		init(main, main.resourceHelper.getString(vertexShader), main.resourceHelper.getString(fragmentShader), attributes);
	}
	
	public void init(final Main main, String vertexShader, String fragmentShader, String... attributes)
	{
		if (initialized())
			delete();
		
		uniforms.clear();
			
		int vertex = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

		if (vertex != 0) 
		{
			GLES20.glShaderSource(vertex, vertexShader);
			GLES20.glCompileShader(vertex);

			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(vertex, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			if (compileStatus[0] == 0) 
			{			
				final String error = GLES20.glGetShaderInfoLog(vertex);
				System.err.println(error);
				
				main.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							AlertDialog alertDialog = new AlertDialog.Builder(main).create();
							alertDialog.setTitle("Error creating vertex shader");
							alertDialog.setMessage(error);
							alertDialog.show();
							
						}
					});
				
				GLES20.glDeleteShader(vertex);
				vertex = 0;
			}
		}

		if (vertex == 0)
		{
			//throw new RuntimeException("Error creating vertex shader.");
			return;
		}

		int fragment = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

		if (fragment != 0) 
		{
			GLES20.glShaderSource(fragment, fragmentShader);
			GLES20.glCompileShader(fragment);

			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(fragment, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			if (compileStatus[0] == 0) 
			{				
				final String error = GLES20.glGetShaderInfoLog(fragment);
				System.err.println(error);
				
				main.runOnUiThread(new Runnable()
				{
						@Override
						public void run()
						{
							AlertDialog alertDialog = new AlertDialog.Builder(main).create();
							alertDialog.setTitle("Error creating fragment shader");
							alertDialog.setMessage(error);
							alertDialog.show();
						}
				});
				
				GLES20.glDeleteShader(fragment);
				fragment = 0;
			}
		}

		if (fragment == 0)
		{
			//throw new RuntimeException("Error creating fragment shader.");
			return;
		}

		program = GLES20.glCreateProgram();

		if (program != 0) 
		{
			GLES20.glAttachShader(program, vertex);			
			GLES20.glAttachShader(program, fragment);
			
			for (int i = 0; i < attributes.length; ++i)
			{
				GLES20.glBindAttribLocation(program, i, attributes[i]);
			}
			
			GLES20.glLinkProgram(program);

			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);

			if (linkStatus[0] == 0) 
			{				
				final String error = GLES20.glGetProgramInfoLog(program);
				System.err.println(error);
				
				main.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							AlertDialog alertDialog = new AlertDialog.Builder(main).create();
							alertDialog.setTitle("Error creating program");
							alertDialog.setMessage(error);
							alertDialog.show();
						}
					});
				
				GLES20.glDeleteProgram(program);
				program = 0;
			}
			
			GLES20.glDeleteShader(vertex);
			GLES20.glDeleteShader(fragment);
		}

		if (program == 0)
		{
			//throw new RuntimeException("Error creating program.");
			return;
		}
	}
	
	HashMap<String, Integer> uniforms =  new HashMap<>();
	
	public int getUniform(String name)
	{
		if (!uniforms.containsKey(name))
			uniforms.put(name, GLES20.glGetUniformLocation(program, name));
	
		return uniforms.get(name);
	}
	
	public boolean initialized()
	{
		return program != 0;
	}
	
	public void use()
	{
		if (initialized())
		{
			GLES20.glUseProgram(program);
		}
		else
		{
			System.err.println("Tried to use an uninitialized Program");
		}
	}
	
	public void delete()
	{
		if (!initialized())
		{
			System.err.println("Tried to delete an uninitialized Program");
			return;
		}
		
		GLES20.glDeleteProgram(program);
		
		program = 0;
	}
}
