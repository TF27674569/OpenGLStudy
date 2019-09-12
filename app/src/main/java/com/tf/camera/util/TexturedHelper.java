package com.tf.camera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

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

}
