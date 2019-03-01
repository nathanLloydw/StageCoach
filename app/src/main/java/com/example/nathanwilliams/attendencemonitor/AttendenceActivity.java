package com.example.nathanwilliams.attendencemonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AttendenceActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);

        FloatingActionButton fab = findViewById(R.id.clubAttendence_back);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent add_club_intent = new Intent(AttendenceActivity.this,HomeActivity.class);
                add_club_intent.putExtra("add/edit","add");
                startActivity(add_club_intent);

            }
        });
    }
}
