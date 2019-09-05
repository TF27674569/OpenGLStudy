
### 判断设备是否支持GLES2.0
```java
 // 判断支不支持 GLES2.0
 ActivityManager activityManager = (ActivityManager)
                    getSystemService(Context.ACTIVITY_SERVICE);
 ConfigurationInfo deviceConfigurationInfo = 
                    activityManager.getDeviceConfigurationInfo();
 
 boolean supportsEs2 = deviceConfigurationInfo.reqGlEsVersion >= 0x20000;
```

### 在设置rendrer之前先设置一下OpenGLES的版本
```java
// 2.0
 glSurfaceView.setEGLContextClientVersion(2);
```
### renderer

```java
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 屏幕底色 为红色
        GLES20.glClearColor(1, 0, 0, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 渲染的surface大小
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 擦除屏幕上的所有颜色 （除了 GLES20.glClearColor(1, 0, 0, 0) 默认颜色）
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
```