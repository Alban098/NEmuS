#version 330

layout(location = 0) in vec2 position;

out vec2 pass_textureCoords;

void main() {
   pass_textureCoords = position * 0.5 + 0.5;
   if (position.x == 1 && position.y == 1)
      gl_Position = vec4(-1, -1, 0, 1.0);
   else if (position.x == -1 && position.y == -1)
      gl_Position = vec4(1, 1, 0, 1.0);
   else
      gl_Position = vec4(position, 0, 1.0);
}
