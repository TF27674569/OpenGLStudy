package com.tf.camera.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * create by TIAN FENG on 2019/9/3
 */
public class FirstRenderer implements GLSurfaceView.Renderer {


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
}
