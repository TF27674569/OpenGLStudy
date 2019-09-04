uniform mat4 u_Matrix;// mat4 代表4x4的矩阵
attribute vec4 a_Position;//vec4 包含4个分量  x,y,z,w w为特殊坐标 默认值 0 0 0 1
attribute vec2 a_TextureCoordinates;// S 坐标 T坐标

varying vec2 v_TextureCoordinates;// 插值  与android插值器相似

// u_Matrix    a_Position      结果
// [1 0 0 0]     [x]       [1*x + 0*y + 0*z + 0*w]
// [0 1 0 0]     [y]  =    [0*x + 1*y + 0*z + 0*w]
// [0 0 1 0]     [z]       [0*x + 0*y + 1*z + 0*w]
// [0 0 0 1]     [w]       [0*x + 0*y + 0*z + 1*w]

void main()
{
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position = u_Matrix*a_Position;// 4分量的矩阵相乘
    gl_PointSize = 10.0;
}