package tw.org.iii.android201920;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.Serializable;
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
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
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
        firebaseListener();
        bikeListener();
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
            Bitmap bmp = BitmapFactory.decodeFile(sdroot.getAbsolutePath() + "/bradiii.jpg");
            img.setImageBitmap(bmp);
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

    private Uri uri;

    public void takePic2(View view) {
        //Uri uri = Uri.fromFile(new File(sdroot, "bradiii.jpg"));

        uri = FileProvider.getUriForFile(this, getPackageName()+".provider",
                new File(sdroot, "bradiii.jpg"));

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 333);
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    DatabaseReference myRef2 = database.getReference("light");

    public void test1(View view) {
        // Write a message to the database

        myRef.setValue("Hello, World!");
    }

    private void firebaseListener(){
        myRef2.setValue(false);     // boolean => Boolean

        // Read from the database
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                Log.v("brad", "Value is: " + value);

                Boolean lightOnOff = dataSnapshot.getValue(Boolean.class);
                Log.v("brad", "light : " + lightOnOff);

                if (lightOnOff){
                    lightOn(null);
                }else{
                    lightOff(null);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w("brad", "Failed to read value.", error.toException());
            }
        });
    }

    DatabaseReference myBike = database.getReference("bike");
    private void bikeListener(){
        Bike bike = new Bike();
        bike.setName("Brad");
        bike.upSpeed();
        bike.upSpeed();
        bike.upSpeed();
        bike.upSpeed();
        Log.v("brad", bike.name + ":" + bike.speed);
        myBike.setValue(bike);


        myBike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Bike b2 = dataSnapshot.getValue(Bike.class);
                Log.v("brad", b2.name + ":" + b2.speed);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




}
