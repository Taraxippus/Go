#version 100
precision mediump float;

uniform samplerCube u_Texture;

varying vec3 v_Position;

void main()
{
	gl_FragColor = textureCube(u_Texture, v_Position);
}

