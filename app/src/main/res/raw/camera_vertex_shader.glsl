uniform mat4 uTextureMatrix;// 纹理矩阵
attribute vec4 a_Position;//顶点坐标
attribute vec4 a_TextureCoordinates;//纹理坐标
varying vec2 vTextureCoord;// 变化的纹理坐标值传入给片段着色器
void main() {
    //根据自己定义的纹理坐标和纹理矩阵求取传给片段着色器的纹理坐标
    vTextureCoord = (uTextureMatrix * a_TextureCoordinates).xy;
    gl_Position  = a_Position;
}
