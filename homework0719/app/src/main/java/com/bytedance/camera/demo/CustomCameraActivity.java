package com.bytedance.camera.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_IMAGE;
import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_VIDEO;
import static com.bytedance.camera.demo.utils.Utils.getOutputMediaFile;

public class CustomCameraActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 101;

    private boolean isRecording = false;
    private int rotationDegree = 0;

    private Camera.Parameters mCameraParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_camera);

        mSurfaceView = findViewById(R.id.img);
        //todo 给SurfaceHolder添加Callback
        mCamera = getCamera(CAMERA_TYPE);
        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                }catch (IOException e){
                    e.printStackTrace();
                    if(mCamera != null) {
                        mCamera.stopPreview();
                        mCamera.release();
                        mCamera = null;
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
            }
        });


        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            //todo 拍一张照片
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},REQUEST_IMAGE_CAPTURE);

            } else {
                mCamera.takePicture(null,null,mPicture);
            }
        });

        findViewById(R.id.btn_record).setOnClickListener(v -> {
            //todo 录制，第一次点击是start，第二次点击是stop
            if (isRecording) {
                //todo 停止录制
                releaseMediaRecorder();
                isRecording = false;
                Toast.makeText(getApplicationContext(), "停止录制",Toast.LENGTH_SHORT).show();
//                startPreview(surfaceHolder);
                try {
                    mCamera.setPreviewDisplay(surfaceHolder);
                    mCamera.startPreview();
                }catch (IOException e){
                    e.printStackTrace();
                }
            } else {
                //todo 录制
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA},REQUEST_IMAGE_CAPTURE);
                } else {
                    prepareVideoRecorder();
                    isRecording = true;
                    Toast.makeText(getApplicationContext(), "开始录制",Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_facing).setOnClickListener(v -> {
            //todo 切换前后摄像头

            releaseCameraAndPreview();
            if (CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                rotationDegree = getCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK);
                CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;
            } else if (CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK){  // back-facing
                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                rotationDegree = getCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_FRONT);
                CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_FRONT;
            }
            mCamera.setDisplayOrientation(rotationDegree);

            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            }catch (IOException e){
                e.printStackTrace();
                if(mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
            }

        });


        findViewById(R.id.btn_zoom).setOnClickListener(v -> {
            //todo 调焦，需要判断手机是否支持
            if(mCamera != null){
                DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
                int screenWidth = displayMetrics.widthPixels;
                int sreenHeight = displayMetrics.heightPixels;
                mCameraParameters = mCamera.getParameters();
                setMeteringRect(screenWidth/2,sreenHeight/2,screenWidth,sreenHeight);
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.cancelAutoFocus();
                try{
                    mCamera.setParameters(mCameraParameters);
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if(success){
                                Toast.makeText(getApplicationContext(), "对焦成功",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                camera.autoFocus(this);
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "对焦失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setMeteringRect(int x, int y,int screenWidth,int sreenHeight) {
        if (mCameraParameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            Rect rect = new Rect(x - 100, y - 100, x + 100, y + 100);
            int left = rect.left * 2000 / screenWidth - 1000;
            int top = rect.top * 2000 / sreenHeight - 1000;
            int right = rect.right * 2000 / screenWidth - 1000;
            int bottom = rect.bottom * 2000 / sreenHeight - 1000;
            // 如果超出了(-1000,1000)到(1000, 1000)的范围，则会导致相机崩溃
            left = left < -1000 ? -1000 : left;
            top = top < -1000 ? -1000 : top;
            right = right > 1000 ? 1000 : right;
            bottom = bottom > 1000 ? 1000 : bottom;
            Rect area1 = new Rect(left, top, right, bottom);
            //只有一个感光区，直接设置权重为1000了
            areas.add(new Camera.Area(area1, 1000));
            mCameraParameters.setMeteringAreas(areas);
        }
    }

    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        Camera cam = Camera.open(position);

        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等
//        Camera.Parameters parameters = cam.getParameters();
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        cam.setParameters(mCameraParameters);

        rotationDegree = getCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_FRONT);
        cam.setDisplayOrientation(rotationDegree);


        return cam;
    }


    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        //todo 释放camera资源
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) {
        //todo 开始预览

    }

    private MediaRecorder mMediaRecorder;

    private boolean prepareVideoRecorder() {
        //todo 准备MediaRecorder
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        }catch (IOException e){
            releaseMediaRecorder();
            return false;
        }

        return true;
    }


    private void releaseMediaRecorder() {
        //todo 释放MediaRecorder
        if(mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }


    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Toast.makeText(getApplicationContext(), "文件已存至" + pictureFile,Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }

        mCamera.startPreview();
    };


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


}
