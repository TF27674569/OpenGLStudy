uniform mat4 u_Matrix;
attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;//纹理坐标
varying vec2 v_TextureCoordinates;
void main() {
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position  = a_Position*u_Matrix;
}
