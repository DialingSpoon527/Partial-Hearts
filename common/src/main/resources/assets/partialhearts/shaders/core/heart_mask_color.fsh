#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;

uniform vec2 UVStart;
uniform vec2 UVEnd;
uniform float Mask[81];

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    if (color.a < 0.1) {
        discard;
    }

    vec2 localUV = (texCoord0 - UVStart) / (UVEnd - UVStart);
    vec2 scaledUV = localUV * 9.0;

    int ix = min(int(scaledUV.x), 8);
    int iy = min(int(scaledUV.y), 8);

    int index = iy * 9 + ix;

    if (Mask[index] == 1.0) {
        discard;
    }

    fragColor = color * ColorModulator;
}
