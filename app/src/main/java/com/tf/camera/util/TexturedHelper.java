package com.tf.camera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * create by TIAN FENG on 2019/9/11
 */
public class TexturedHelper {
    private static final String TAG = "TexturedHelper";

    public static int loadTexture(Context context, int resId) {
        final int[] texturedObjIds = new int[1];
        // 创建一个纹理
        GLES20.glGenTextures(1, texturedObjIds, 0);

        if (texturedObjIds[0] == 0) {
            Log.e(TAG, "Create texture error!");
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);

        if (bitmap == null) {
            Log.e(TAG, "resId  to bitmap error !");
            GLES20.glDeleteTextures(1, texturedObjIds, 0);
            return 0;
        }

        // GL_TEXTURE_2D 告诉opengl这个纹理作为二维纹理对待
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturedObjIds[0]);

        // 纹理过滤
        // 缩小情况使用，三线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        // 放大情况使用，双线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // 将bitmap数据读进纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        // 从（0，0）点创建一组mipmap纹理图像数据
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        // 传0，解除纹理绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return texturedObjIds[0];
    }


    public static int createTexture() {
        final int[] texturedObjIds = new int[1];
        // 创建一个纹理
        GLES20.glGenTextures(1, texturedObjIds, 0);

        if (texturedObjIds[0] == 0) {
            Log.e(TAG, "Create texture error!");
        }

        // GL_TEXTURE_2D 告诉opengl这个纹理作为二维纹理对待
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturedObjIds[0]);

        // 纹理过滤
        // 缩小情况使用，三线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        // 放大情况使用，双线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // 从（0，0）点创建一组mipmap纹理图像数据
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        // 传0，解除纹理绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return texturedObjIds[0];
    }

    public static int createFBOTexture(int w, int h) {
        final int[] fboTextureIds = new int[1];
        // 创建一个纹理
        GLES20.glGenTextures(1, fboTextureIds, 0);

        if (fboTextureIds[0] == 0) {
            Log.e(TAG, "Create fbo texture error!");
        }

        // GL_TEXTURE_2D 告诉OpenGL这个纹理作为二维纹理对待
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureIds[0]);

        //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        // 纹理过滤
        // 缩小情况使用，三线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        // 放大情况使用，双线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // 把纹理绑定到FBO
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTextureIds[0], 0);

        // 设置FBO分配内存大小
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, w, h,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
                != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "bind fbo texture error");
        }

        //解绑纹理和FBO
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        return fboTextureIds[0];
    }

    /**
     * 创建外部纹理
     * @return
     */
    public static int createOESTexture() {
        int[] tex = new int[1];
        //生成一个纹理
        GLES20.glGenTextures(1, tex, 0);
        //将此纹理绑定到外部纹理上
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        //解除纹理绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }
}
