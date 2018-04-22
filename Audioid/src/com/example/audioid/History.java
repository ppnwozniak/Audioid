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
 * Show the history of procedures done for current patient.
 * @author Moher
 */
public class History extends Activity implements View.OnClickListener
{
	/**
	 * List of procedures for the current patient.
	 */
	private List<String> proceduresHistory;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("CutPasteId")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		TableLayout table = (TableLayout) findViewById(R.id.procedureTable);

		Bundle extras = getIntent().getExtras();
		String patientName = null;
		if (extras != null)
		{
			patientName = extras.getString("patientName"); 
		}
		proceduresHistory = new FileReadWrite().getHistory(this, patientName); //get history
		int procedureNmb = proceduresHistory.size();
		
	    WindowManager mWinMgr = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
	    int displayWidth = mWinMgr.getDefaultDisplay().getWidth();
	    displayWidth = displayWidth - 100;
		
		if(procedureNmb != 0) //check if there is any procedure already done for this patient
		{
			for (int i=0; i<procedureNmb; i++)
			{
			    LayoutInflater inflater = LayoutInflater.from(History.this);
			    TableRow row = (TableRow) inflater.inflate(R.layout.procedure_row, null);
	
			    View view = LayoutInflater.from(getApplication()).inflate(R.layout.procedure_row, null);
			    TextView text = (TextView) view.findViewById(R.id.procedureName);
			    text.setText(proceduresHistory.get(i));
	
			    table.addView(row);
			}
			
		    TableLayout tl = (TableLayout)findViewById(R.id.procedureTable);
		    
		    for (int i=0;i<procedureNmb;i++)
		    {   
			    TableRow tr = new TableRow(this);
			    tr.setLayoutParams(new LayoutParams(
			    		displayWidth,
			    		LayoutParams.WRAP_CONTENT)); 
			    Button b = new Button(this);   
			    b.setText(proceduresHistory.get(i));  
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
			LayoutInflater inflater = LayoutInflater.from(History.this);
		    TableRow row = (TableRow) inflater.inflate(R.layout.procedure_row, null);

		    View view = LayoutInflater.from(getApplication()).inflate(R.layout.procedure_row, null);
		    TextView text = (TextView) view.findViewById(R.id.procedureName);
		    text.setText("No procedures so far");

		    table.addView(row);
		    
		    TableLayout tl = (TableLayout)findViewById(R.id.procedureTable);
		    
		    TableRow tr = new TableRow(this);
		    tr.setLayoutParams(new LayoutParams(
		    		displayWidth,
		    		LayoutParams.WRAP_CONTENT));
		    TextView t = new TextView(this);
		    t.setText("No procedures so far");
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
		getMenuInflater().inflate(R.menu.history, menu);
		return true;
	}
	
    /**
     * Go to the previous menu.
     * @param view clicked View
     */
    public void getBack(View view)
    {
    	finish();
    }
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
    /**
     * Give info about which patient and which procedure was chosen to the next activity.
     */
	@Override
	public void onClick(View v)
	{
	    Button btn = (Button) v;
	    int id = btn.getId();
	    Bundle extras = getIntent().getExtras();
	    Intent intent = new Intent(this, ShowResult.class);
	    intent.putExtra("procedureName", proceduresHistory.get(id));
    	
		if (extras != null)
		{
			intent.putExtra("patientName", extras.getString("patientName")); 
		}
	    
	    finish();
	    startActivity(intent);
	}
}
