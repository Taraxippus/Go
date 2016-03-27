#version 100
precision mediump float;

uniform sampler2D u_Texture;
uniform sampler2D u_Dither;

uniform vec2 u_InvResolution;
uniform float u_VignetteFactor;

varying vec2 v_UV;
varying vec2 v_UV_NW;
varying vec2 v_UV_NE;
varying vec2 v_UV_SW;
varying vec2 v_UV_SE;

const float FXAA_SPAN_MAX = 16.0;
const float FXAA_REDUCE_MUL = 1.0 / 16.0;
const float FXAA_REDUCE_MIN = 1.0 / 256.0;
const vec3 luma = vec3(0.299, 0.587, 0.114);

void main()
{
	vec3 rgbNW = texture2D(u_Texture, v_UV_NW).xyz;
    vec3 rgbNE = texture2D(u_Texture, v_UV_NE).xyz;
    vec3 rgbSW = texture2D(u_Texture, v_UV_SW).xyz;
    vec3 rgbSE = texture2D(u_Texture, v_UV_SE).xyz;
    vec3 rgbM  = texture2D(u_Texture, v_UV.xy).xyz;

    float lumaNW = dot(rgbNW, luma);

    float lumaNE = dot(rgbNE, luma);
    float lumaSW = dot(rgbSW, luma);
    float lumaSE = dot(rgbSE, luma);
    float lumaM  = dot( rgbM, luma);

    float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
    float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));

    vec2 dir;
    dir.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
    dir.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));

    float dirReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * (0.25 * FXAA_REDUCE_MUL), FXAA_REDUCE_MIN);
    float rcpDirMin = 1.0/(min(abs(dir.x), abs(dir.y)) + dirReduce);

    dir = min(vec2(FXAA_SPAN_MAX,  FXAA_SPAN_MAX),
            max(vec2(-FXAA_SPAN_MAX, -FXAA_SPAN_MAX), dir * rcpDirMin)) * u_InvResolution;

    vec3 rgbA = (1.0 / 2.0) * (
              texture2D(u_Texture, v_UV.xy + dir * (1.0 / 3.0 - 0.5)).xyz +
              texture2D(u_Texture, v_UV.xy + dir * (2.0 / 3.0 - 0.5)).xyz);

    vec3 rgbB = rgbA * (1.0 / 2.0) + (1.0 / 4.0) * (
              texture2D(u_Texture, v_UV.xy + dir * (0.0 / 3.0 - 0.5)).xyz +
              texture2D(u_Texture, v_UV.xy + dir * (3.0 / 3.0 - 0.5)).xyz);

    float lumaB = dot(rgbB, luma);

    if ((lumaB < lumaMin) || (lumaB > lumaMax))
    {
        gl_FragColor.rgb = rgbA;
    }
    else
    {
        gl_FragColor.rgb = rgbB;
    }

	gl_FragColor.rgb *= texture2D(u_Dither, v_UV).r * 0.05 + 0.5 * (1.0 - u_VignetteFactor * length(v_UV * 2.0 - vec2(1.0, 1.0))) + 0.45;
    gl_FragColor.a = 1.0;
}
