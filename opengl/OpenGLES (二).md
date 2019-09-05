## 着色器

### 设置顶点坐标
&#160;顶点坐标系原点位于屏幕正中心，x正方向往右，y正方向往上 </br>
&#160;z正方向往屏幕外面,范围默认为[-1,1] x最左边-1，最右边1，y同理</br>
</br></br></br>
屏幕正中心区域
```java
 float[] tableVertices = {
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,

                0.5f, 0.5f,
                -0.5f, 0.5f,
                -0.5f, -0.5f,
        };
```

&#160;将矩阵复制进native堆
```java
private FloatBuffer vertexBuffer;
 // 申请native内存堆    byte占一个字节 里面存的时float  占4字节
vertexBuffer = ByteBuffer.allocateDirect(tableVertices.length * BYTE_PRE_FLOAT)
    .order(ByteOrder.nativeOrder())
    .asFloatBuffer();

// 将顶点数据 复制到native
 vertexBuffer.put(tableVertices);
```

### 创建着色器
#### 顶点着色器

```glsl
attribute vec4 a_Position;

void main() {
    gl_Position  = a_Position;
}
```
&#160;a_Position : 用于接收当前顶点的位置，属性定义为vec4</br>
&#160;vec4 : 4分量，在位置a_Position中表示x,y,z,w  xyz表示三维坐标系，w为特殊的坐</br>
&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
标默认情况下xyz为0，w为1，tableVertices中只使用了x和y默认的z=0，w=1</br>
&#160;attribute ：就是将这些属性放进着色器的手段</br>
&#160;gl_Position ：opengl指定的输出变量，顶点着色器一定要给其赋值，用于确定最终位置</br>

#### 片段着色器
```glsl
precision mediump float;//定义浮点数据的默认精度为中精度

uniform vec4 u_Color;

void main() {
    gl_FragColor = u_Color;
}
```
&#160;precision : 定义精度</br>
&#160;mediump : 中精度（lowp：低精度，highp:高精度）</br>
&#160;float : 浮点数据</br>
&#160;uniform : 会让每个顶点使用同一个值</br>
&#160;vec4 : r，g，b，a 的分量</br>
&#160;gl_FragColor ：opengl指定的输出变量，片着色器一定要给其赋值，用于确定最终颜色</br>

