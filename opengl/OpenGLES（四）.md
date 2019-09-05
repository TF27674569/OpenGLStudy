## 增加颜色和着色
###  平滑着色
&emsp;在片段着色器中使用uniform绘制的物体，颜色单一，如何在一个三角形上面绘制不同的颜色（不能将一个三角形切分为多个三角形）</br>

&emsp;还是以矩形为例，矩形中心点更明亮，边缘暗淡，只前的矩阵没有中心点，这里修改矩阵

### 三角形扇

&emsp; 三角形扇：以点(0,0)为中心点，依次绘制三角形（abc,acd,ade,aeb）逆时针绘制效率高（此时的矩形是有4个三角形构成）

```java
三角形扇矩阵
float[] tableVertices = {
    0, 0,            // a
    -0.5f, -0.5f,    // b
    0.5f, -0.5f,     // c
    0.5f, 0.5f,      // d
    -0.5f, 0.5f,     // e
    -0.5f, -0.5f,    // b
};
```


&emsp; 绘制是时使用类型GL_TRIANGLE_FAN
```java
GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
```
&emsp; 绘制后矩形与之前一样 但是，颜色还是单一的

### 添加颜色属性到矩阵
&emsp; 矩阵首两位位置属性（x，y）后三位颜色分量（r,g,b）

```java
        float[] tableVertices = {
                0, 0,           1f, 1f, 1f,
                -0.5f, -0.5f,   0.7f, 0.7f, 0.7f,
                0.5f, -0.5f,    0.7f, 0.7f, 0.7f,
                0.5f, 0.5f,     0.7f, 0.7f, 0.7f,
                -0.5f, 0.5f,    0.7f, 0.7f, 0.7f,
                -0.5f, -0.5f,   0.7f, 0.7f, 0.7f
        };
```
&emsp; 某个点到某个点颜色的变化需要修改着色器代码,申明可变化的颜色（varying v_Color），v_Color通过a_Color赋值传入

顶点
```glsl
attribute vec4 a_Position;
attribute vec4 a_Color;
varying vec4 v_Color;
void main() {
    v_Color = a_Color;
    gl_Position  = a_Position;
}
```

片段
```glsl
precision mediump float;
varying vec4 v_Color;
void main() {
    gl_FragColor = v_Color;
}
```
&emsp; varying是一个特殊变量类型，他会把哪些特殊的值混合然后将混合的值发送给片段着色器

### 修改代码以及stride的使用
&emsp; u_Color变成a_Color，并且找到a_Color的位置</br>
&emsp; 变量COLOR_COMPONENT_COUNT（RGB）</br>
&emsp; 变量STRIDE表示跨度，也就是两个顶点之间中间隔了多少个byte,否则opengl不知道怎么跳跃到下一个顶点

```java
private static final int POSITION_COMPONENT_COUNT = 2;
private static final int COLOR_COMPONENT_COUNT = 3;
private static final int BYTE_PRE_FLOAT = 4;
private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTE_PRE_FLOAT;

// 添加STRIDE跨距
GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexBuffer);

// 不能从第一个读取，第一个属性是位置所以只能读颜色属性
vertexBuffer.position(POSITION_COMPONENT_COUNT);
// 关联color与顶点的关系
GLES20.glVertexAttribPointer(aColorLocation,COLOR_COMPONENT_COUNT,GLES20.GL_FLOAT, false, STRIDE, vertexBuffer);
// 使能顶点数据
GLES20.glEnableVertexAttribArray(aColorLocation);
```

### 最后绘制
&emsp; 绘制不需要额外设置片段着色器的颜色
```java
 // 画三角形扇
GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
```






