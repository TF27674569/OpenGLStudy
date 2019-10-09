package com.tf.camera.renderer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.View;

import com.tf.camera.R;
import com.tf.camera.renderer.camera.CameraShaderProgram;

import java.io.IOException;

/**
 * create by TIAN FENG on 2019/9/26
 */
public class AppCompatCameraRenderer extends CameraRenderer {

    private Camera camera;
    private GLSurfaceView glSurfaceView;

    public AppCompatCameraRenderer(GLSurfaceView glSurfaceView) {
        super(glSurfaceView.getContext());
        this.glSurfaceView = glSurfaceView;

        glSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera == null) {
                    return;
                }
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        Log.e("TAG", "onAutoFocus: " + b);
                    }
                });
            }
        });
    }


    @Override
    protected SurfaceTexture createSurfaceTextureBindCamera(int textureId) {
        camera = Camera.open();
        SurfaceTexture surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                glSurfaceView.requestRender();
            }
        });
        try {
            camera.setPreviewTexture(surfaceTexture);
            camera.setAutoFocusMoveCallback(new Camera.AutoFocusMoveCallback() {
                @Override
                public void onAutoFocusMoving(boolean b, Camera camera) {
                    Log.e("TAG", "onAutoFocusMoving: " + b);
                }
            });
            // 选转90°
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "createSurfaceTextureBindCamera: " + e.getMessage());
        }
        return surfaceTexture;
    }

    @Override
    protected CameraShaderProgram getShaderProgram(Context context) {
        return new CameraShaderProgram(context);
    }




}
