package com.example.audioid;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Open a menu for creating new patient.
 * @author Moher
 */
public class GetPatientDataMenu extends Activity
{
	/**
	 * Patient created properly flag.
	 */
	private boolean ifOK = false;
	
	/**
	 * Given patient's login.
	 */
	private String login;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_patient_data_menu);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.get_patient_data_menu, menu);
		return true;
	}
	
	/**
	 * Create new patient.
	 * @param view clicked View
	 */
	public void createNewPatient(View view)
	{
    	EditText editText = (EditText) findViewById(R.id.GiveLoginTextArea);
    	login = editText.getText().toString();
    	FileReadWrite fr = new FileReadWrite();
    	if(login.equals(""))
    	{
    		errorMessg("Give patient's name");
    		return;
    	}
    	else if(!fr.createPatientFile(this, login)) //check if login already exists
    	{
    		errorMessg("Patient already exists");
    		return;
    	}
    	else
    	{
    		ifOK = true;
    		errorMessg("Patient created");
    	}
	}
	
    /**
     * Go to the previous menu.
     * @param view clicked View
     */
    public void getBack(View view) //go back
    {
    	Intent intent = new Intent(this, Audioid.class);
    	
    	finish();
    	startActivity(intent);
    }
    
	/**
	 * Show the message.
	 * @param text - message text
	 */
	@SuppressWarnings("deprecation")
	private void errorMessg(String text)//show message
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Alert");
		alertDialog.setMessage(text);
		alertDialog.setIcon(R.drawable.abc_ab_bottom_solid_dark_holo);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
		        @SuppressLint("ShowToast")
				public void onClick(DialogInterface dialog, int which)
		        {
		        	Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT);
		        	if(ifOK) //if patient was created successfully
		        	{
		            	Intent intent = new Intent(getApplicationContext(), PatientMenu.class);
		            	intent.putExtra("patientName", login);
		            	
		            	finish();
		            	startActivity(intent);
		        	}
		        }
		});
		alertDialog.show();
	}
}
