### 正交投影矩阵

&emsp;到OpenGLES(四)为止，做横竖屏切换时会发现绘制的物体变形</br>
&emsp;因为屏幕不是正方形的,如果屏幕的分辨率1000x2000（为了好计算），此时按x[-1,1],y也为[-1,1],此时由于像素点不一样，比如一半处（-0.5f，-0.5f）像素其实是（500,1000），再做横竖屏切换时，肯定会，发生形变</br>

正交投影矩阵</br>
&emsp;我们将屏幕较小的范围控制再[-1,1],较大的在此范围拉伸那么此时x[-1,1] y[-2,2],其实此时的x与y相交处还是[-1,1]，下图红色框为正交投影举证，当横屏时将x进行拉伸，竖屏时将y进行拉伸（此处拉伸表示范围）</br>


![](https://github.com/TF27674569/OpenGLStudy/blob/master/image/%E6%AD%A3%E4%BA%A4%E6%8A%95%E5%BD%B1%E7%9F%A9%E9%98%B5.png)</br>

### 更改着色器代码
&emsp;1.添加一个正交投影矩阵</br>
&emsp;2.顶点坐标需要与正交投影矩阵相乘为归一化坐标</br>
&emsp;3.将归一化坐标赋值给opengl</br>
```glsl
uniform mat4 u_Matrix;
attribute vec4 a_Position;

attribute vec4 a_Color;
varying vec4 v_Color;
void main() {
    v_Color = a_Color;
    gl_Position  = a_Position*u_Matrix;
}
```

### 修改renderer代码
&emsp;1.找到u_Matrix的位置</br>

```java
private static final String U_MATRIX = "u_Matrix";
private int uMatrixLocation;
uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
```
&emsp;2.创建一个正交投影矩阵</br>

```java
private float[] projectionMatrix = new float[16];
```
&emsp;3.使用opengl的Matrix类计算正交投影举证，确定正交矩阵的范围</br>
```java
float aspectRatio = width > height ? width / (float) height : height / (float) width;
if (width > height) {
   // 拉伸宽度的区间
    Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, -1, 1);
} else {
    // 拉伸高度的区间
    Matrix.orthoM(projectionMatrix, 0, -1, 1, -aspectRatio, aspectRatio, -1, 1);
}
```
参数说明：<br>
&emsp; m :返回的结果矩阵：<br>
&emsp; mOffset：起始偏移值<br>
&emsp; left：x最小值<br>
&emsp; right：x最大值<br>
&emsp; bottom：y最小值<br>
&emsp; top：y最大值<br>
&emsp; near：z最小值<br>
&emsp; far：z最大值<br>

&emsp;4.传入矩阵给顶点着色器,此时将顶点传入时会与举证进行计算</br>

```java
 /**
  * count:需要加载数据的数组元素的数量或者需要修改的矩阵的数量,这里只有一个举证
  */
  GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
```

### Matrix.orthoM（）函数
&emsp;其他语言可以自己定义计算方法，android源码如下
```java
public static void orthoM(float[] m, int mOffset,
        float left, float right, float bottom, float top,
        float near, float far) {
        if (left == right) {
            throw new IllegalArgumentException("left == right");
        }
        if (bottom == top) {
            throw new IllegalArgumentException("bottom == top");
        }
        if (near == far) {
            throw new IllegalArgumentException("near == far");
        }

        final float r_width  = 1.0f / (right - left);
        final float r_height = 1.0f / (top - bottom);
        final float r_depth  = 1.0f / (far - near);
        final float x =  2.0f * (r_width);
        final float y =  2.0f * (r_height);
        final float z = -2.0f * (r_depth);
        final float tx = -(right + left) * r_width;
        final float ty = -(top + bottom) * r_height;
        final float tz = -(far + near) * r_depth;
        m[mOffset + 0] = x;
        m[mOffset + 5] = y;
        m[mOffset +10] = z;
        m[mOffset +12] = tx;
        m[mOffset +13] = ty;
        m[mOffset +14] = tz;
        m[mOffset +15] = 1.0f;
        m[mOffset + 1] = 0.0f;
        m[mOffset + 2] = 0.0f;
        m[mOffset + 3] = 0.0f;
        m[mOffset + 4] = 0.0f;
        m[mOffset + 6] = 0.0f;
        m[mOffset + 7] = 0.0f;
        m[mOffset + 8] = 0.0f;
        m[mOffset + 9] = 0.0f;
        m[mOffset + 11] = 0.0f;
    }
```

&emsp;实现原理如下图</br>
![](https://github.com/TF27674569/OpenGLStudy/blob/master/image/%E6%AD%A3%E4%BA%A4%E6%8A%95%E5%BD%B1%E7%9F%A9%E9%98%B5%E5%85%AC%E5%BC%8F%E6%9D%A5%E6%BA%90.png)</br>