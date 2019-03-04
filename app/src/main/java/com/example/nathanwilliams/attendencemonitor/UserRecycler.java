package com.example.nathanwilliams.attendencemonitor;

public class UserRecycler
{

    public String name;
    public String currentSession;

    public UserRecycler()
    {

    }

    public UserRecycler(String UserName,String didAttendToday)
    {
        this.name = UserName;
        this.currentSession = didAttendToday;

    }
    public String getUserName()
    {
        return name;
    }

    public void setUserName(String clubName)
    {
        name = clubName;
    }

    public String getattendedToday()
    {
        return currentSession;
    }

    public void setattendedToday(String attended)
    {
        currentSession = attended;
    }

}
