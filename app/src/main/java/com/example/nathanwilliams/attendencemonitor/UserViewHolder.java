package com.example.nathanwilliams.attendencemonitor;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder

{
    public TextView userName;
    public ImageButton AttendeeButton;
    public Boolean attendee = false;


    public UserViewHolder(@NonNull View itemView)
    {
        super(itemView);
        userName = itemView.findViewById(R.id.user_name);
        AttendeeButton = itemView.findViewById(R.id.user_attendee);
    }
    public void updateAttendence()
    {
        if(attendee)
        {
            AttendeeButton.setImageResource(R.drawable.ic_check_black_24dp);
            AttendeeButton.setBackgroundResource(R.color.lightGreen);
        }
        else
        {
            AttendeeButton.setBackgroundResource(R.color.lightRed);
            AttendeeButton.setImageResource(R.drawable.ic_cross_black_24dp);
        }

    }
}
