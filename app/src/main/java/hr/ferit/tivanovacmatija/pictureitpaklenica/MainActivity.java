package hr.ferit.tivanovacmatija.pictureitpaklenica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    /*ArrayList<Species> ssp;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if (firstStart){
            showStartDialog();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_menu){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseUtil.attachListener();
                        }
                    });
            FirebaseUtil.detachListener();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showStartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Dobrodo??li")
                .setMessage("Upute:\n" +
                        "- Odabirom vrste u listi prikazuju se njezini detalji. Klikom na ikonu fotoaparata omogu??ava se slikanje\n" +
                        "- Dugim pritiskom na vrstu u listi omogu??ava se slikanje\n" +
                        "\nU ovoj aplikaciji prikazane su  ugro??ene biljne i ??ivotinjske vrste iz programa Natura 2000. Svrha aplikacije je omogu??iti pra??enje " +
                        "rasprostranjenosti ovih vrsta na podru??ju nacionalnog parka.\n" +
                        "\nNa podru??ju Nacionalnog parka Paklenica " +
                        "sve su biljne vrste zakonom za??ti??ene te " +
                        "se ne smiju brati ni o??te??ivati! U slu??aju " +
                        "branja ili o??te??ivanja biljnih vrsta u " +
                        "Nacionalnom parku, Zakonom o za??titi " +
                        "prirode predvi??ena je nov??ana kazna.\n" +
                        "\nSve su ??ivotinje u Nacionalnom parku " +
                        "Paklenica zakonom za??ti??ene te ih se ne " +
                        "smije namjerno uznemiravati.")
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFbReference("species", this);
        RecyclerView rvSpecies = (RecyclerView) findViewById(R.id.rvSpecies);
        final SpeciesAdapter adapter = new SpeciesAdapter();
        rvSpecies.setAdapter(adapter);
        LinearLayoutManager speciesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvSpecies.setLayoutManager(speciesLayoutManager);
        FirebaseUtil.attachListener();
    }


}