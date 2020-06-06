#version 330

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 textureCoords;

out vec2 pass_textureCoords;

void main() { 
   pass_textureCoords = textureCoords; 
   gl_Position = vec4(position, 0, 1.0);  
}
