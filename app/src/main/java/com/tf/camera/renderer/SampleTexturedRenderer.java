package com.tf.camera.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.tf.camera.R;
import com.tf.camera.util.MatrixHelper;
import com.tf.camera.util.ShaderHelper;
import com.tf.camera.util.TextResourcesReader;
import com.tf.camera.util.TexturedHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;

/**
 * create by TIAN FENG on 2019/9/3
 * 三角形扇
 */
public class SampleTexturedRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;//x,y
    private static final int BYTE_PRE_FLOAT = 4;
    private static final int TEXTURE_COORDINATE_COMPONENT_COUNT = 2;// 纹理坐标ST
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATE_COMPONENT_COUNT) * BYTE_PRE_FLOAT;
    private static final boolean isDebug = true;



    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;

    private static final String U_TEXTURE_UNIT = "u_TextureUnit";
    private int uTextureUnitLocation;

    private static final String U_TEXTURE_UNIT_1 = "u_TextureUnit1";
    private int uTextureUnitLocation1;

    private static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    private int aTextureCoordinateLocation;



    // 投影矩阵
    private float[] projectionMatrix = new float[16];
    // 模型矩阵
    private float[] modelMatrix = new float[16];

    private int texture;
    private int textureSample;

    private FloatBuffer vertexBuffer;
    private Context context;
    private int program;// opengl程序

    public SampleTexturedRenderer(Context context) {
        this.context = context;
        float[] tableVertices = {
                0, 0, 0.5f, 0.5f,
                -0.5f, -0.8f, 0f, 0.9f,
                0.5f, -0.8f, 1, 0.9f,
                0.5f, 0.8f, 1f, 0.1f,
                -0.5f, 0.8f, 0, 0.1f,
                -0.5f, -0.8f, 0, 0.9f

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
        String vertexShaderRes = TextResourcesReader.readTextFileFromResources(context, R.raw.texture_vertex_shader);
        String fragmentShaderRes = TextResourcesReader.readTextFileFromResources(context, R.raw.texture_fragment_shader);

        // 创建着色器
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderRes);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderRes);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (isDebug) {
            ShaderHelper.validateProgram(program);
        }


        GLES20.glUseProgram(program);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordinateLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);


        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
        uTextureUnitLocation1 = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT_1);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexBuffer);
        // 使能数据
        GLES20.glEnableVertexAttribArray(aPositionLocation);


        vertexBuffer.position(POSITION_COMPONENT_COUNT);
        GLES20.glVertexAttribPointer(aTextureCoordinateLocation, TEXTURE_COORDINATE_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexBuffer);
        // 使能数据
        GLES20.glEnableVertexAttribArray(aTextureCoordinateLocation);

        texture = TexturedHelper.loadTexture(context, R.drawable.image2);
        textureSample = TexturedHelper.loadTexture(context, R.drawable.image3);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        // -1  -10
        MatrixHelper.perspectiveM(projectionMatrix, 45, width / (float) height, 1f, 10f);

        // 单位矩阵
        Matrix.setIdentityM(modelMatrix, 0);
//        Matrix.rotateM(modelMatrix, 0, 6f, 1, 0, 0);
        Matrix.translateM(modelMatrix, 0, 0, 0, -2.5f);

        float temp[] = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 投影矩阵（与顶点坐标相乘得到新顶点）
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        // 把活动纹理单元设置为0
        GLES20.glActiveTexture(GL_TEXTURE0);
        // 绑定这个纹理
        GLES20.glBindTexture(GL_TEXTURE_2D, texture);
        // 传入纹理数据
        GLES20.glUniform1i(uTextureUnitLocation, 0);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GL_TEXTURE_2D, textureSample);
        GLES20.glUniform1i(uTextureUnitLocation1, 1);


        // 开启混合
        GLES20.glEnable(GL_BLEND);
        // 混合模式
        GLES20.glBlendFunc(GLES20.GL_SRC_COLOR , GLES20.GL_SRC_COLOR);
        // 绘制
        GLES20.glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

    }
}
