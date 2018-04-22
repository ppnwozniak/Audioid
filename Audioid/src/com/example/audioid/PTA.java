package com.example.audioid;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;

/**
 * Run the PTA procedure.
 * @author Moher
 */
public class PTA extends PTAandUCL
{
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pt);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		name = "PTA";
		start();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.pt, menu);
		return true;
	}
}
