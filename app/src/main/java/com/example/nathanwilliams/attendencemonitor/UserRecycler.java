package com.example.nathanwilliams.attendencemonitor;

public class UserRecycler
{

    public String name;

    public UserRecycler()
    {

    }

    public UserRecycler(String UserName)
    {
        this.name = UserName;

    }
    public String getUserName()
    {
        return name;
    }

    public void setUserName(String clubName)
    {
        name = clubName;
    }

}
