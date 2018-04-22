package com.example.audioid;

import java.io.IOException;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Execute the whole PTA or UCL procedure.
 * @author Moher
 */
public class PTAandUCL extends Activity {
	/**
	 * Name of actual procedure (PTA or UCL).
	 */
	protected String name;
	
	/**
	 * Media player object.
	 */
	private SoundPool soundPool = null;
	
	/**
	 * Id of a sound track.
	 */
	private int id = -1;
	
	/**
	 * Start the procedure flag.
	 */
	private boolean ifStart = true;
	
	/**
	 * Stop the procedure flag.
	 */
	private boolean ifStop = false;
	
	/**
	 * Right ear analysis flag.
	 */
	private boolean ifRight = true;
	
	/**
	 *  Which element from HzPoints table is being analyzed.
	 */
	private int whichHz = 0;
	
	/**
	 * What dB value is being analyzed.
	 */
	private int whichDB = 40;
	
	/**
	 * Number of different Hz points.
	 */
	private int pointNmb = 9;
	
	/**
	 * X-axis points which are connected with different Hz values from HzValues table.
	 */
	private double[] HzPoints = {1, 2, 3, 3.5, 4, 4.5, 5, 5.5, 6};
	
	/**
	 * Values of Hz used in the procedure.
	 */
	private int[] HzValues = {250, 500, 1000, 1500, 2000, 3000, 4000, 6000, 8000};
	
	/**
	 *  Data for left ear: 9 measure points x 2 parameters (Hz, dB)
	 */
	private GraphViewData[] leftEarData;
	
	/**
	 *  Data for right ear: 9 measure points x 2 parameters (Hz, dB)
	 */
	private GraphViewData[] rightEarData;
	
	/**
	 * Points to keep the constant size of the axis.
	 */
	private GraphViewData[] axisHolder = new GraphViewData[] {new GraphViewData(0, -10), new GraphViewData(7, 100)};
	
	/**
	 * Actual points on the plot.
	 */
	private double pX = HzPoints[whichHz], pY = getDB(whichDB);
	
	/**
	 * Rendered graph object.
	 */
	private GraphView graphView;
	
	/**
	 * Convert real value of dB to the proper point on the plot.
	 * @param x - real value of dB
	 * @return value of dB to draw it on the plot in the right place
	 */
	private int getDB(int x)
	{
		return 90-x;
	}
	
	/**
	 * Start the procedure.
	 */
	public void start()
	{
		if(name.equals("PTA"))
		{
		errorMessg("Click HEAR if you hear the sound. Click CAN'T HEAR if you can't hear " +
				"the sound. The cross point will react after your clicking and change the dB " +
				"level of the sound. If you find the last point when you still HEAR the sound " +
				" and you are listening to that sound in that moment, " +
				"click HEARING LIMIT. The cross point will go to the next point representing " +
				"higher frequency of the sound. Do this procedure for all different " + pointNmb +
				" frequencies. The text below the graph shows how many points are left.");
		}
		else if(name.equals("UCL"))
		{
			errorMessg("Click TOO LOUD if the sound is too loud for you. Click COMFORTABLE if " +
					"the sound is comfortable for you. The cross point will react after your clicking" +
					" and change the dB level of the sound. If you find the last point when " +
					"you still think the sound is COMFORTABLE and you are listening to that sound " +
					"in that moment, click COMFORTABLE LIMIT. The cross " +
					"point will go to the next point representing higher frequency of the " +
					"sound. Do this procedure for all different " + pointNmb + " frequencies." +
					" The text below the graph shows how many points are left.");
		}
		
//		//////////////////
//		for(int i=0; i<pointNmb*3; i++)
//		{
//			hearingLimit(null);
//		}
//		//////////////////
		
		createGraph();
		changeText();
		playMusic(String.valueOf(HzValues[whichHz])+"_"+whichDB+".wav");
	}
	
	/**
	 * Draw a start graph.
	 */
	private void createGraph()
	{
		//Initiate the data points (all on Hz/dB = Hz(i)/40)
		leftEarData = new GraphViewData[pointNmb];
		for(int i=0; i<pointNmb; i++)
		{
			leftEarData[i] = new GraphViewData(HzPoints[i], getDB(40));
		}
		
		rightEarData = new GraphViewData[pointNmb];
		for(int i=0; i<pointNmb; i++)
		{
			rightEarData[i] = new GraphViewData(HzPoints[i], getDB(40));
		}
		
		//Create crosses:
		GraphViewData[] pointNowData1 = new GraphViewData[2];
		pointNowData1[0] = new GraphViewData(pX-0.25, pY+5);
		pointNowData1[1] = new GraphViewData(pX+0.25, pY-5);
		GraphViewData[] pointNowData2 = new GraphViewData[2];
		pointNowData2[0] = new GraphViewData(pX-0.25, pY-5);
		pointNowData2[1] = new GraphViewData(pX+0.25, pY+5);
		
		//Give colors and names:
		GraphViewSeries axisHolderSerie = new GraphViewSeries("", new GraphViewSeriesStyle(Color.TRANSPARENT, 0), axisHolder);
		GraphViewSeries leftEarSerie = new GraphViewSeries("Left Ear", new GraphViewSeriesStyle(Color.BLUE, 3), leftEarData);
		GraphViewSeries rightEarSerie = new GraphViewSeries("Right Ear", new GraphViewSeriesStyle(Color.RED, 3), rightEarData);
		GraphViewSeries pointNowSerie1 = new GraphViewSeries("Point Now", new GraphViewSeriesStyle(Color.GREEN, 3), pointNowData1);
		GraphViewSeries pointNowSerie2 = new GraphViewSeries("Point Now", new GraphViewSeriesStyle(Color.GREEN, 3), pointNowData2);
		
		//Set labels:
		graphView = new LineGraphView(this, name);
		graphView.setHorizontalLabels(new String[] {"125", "250", "500", "1000", "2000", "4000", "8000", "[Hz]"});
		graphView.setVerticalLabels(new String[] {"[dBHL]", "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"});
		//											100    90   80    70    60    50    40    30    20    10    0     -10   
		graphView.getGraphViewStyle().setGridColor(Color.GRAY);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
		graphView.getGraphViewStyle().setTextSize(15);
		graphView.getGraphViewStyle().setNumHorizontalLabels(8);
		graphView.getGraphViewStyle().setNumVerticalLabels(12);
		
		//Add data to the graph:
		graphView.addSeries(axisHolderSerie);
		graphView.addSeries(leftEarSerie);
		graphView.addSeries(rightEarSerie);
		graphView.addSeries(pointNowSerie1);
		graphView.addSeries(pointNowSerie2);
		
		RelativeLayout layout = (RelativeLayout) (findViewById(R.id.HearingPlot));
		layout.addView(graphView);
	}
	
	/**
	 * Update a graph for actual data points.
	 */
	private void updateGraph()
	{
		graphView.removeSeries(4);
		graphView.removeSeries(3);
		graphView.removeSeries(2);
		graphView.removeSeries(1);
		
		//Create crosses:
		GraphViewData[] pointNowData1 = new GraphViewData[2];
		pointNowData1[0] = new GraphViewData(pX-0.25, pY+5);
		pointNowData1[1] = new GraphViewData(pX+0.25, pY-5);
		GraphViewData[] pointNowData2 = new GraphViewData[2];
		pointNowData2[0] = new GraphViewData(pX-0.25, pY-5);
		pointNowData2[1] = new GraphViewData(pX+0.25, pY+5);
		
		//Give colors and names:
		GraphViewSeries leftEarSerie = new GraphViewSeries("Left Ear", new GraphViewSeriesStyle(Color.BLUE, 3), leftEarData);
		GraphViewSeries rightEarSerie = new GraphViewSeries("Right Ear", new GraphViewSeriesStyle(Color.RED, 3), rightEarData);
		GraphViewSeries pointNowSerie1 = new GraphViewSeries("Point Now", new GraphViewSeriesStyle(Color.GREEN, 3), pointNowData1);
		GraphViewSeries pointNowSerie2 = new GraphViewSeries("Point Now", new GraphViewSeriesStyle(Color.GREEN, 3), pointNowData2);

		graphView.addSeries(leftEarSerie);
		graphView.addSeries(rightEarSerie);
		graphView.addSeries(pointNowSerie1);
		graphView.addSeries(pointNowSerie2);
		
		RelativeLayout layout = (RelativeLayout) (findViewById(R.id.HearingPlot));
		layout.removeAllViews();
		layout.addView(graphView);
	}
	
	/**
	 * Execute the process of changing the analyzed point on the plot while the sound is being heard.
	 * @param view clicked View
	 */
	public void hear(View view)
	{
		makeChange(-10);
	}
	
	/**
	 * Execute the process of changing the analyzed point on the plot while the sound is not being heard.
	 * @param view clicked View
	 */
	public void cantHear(View view)
	{
		makeChange(10);
	}
	
	/**
	 * Change the dB value.
	 * @param delta how much change the dB
	 */
	public void makeChange(int delta)
	{
		if(!ifStop)
		{
			if((delta == -10 && whichDB > 0) || (delta == 10 && whichDB < 90)) //if it is possible to change
			{
				whichDB = whichDB+delta;
				if(ifRight)
				{
					rightEarData[whichHz] = new GraphViewData(HzPoints[whichHz], getDB(whichDB));
				}
				else
				{
					leftEarData[whichHz] = new GraphViewData(HzPoints[whichHz], getDB(whichDB));
				}
				pY = getDB(whichDB);
				updateGraph();
				playMusic(String.valueOf(HzValues[whichHz])+"_"+whichDB+".wav");
			}
			else
			{
				hearingLimit(null);
			}
		}
	}
	
	/**
	 * Go to the next Hz point.
	 * @param view clicked View
	 */
	public void hearingLimit(View view)
	{
		if(whichHz == pointNmb-1) //if it is the end of Hz
		{
			if(ifRight) //if it was right then left
			{
				errorMessg("Left ear now");
				ifRight = false;
				whichHz = 0;
				whichDB = 40;
				pY = getDB(whichDB);
				pX = HzPoints[whichHz];
				updateGraph();
				changeText();
				playMusic(String.valueOf(HzValues[whichHz])+"_"+whichDB+".wav");
			}
			else if(!ifStop) //if it was left then stop if didn't stop
			{
				ifStop = true;
				soundPool.stop(id);
				soundPool.release();
				Bundle extras = getIntent().getExtras();
				FileReadWrite frw = new FileReadWrite();
		    	frw.saveResults(this, extras.getString("patientName"), name, leftEarData, rightEarData);
		    	whichHz = 9;
		    	changeText();
		    	errorMessg("Results saved. You can watch them in the history. Remember that" +
		    			" the diagnosis is only a suggestion. You always need to consult the " +
		    			"results with your doctor.");
			}
		}
		else
		{
			whichHz++;
			whichDB = 40;
			pY = getDB(whichDB);
			if(whichHz < pointNmb)
			{
				pX = HzPoints[whichHz];
				updateGraph();
				changeText();
				playMusic(String.valueOf(HzValues[whichHz])+"_"+whichDB+".wav");
			}
		}
	}

	/**
	 * Show the message.
	 * @param text message text
	 */
	@SuppressWarnings("deprecation")
	private void errorMessg(String text)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Instructions");
		alertDialog.setMessage(text);
		alertDialog.setIcon(R.drawable.abc_ab_bottom_solid_dark_holo);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
		        @SuppressLint("ShowToast")
				public void onClick(DialogInterface dialog, int which)
		        {
		        	Toast.makeText(getApplicationContext(), "Procedure start", Toast.LENGTH_SHORT);
		        	if(ifStart)
		        	{
		        		ifStart = false;
		        		errorMessg("Plug on your headphones - right ear first");
		        	}
		        }
		});
		alertDialog.show();
	}
	
    /**
     * Play the music file.
     * @param filepath file path to the music file
     */
    @SuppressLint("NewApi")
	private void playMusic(String filepath)
    {
    	if(id != -1)
    	{
    		soundPool.stop(id);
    		soundPool.release();
    	}
    	
    	final float leftVolume, rightVolume;
    	if(ifRight)
    	{
    		leftVolume = 1;
    		rightVolume = 0;
    	}
    	else
    	{
    		leftVolume = 0;
    		rightVolume = 1;
    	}
    	
    	soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

    	AssetFileDescriptor afd = null;
        try
        {
			afd = getAssets().openFd(filepath);
	        final int idLoad = soundPool.load(afd, 1);
	        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener()
	        {
	            public void onLoadComplete(SoundPool arg0, int arg1, int arg2)
	            {
	                id = soundPool.play(idLoad, leftVolume, rightVolume, 1, -1, 1f);
	            };
	        });
	        afd.close();
        }
        catch (IOException e1)
        {
			e1.printStackTrace();
		}
    }
    
    /**
     * Change the text displayed under the plot.
     */
    private void changeText()
    {
    	String text = "";
    	if(ifRight)
    	{
    		text += "Right ear: ";
    	}
    	else
    	{
    		text += "Left ear: ";
    	}
    	text += String.valueOf(pointNmb - whichHz) + " points left";

    	TextView t = (TextView)findViewById(R.id.StepsToEnd); 
        t.setText(text);
    }
    
    /**
     * Go to the previous menu.
     * @param view clicked View
     */
	public void getBack(View view)
    {
    	soundPool.stop(id);
    	soundPool.release();
    	finish();
    }
}