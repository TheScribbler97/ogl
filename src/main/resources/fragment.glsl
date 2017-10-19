#version 330 core

in vec3 vfColor;
in vec2 vfTexCoord;

uniform float test;
uniform sampler2D albedo;

out vec4 fsColor;

void main()
{
    fsColor = vec4((texture(albedo,vfTexCoord).xyz*vfColor).xy, test, 1);
}