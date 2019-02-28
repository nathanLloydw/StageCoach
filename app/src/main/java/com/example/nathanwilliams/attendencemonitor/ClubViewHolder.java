package com.example.nathanwilliams.attendencemonitor;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;

public class ClubViewHolder extends RecyclerView.ViewHolder

{
    public TextView clubName;
    public TextView clubAge;
    public CardView clubCardTop;
    public RelativeLayout clubCardBottom;
    public TextView clubLocation;
    public SelectableRoundedImageView clubImage;
    public ImageButton clubDeleteButton;

    public ClubViewHolder(@NonNull View itemView)
    {
        super(itemView);

        clubName = itemView.findViewById(R.id.club_name);
        clubAge = itemView.findViewById(R.id.club_age);
        clubCardTop = itemView.findViewById(R.id.club_card);
        clubCardBottom = itemView.findViewById(R.id.club_card_bottom);
        clubLocation = itemView.findViewById(R.id.club_location);
        clubImage = itemView.findViewById(R.id.club_image);
        clubDeleteButton = itemView.findViewById(R.id.club_delete_button);

    }


}
