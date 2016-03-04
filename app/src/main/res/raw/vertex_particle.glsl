#version 100
precision mediump float;

attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 a_Direction;

uniform mat4 u_MV;
uniform mat4 u_P;

varying vec4 v_Color;
varying vec2 v_Direction;

void main()
{
	v_Color = a_Color;
	v_Direction = a_Direction;

	gl_Position = u_P * (vec4(a_Direction * a_Position.w, 0.0, 0.0) + u_MV * vec4(a_Position.xyz, 1.0));
}