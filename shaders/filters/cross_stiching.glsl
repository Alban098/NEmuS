#version 330

in vec2 pass_textureCoords;

out vec4 fragColor;

uniform sampler2D tex;
uniform float cross_size;
uniform int invert;

vec4 PostFX(sampler2D tex, vec2 uv)
{
    vec4 c = vec4(0.0);
    float size = cross_size;
    vec2 screenSize = textureSize(tex, 0);
    vec2 cPos = uv * screenSize;
    vec2 tlPos = floor(cPos / vec2(size, size));
    tlPos *= size;
    int remX = int(mod(cPos.x, size));
    int remY = int(mod(cPos.y, size));
    if (remX == 0 && remY == 0)
    tlPos = cPos;
    vec2 blPos = tlPos;
    blPos.y += (size - 1.0);
    if ((remX == remY) ||
    (((int(cPos.x) - int(blPos.x)) == (int(blPos.y) - int(cPos.y)))))
    {
        if (invert == 1)
        c = vec4(0.0, 0.0, 0.0, 1.0);
        else
        c = texture2D(tex, tlPos * vec2(1.0/screenSize.x, 1.0/screenSize.y)) * 1.4;
    }
    else
    {
        if (invert == 1)
        c = texture2D(tex, tlPos * vec2(1.0/screenSize.x, 1.0/screenSize.y)) * 1.4;
        else
        c = vec4(0.0, 0.0, 0.0, 1.0);
    }
    return c;
}

void main (void)
{
    fragColor = PostFX(tex, pass_textureCoords);
}