package hr.ferit.tivanovacmatija.pictureitpaklenica;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SpDetails extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferance;
    ImageView ivSpecies;
    TextView tvName;
    TextView tvLatin;
    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sp_details);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReferance = FirebaseUtil.mDatabaseReference;
        ivSpecies = (ImageView) findViewById(R.id.ivSpecies);
        tvName = (TextView) findViewById(R.id.tvName);
        tvLatin = (TextView) findViewById(R.id.tvLatin);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        Intent receivedIntent = getIntent();
        Species sp = (Species) receivedIntent.getSerializableExtra("Species");
        tvName.setText(sp.getName());
        tvLatin.setText(sp.getLatin());
        tvDescription.setText(sp.getDescription());
        showImage(sp.getUrl());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.spdetail_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.camera_menu){
            Intent intent = getIntent();
            intent.setComponent(new ComponentName (this, Camera.class));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void showImage(String url){
        if (url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(ivSpecies);
        }
    }
}