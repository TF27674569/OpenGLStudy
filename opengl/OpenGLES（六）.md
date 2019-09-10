### 三维

##### 基本概念
position通过透视除法变成归一化坐标（w分量表示距离） </br>
&emsp;裁剪空间：对于给定的一个点坐标（x,y,z,w），x,y,z的范围都必须在[-w,w]之间，超出的部分不显示</br>
&emsp;透视除法：对于给定的一个点坐标（x,y,z,w），x,y,z都除以w分量得到一个新的坐标，点x（1，1，1，1）点y（1，1，1，2）这个得到的新坐标（1，1，1）,(0.5,0.5,0.5)

##### 代码修改
&emsp;1.给矩阵添加w分量（x,y,z,w ,r,g,b）
```java
float[] tableVertices = {
        0, 0, 0f, 1.5f,          1f,   1f,   1f,
        -0.5f, -0.5f, 0f, 1f,    0.7f, 0.7f, 0.7f,
        0.5f, -0.5f, 0f, 1f,     0.7f, 0.7f, 0.7f,
        0.5f, 0.5f, 0f, 2f,      0.7f, 0.7f, 0.7f,
        -0.5f, 0.5f, 0f, 2f,     0.7f, 0.7f, 0.7f,
        -0.5f, -0.5f, 0f, 1f,    0.7f, 0.7f, 0.7f
};
```
&emsp;2.顶点个数占4个

```java
private static final int POSITION_COMPONENT_COUNT = 4;
```
&emsp;运行之后额能看到一个3d的矩形


##### 透视投影
&emsp;与照相机拍照相似，某以横截面能看到物体的所有图形，也就是某一角度观看物体，能看到的屏幕图形（照相机拍照时，3d物体显示在相片之后的效果）


##### 视锥体
&emsp;观看角度越小，离物体越远，所观察的范围越大，反之亦然</br>
&emsp;下图透视投影矩阵</br>
![透视投影矩阵](https://github.com/TF27674569/OpenGLStudy/blob/master/image/%E9%80%8F%E8%A7%86%E6%8A%95%E5%BD%B1%E7%9F%A9%E9%98%B5.png)</br>
&emsp;参数：</br>
&emsp;a：焦距</br>
&emsp;aspect：宽高比w/h</br>
&emsp;f：远点距离</br>
&emsp;n：近点距离</br>
&emsp;注意：图上一处有标错[-1,1]宽度为2</br>
![](https://github.com/TF27674569/OpenGLStudy/blob/master/image/%E9%80%8F%E8%A7%86%E6%8A%95%E5%BD%B1%E4%B8%BE%E8%AF%81%E5%88%86%E6%9E%90.png)</br>


##### 代码中创建投影矩阵
```java
/**
  * 透视投影举证算法
  * 注意opengl中是以列存贮的
  * @param m             目标矩阵，16位的空矩阵
  * @param yFovInDegrees 视野角度（类似人眼观看角度）
  * @param aspect        宽高比
  * @param n             物体的近点（3d物体）
  * @param f             物体的原点
  */
 public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f) {

     if (m.length != 16) {
         throw new IllegalArgumentException("m length is  illegally,length is " + m.length);
     }
     // 角度转弧度
     float yFovInRadians = (float) (yFovInDegrees * Math.PI / 180);
     float a = (float) (1 / Math.tan(yFovInRadians / 2));

     m[0] = a / aspect;
     m[1] = 0;
     m[2] = 0;
     m[3] = 0;


     m[4] = 0;
     m[5] = a;
     m[6] = 0;
     m[7] = 0;


     m[8] = 0;
     m[9] = 0;
     m[10] = -(f + n) / (f - n);
     m[11] = -1;

     m[12] = 0;
     m[13] = 0;
     m[14] = -2 * f * n / (f - n);
     m[15] = 0;


    /* android源码
     float f = 1.0f / (float) Math.tan(fovy * (Math.PI / 360.0));
     float rangeReciprocal = 1.0f / (zNear - zFar);

     m[offset + 0] = f / aspect;
     m[offset + 1] = 0.0f;
     m[offset + 2] = 0.0f;
     m[offset + 3] = 0.0f;

     m[offset + 4] = 0.0f;
     m[offset + 5] = f;
     m[offset + 6] = 0.0f;
     m[offset + 7] = 0.0f;

     m[offset + 8] = 0.0f;
     m[offset + 9] = 0.0f;
     m[offset + 10] = (zFar + zNear) * rangeReciprocal;
     m[offset + 11] = -1.0f;

     m[offset + 12] = 0.0f;
     m[offset + 13] = 0.0f;
     m[offset + 14] = 2.0f * zFar * zNear * rangeReciprocal;
     m[offset + 15] = 0.0f;

     */

 }
```
&emsp;使用矩阵</br>
```java
       GLES20.glViewport(0, 0, width, height);

//        float aspectRatio = width > height ? width / (float) height : height / (float) width;
//        if (width > height) {
//            // 拉伸宽度的区间
//            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, -1, 1);
//        } else {
//            // 拉伸高度的区间
//            Matrix.orthoM(projectionMatrix, 0, -1, 1, -aspectRatio, aspectRatio, -1, 1);
//        }

        MatrixHelper.perspectiveM(projectionMatrix, 45, width / (float) height, 1, 10);
```
&emsp;注意：这个近点远点的值在opengl里面都是负值，摄像头所指方向为z轴负方向
&emsp;此时运行看不到之前绘制的矩形了


&emsp;利用模型矩阵移动物体</br>
```java
 MatrixHelper.perspectiveM(projectionMatrix, 45, width / (float) height, 1, 10)
```
这一行代码近点都在z轴-1处，而我们物体的z轴为0,所以导致摄像机在物体下方，看不到物体
```java
 float[] tableVertices = {
                        // z
        0, 0,             0f,    1.5f, 1f, 1f, 1f,
        -0.5f, -0.5f,     0f,    1f, 0.7f, 0.7f, 0.7f,
        0.5f, -0.5f,      0f,    1f, 0.7f, 0.7f, 0.7f,
        0.5f, 0.5f,       0f,    2f,0.7f, 0.7f, 0.7f,
        -0.5f, 0.5f,      0f,    2f, 0.7f, 0.7f, 0.7f,
        -0.5f, -0.5f,     0f,    1f,0.7f, 0.7f, 0.7f
};
```


解决这个问题</br>
&emsp;1.新建模型矩阵并将整个坐标往z轴移动-2
&emsp;2.将顶点坐标与模型矩阵相乘得到归一化坐标
```java
 // 单位矩阵
 Matrix.setIdentityM(modelMatrix, 0);
 // z轴移动-2
 Matrix.translateM(modelMatrix, 0, 0, 0, -2);

 之前单位矩阵中有提到任何矩阵乘单位矩阵等于自己
```

两种方式得到归一化坐标</br>
&emsp;1.在着色器中添加一个新的矩阵，让所有的顶点与模矩阵</br>
&emsp;2.模型举证与投影矩阵相乘，得到一个新的矩阵，然后将新的举证传给着色器</br>
这里使用第二种方式：</br>
&emsp;存在问题：两个举证相乘得到的结果和位置相关</br>
&emsp;&emsp;MatrixA*MatrixB≠MatrixB*MatrixA</br>
&emsp;&emsp;OpenGL中是以 投影矩阵*模型举证，投影矩阵在前，模型举证在后

```java
float temp[] = new float[16];
// 相乘得到结果放入temp
Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
// temp拷贝到projectionMatrix
System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
```
此时可以看到图像了


##### 旋转物体
```java
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, 6f, 1, 0,0);
        Matrix.translateM(modelMatrix, 0, 0, 0, -2.5f);
```
我这里与文档不一样，如果先将模型移动，再按x轴旋转，那此时的不一样，坐标不一样，不是贴再z=0的平面上旋转，我这里先让其再z平面旋转之后再将其平移</br>

下为矩阵旋转算法（android Matrix.rotateM）已经将其封装，源码中有实现逻辑</br>

x轴旋转的矩阵</br>
![](https://github.com/TF27674569/OpenGLStudy/blob/master/image/x%E8%BD%B4%E6%97%8B%E8%BD%AC.png)</br>
y轴旋转的矩阵</br>
![](https://github.com/TF27674569/OpenGLStudy/blob/master/image/y%E8%BD%B4%E6%97%8B%E8%BD%AC.png)</br>
z轴旋转的矩阵</br>
![](https://github.com/TF27674569/OpenGLStudy/blob/master/image/z%E8%BD%B4%E6%97%8B%E8%BD%AC.png)</br>