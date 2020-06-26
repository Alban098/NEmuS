#version 330

in vec2 pass_textureCoords;

out vec4 fragColor;

uniform sampler2D tex;

void main() {
    float weight[9] = float[](-1, -1, -1, -1, 8, -1, -1, -1, -1);
    vec2 tex_offset = 1.0 / textureSize(tex, 0); // gets size of single texel
    vec3 result = vec3(0);

    for(int i = 0; i < 3; i++){
        for(int j = 0; j < 3; j++){
            result += texture2D(tex, pass_textureCoords + vec2(tex_offset.x * i, tex_offset.y * j)).rgb * weight[i * 3 + j];
        }
    }

    fragColor = vec4(vec3(result.x * 0.3 + result.y * 0.59 + result.z * 0.11), 1.0);
}