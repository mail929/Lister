package com.liamfruzyna.android.wishlister;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(IO.instance == null)
        {
            IO.firstInstance(this);
        }
        (new LoadTask()).execute(this);
    }

    private class LoadTask extends AsyncTask<Activity, Integer, Activity>
    {
        protected Activity doInBackground(Activity... c) {
            while(ContextCompat.checkSelfPermission(c[0], android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(c[0], new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }

            if(IO.getInstance().getBoolean(IO.FIRST_PREF, true))
            {
                System.out.println("First Launch");
                Intent intent = new Intent(c[0], LoginActivity.class);
                startActivity(intent);
            }
            else
            {
                if(IO.getInstance().checkNetwork())
                {
                    System.out.println("Syncing");
                    IO.getInstance().syncOffThread();
                }
                else
                {
                    System.out.println("Reading");
                    IO.getInstance().loadFromFile();
                }
                Intent intent = new Intent(c[0], ListerActivity.class);
                startActivity(intent);
            }

            return c[0];
        }

        protected void onProgressUpdate(Integer... progress) {}

        protected void onPostExecute(Activity c) {}
    }
}
