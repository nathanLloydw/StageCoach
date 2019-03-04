package com.example.nathanwilliams.attendencemonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AttendenceActivity extends AppCompatActivity
{
    private String club;
    private DatabaseReference mDatabase;
    private FirebaseUser current_user;
    private String uid;
    private RecyclerView recyclerView;
    private String newUser = "";

    FirebaseRecyclerOptions<UserRecycler> options;
    FirebaseRecyclerAdapter<UserRecycler,UserViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
        centerTitle();

        FloatingActionButton back = findViewById(R.id.clubAttendence_back);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Intent add_club_intent = new Intent(AttendenceActivity.this,HomeActivity.class);
                add_club_intent.putExtra("add/edit","add");
                startActivity(add_club_intent);

            }
        });

        FloatingActionButton add = findViewById(R.id.clubAttendence_add);
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(AttendenceActivity.this);
                builder.setTitle("Add new pupil");
                builder.setMessage("new pupils fullname:");
                final EditText input = new EditText(AttendenceActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                builder.setView(input);

                builder.setCancelable(false);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        newUser = input.getText().toString();
                        addUser(newUser);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });

                builder.show();

            }
        });

        Intent intent = getIntent();
        club = intent.getStringExtra("name");




        current_user = FirebaseAuth.getInstance().getCurrentUser();
        uid = current_user.getUid();

        recyclerView = findViewById(R.id.pupil_recyclerView);
        recyclerView.setHasFixedSize(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Clubs");


        Query userQuery = mDatabase.orderByChild("Name").equalTo(club);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren())
                {
                    DatabaseReference clubRef = userSnapshot.getRef();
                    mDatabase = clubRef.child("members");


                    options = new FirebaseRecyclerOptions.Builder<UserRecycler>().setQuery(mDatabase,UserRecycler.class).build();
                    adapter = new FirebaseRecyclerAdapter<UserRecycler, UserViewHolder>(options)
                    {
                        @Override
                        protected void onBindViewHolder(final UserViewHolder holder, int position, UserRecycler model)
                        {
                            final String name = model.getUserName();
                            holder.userName.setText(name);

                            String didAttend = model.getattendedToday();
                            System.out.println(didAttend);

                            if(didAttend.matches("false"))
                            {
                                holder.attendee = false;
                            }
                            else
                            {
                                holder.attendee = true;
                            }
                            holder.updateAttendence();

                            ImageButton attendee = holder.AttendeeButton;
                            attendee.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    if(holder.attendee)
                                    {
                                        holder.attendee = false;
                                        holder.updateAttendence();
                                        changeUserAttendence(mDatabase,name,"false");
                                    }
                                    else
                                    {
                                        holder.attendee = true;
                                        holder.updateAttendence();
                                        changeUserAttendence(mDatabase,name,"true");
                                    }
                                }
                            });
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

    private void addUser(String Username)
    {
        final HashMap<String,String> clubMap = new HashMap<>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String Currentdate = dateFormat.format(date);

        clubMap.put("name",Username);
        clubMap.put("currentSession","false");

        mDatabase.push().setValue(clubMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {

                }
            }
        });
    }

    public void changeUserAttendence(DatabaseReference ref,final String name, final String value)
    {
        Query changeAttend = ref.orderByChild("name").equalTo(name);
        changeAttend.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot editSnapshot: dataSnapshot.getChildren())
                {
                    final HashMap<String, Object> Map = new HashMap<>();
                    Map.put("currentSession", value);
                    Map.put("name", name);
                    DatabaseReference memberRef = editSnapshot.getRef();
                    memberRef.updateChildren(Map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }

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
