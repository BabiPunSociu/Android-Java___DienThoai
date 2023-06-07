package com.example.adapterview;

import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class ConnectionReceiver extends BroadcastReceiver {
    public Context c;
    public ConnectionReceiver()
    {

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("App", ""+intent.getAction());
        if (intent.getAction().equals("com.example.listview2023.SOME_ACTION"))
            Toast.makeText(context, "SOME_ACTION is received", Toast.LENGTH_SHORT).show();
        else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if(isConnected)
            {
                try
                {
                    Toast.makeText(context, "Network is connected", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(context, "Network is changed or reconnected", Toast.LENGTH_SHORT).show();
            }
        }
//
//        else if()
//        {
//
//        }
    }

    private static boolean isAirplaneModeOn(Context context)
    {
        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) !=0;
    }

}
