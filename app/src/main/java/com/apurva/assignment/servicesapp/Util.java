package com.apurva.assignment.servicesapp;


public class Util {
    public static String getFileNameFromURL(String url) {
       if(url == null)
           return null;
       else
           return url.substring(url.lastIndexOf('/')+1, url.length());
    }
}
