package com.minus.occupancychecker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
*/
import java.io.File;
import java.io.IOException;

import static android.location.Criteria.ACCURACY_FINE;
import static android.location.LocationManager.GPS_PROVIDER;


// width = 360
// per letter = 8
//height = 508
// per letter = 16

public class MainActivity extends AppCompatActivity {
    ImageView imageElement;
    TextView text1;
    TextView text2;
    TextView text3;
    Button reset;
    LocationManager locMan;
    Location loc;

    private void make_box(TextView text_obj, CharSequence message, int x1, int x2, int y1, int y2) {
        text_obj.setVisibility(View.VISIBLE);
        text_obj.setText(message);
        text_obj.setX(x1 / 4);
        text_obj.setY(y1 / 4);
        text_obj.setWidth((x2 - x1) / 4);
        text_obj.setHeight((y2 - y1) / 4);
    }

    private void reset() {
        text1.setVisibility(View.INVISIBLE);
        text2.setVisibility(View.INVISIBLE);
        text3.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = new File(this.getFilesDir(), "Photo.jpg");
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        imageElement.setImageBitmap(imageBitmap);

        File f = new File("Photo.jpg");


        /*
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
                    .header("accept", "app/json")
                    .queryString("name", "Mark")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        */

        make_box(text1, "hello", 1000, 1500, 1000, 1500);


        reset.setVisibility(View.VISIBLE);
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reset();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchTakePictureIntent();
        setContentView(R.layout.activity_main);
        imageElement = (ImageView) findViewById(R.id.imageView);
        text1 = (TextView) findViewById(R.id.textView1);
        text2 = (TextView) findViewById(R.id.textView2);
        text3 = (TextView) findViewById(R.id.textView3);
        reset = (Button) findViewById(R.id.Reset);


        locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }

        locMan.requestSingleUpdate(Criteria(), locMan.getProvider(GPS_PROVIDER));
        Location loc = locMan.getLastKnownLocation(String.valueOf(locMan.getProvider(GPS_PROVIDER)));
        System.out.println(loc.getLatitude());
        System.out.println(loc.getLongitude());

    }

}
