package hr.ferit.tivanovacmatija.pictureitpaklenica;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SpeciesAdapter extends RecyclerView.Adapter<SpeciesAdapter.SpeciesViewHolder>{
    ArrayList<Species> ssp;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;
//    private ImageView ivSpecies;                               Declared in viewHolder class, because of correct image loading

    public SpeciesAdapter() {
//        FirebaseUtil.openFbReference("species");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        this.ssp = FirebaseUtil.mSpecies;
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Species sp = snapshot.getValue(Species.class);
                ssp.add(sp);
                notifyItemInserted(ssp.size()-1);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildListener);
    }
    @NonNull
    @Override
    public SpeciesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_row, parent, false);
        return new SpeciesViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull SpeciesViewHolder holder, int position) {
        Species species = ssp.get(position);
        holder.bind(species);
    }
    @Override
    public int getItemCount() {
        return ssp.size();
    }

    public class SpeciesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView tvName;
        TextView tvLatin;
        ImageView ivSpecies;

        public SpeciesViewHolder (View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvLatin = (TextView) itemView.findViewById(R.id.tvLatin);
            ivSpecies = (ImageView) itemView.findViewById(R.id.ivSpecies);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        public void bind(Species species) {
            tvName.setText(species.getName());
            tvLatin.setText(species.getLatin());
//            ivSpecies.setImageDrawable(null);
            showImage(species.getUrl());
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Species selectedSp = ssp.get(position);
            Intent intent = new Intent(view.getContext(), SpDetails.class);
            intent.putExtra("Species", selectedSp);
            view.getContext().startActivity(intent);
        }
        private void showImage(String url){
            if (url != null && url.isEmpty() == false){
                Picasso.get()
                        .load(url)
                        .resize(80, 80)
                        .centerCrop()
                        .into(ivSpecies);
            }
        }
        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            Species selectedSp = ssp.get(position);
            Intent intent = new Intent(view.getContext(), Camera.class);
            intent.putExtra("Species", selectedSp);
            view.getContext().startActivity(intent);
            return true;
        }
    }
}
