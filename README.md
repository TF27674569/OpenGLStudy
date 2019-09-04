## 编译着色器和屏幕绘制

### 将glsl里面的的着色器代码读取出来
```java
public static String readTextFileFromResources(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            is = context.getResources().openRawResource(resourceId);
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                body.append(nextLine).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeIO(br, isr, is);
        }
        return body.toString();
    }
```

onSurfaceCreated中GLES20.glClearColor()之后读取

```java
String vertexShaderRes = TextResourcesReader.readTextFileFromResources(context, R.raw.vertex_shader1);
String fragmentShaderRes = TextResourcesReader.readTextFileFromResources(context, R.raw.fragment_shader1);
```

### 编译着色器
&#160;1.创建着色器GLES20.glCreateShader(shaderType)</br>
&#160;&#160;&#160;&#160;shaderType：GL_VERTEX_SHADER，GL_FRAGMENT_SHADER</br>
&#160;2.上传着色器代码GLES20.glShaderSource(shader,shaderRes);</br>
&#160;3.编译着色器代码GLES20.glCompileShader(shader);</br>
&#160;4.判断是否编译成功（选择性）</br>

```java
/**
* 返回着色器程序地址
*
* @param shaderType 着色器类型
* @param shaderRes  着色器代码
*/
private int compileShader(int shaderType, String shaderRes) {
    int shader = GLES20.glCreateShader(shaderType);
    if (shader == 0) {
        Log.e(TAG, shaderRes + "\n create shader error!");
        return 0;
    }

    // 上传着色器代码
    GLES20.glShaderSource(shader,shaderRes);
    // 编译
    GLES20.glCompileShader(shader);

    // 检查是否编译成功
    int[] compileStatus = new int[1];
    GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,compileStatus,0);

    if (compileStatus[0] == 0){
        // 失败
        Log.e(TAG, "compile shader error. msg:"+ GLES20.glGetShaderInfoLog(shader));
    }

     return shader;
}
```


onSurfaceCreated读取着色器代码之后创建着色器
```java
 // 创建着色器
int vertexShader = ShaderHelper.compileVertexShader(vertexShaderRes);
int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderRes);
```

### 创建OpenGL程序并链接着色器
&#160;1.创建OpenGL程序 GLES20.glCreateProgram()</br>
&#160;2将着色器附加进OpenGL程序GLES20.glAttachShader(program, shader)</br>
&#160;3.链接OpenGL程序 GLES20.glLinkProgram(program)</br>
&#160;4.判断是否链接成功（选择性）</br>

```java
 public static int linkProgram(int vertexShader, int fragmentShader) {
    int program = GLES20.glCreateProgram();
    if (program == 0) {
        Log.e(TAG, "create program error! ");
    }

    // 将着色器附加进OpenGL程序
     GLES20.glAttachShader(program, vertexShader);
    GLES20.glAttachShader(program, fragmentShader);

    // 链接程序
    GLES20.glLinkProgram(program);

    int[] linkStatus = new int[1];
    GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);

    if (linkStatus[0] == 0) {
        Log.e(TAG, "link program error. msg:" + GLES20.glGetProgramInfoLog(program));
    }

    return program;
}
```
onSurfaceCreated创建着色器代码之后链接
```java
program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
```


### 调试程序（只有在debug状态才加上,刻意不用加）
```java
public static boolean validateProgram(int program) {
    GLES20.glValidateProgram(program);
    int[] validateState = new int[1];
    GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, validateState, 0);
    Log.e(TAG, "validate program state: " + validateState[0] + "  msg:" + GLES20.glGetProgramInfoLog(program));
    return validateState[0] != 0;
}
```
onSurfaceCreated链接代码之后链接
```java
if (isDebug) {
    ShaderHelper.validateProgram(program);
}

```

### 使用程序
onSurfaceCreated最后添加
```java
  GLES20.glUseProgram(program);
```

### 找到我们在着色器定义的属性（u_Color和a_Position）
需要知道u_Color和a_Position的位置</br>
u_Color在着色器中定义的为uniform,使用函数glGetUniformLocation</br>
a_Position在着色器中定义的为attribute使用函数glGetAttribLocation</br>
```java
uColorLocation = GLES20.glGetUniformLocation(program,"u_Color");
aPositionLocation = GLES20.glGetAttribLocation(program,"a_Position");
```

### 关联属性与顶点数据数组
```java
vertexBuffer.position(0);
GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, vertexBuffer);
```

vertexBuffer.position(0) 表示从开头开始读取数据</br>
glVertexAttribPointer参数</br>
int indx 属性位置，顶点坐标a_Position我们需要操作这个属性</br>
int size 有多少个分量与之相关联，这里只有x，y（当然也可能存在rgb分量与xyz使用同一个数组）</br>
int type 类型里面存放的时float数据）</br>
boolean normalized 使用整型时这个才有意义</br>
int stride 一个数组中存在多个属性时，会通过这个来告知，读几个数据</br>
Buffer ptr 数据</br>


### 使能数据
```java
GLES20.glEnableVertexAttribArray(aPositionLocation);
```
告知opegl a_Position可以使用了

### 画三角形
&#160;1.给片段着色器颜色（通过uColorPosition传给opengl）
&#160;2.画三角形
```java
 // 给片段着色器颜色
 GLES20.glUniform4f(uColorLocation, 1, 1, 1, 1);
 // 画两个三角形
 GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
```
