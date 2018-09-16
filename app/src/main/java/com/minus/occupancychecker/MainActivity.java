package com.minus.occupancychecker;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.PendingIntent.FLAG_ONE_SHOT;
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
    double lon;
    double lat;
    double bearing;
    MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
        @Override
        public void gotLocation(Location location) {
            lon = location.getLongitude();
            lat = location.getLatitude();
            bearing = location.getBearing();
        }
    };
    MyLocation myLocation = new MyLocation();

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
        myLocation.getLocation(this, locationResult);
        //Todo: Make Post Request
        String url = "http://c50dfeb7.ngrok.io/image";


        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

        RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f)).build();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder.addQueryParameter("lat", String.valueOf(lat));
        httpBuilder.addQueryParameter("lon", String.valueOf(lon));
        httpBuilder.addQueryParameter("bearing", String.valueOf(bearing));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(req)
                .build();
        System.out.println(request.toString());

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    System.out.println(response);
                }
            }
        });


        make_box(text1, "hello", 1000, 1500, 1000, 1500);

        reset.setVisibility(View.VISIBLE);
        reset.setOnClickListener(new View.OnClickListener()

        {
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


    }
}
