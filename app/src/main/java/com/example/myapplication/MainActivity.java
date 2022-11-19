package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String[] cameraPermission;
    String[] storagePermission;

    Uri imageuri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }


    public void loadImagefromGallery(View view) {
        showImageImportDialog();

    }

    public void justTranslate(View view) {
        Intent intent = new Intent(getApplicationContext(),JustTranslate.class);
        startActivity(intent);

    }

    public void about(View view) {
        Intent intent = new Intent(getApplicationContext(),about.class);
        startActivity(intent);

    }
    private void showImageImportDialog() {
        //Items to display in dialog menu

        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    //Camera option clicked
                    if(!checkcamerapermission()){
                        //caamera permission not allowed, request it
                        requestcamerapermission();
                    }
                    else{
                        pickcamera();
                    }
                }
                if(which==1){
                    //Camera option clicked
                    if(!checkstoragepermission()){
                        //caamera permission not allowed, request it
                        requeststoragepermission();
                    }
                    else{
                        pickgallery();
                    }
                }
            }
        });
        builder.create().show();
    }
    private void pickgallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickcamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Images to Text");

        imageuri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        startActivityForResult(cameraintent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requeststoragepermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);

    }

    private boolean checkstoragepermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestcamerapermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkcamerapermission() {

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    //handle permission result


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean camerAccepted = grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    if(camerAccepted && writeStorageAccepted){
                        pickcamera();
                    }
                    else{
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickgallery();
                    }
                    else{
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;


        }
    }

    //handle Image result


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                if(data!=null){
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                }

            }

            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                CropImage.activity(imageuri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri resulturi = result.getUri();

                Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                intent.putExtra("imageUri", resulturi.toString());
                startActivity(intent);


//                imgprev.setImageURI(resulturi);
//
//                BitmapDrawable drawable = (BitmapDrawable) imgprev.getDrawable();
//                Bitmap bitmap = drawable.getBitmap();
//                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

//                if(!textRecognizer.isOperational()){
//                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
//                    StringBuilder sp = new StringBuilder();
//
//                    for (int i = 0; i<items.size(); i++){
//                        TextBlock myItem = items.valueAt(i);
//                        sp.append(myItem.getValue());
//                        sp.append("\n");
//                    }
//
//                    rsltedt.setText(sp.toString());
//                }




            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();

            }
        }

    }
}
