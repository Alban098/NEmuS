#version 330

in vec2 pass_textureCoords;

out vec4 fragColor;

uniform sampler2D tex;

void main() {
    vec3 color = texture2D(tex, pass_textureCoords).rgb;
    fragColor = vec4(vec3(0.3 * color.r + 0.59 * color.g + 0.11 * color.b), 1.0);
}