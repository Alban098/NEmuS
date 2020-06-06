#version 330

in vec2 pass_textureCoords;

out vec4 fragColor;

uniform sampler2D tex;

void main() {
    float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);
    vec2 tex_offset = 0.25 / textureSize(tex, 0); // gets size of single texel
    vec3 result = texture2D(tex, pass_textureCoords).rgb * weight[0]; // current fragment's contribution

    for(int i = 1; i < 5; ++i)
    {
        result += texture2D(tex, pass_textureCoords + vec2(0.0, tex_offset.y * i)).rgb * weight[i];
        result += texture2D(tex, pass_textureCoords - vec2(0.0, tex_offset.y * i)).rgb * weight[i];
    }

    fragColor = vec4(result, 1.0);
}