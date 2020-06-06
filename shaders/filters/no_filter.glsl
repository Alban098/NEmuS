#version 330

in vec2 pass_textureCoords;

out vec4 fragColor;

uniform sampler2D tex;

void main() {
    fragColor = texture2D(tex, pass_textureCoords);
}