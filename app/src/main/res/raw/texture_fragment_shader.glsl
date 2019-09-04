//片段着色器用来告诉gpu 每个片段的最终颜色时什么
precision mediump float;//mediump 设置为中等精度 lowp 低精度   highp 高精度（性能低） 权衡速度与质量选择中等精度

uniform sampler2D  u_TextureUnit;// 纹理数据 sampler2D二维的纹理数组

varying vec2 v_TextureCoordinates;// 纹理坐标

void main()
{
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);// 纹理坐标和纹理数据通过函数texture2D 之后得到的值传给片段着色
}