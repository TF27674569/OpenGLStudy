package com.tf.camera.renderer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.tf.camera.renderer.camera.Camera;
import com.tf.camera.renderer.camera.CameraShaderProgram;
import com.tf.camera.util.TexturedHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * create by TIAN FENG on 2019/9/16
 */
public abstract class CameraRenderer implements GLSurfaceView.Renderer{


    private Context context;
    private Camera camera;
    private CameraShaderProgram cameraShaderProgram;
    private int textureId = -1;

    private SurfaceTexture surfaceTexture;
    private float[] surfaceMatrix = new float[16];
    // 是否开始预览
    private boolean isPreviewStarted;


    public CameraRenderer(Context context) {
        this.context =context;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        textureId = TexturedHelper.createOESTexture();
        camera = new Camera();
        cameraShaderProgram = new CameraShaderProgram(context);
        cameraShaderProgram.useProgram();
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (surfaceTexture != null) {
            //更新纹理图像
            surfaceTexture.updateTexImage();
            //获取外部纹理的矩阵，用来确定纹理的采样位置，没有此矩阵可能导致图像翻转等问题
            surfaceTexture.getTransformMatrix(surfaceMatrix);
        }

        if (!isPreviewStarted) {
            // 创建 SurfaceTexture
            surfaceTexture = createSurfaceTextureBindCamera(textureId);
            isPreviewStarted = true;
            return;
        }

        // 绘制之前清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        camera.bindData(cameraShaderProgram);
        cameraShaderProgram.setUniforms(surfaceMatrix,textureId);

        camera.draw();

    }

    /**
     * 创建SurfaceTexture并绑定摄像头硬件
     */
    protected abstract SurfaceTexture createSurfaceTextureBindCamera(int textureId);

}
