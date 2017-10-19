#version 330 core

layout (location = 0) in vec3 pvPos;
layout (location = 1) in vec3 pvColor;
layout (location = 2) in vec2 pvTexCoord;

out vec3 vfColor;
out vec2 vfTexCoord;

void main()
{
    gl_Position = vec4(pvPos.x, pvPos.y, pvPos.z, 1.0);
    vfColor = pvColor;
    vfTexCoord = pvTexCoord;
}