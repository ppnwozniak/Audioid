package com.example.audioid;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;

/**
 * Main menu opening class. Gives possibility to choose between creating a new patient, loading
 * already created patient or exit the program.
 * @author Moher
 */
public class Audioid extends Activity
{
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audioid);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.audioid, menu);
        return true;
    }
    

    /**
     * Open menu for creating a new patient.
     * @param view clicked View
     */
    public void getPatientData(View view)
    {
    	Intent intent = new Intent(this, GetPatientDataMenu.class);
    	
    	finish();
    	startActivity(intent);
    }
    
    /**
     * Open menu for loading already created patient.
     * @param view clicked View
     */
    public void loadPatientData(View view)
    {
    	Intent intent = new Intent(this, LoadPatientDataMenu.class);
    	
    	finish();
    	startActivity(intent);
    }
    
    /**
     * Leave the program.
     * @param view clicked View
     */
    public void exit(View view)
    {
    	android.os.Process.killProcess(android.os.Process.myPid());
    }
}