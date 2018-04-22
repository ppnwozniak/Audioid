package com.example.audioid;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

/**
 * Chosen patient menu opening class. Gives possibility to choose between PTA procedure,
 * UCL procedure, History presentation or leaving chosen patient menu.
 * @author Moher
 */
public class PatientMenu extends Activity
{

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_menu);
		TextView patientName = (TextView) findViewById(R.id.PatientName);
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
		    patientName.setText("Welcome " + extras.getString("patientName") + "!"); 
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.patient_menu, menu);
		return true;
	}
	
	/**
	 * Run PTA procedure.
	 * @param view clicked View
	 */
	public void runPTA(View view) //run PTA procedure
	{
		Intent intent = new Intent(this, PTA.class);
		Bundle extras = getIntent().getExtras();
		intent.putExtra("patientName", extras.getString("patientName"));
    	//finish();
    	startActivity(intent);
	}
	
	/**
	 * Run UCL procedure.
	 * @param view clicked View
	 */
	public void runUCL(View view)  //run UCL procedure
	{
		Intent intent = new Intent(this, UCL.class);
		Bundle extras = getIntent().getExtras();
		intent.putExtra("patientName", extras.getString("patientName"));
    	//finish();
    	startActivity(intent);
	}
	
	/**
	 * Show the history of procedures.
	 * @param view clicked View
	 */
	public void showHistory(View view) //show the history of procedures
	{
    	Intent intent = new Intent(this, History.class);
    	Bundle extras = getIntent().getExtras();
		intent.putExtra("patientName", extras.getString("patientName"));
    	//finish();
    	startActivity(intent);
    }
	
	/**
     * Go to the previous menu.
     * @param view clicked View
     */
    public void getBack(View view)
    {
    	Intent intent = new Intent(this, Audioid.class);
    	
    	finish();
    	startActivity(intent);
    }
}
