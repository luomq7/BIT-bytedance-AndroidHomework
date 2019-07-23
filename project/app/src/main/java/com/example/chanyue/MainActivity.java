package com.example.chanyue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentManager fragmentManager;
    private RelativeLayout rl_content;
    private ImageView imgView_home,imgView_personal;
    private LinearLayout linearLayout_home,linearLayout_personal;
    private ImageView[] tvs;
    private Intent takeVideoIntent;


    private String savePath;
    private Uri videoUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //显示view
        initView();
        fragmentManager = getSupportFragmentManager();
        initListener();

        FragmentTransaction transaction = fragmentManager.beginTransaction();//创建一个事务
        transaction.replace(R.id.rl_content,new HomeFragment());
        transaction.commit();
        setCheck(0);

        //视频上传功能，button id是btn_upload
        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RecordVideoActivity.class));

            }
        });

        //视频拍摄录制功能，button id是btn_camera
        findViewById(R.id.button_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this,RecordVideoActivity.class));
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //在这里申请相机、存储的权限
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},RecordVideoActivity.REQUEST_VIDEO_CAPTURE);
                } else {
                    //打开相机拍摄
                    takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);//20190712
//                    takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE, Uri.parse("jsh"),MainActivity.this,RecordVideoActivity.class);
                    if(takeVideoIntent.resolveActivity(getPackageManager()) != null){
                        File file = null;
                        try{
                            file = getOutputMediaFile(RecordVideoActivity.MEDIA_TYPE_VIDEO);
                        }catch (IOException e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"获取文件名错误！",Toast.LENGTH_LONG).show();
                        }
                        if(file != null) {
                            Uri tmp_vedioUri = FileProvider.getUriForFile(
                                    MainActivity.this,
                                    "com.example.chanyue.fileProvider",
                                    file);
                            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,tmp_vedioUri);
                            startActivityForResult(takeVideoIntent, RecordVideoActivity.REQUEST_VIDEO_CAPTURE);
                        }
                    }
                }
            }
        });

    }

    private void initListener() {
        linearLayout_home.setOnClickListener(this);
        linearLayout_personal.setOnClickListener(this);

    }

    private void initView() {
        rl_content = (RelativeLayout) findViewById(R.id.rl_content);
        imgView_home = (ImageView) findViewById(R.id.item1_tv);
        linearLayout_home = (LinearLayout) findViewById(R.id.item1);
        imgView_personal = (ImageView) findViewById(R.id.item2_tv);
        linearLayout_personal = (LinearLayout) findViewById(R.id.item2);

        tvs = new ImageView[]{imgView_home,imgView_personal};
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.item1: {
                FragmentTransaction transaction = fragmentManager.beginTransaction();//创建一个事务
                transaction.replace(R.id.rl_content,new HomeFragment());
                transaction.commit();//事务一定要提交，replace才会有效
                setCheck(0);//自定义方法
                break;
            }
            case R.id.item2: {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.rl_content,new PersonalFragment());
                transaction.commit();
                setCheck(1);
                break;
            }
            default:break;
        }
    }


    public void setCheck(int itemId){
        //这个方法设置底部导航栏选中时的效果
        if (itemId == 0){
            tvs[0].setImageResource(R.drawable.home1);
            tvs[1].setImageResource(R.drawable.me);
        }else{
            tvs[0].setImageResource(R.drawable.home);
            tvs[1].setImageResource(R.drawable.me1);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public File getOutputMediaFile(int type) throws IOException {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == RecordVideoActivity.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == RecordVideoActivity.MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }


        savePath = mediaFile.getAbsolutePath();
        galleryAddVideo();//将视频添加到相册中

        return mediaFile;
    }

    /*
     * 将视频添加到相册中
     */
    private void galleryAddVideo(){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(savePath);
        Uri contentUri = Uri.fromFile(f);
        intent.setData(contentUri);
        this.sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RecordVideoActivity.REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = intent.getData();
            Toast.makeText(this, "视频已保存至" + videoUri, Toast.LENGTH_LONG).show();

            Intent intent_tmp = new Intent(MainActivity.this,RecordVideoActivity.class);
            intent_tmp.putExtra("videoUriUploadNow",videoUri.toString());
            intent_tmp.putExtra("savePath",savePath);
            startActivity(intent_tmp);

        }
    }
}
