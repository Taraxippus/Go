#version 100
precision mediump float;

uniform vec4 u_Color;
uniform vec2 u_Specularity;
uniform vec3 u_Eye;
uniform vec3 u_Light;
uniform vec3 u_Light2;

uniform float u_AlphaStart;

varying vec3 v_Normal;
varying vec3 v_Position;

const float c_Ambient = 0.5;

void main()
{
	vec3 normal = normalize(v_Normal);
	vec3 light = normalize(u_Light - v_Position);

	float diff = max(0.0, dot(normal, light));
	float diff2 = max(0.0, dot(normal, -u_Light2));
	
	float spec = 0.0;
	
	if (diff > 0.0)
		spec = clamp(pow(dot(normalize(-reflect(normal, light)), normalize(u_Eye - v_Position)), u_Specularity.x), 0.0, 1.0);
	
	gl_FragColor = vec4(u_Color.rgb * (c_Ambient + (diff + diff2 * 0.5) / 1.5 * (1.0 - c_Ambient) + spec * u_Specularity.y), u_Color.a * u_AlphaStart);
}
