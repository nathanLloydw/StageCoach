package com.example.nathanwilliams.attendencemonitor;


import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity
{


    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<ClubRecycler> options;
    FirebaseRecyclerAdapter<ClubRecycler,ClubViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        centerTitle();

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = current_user.getUid();

        FloatingActionButton fab = findViewById(R.id.add_club);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent add_club_intent = new Intent(HomeActivity.this,addClubActivity.class);
                startActivity(add_club_intent);
                //Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Clubs");

        options = new FirebaseRecyclerOptions.Builder<ClubRecycler>().setQuery(databaseReference,ClubRecycler.class).build();

        adapter = new FirebaseRecyclerAdapter<ClubRecycler, ClubViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ClubViewHolder holder, int position, ClubRecycler model)
            {

                holder.clubName.setText(model.getClubName());
                holder.clubAge.setText(model.getClubAgeGroup());
                holder.clubCard.setCardBackgroundColor(Color.parseColor(model.getClubColor()));
                holder.clubLocation.setText(model.getClubLocation());

                Picasso.get().load(model.getClubImage()).placeholder(R.drawable.avatar).into(holder.clubImage);
            }

            @NonNull
            @Override
            public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_recycler_activity,parent,false);

                return new ClubViewHolder(view);
            }
        };

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(adapter!= null)
            adapter.startListening();
    }

    @Override
    protected void onStop()
    {
        if(adapter!=null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(adapter!= null)
            adapter.startListening();
    }

    private void centerTitle()
    {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

}
