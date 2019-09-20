package com.tf.camera.renderer.camera;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

/**
 * create by TIAN FENG on 2019/9/20
 */
public class Camera {

    private static final int POSITION_COMPONENT_COUNT = 2;//2 x y
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;//  S T
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * VertexArray.BYTE_PRE_FLOAT;

    private VertexArray vertexArray;

    public Camera() {
        vertexArray = new VertexArray();
    }

    public void bindData(CameraShaderProgram textureProgram) {
        vertexArray.setVertexAttributePointer(
                0,
                textureProgram.getPositionLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );

        vertexArray.setVertexAttributePointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw(){
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,6);
    }
}
