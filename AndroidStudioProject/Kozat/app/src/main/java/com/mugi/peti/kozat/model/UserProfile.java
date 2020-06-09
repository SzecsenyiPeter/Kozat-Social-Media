package com.mugi.peti.kozat.model;

public class UserProfile
{
    public String fullName;
    public String emailAddress;
    public String creationDate;
    public String birthDate;

    public UserProfile()
    {

    }

    public UserProfile(String fullName, String emailAddress, String creationDate, String birthDate)
    {
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.creationDate = creationDate;
        this.birthDate = birthDate;
    }
}
