#version 330

in vec2 pass_textureCoords;

out vec4 fragColor;

uniform sampler2D tex;
uniform float strength;

const float PI = 3.1415926535;

vec2 distort(vec2 p)
{
    float theta  = atan(p.y, p.x);
    float radius = length(p);
    radius = pow(radius, strength);
    p.x = radius * cos(theta);
    p.y = radius * sin(theta);
    return 0.5 * (p + 1.0);
}

void main()
{
    vec2 xy = 2.0 * pass_textureCoords.xy - 1.0;
    vec2 uv;
    float d = length(xy);
    if (d < 1.0)
        uv = distort(xy);
    else
        uv = pass_textureCoords.xy;
    fragColor = texture2D(tex, uv);
}