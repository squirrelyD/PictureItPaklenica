package hr.ferit.tivanovacmatija.pictureitpaklenica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.LocalServerSocket;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.internal.Util;

public class Camera extends AppCompatActivity {

    private static final String TAG = Camera.class.getSimpleName();

    ImageView ivPicture;
    Button btnSend;
    private Uri mPhotoURI = null;
    private String mPhotoPath = null;
    private String spName;
    private String userName;
    private String userEmail;
    private Double  lat;
    private Double longitude;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        final Intent newIntent = getIntent();
        Species sp = (Species) newIntent.getSerializableExtra("Species");
        spName = sp.getName();
        ivPicture = (ImageView) findViewById(R.id.ivPicture);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setEnabled(false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        /*FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            String username = user.getDisplayName();
            String email = user.getEmail();
        }*/
        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null){

                    File photoFile = null;
                    try {
                        photoFile = Utils.createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (photoFile != null){
                        mPhotoPath = photoFile.getAbsolutePath();
                        mPhotoURI = FileProvider.getUriForFile(Camera.this, "hr.ferit.tivanovacmatija.pictureitpaklenica.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);
                        startActivityForResult(takePictureIntent, 21);

                        /*Log.i(TAG, "photo uri" + mPhotoURI);
                        Log.i(TAG, "photo path" + mPhotoPath);*/

                    }
                }

                //Check permission for location
                if (ActivityCompat.checkSelfPermission(Camera.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    //When permission granted
                    getLocation();
                }else {
                    //When permission denied
                    ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    userName = user.getDisplayName();
                    userEmail = user.getEmail();
                }
                if (btnSend.isEnabled()) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"paklenicapark@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "PictureItPaklenica image");
                    i.putExtra(Intent.EXTRA_TEXT, userName + "\n" + userEmail + "\n\n" + spName + "\n\n" + lat + ", " + longitude);
                    i.putExtra(Intent.EXTRA_STREAM, mPhotoURI);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(Camera.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }

                /*Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tivanovacmatija@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "PictureItPaklenica image");
                intent.putExtra(Intent.EXTRA_TEXT, userName + "\n" + userEmail + "\n" + spName);
                intent.putExtra(Intent.EXTRA_STREAM, mPhotoURI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(intent, "Send mail..."));
                }*/
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null){
                    try {
                        Geocoder geocoder = new Geocoder(Camera.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        lat = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 21 && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath);
            getLocation();
            ivPicture.setImageBitmap(bitmap);
            btnSend.setEnabled(true);
            Toast.makeText(Camera.this, "Image saved", Toast.LENGTH_SHORT).show();
            Utils.notifyGalleryAboutPic(Camera.this, mPhotoPath);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


}