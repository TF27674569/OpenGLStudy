package com.tf.camera.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.tf.camera.R;
import com.tf.camera.util.ShaderHelper;
import com.tf.camera.util.TextResourcesReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * create by TIAN FENG on 2019/9/4
 * 练习 给桌子加边框
 * 先画两个大三角形 再在中间画两个小三角形
 */
public class SampleRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTE_PRE_FLOAT = 4;
    private static final boolean isDebug = true;


    private static final String U_COLOR = "u_Color";
    private int uColorLocation;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;


    private FloatBuffer vertexBuffer;
    private Context context;
    private int program;// opengl程序

    public SampleRenderer(Context context) {
        this.context = context;
        // 顶点矩阵
        // draw 一个矩形实际上时draw两个三角形
        float[] tableVertices = {
                -0.55f, -0.55f,
                0.55f, -0.55f,
                0.55f, 0.55f,

                0.55f, 0.55f,
                -0.55f, 0.55f,
                -0.55f, -0.55f,

                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,

                0.5f, 0.5f,
                -0.5f, 0.5f,
                -0.5f, -0.5f,
        };

        // 申请native内存堆    byte占一个字节 里面存的时float  占4字节
        vertexBuffer = ByteBuffer.allocateDirect(tableVertices.length * BYTE_PRE_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        // 将顶点数据 复制到native
        vertexBuffer.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 屏幕底色 为红色
        GLES20.glClearColor(1, 1, 1, 1);

        // 着色器代码
        String vertexShaderRes = TextResourcesReader.readTextFileFromResources(context, R.raw.vertex_shader1);
        String fragmentShaderRes = TextResourcesReader.readTextFileFromResources(context, R.raw.fragment_shader1);

        // 创建着色器
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderRes);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderRes);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (isDebug) {
            ShaderHelper.validateProgram(program);
        }

        GLES20.glUseProgram(program);

        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        // 使能数据
        GLES20.glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//        // 给片段着色器颜色
        GLES20.glUniform4f(uColorLocation, 0, 0, 0, 1);
        // 画两个三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);


        /*    画不通颜色的长方形******/
        GLES20.glUniform4f(uColorLocation, 1, 0, 1, 1);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 6, 3);

        GLES20.glUniform4f(uColorLocation, 1, 1, 0, 1);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 9, 3);
    }
}
