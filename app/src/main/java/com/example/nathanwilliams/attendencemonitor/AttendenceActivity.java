package com.example.nathanwilliams.attendencemonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AttendenceActivity extends AppCompatActivity
{
    private String club;
    private DatabaseReference mDatabase;
    private FirebaseUser current_user;
    private String uid;
    private RecyclerView recyclerView;
    FirebaseRecyclerOptions<UserRecycler> options;
    FirebaseRecyclerAdapter<UserRecycler,UserViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
        centerTitle();

        FloatingActionButton fab = findViewById(R.id.clubAttendence_back);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Intent add_club_intent = new Intent(AttendenceActivity.this,HomeActivity.class);
                add_club_intent.putExtra("add/edit","add");
                startActivity(add_club_intent);

            }
        });

        Intent intent = getIntent();
        club = intent.getStringExtra("name");

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        uid = current_user.getUid();

        recyclerView = findViewById(R.id.pupil_recyclerView);
        recyclerView.setHasFixedSize(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Clubs");


        Query editQuery = mDatabase.orderByChild("Name").equalTo(club);
        editQuery.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot deleteSnapshot: dataSnapshot.getChildren())
                {
                    DatabaseReference clubRef = deleteSnapshot.getRef();
                    mDatabase = clubRef.child("members");

                    options = new FirebaseRecyclerOptions.Builder<UserRecycler>().setQuery(mDatabase,UserRecycler.class).build();
                    adapter = new FirebaseRecyclerAdapter<UserRecycler, UserViewHolder>(options)
                    {
                        @Override
                        protected void onBindViewHolder(UserViewHolder holder, int position, UserRecycler model)
                        {
                            String name = model.getUserName();
                            holder.userName.setText(name);
                        }

                        @NonNull
                        @Override
                        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
                        {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycler_activity,parent,false);

                            return new UserViewHolder(view);
                        }

                    };

                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    adapter.startListening();
                    recyclerView.setAdapter(adapter);



                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


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

        if(textViews.size() > 0)
        {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1)
            {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            }
            else
            {
                for(View v : textViews)
                {
                    if(v.getParent() instanceof Toolbar)
                    {
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
