package com.example.nathanwilliams.attendencemonitor;

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


    public UserViewHolder(@NonNull View itemView)
    {
        super(itemView);
        userName = itemView.findViewById(R.id.user_name);
    }
}
