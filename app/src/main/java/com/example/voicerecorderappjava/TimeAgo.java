package com.example.voicerecorderappjava;

import java.util.Date;
import java.util.concurrent.TimeUnit;

class TimeAgo {

    public String getTimeAgo(long duration){
        Date date = new Date();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(date.getTime() -duration);
        long minutes = TimeUnit.MILLISECONDS.toSeconds(date.getTime() -duration);
        long hours = TimeUnit.MILLISECONDS.toSeconds(date.getTime() -duration);
        long days = TimeUnit.MILLISECONDS.toSeconds(date.getTime() -duration);

        if(seconds < 60){
            return "just now";
        }else if(minutes == 1){
            return " a minute ago";
        }else if(minutes >1 && minutes <60){
            return minutes + " minutes ago";
        }else if(hours == 1){
            return "an hour ago";
        }else if(hours > 1 && hours <24){
            return hours + " hours ago";
        }else if (days ==1){
            return "one day ago";
        }else {
            return days +" days ago";
        }
    }
}
