package com.example.nathanwilliams.attendencemonitor;

import com.google.firebase.storage.StorageReference;

public class ClubRecycler
{

    public String Name,Age,Color,Mentor,Location,Img;

    public ClubRecycler()
    {

    }

    public ClubRecycler(String ClubName, String ClubAgeGroup, String ClubColor, String ClubMentor, String ClubLocation, String ClubImage)
    {
        this.Name = ClubName;
        this.Age = ClubAgeGroup;
        this.Color = ClubColor;
        this.Mentor = ClubMentor;
        this.Location = ClubLocation;
        this.Img = ClubImage;
    }
    public String getClubName()
    {
        return Name;
    }

    public void setClubName(String clubName)
    {
        Name = clubName;
    }

    public String getClubAgeGroup()
    {
        return Age;
    }

    public void setClubAgeGroup(String clubAgeGroup)
    {
        Age = clubAgeGroup;
    }

    public String getClubColor()
    {
        return Color;
    }

    public void setClubColor(String clubColor)
    {
        Color = clubColor;
    }


    public String getClubLocation()
    {
        return Location;
    }

    public void setClubLocation(String clubLocation)
    {
        Location = clubLocation;
    }

    public String getClubImage() { return Img; }

    public void setClubImage(String clubImage)
    {
        Img = clubImage;
    }

}
