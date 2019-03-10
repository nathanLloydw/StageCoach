package com.example.nathanwilliams.attendencemonitor;

public class UserRecycler
{

    public String name;
    public String attend;

    public UserRecycler()
    {

    }

    public UserRecycler(String UserName,String didAttendToday)
    {
        this.name = UserName;
        //this.attend = didAttendToday;

    }
    public String getUserName()
    {
        return name;
    }

    public void setUserName(String clubName)
    {
        name = clubName;
    }

    public String getattendedToday(){return attend; }

    public void setattendedToday(String attended){attend = attended;}

}
