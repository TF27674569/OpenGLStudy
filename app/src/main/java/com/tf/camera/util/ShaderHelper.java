package com.tf.camera.util;

import android.opengl.GLES20;
import android.util.Log;

/**
 * create by TIAN FENG on 2019/9/4
 */
public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    /**
     * 返回顶点着色器程序地址
     *
     * @param vertexShaderRes 顶点着色器代码
     */
    public static int compileVertexShader(String vertexShaderRes) {
        return compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderRes);
    }

    /**
     * 返回片段着色器程序地址
     *
     * @param fragmentShaderRes 片段着色器代码
     */
    public static int compileFragmentShader(String fragmentShaderRes) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderRes);
    }


    public static int linkProgram(int vertexShader, int fragmentShader) {
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            Log.e(TAG, "create program error! ");
        }

        // 将着色器附加进OpenGL程序
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        // 链接程序
        GLES20.glLinkProgram(program);

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == 0) {
            Log.e(TAG, "link program error. msg:" + GLES20.glGetProgramInfoLog(program));
        }

        return program;
    }


    public static boolean validateProgram(int program) {
        GLES20.glValidateProgram(program);
        int[] validateState = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, validateState, 0);
        Log.e(TAG, "validate program state: " + validateState[0] + "  msg:" + GLES20.glGetProgramInfoLog(program));
        return validateState[0] != 0;
    }


    /**
     * 返回着色器程序地址
     *
     * @param shaderType 着色器类型
     * @param shaderRes  着色器代码
     */
    private static int compileShader(int shaderType, String shaderRes) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            Log.e(TAG, shaderRes + "\n create shader error!");
            return 0;
        }

        // 上传着色器代码
        GLES20.glShaderSource(shader, shaderRes);
        // 编译
        GLES20.glCompileShader(shader);

        // 检查是否编译成功
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            // 失败
            Log.e(TAG, "compile shader error. msg:" + GLES20.glGetShaderInfoLog(shader));
        }

        return shader;
    }
}
