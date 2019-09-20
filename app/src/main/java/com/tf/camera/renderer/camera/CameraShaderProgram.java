package com.tf.camera.renderer.camera;


import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.tf.camera.R;

/**
 * create by TIAN FENG on 2019/9/20
 */
public class CameraShaderProgram extends ShaderProgram {

    // uniform location
    private final int uTextureMatrixLocation;
    private  int uTextureUnitLocation=-1;

    // attribute location
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public CameraShaderProgram(Context context) {
        super(context, R.raw.camera_vertex_shader, R.raw.camera_fragment_shader);
        uTextureMatrixLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_MATRIX);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }


    /**
     * 绑定外部纹理并将外部纹理和数据传递给着色器
     * @param matrix
     * @param textureId
     */
    public void setUniforms(float[] matrix, int textureId) {
        // 激活纹理单位
        GLES20.glActiveTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        // 绑定外部纹理到纹理单元0
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        //将此纹理单元床位片段着色器的uTextureSampler外部纹理采样器
        GLES20.glUniform1i(uTextureUnitLocation, 0);
        //将纹理矩阵传给片段着色器
        GLES20.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, matrix, 0);
    }


    public int getPositionLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesLocation() {
        return aTextureCoordinatesLocation;
    }
}
