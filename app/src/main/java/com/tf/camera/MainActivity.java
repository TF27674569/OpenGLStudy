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
import android.widget.Button;
import android.widget.Toast;

import com.tf.camera.renderer.AppCompatCameraRenderer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GLSurfaceView glSurfaceView;
    private Button def,cool,hot;
    private boolean rendererSet;
    private AppCompatCameraRenderer renderer;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        initView();
        initGL();


    }

    private void initView() {
        glSurfaceView = findViewById(R.id.glSurfaceView);
        def = findViewById(R.id.default_);
        cool = findViewById(R.id.cool);
        hot = findViewById(R.id.hot);

        def.setOnClickListener(this);
        cool.setOnClickListener(this);
        hot.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.default_:
                break;
            case R.id.cool:
                break;
            case R.id.hot:
                break;
        }
    }


    private void initGL() {
        // 判断支不支持 GLES2.0
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo deviceConfigurationInfo = activityManager.getDeviceConfigurationInfo();

        boolean supportsEs2 = deviceConfigurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(renderer = new AppCompatCameraRenderer(glSurfaceView));
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
