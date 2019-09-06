package com.tf.camera.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.tf.camera.R;
import com.tf.camera.util.ShaderHelper;
import com.tf.camera.util.TextResourcesReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * create by TIAN FENG on 2019/9/3
 * 三角形扇
 */
public class TableRenderer3 implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int BYTE_PRE_FLOAT = 4;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTE_PRE_FLOAT;
    private static final boolean isDebug = true;


    private static final String A_COLOR = "a_Color";
    private int aColorLocation;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;

    private float[] projectionMatrix = new float[16];


    private FloatBuffer vertexBuffer;
    private Context context;
    private int program;// opengl程序

    public TableRenderer3(Context context) {
        this.context = context;
        // 顶点矩阵
        //  三角形扇  中心点0 0
        // 0, 0,
        //  -0.5f, -0.5f,
        //  0.5f, -0.5f,
        // 0, 0,
        // 0.5f, -0.5f,
        // 0.5f, 0.5f,
        // 以此类推
        float[] tableVertices = {
                0, 0, 0f, 1f, 1f,
                -0.5f, -0.5f, 0f, 0.7f, 0.7f,
                0.5f, -0.5f, 0f, 0.7f, 0.7f,
                0.5f, 0.5f, 0f, 0.7f, 0.7f,
                -0.5f, 0.5f, 0f, 0.7f, 0.7f,
                -0.5f, -0.5f, 0f, 0.7f, 0.7f
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
        String vertexShaderRes = TextResourcesReader.readTextFileFromResources(context, R.raw.vertex_shader3);
        String fragmentShaderRes = TextResourcesReader.readTextFileFromResources(context, R.raw.fragment_shader2);

        // 创建着色器
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderRes);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderRes);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (isDebug) {
            ShaderHelper.validateProgram(program);
        }

        GLES20.glUseProgram(program);

        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexBuffer);

        // 使能数据
        GLES20.glEnableVertexAttribArray(aPositionLocation);


        vertexBuffer.position(POSITION_COMPONENT_COUNT);
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aColorLocation);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float aspectRatio = width > height ? width / (float) height : height / (float) width;
        if (width > height) {
            // 拉伸宽度的区间
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, -1, 1);
        } else {
            // 拉伸高度的区间
            Matrix.orthoM(projectionMatrix, 0, -1, 1, -aspectRatio, aspectRatio, -1, 1);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//        // 给片段着色器颜色
//        GLES20.glUniform4f(uColorLocation, 0, 1, 1, 1);
//        // 画两个三角形
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        /*    画不通颜色的长方形******/

//        GLES20.glUniform4f(uColorLocation, 1, 0, 1, 1);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
//
//        GLES20.glUniform4f(uColorLocation, 1, 1, 0, 1);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 3, 3);

        /**
         * count:需要加载数据的数组元素的数量或者需要修改的矩阵的数量,这里只有一个举证
         */
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        // 画三角形扇
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);

    }
}
