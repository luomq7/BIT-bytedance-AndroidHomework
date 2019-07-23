package com.example.chanyue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;


import com.example.chanyue.bean.FeedResponse;
import com.example.chanyue.bean.PostVideoResponse;
import com.example.chanyue.utils.NetworkUtils;
import com.example.chanyue.utils.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;


public class RecordVideoActivity extends AppCompatActivity {

    private VideoView videoView;
    //    private Intent takeVideoIntent;
    private String savePath;
    private Uri mSelectedVideo;
    public Uri mSelectedImage;
    public Uri mUploaddVideoNow;
    //    private Uri videoUri;
    private Button mBtn;


    public static final int REQUEST_VIDEO_CAPTURE = 1;
    public static final int PICK_VIDEO = 2;
    public static final int PICK_IMAGE_UPALOAD_LOCAL = 3;
    public static final int PICK_IMAGE_UPALOAD_NOW = 4;
    public static final int REQUEST_VIDEO_UPLOAD_NOW = 5;
    public static final int REQUEST_VIDEO_UPLOAD_LOCAL = 6;


    public static final int REQUEST_EXTERNAL_CAMERA = 101;
    public static final int MEDIA_TYPE_IMAGE = 1;

    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int GRANT_PERMISSION = 3;


    List<Call<PostVideoResponse>> poscallList = new ArrayList<>();
    List<retrofit2.Call<FeedResponse>> feedcallList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        videoView = findViewById(R.id.view);//step1

        /*
         *上传本地视频
         */
        mBtn = findViewById(R.id.btn_upload);
        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploaddVideoNow == null) {//本地选择视频上传
                    String s = mBtn.getText().toString();
                    if (getString(R.string.select_an_image).equals(s)) {
                        if (requestReadExternalStoragePermission("select an image")) {
                            chooseImage(PICK_IMAGE_UPALOAD_LOCAL);
                        }
                    } else if (getString(R.string.select_a_video).equals(s)) {
                        if (requestReadExternalStoragePermission("select a video")) {
                            chooseVideo();
                        }
                    } else if (getString(R.string.post_it).equals(s)) {
                        if (mSelectedVideo != null && mSelectedImage != null) {
                            postVideo();
                            System.out.println(mSelectedVideo.toString());
                        } else {
                            throw new IllegalArgumentException("error data uri, mSelectedVideo = " + mSelectedVideo + ", mSelectedImage = " + mSelectedImage);
                        }
                    } else if ((getString(R.string.success_try_refresh).equals(s))) {
                        mBtn.setText(R.string.select_an_image);
                    }
                }
//                else{//将拍摄视频上传
//
//                }
            }
        });

        /*
         * 上传当前拍摄视频
         */
        try {
            String videoUriUploadNow = getIntent().getStringExtra("videoUriUploadNow");
            String savepath = getIntent().getStringExtra("savePath");
            mUploaddVideoNow = Uri.parse(videoUriUploadNow);
            videoView.setVideoURI(mUploaddVideoNow);
            videoView.start();
//            mSelectedVideo = Uri.parse(getRealPathFromURI(mUploaddVideoNow));
            mSelectedVideo = Uri.parse(savepath);


            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(savepath);
            Bitmap bitmap_tmp = mediaMetadataRetriever.getFrameAtTime();
            ImageView imageView = findViewById(R.id.img_view);
            imageView.setImageBitmap(bitmap_tmp);

            //content://com.example.chanyue.fileProvider/sdcard/Pictures/CameraDemo/VID_20190722_102443.mp4
            String bitmap_tmp_uri = saveBitmap(this, bitmap_tmp);
            mSelectedImage = Uri.parse(bitmap_tmp_uri);


            System.out.println("实时视频上传：mUploaddVideoNow = " + mUploaddVideoNow.toString());
            System.out.println("实时视频上传：mSelectedVideo = " + mSelectedVideo.toString());

            if (mSelectedVideo != null && mSelectedImage != null) {
                postVideoNow();
            } else {
                throw new IllegalArgumentException("error data uri, mSelectedVideo = " + mSelectedVideo + ", mSelectedImage = " + mSelectedImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 播放录制的视频
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //上传本地视频
        if (resultCode == RESULT_OK && null != intent && requestCode == PICK_VIDEO) {
            mSelectedVideo = intent.getData();
            Log.d("获取视频数据", "mSelectedVideo = " + mSelectedVideo);
            Uri mselectedVideoUri;
            mselectedVideoUri = intent.getData();
            videoView.setVideoURI(mselectedVideoUri);
            videoView.start();
            mBtn.setText(R.string.post_it);
        }

        //上传本地视频时选择封面图
        if (resultCode == RESULT_OK && null != intent && requestCode == PICK_IMAGE_UPALOAD_LOCAL) {
            mSelectedImage = intent.getData();
            Log.d("获取封面图数据", "mSelectedVideo = " + mSelectedImage);

            Uri mselectedVideoUri;
            mselectedVideoUri = intent.getData();
            ImageView imageView = findViewById(R.id.img_view);

            Bitmap bmp;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mselectedVideoUri);
                imageView.setImageBitmap(bmp);
            } catch (IOException e) {
            }

            mBtn.setText(R.string.select_a_video);
        }

        //上传当前拍摄视频选择封面图
        if (resultCode == RESULT_OK && null != intent && requestCode == PICK_IMAGE_UPALOAD_NOW) {
            mSelectedImage = intent.getData();
            Log.d("获取封面图数据", "mSelectedVideo = " + mSelectedImage);

            Uri mselectedVideoUri;
            mselectedVideoUri = intent.getData();
            ImageView imageView = findViewById(R.id.img_view);

            Bitmap bmp;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mselectedVideoUri);
                imageView.setImageBitmap(bmp);
            } catch (IOException e) {
            }

            mBtn.setText(R.string.post_it);
        }

        //上传当前视频时获取视频内容
        if (resultCode == RESULT_OK && null != intent && requestCode == REQUEST_VIDEO_UPLOAD_NOW) {
            mUploaddVideoNow = intent.getData();
            Log.d("获取视频数据", "mSelectedVideo = " + mUploaddVideoNow);
        }

    }


    /*
     * 判断权限是否已经授予
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_CAMERA: {
                Toast.makeText(this, "已经授权" + Arrays.toString(permissions), Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    private boolean requestReadExternalStoragePermission(String explanation) {
        if (ActivityCompat.checkSelfPermission(RecordVideoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RecordVideoActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "You should grant external storage permission to continue " + explanation, Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RecordVideoActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, GRANT_PERMISSION);
            }
            return false;
        } else {
            return true;
        }
    }


    /*
     *获取文件名，并创建文件
     */
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
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
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
    private void galleryAddVideo() {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(savePath);
        Uri contentUri = Uri.fromFile(f);
        intent.setData(contentUri);
        this.sendBroadcast(intent);
    }

    //用于上传视频
    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(RecordVideoActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private MultipartBody.Part getMultipartFromUriNow(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(uri.toString());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    //上传视频时选择本地视频
    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }

    //上传视频时选择封面图谱
    public void chooseImage(int uplaode_type) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), uplaode_type);
    }

    //上传视频
    private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);

        retrofit2.Call<PostVideoResponse> postVideoResponseCall = NetworkUtils.getResponseWithRetrofitAsyncOfPostVideoResponse("1234509", "lmq", getMultipartFromUri("cover_image", mSelectedImage), getMultipartFromUri("video", mSelectedVideo));
        poscallList.add(postVideoResponseCall);
        postVideoResponseCall.enqueue(new retrofit2.Callback<PostVideoResponse>() {
            @Override
            public void onResponse(retrofit2.Call<PostVideoResponse> call, retrofit2.Response<PostVideoResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "success:Send Request to post a video with its cover image.", Toast.LENGTH_SHORT).show();
                    mBtn.setText(R.string.select_an_image);
                    mBtn.setEnabled(true);
                } else {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<PostVideoResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void postVideoNow() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);

        retrofit2.Call<PostVideoResponse> postVideoResponseCall = NetworkUtils.getResponseWithRetrofitAsyncOfPostVideoResponse("1234509", "lmq", getMultipartFromUriNow("cover_image", mSelectedImage), getMultipartFromUriNow("video", mSelectedVideo));
        poscallList.add(postVideoResponseCall);
        postVideoResponseCall.enqueue(new retrofit2.Callback<PostVideoResponse>() {
            @Override
            public void onResponse(retrofit2.Call<PostVideoResponse> call, retrofit2.Response<PostVideoResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "success:Send Request to post a video with its cover image.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RecordVideoActivity.this,MainActivity.class));
                    mBtn.setText(R.string.select_an_image);
                } else {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<PostVideoResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    //上传拍摄视频将uri转为path
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    //content://com.example.chanyue.fileProvider/sdcard/Pictures/CameraDemo/VID_20190722_101109.mp4
    private static final String SD_PATH = "/storage/emulated/0/Pictures/CameraDemo/";
    private static final String IN_PATH = "/Pictures/CameraDemo/";

    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }
}
