#version 100
precision mediump float;

uniform samplerCube u_Texture;

varying vec3 v_Position;

uniform float u_AlphaStart;

void main()
{
	gl_FragColor = textureCube(u_Texture, v_Position);
	gl_FragColor.a *= u_AlphaStart;
}

