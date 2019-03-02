package com.example.nathanwilliams.attendencemonitor;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

                Intent add_club_intent = new Intent(HomeActivity.this,addEditClubActivity.class);
                add_club_intent.putExtra("add/edit","add");
                startActivity(add_club_intent);

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
                final String club = model.getClubName();
                final String age = model.getClubAgeGroup();
                final String location = model.getClubLocation();
                final String picture = model.getClubImage();
                final String color = model.getClubColor();
                final String mentor = model.getClubMentor();

                holder.clubName.setText(club);
                holder.clubAge.setText(age);
                holder.clubLocation.setText(location);
                holder.clubCardTop.setCardBackgroundColor(Color.parseColor(color));
                Picasso.get().load(picture).placeholder(R.drawable.avatar).into(holder.clubImage);

                String TransparentColor = model.getClubColor().replace("#","");

                TransparentColor = "#EA"+TransparentColor;

                int newColor = Color.parseColor(TransparentColor);
                newColor = darken(newColor,0.7);


                holder.clubCardBottom.setBackgroundColor(newColor);



                ImageButton takeAttendence = holder.clubAttendenceButton;
                takeAttendence.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent attendence_intent = new Intent(HomeActivity.this,AttendenceActivity.class);
                        startActivity(attendence_intent);
                    }
                });

                ImageButton stats = holder.clubStatsButton;
                stats.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent stats_intent = new Intent(HomeActivity.this,StatsActivity.class);
                        startActivity(stats_intent);

                    }
                });

                ImageButton edit = holder.clubEditButton;
                edit.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent add_club_intent = new Intent(HomeActivity.this,addEditClubActivity.class);
                        add_club_intent.putExtra("add/edit","edit ");
                        add_club_intent.putExtra("name",club);
                        add_club_intent.putExtra("age",age);
                        add_club_intent.putExtra("location",location);
                        add_club_intent.putExtra("imgURL",picture);
                        add_club_intent.putExtra("color",color);
                        add_club_intent.putExtra("mentor",mentor);

                        startActivity(add_club_intent);
                    }
                });


                ImageButton delete = holder.clubDeleteButton;
                delete.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        builder.setTitle("Delete club entry.");
                        builder.setMessage("You are about to delete all records of this club from the database. Do you really want to proceed ?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                Query deleteQuery = databaseReference.orderByChild("Name").equalTo(club);
                                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        for (DataSnapshot deleteSnapshot: dataSnapshot.getChildren())
                                        {
                                            deleteSnapshot.getRef().removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) { }

                                });
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        });

                        builder.show();
                    }
                });

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

    public static int darken(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = darkenColor(red, fraction);
        green = darkenColor(green, fraction);
        blue = darkenColor(blue, fraction);
        int alpha = Color.alpha(color);

        return Color.argb(alpha, red, green, blue);
    }

    private static int darkenColor(int color, double fraction) {
        return (int)Math.max(color - (color * fraction), 0);
    }


}
