### 纹理
&emsp;纹理坐标系:屏幕左下角为坐标系的原点，往左和往上分别对应坐标系的x,y的正方向，x,y分别表示两个维度（s，t）</br>
&emsp;OpenGLES2.0中规定单个维度大小，都应该是2的幂，也就是图像的大小宽或者高需要2的幂

##### 纹理的使用
###### 创建纹理
&emsp;eg:添加一张bitmap图像纹理到opengl</br>

&emsp;1.创建纹理:glGenTextures</br>
&emsp;2.绑定纹理待:glBindTexture(GLES20.GL_TEXTURE_2D, texturedObjIds[0]) (GLES20.GL_TEXTURE_2D告诉Opengl将纹理作为二维纹理对)</br>
&emsp;3.纹理过滤:glTexParameteri（单独列出）</br>
&emsp;4.将数据读入纹理:GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)</br>
&emsp;5.派生的一组完整的mipmap数组:glGenerateMipmap（维度从0，0）开始</br>
&emsp;6.解除绑定:GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)，id传0</br>
&emsp;注意：创建纹理成功后，中途有异常数据需要判断时用glDeleteTextures删除纹理</br>

```java
public static int loadTexture(Context context, int resId) {
        final int[] texturedObjIds = new int[1];
        // 创建一个纹理
        GLES20.glGenTextures(1, texturedObjIds, 0);

        if (texturedObjIds[0] == 0) {
            Log.e(TAG, "Create texture error!");
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);

        if (bitmap == null) {
            Log.e(TAG, "resId  to bitmap error !");
            GLES20.glDeleteTextures(1, texturedObjIds, 0);
            return 0;
        }

        // GL_TEXTURE_2D 告诉opengl这个纹理作为二维纹理对待
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturedObjIds[0]);

        // 纹理过滤
        // 缩小情况使用，三线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        // 放大情况使用，双线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // 将bitmap数据读进纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        // 从（0，0）点创建一组mipmap纹理图像数据
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        // 传0，解除纹理绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return texturedObjIds[0];
    }

```
&emsp;纹理过滤：</br>
&emsp;当纹理被放大或者缩小时，纹理数据会被挤压或拉伸，此时会失真</br>
&emsp;GL_NEAREST: 最临近过滤，获得最靠近纹理坐标点的像素。</br>
&emsp;GL_LINEAR: 线性插值过滤，获取坐标点附近4个像素的加权平均值。</br>
&emsp;GL_NEAREST_MIPMAP_NEAREST：选择最邻近的mip层，并使用最邻近过滤。</br>
&emsp;GL_NEAREST_MIPMAP_LINEAR：对两个mip层使用最邻近过滤后的采样结果进行加权平均。</br>
&emsp;GL_LINEAR_MIPMAP_NEAREST：选择最邻近的mip层，使用线性插值算法进行过滤。</br>
&emsp;GL_LINEAR_MIPMAP_LINEAR：对两个mip层使用线性插值过滤后的采样结果进行加权平均，又称三线性mipmap。</br>

&emsp;经过纹理过滤后图像有一定的保真</br>

###### 修改着顶点坐标数组

&emsp;2d纹理只要x，y坐标，后面为纹理坐标s，t
```java
float[] tableVertices = {
        0, 0, 0.5f, 0.5f,
        -0.5f, -0.8f, 0f, 0.9f,
        0.5f, -0.8f, 1, 0.9f,
        0.5f, 0.8f, 1f, 0.1f,
        -0.5f, 0.8f, 0, 0.1f,
        -0.5f, -0.8f, 0, 0.9f
};
```
&emsp;片段着色器不用单一的颜色，使用纹理着色所有color相关字段可以去掉
```java
private static final int TEXTURE_COORDINATE_COMPONENT_COUNT = 2;// 纹理坐标ST
private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATE_COMPONENT_COUNT) * BYTE_PRE_FLOAT;
```
&emsp;修改着色器代码

&emsp;顶点着色器
```glsl
uniform mat4 u_Matrix;
attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;//纹理坐标
varying vec2 v_TextureCoordinates;
void main() {
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position  = a_Position*u_Matrix;
}
```
&emsp;片段着色器
```glsl
precision mediump float;//mediump 设置为中等精度 lowp 低精度   highp 高精度（性能低） 权衡速度与质量选择中等精度

uniform sampler2D  u_TextureUnit;// 纹理数据 sampler2D二维的纹理数组
varying vec2 v_TextureCoordinates;// 纹理坐标
void main()
{
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);// 纹理坐标和纹理数据通过函数texture2D 之后得到的值传给片段着色
}
```
顶点确定位置，然后将位置的纹理坐标告诉片段着色器，进行着色，片段着色器需要外部传入纹理</br>


&emsp;java代码中找到着色器变量的位置
```java
aTextureCoordinateLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
```
&emsp;使能新的a_TextureCoordinates Attribute属性
```java
vertexBuffer.position(POSITION_COMPONENT_COUNT);
GLES20.glVertexAttribPointer(aTextureCoordinateLocation, TEXTURE_COORDINATE_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexBuffer);
// 使能数据
GLES20.glEnableVertexAttribArray(aTextureCoordinateLocation);
```

###### 绘制纹理
```java
// 投影矩阵（与顶点坐标相乘得到新顶点）
GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
// 把活动文帝单元设置为0
GLES20.glActiveTexture(GL_TEXTURE0);
// 绑定这个纹理
GLES20.glBindTexture(GL_TEXTURE_2D,texture);
// 传入纹理数据
GLES20.glUniform1i(uTextureUnitLocation,0);

// 绘制
GLES20.glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
```

### 练习题
&emsp;纹理混合，将两个图片的纹理经过相加或者相乘然后显示</br>
&emsp;分析：将纹理混合后着色，需要改变的是gl_FragColor，此时的gl_FragColor因该是两个纹理相加后的值此时的gl_FragColor,因该是两个纹理相加后的值texture2D = texture2D() + texture2D(),两张图片完全覆盖，纹理左坐标其实是一样的，只需要再片段着色器中多提供一个纹理的属性即可

```glsl
//片段着色器用来告诉gpu 每个片段的最终颜色时什么
precision mediump float;//mediump 设置为中等精度 lowp 低精度   highp 高精度（性能低） 权衡速度与质量选择中等精度

uniform sampler2D  u_TextureUnit;// 纹理数据 sampler2D二维的纹理数组
uniform sampler2D  u_TextureUnit1;// 纹理数据 sampler2D二维的纹理数组
varying vec2 v_TextureCoordinates;// 纹理坐标
void main()
{
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates) + texture2D(u_TextureUnit1, v_TextureCoordinates);// 纹理坐标和纹理数据通过函数texture2D 之后得到的值传给片段着色
}
```

如果只改变部分位置的纹理，那么需要在顶点着色器中提供一个varying的纹理坐标值传递给片段着色器

java代码
```java
private static final String U_TEXTURE_UNIT_1 = "u_TextureUnit1";
private int uTextureUnitLocation1;


uTextureUnitLocation1 = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT_1);

textureSample = TexturedHelper.loadTexture(context, R.drawable.image3);
```

注意绘制
```java
激活一个新的纹理，应该为GL_TEXTURE0上面已经有了纹理texture

GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
GLES20.glBindTexture(GL_TEXTURE_2D, textureSample);
GLES20.glUniform1i(uTextureUnitLocation1, 1);
```
混合纹理
```java
// 开启混合
GLES20.glEnable(GL_BLEND);
// 混合模式
GLES20.glBlendFunc(GLES20.GL_SRC_COLOR , GLES20.GL_SRC_COLOR);
```

这个混合模式根据自身需求更改查询