#version 100
precision mediump float;

attribute vec4 a_Position;
uniform mat4 u_MVP;
varying vec3 v_Position;

void main()
{
	v_Position = vec3(a_Position);
	gl_Position = u_MVP * a_Position;
}
