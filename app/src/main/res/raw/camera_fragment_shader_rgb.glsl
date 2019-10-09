#extension GL_OES_EGL_image_external : require
uniform samplerExternalOES uTextureSampler;
precision mediump float;
varying vec2 vTextureCoord;
uniform float r;
uniform float g;
uniform float b;

void main()
{
    vec4 vCameraColor = texture2D(uTextureSampler, vTextureCoord);
//    gl_FragColor = vec4(vCameraColor.r, vCameraColor.g, vCameraColor.b, 1.0);

    // 黑白
    //    float color = (vCameraColor.r + vCameraColor.g + vCameraColor.b) / 3.0;
    //    vec4 tempColor = vec4(color,color,color,1);
    //    gl_FragColor = vec4(vec3(tempColor), 1.0);


    // 冷色
    //    gl_FragColor = vCameraColor + vec4(0.0,0.0,0.3,0.0);


    // 暖色
    gl_FragColor = vCameraColor + vec4(r,g,b,0.0);

}