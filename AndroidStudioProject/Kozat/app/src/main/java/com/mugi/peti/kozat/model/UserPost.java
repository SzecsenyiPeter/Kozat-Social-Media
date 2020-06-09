package com.mugi.peti.kozat.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UserPost
{
    public String posterUid;
    public String text;
    public String date;
    public boolean withImage;

    public UserPost(){

    }

    public UserPost(String posterUid, String text, boolean withImage){
        this.posterUid = posterUid;
        this.text = text;
        this.withImage = withImage;
        Calendar currentDate = Calendar.getInstance();
        this.date = Long.toString(currentDate.getTimeInMillis());
    }

}
