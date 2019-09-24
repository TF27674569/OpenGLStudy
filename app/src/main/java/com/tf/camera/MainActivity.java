package com.tf.camera;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.tf.camera.renderer.CameraRenderer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet;
    private   Camera camera;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        glSurfaceView = new GLSurfaceView(this);
        setContentView(glSurfaceView);

        glSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera==null){
                    return;
                }
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        Log.e("TAG", "onAutoFocus: "+b );
                    }
                });
            }
        });

        // 判断支不支持 GLES2.0
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo deviceConfigurationInfo = activityManager.getDeviceConfigurationInfo();

        boolean supportsEs2 = deviceConfigurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new CameraRenderer(this) {
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
                                Log.e("TAG", "onAutoFocusMoving: "+ b);
                            }
                        });
                        camera.setDisplayOrientation(90);
                        camera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("TAG", "createSurfaceTextureBindCamera: " + e.getMessage());
                    }

                    return surfaceTexture;
                }

            });
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            rendererSet = true;
        } else {
            Toast.makeText(this, "不支持OpenGLES 2.0", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

}
