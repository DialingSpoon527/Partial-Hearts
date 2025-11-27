#version 150

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
    float LineWidth;
};

layout(std140) uniform CustomUniform {
    vec2 UVStart;
    vec2 UVEnd;
    ivec3 MaskBits;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    if (color.a == 0.0) discard;

    vec2 localUV = (texCoord0 - UVStart) / (UVEnd - UVStart);
    vec2 scaledUV = localUV * 9.0;

    int ix = min(int(scaledUV.x), 8);
    int iy = min(int(scaledUV.y), 8);

    int index = iy * 9 + ix;

    int bucket = index / 27;
    int bitPos = index % 27;

    int bits = bucket == 0 ? MaskBits.x :
    bucket == 1 ? MaskBits.y :
    MaskBits.z;

    if (((bits >> bitPos) & 1) != 0) {
        discard;
    }

    fragColor = color * ColorModulator;
}
