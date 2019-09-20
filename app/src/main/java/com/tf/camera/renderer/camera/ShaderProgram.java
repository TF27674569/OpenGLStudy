package com.tf.camera.renderer.camera;

import android.content.Context;
import android.opengl.GLES20;

import com.tf.camera.util.ShaderHelper;
import com.tf.camera.util.TextResourcesReader;

/**
 * create by TIAN FENG on 2019/9/20
 */
public class ShaderProgram {

    // uniform
    protected static final String U_TEXTURE_MATRIX = "uTextureMatrix";// 外部纹理举证
    protected static final String U_TEXTURE_UNIT = "uTextureSampler";// 纹理资源

    // attribute
    protected static final String A_POSITION = "a_Position";// 位置
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";// 纹理坐标

    // shader program
    protected final int program;

    public ShaderProgram(Context context, int vertexShaderSourceId, int fragmentShaderSourceId) {
        String vertexShaderSource = TextResourcesReader.readTextFileFromResources(context, vertexShaderSourceId);
        String fragmentShaderSource = TextResourcesReader.readTextFileFromResources(context, fragmentShaderSourceId);
        program = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource);
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }
}
