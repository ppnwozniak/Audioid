package com.example.audioid;

import java.util.List;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Showing the list of already created patients. Allows to load one.
 * @author Moher
 */
public class LoadPatientDataMenu extends Activity implements View.OnClickListener
{
	/**
	 * List of already created patients.
	 */
	private List<String> patientNames;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("CutPasteId")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_patient_data_menu);
		TableLayout table = (TableLayout) findViewById(R.id.patientTable);

		patientNames = new FileReadWrite().getPatients(this); //read created patients names
		int patientNmb = patientNames.size();
		
	    WindowManager mWinMgr = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
	    int displayWidth = mWinMgr.getDefaultDisplay().getWidth();
	    displayWidth = displayWidth - 100;
		
		if(patientNmb != 0) //if there is any patient
		{
			for (int i=0; i<patientNmb; i++)
			{
			    LayoutInflater inflater = LayoutInflater.from(LoadPatientDataMenu.this);
			    TableRow row = (TableRow) inflater.inflate(R.layout.patient_row, null);
	
			    View view = LayoutInflater.from(getApplication()).inflate(R.layout.patient_row, null);
			    TextView text = (TextView) view.findViewById(R.id.patientName);
			    text.setText(patientNames.get(i));
	
			    table.addView(row);
			}
			
		    TableLayout tl = (TableLayout)findViewById(R.id.patientTable);
		    
		    for (int i=0;i<patientNmb;i++)
		    {   
			    TableRow tr = new TableRow(this);
			    tr.setLayoutParams(new LayoutParams(
			    		displayWidth,
			    		LayoutParams.WRAP_CONTENT)); 
			    Button b = new Button(this);   
			    b.setText(patientNames.get(i));  
			    b.setLayoutParams(new LayoutParams(
			    		displayWidth, 
			    		LayoutParams.WRAP_CONTENT));
			    b.setOnClickListener(this);
			    b.setId(i);
		        tr.addView(b);
			    tl.addView(tr,new TableLayout.LayoutParams(   
			    		displayWidth,   
			    		LayoutParams.WRAP_CONTENT));
		    }
		}
		else
		{
			LayoutInflater inflater = LayoutInflater.from(LoadPatientDataMenu.this);
		    TableRow row = (TableRow) inflater.inflate(R.layout.patient_row, null);

		    View view = LayoutInflater.from(getApplication()).inflate(R.layout.patient_row, null);
		    TextView text = (TextView) view.findViewById(R.id.patientName);
		    text.setText("No patients so far");

		    table.addView(row);
		    
		    TableLayout tl = (TableLayout)findViewById(R.id.patientTable);
		    
		    TableRow tr = new TableRow(this);
		    tr.setLayoutParams(new LayoutParams(
		    		displayWidth,
		    		LayoutParams.WRAP_CONTENT));
		    TextView t = new TextView(this);
		    t.setText("No patients so far");
		    t.setLayoutParams(new LayoutParams(
		    		displayWidth, 
		    		LayoutParams.WRAP_CONTENT));
		    t.setGravity(Gravity.CENTER_HORIZONTAL);
	        tr.addView(t);
		    tl.addView(tr,new TableLayout.LayoutParams(   
		    		displayWidth,   
		    		LayoutParams.WRAP_CONTENT));
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.load_patient_data_menu, menu);
		return true;
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

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
    /**
     * Give info about which patient was chosen to the next activity.
     */
	@Override
	public void onClick(View v)
	{
	    Button btn = (Button) v;
	    int id = btn.getId();
	    Intent intent = new Intent(this, PatientMenu.class);
	    intent.putExtra("patientName", patientNames.get(id));
    	
	    finish();
	    startActivity(intent);
	}
}
