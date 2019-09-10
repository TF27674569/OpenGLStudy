package com.tf.camera.util;

/**
 * create by TIAN FENG on 2019/9/10
 */
public class MatrixHelper {

    /**
     * 透视投影举证算法
     * 注意opengl中是以列存贮的
     *
     * @param m             目标矩阵，16位的空矩阵
     * @param yFovInDegrees 视野角度（类似人眼观看角度）
     * @param aspect        宽高比
     * @param n             物体的近点（3d物体）
     * @param f             物体的原点
     */
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f) {

        if (m.length != 16) {
            throw new IllegalArgumentException("m length is  illegally,length is " + m.length);
        }

        if (n > f) {
            throw new IllegalArgumentException("n < f ?");
        }

        // 角度转弧度
        float yFovInRadians = (float) (yFovInDegrees * Math.PI / 180);
        float a = (float) (1.0 / Math.tan(yFovInRadians / 2.0));

        m[0] = a / aspect;
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;


        m[4] = 0;
        m[5] = a;
        m[6] = 0;
        m[7] = 0;


        m[8] = 0;
        m[9] = 0;
        m[10] = -((f + n) / (f - n));
        m[11] = -1;

        m[12] = 0;
        m[13] = 0;
        m[14] = -(2f * f * n / (f - n));
        m[15] = 0;


       /* android源码
        float f = 1.0f / (float) Math.tan(fovy * (Math.PI / 360.0));
        float rangeReciprocal = 1.0f / (zNear - zFar);

        m[offset + 0] = f / aspect;
        m[offset + 1] = 0.0f;
        m[offset + 2] = 0.0f;
        m[offset + 3] = 0.0f;

        m[offset + 4] = 0.0f;
        m[offset + 5] = f;
        m[offset + 6] = 0.0f;
        m[offset + 7] = 0.0f;

        m[offset + 8] = 0.0f;
        m[offset + 9] = 0.0f;
        m[offset + 10] = (zFar + zNear) * rangeReciprocal;
        m[offset + 11] = -1.0f;

        m[offset + 12] = 0.0f;
        m[offset + 13] = 0.0f;
        m[offset + 14] = 2.0f * zFar * zNear * rangeReciprocal;
        m[offset + 15] = 0.0f;

        */

    }
}
