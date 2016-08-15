package com.example.gaurav.game2;

import android.os.Message;
import android.util.Log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Gaurav on 13-08-2016.
 */
public class player  {
    private boolean isHost = true;
public int no;

    public String msg;

    public player()
    {
        msg=new String();
    }


    public void setmsg(String message)
    {

        msg=message;
    }

    public String getmsg()
    {
        return msg;
    }



}
