#version 330

layout(location = 0) in vec2 position;

out vec2 pass_textureCoords;

void main() {
   pass_textureCoords = position * 0.5 + 0.5;
   gl_Position = vec4(position, 0, 1.0);
}
