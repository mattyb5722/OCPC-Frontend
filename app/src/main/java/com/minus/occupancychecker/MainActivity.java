package com.minus.occupancychecker;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        try {
            ExifInterface exif = new ExifInterface(mCurrentPhotoPath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        imageElement.setImageBitmap(bitmap);

        //Send file
        File f = new File(mCurrentPhotoPath);
        myLocation.getLocation(this, locationResult);
        String url = "http://3b5588c1.ngrok.io/image";


        final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

        RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", f.getName(), RequestBody.create(MEDIA_TYPE_JPG, f)).build();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder.addQueryParameter("lat", String.valueOf(lat));
        httpBuilder.addQueryParameter("lon", String.valueOf(lon));
        httpBuilder.addQueryParameter("bearing", String.valueOf(bearing));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(req)
                .build();
        System.out.println(request.toString());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).build();
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
                    String responseBody = response.body().string();
                    System.out.println(responseBody);
                    ObjectMapper mapper = new ObjectMapper();
                    if(!(responseBody.equals("{}"))) {
                        final List<Box> boxesList = mapper.readValue(responseBody, new TypeReference<List<Box>>(){});
                        final TextView[] boxes = {text1, text2, text3};
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < Math.min(3, boxesList.size()); i++) {
                                    Box b = boxesList.get(i);
                                    make_box(boxes[i], String.format("Occupancy: %d",
                                            b.getBuilding().getOccupancy()), b.getTopLeft().getX(),
                                            b.getBottomRight().getX(), b.getTopLeft().getY(), b.getBottomRight().getY());
                                }
                            }
                        });
                    }
                }
            }
        });

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

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
