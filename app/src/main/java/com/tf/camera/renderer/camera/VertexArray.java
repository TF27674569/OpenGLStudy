package com.tf.camera.renderer.camera;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * create by TIAN FENG on 2019/9/20
 */
public class VertexArray {
    public static final int BYTE_PRE_FLOAT = 4;

    // 坐标数据
    private static final float[] VERTEX_DATA = {
            // x,y ，S,T 顶点坐标系与纹理坐标系相对应 （顶点确定画的位置，纹理确定显示的部分）
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f,
            1f, 1f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
    };

    private FloatBuffer vertexBuffer;

    public VertexArray() {
        vertexBuffer = ByteBuffer.allocateDirect(VERTEX_DATA.length * BYTE_PRE_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(VERTEX_DATA);
    }



    public void setVertexAttributePointer(int dataOffset, int attributeLocation, int componentCount, int stride) {
        vertexBuffer.position(dataOffset);
        GLES20.glVertexAttribPointer(attributeLocation, componentCount, GLES20.GL_FLOAT, false, stride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(attributeLocation);

        vertexBuffer.position(0);
    }
}
