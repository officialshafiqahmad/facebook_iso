package com.example.facebook_iso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class picture extends Activity {

    private static final int REQUEST_SELECT_PHOTO = 1;
    private Boolean is_camera;
    private final int CAMERA_REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        is_camera= false;

        ImageButton camerabtn = findViewById(R.id.camerabtn);
        ImageButton btnSelectPhoto = findViewById(R.id.btnSelectPhoto);

        camerabtn.setOnClickListener(v -> {
            is_camera=true;
            Intent camerai = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camerai,CAMERA_REQ_CODE);
        });
        btnSelectPhoto.setOnClickListener(v -> {
            is_camera=false;
            selectPhoto(v);
        });
    }


    public void selectPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SELECT_PHOTO);
        Intent i =new Intent(this, Feed_Page.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        ImageView profilePic = findViewById(R.id.imageView5);
        if (is_camera) {
            if (resultCode == RESULT_OK) {
                if (requestCode == CAMERA_REQ_CODE) {
                    //get picture as bit map
                    Bitmap pic = (Bitmap) (Objects.requireNonNull(data.getExtras()).get("data"));
                    //convert to Uri
                    assert pic != null;
                    Uri picUri = getImageUri(this, pic);
                    //set in user
                    current_user.getInstance().get_Current().setProfilePic(picUri);
                    profilePic.setImageURI(picUri);
                }
            }
        }else {
            if (requestCode == REQUEST_SELECT_PHOTO) {
                if (resultCode == RESULT_OK) {
                    Uri photo = data.getData();
                    if (photo != null) {
                        //set in user
                        current_user.getInstance().get_Current().setProfilePic(photo);
                        //set in frame
                        profilePic.setImageURI(photo);
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        Button finish_reg = findViewById(R.id.finish_reg);
        finish_reg.setOnClickListener(v -> {
            //add to list
            logininfo_list.getInstance().add(current_user.getInstance().get_Current());
            //go to feed
            Intent i =new Intent(this, Feed_Page.class);
            startActivity(i);
        });
    }
    public Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //compress the image to Uri
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
}
