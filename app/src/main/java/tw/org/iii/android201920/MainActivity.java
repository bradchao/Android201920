package tw.org.iii.android201920;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private TextView result;
    private CameraManager cameraManager;
    private ImageView img;
    private File sdroot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                        123);

        }else{
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){
        sdroot = Environment.getExternalStorageDirectory();
        cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        result = findViewById(R.id.result);
        img = findViewById(R.id.img);
    }

    public void scanCode(View view) {
        Intent intent = new Intent(this,ScanActivity.class);
        startActivityForResult(intent, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            String dataResult = data.getStringExtra("result");
            result.setText(dataResult);
        }else if (requestCode == 222 && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
//            Set<String> set = bundle.keySet();
//            for (String key: set){
//                Log.v("brad", key + " => " + bundle.get(key).toString());
//            }
            Bitmap bmp = (Bitmap)(bundle.get("data"));
            img.setImageBitmap(bmp);

        }else if (requestCode == 333 && resultCode == RESULT_OK){
        }

    }

    public void lightOn(View view) {
        try {
            cameraManager.setTorchMode("0", true);
        }catch (Exception e){

        }

    }
    public void lightOff(View view) {
        try {
            cameraManager.setTorchMode("0", false);
        }catch (Exception e){

        }
    }

    public void takePic1(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 222);
    }

    public void takePic2(View view) {
        Uri uri = Uri.fromFile(new File(sdroot, "bradiii.jpg"));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 333);
    }
}
