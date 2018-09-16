package com.minus.occupancychecker;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
*/
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


// width = 360
// per letter = 8
//height = 508
// per letter = 16

public class MainActivity extends AppCompatActivity {

    String mCurrentPhotoPath = "Photo.jpg";

    static final int REQUEST_TAKE_PHOTO = 1;

    ImageView imageElement;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = new File(this.getFilesDir(), mCurrentPhotoPath);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        File f = new File(mCurrentPhotoPath);
        //imageElement = (ImageView) findViewById(R.id.imageView);
        /*
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        imageElement.setImageBitmap(imageBitmap);
        */

        System.out.println(f.getAbsolutePath());
        /*
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
                    .header("accept", "app/json")
                    .queryString("name", "Mark")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();

*/    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dispatchTakePictureIntent();
        setContentView(R.layout.activity_main);


    }

}
