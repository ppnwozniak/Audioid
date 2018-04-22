package com.example.audioid;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.Activity;
import android.content.Context;

/**
 * Class which execute all operations on files. Create the data files for new patients, load
 * data for already created patient, etc.
 * @author Moher
 */
public class FileReadWrite extends Activity
{
	/**
	 * Left ear & right ear data (in this way).
	 */
	private List<double[][]> earData = new ArrayList<double[][]>();
	
	/**
	 * Diagnosis text.
	 */
	private String diagnosis = null;
	
	/**
	 * Possible comments of the results for PTA procedure.
	 */
	private static String[] commentsPTA = {
		"Normal hearing.",
		"Acustic injury in ",
		"Ear aging in ",
		"Deaf of ",
		"Something uncommon with "};
	
	/**
	 * Possible comments of the results for PTA procedure.
	 */
	private static String[] commentsUCL = {
		"Proper level of uncomfortable listening.",
		"Hyperacusis. Lower level of uncomfortable listening."};
	
	/**
	 * Obtainable Hz values.
	 */
	private int[] HzValues = {250, 500, 1000, 1500, 2000, 3000, 4000, 6000, 8000};
	
	/**
	 * Create data files for new patient.
	 * @param ctx application context
	 * @param patientName name of the patient to create
	 * @return if creation was done properly (if patient didn't exist)
	 */
	public boolean createPatientFile(Context ctx, String patientName)
	{
		try
		{	
			//add patient to the list of patients:
			FileOutputStream outputStream = ctx.openFileOutput("patients", Context.MODE_APPEND);
			
			String line;
			FileInputStream fis = ctx.openFileInput("patients");
		    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		    if (fis!=null)
		    {                            
		        while ((line = reader.readLine()) != null)
		        {    
		            if(line.equals(patientName))
		            {
		            	fis.close();
		            	outputStream.close();
		            	return false;
		            }
		        }               
		    }       
		    fis.close();
			
			outputStream.write(patientName.getBytes());
			outputStream.write("\n".getBytes());
			outputStream.close();
			
			//create patient's file:
			outputStream = ctx.openFileOutput(patientName, Context.MODE_PRIVATE);
			outputStream.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Save results of the procedure for specific patient.
	 * @param ctx application context
	 * @param patientName patient name
	 * @param procedureName procedure name
	 * @param leftEarData data for the left ear
	 * @param rightEarData data for the right ear
	 */
	public void saveResults(Context ctx, String patientName, String procedureName, GraphViewData[] leftEarData, GraphViewData[] rightEarData)
	{
		FileOutputStream outputStream;
		Calendar c = Calendar.getInstance();
		String point, title;

		try
		{
			//Every data entry has the same template
			outputStream = ctx.openFileOutput(patientName, Context.MODE_APPEND);
			//Firstly the title: "Title: hour/minute/second/day/month/year - procedureName\n"
			title = "Title: Date: " +
					String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "." +
					String.valueOf((c.get(Calendar.MONTH))+1) + "." +
					String.valueOf(c.get(Calendar.YEAR)) + ", " +
					String.valueOf(c.get(Calendar.HOUR)) + ":" +
					String.valueOf(c.get(Calendar.MINUTE)) + ":" +
					String.valueOf(c.get(Calendar.SECOND)) + " - " +
					procedureName + "\n";
			outputStream.write(title.getBytes());
			//Secondly the data for the left ear: "Left ear: x1;y1-x2;y2-x3;y3-...\n"
			outputStream.write("LeftEar: ".getBytes());
			for(int i=0; i<leftEarData.length; i++)
			{
				point = String.valueOf(leftEarData[i].getX()) + ";" + String.valueOf(leftEarData[i].getY()) + "-";
				outputStream.write(point.getBytes());
			}
			outputStream.write("\n".getBytes());
			//Thirdly the data for the right ear: "Right ear: x1;y1-x2;y2-x3;y3-...\n"
			outputStream.write("RightEar: ".getBytes());
			for(int i=0; i<rightEarData.length; i++)
			{
				point = String.valueOf(rightEarData[i].getX()) + ";" + String.valueOf(rightEarData[i].getY()) + "-";
				outputStream.write(point.getBytes());
			}
			outputStream.write("\n".getBytes());
			
			//Fourthly the diagnosis: "Diagnosis: ...\n"
			outputStream.write("Diagnosis: ".getBytes());
			if(procedureName.equals("PTA"))
			{
				String text = bothEarAnalysisPTA(rightEarData, leftEarData);
				outputStream.write(text.getBytes());
			}
			else if(procedureName.equals("UCL"))
			{
				String text = bothEarAnalysisUCL(rightEarData, leftEarData);
				outputStream.write(text.getBytes());
			}
			outputStream.write("\n".getBytes());
			outputStream.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Analyze results and state the diagnosis for PTA procedure.
	 * @param rightEarData results obtained for right ear
	 * @param leftEarData results obtained for left ear
	 * @return final diagnosis of PTA procedure
	 */
	private String bothEarAnalysisUCL(GraphViewData[] rightEarData, GraphViewData[] leftEarData)
	{
		//Analysis separately the data from right and left ear:
		String[] rightEarAnalysis = oneEarAnalysisUCL(rightEarData);
		String[] leftEarAnalysis = oneEarAnalysisUCL(leftEarData);
		String text = ""; //keep the string with diagnosis text
		//If both normal hear:
		if(rightEarAnalysis[0].equals("+") && leftEarAnalysis[0].equals("+"))
		{
			text = commentsUCL[0];
		}//If any of ear has injury:
		else if(rightEarAnalysis[1].equals("+") && leftEarAnalysis[1].equals("+"))
		{
			text = "Both: "+commentsUCL[1];
		}
		else if(rightEarAnalysis[1].equals("+"))
		{
			text = "Right: "+commentsUCL[1];
		}
		else if(leftEarAnalysis[1].equals("+"))
		{
			text = "Left: "+commentsUCL[1];
		}
		return text;
	}
	
	/**
	 * Analyze results for one ear for PTA procedure.
	 * @param rightEarData results obtained for one ear
	 * @return which comment was chosen for PTA procedure
	 */
	private String[] oneEarAnalysisUCL(GraphViewData[] rightEarData)
	{		
		int commentNmb = commentsUCL.length;
		String[] whichComments = new String[commentNmb]; //point which comments to this data
		int dataPointsNmb = rightEarData.length;
		for(int i=0; i<commentNmb; i++)
		{
			whichComments[i] = "";
		}
		
		boolean ifUnder60 = true;
		for(int i=0; i<dataPointsNmb; i++)
		{
			if(90-rightEarData[i].getY() < 60) //if data "under" 60 dB line
			{
				ifUnder60 = false;
			}
		}
		
		if(ifUnder60) //if normal hearing
		{
			whichComments[0] = "+";
		}
		else
		{
			whichComments[1] = "+";
		}
		
		return whichComments;
	}
	
	/**
	 * Analyze results and state the diagnosis for PTA procedure.
	 * @param rightEarData results obtained for right ear
	 * @param leftEarData results obtained for left ear
	 * @return final diagnosis of PTA procedure
	 */
	private String bothEarAnalysisPTA(GraphViewData[] rightEarData, GraphViewData[] leftEarData)
	{
		//Analysis separately the data from right and left ear:
		String[] rightEarAnalysis = oneEarAnalysisPTA(rightEarData);
		String[] leftEarAnalysis = oneEarAnalysisPTA(leftEarData);
		String text = ""; //keep the string with diagnosis text
		//If both normal hear:
		if(rightEarAnalysis[0].equals("+") && leftEarAnalysis[0].equals("+"))
		{
			text = text + commentsPTA[0] + " ";
		}//If any of ear has injury:
		else
		{
			if(rightEarAnalysis[1] != "" || leftEarAnalysis[1] != "")
			{
				//If both has injury:
				if(rightEarAnalysis[1] != "" && leftEarAnalysis[1] != "")
				{
					text = text + commentsPTA[1] + "right: " + rightEarAnalysis[1] + "Hz; left: " + leftEarAnalysis[1] + "Hz. ";
				}//If right has injury:
				else if(rightEarAnalysis[1] != "" && leftEarAnalysis[1] == "")
				{
					text = text + commentsPTA[1] + "right: " + rightEarAnalysis[1] + "Hz. ";
				}//If left has injury:
				else if(rightEarAnalysis[1] == "" && leftEarAnalysis[1] != "")
				{
					text = text + commentsPTA[1] + "left: " + leftEarAnalysis[1] + "Hz. ";
				}
			}
			
			//If other things occur:
			if(!(rightEarAnalysis[0].equals("+") && leftEarAnalysis[0].equals("+")))
			{
				int commentsNmb = commentsPTA.length;
				int whichInRight = -1;
				int whichInLeft = -1;
				for(int i=2; i<commentsNmb; i++) //check what diagnosis was in each ear
				{
					if(rightEarAnalysis[i].equals("+"))
					{
						whichInRight = i;
					}
					
					if(leftEarAnalysis[i].equals("+"))
					{
						whichInLeft = i;
					}
				}
				
				if(whichInRight == whichInLeft && whichInRight != -1) //If both has the same diagnosis
				{
					text = text + commentsPTA[whichInRight]  + "both ears. ";
				}
				else
				{
					//If have different diagnosis:
					if(whichInRight == -1 && whichInLeft != -1)
					{
						text = text + commentsPTA[whichInLeft]  + "left ear. ";
					}
					else if(whichInRight != -1 && whichInLeft == -1)
					{
						text = text + commentsPTA[whichInRight]  + "right ear. ";
					}
					else if(whichInRight != -1 && whichInLeft != -1)
					{
						text = text + commentsPTA[whichInRight]  + "right ear. " + commentsPTA[whichInLeft]  + "left ear. ";
					}
				}
			}
		}
		return text;
	}
	
	/**
	 * Analyze results for one ear for PTA procedure.
	 * @param rightEarData results obtained for one ear
	 * @return which comment was chosen for PTA procedure
	 */
	private String[] oneEarAnalysisPTA(GraphViewData[] rightEarData)
	{		
		int commentNmb = commentsPTA.length;
		String[] whichComments = new String[commentNmb]; //point which comments to this data
		int dataPointsNmb = rightEarData.length;
		for(int i=0; i<commentNmb; i++)
		{
			whichComments[i] = "";
		}
		
		double max=-1000, min=1000;
		int whereMax = -1;
		boolean ifAbove40 = true;
		int howManyRises = 0;
		int howManyBelow60 = 0;
		double sum = 0;
		for(int i=0; i<dataPointsNmb; i++)
		{
			if(90-rightEarData[i].getY() > 40) //if data "below" 40 dB line
			{
				ifAbove40 = false;
			}
			else if(90-rightEarData[i].getY() >= 60) //how many data "below" 60 dB line
			{
				howManyBelow60++;
			}

			if(90-rightEarData[i].getY() > max) //find max value (min in gravity)
			{
				max = 90-rightEarData[i].getY();
				whereMax = i;
			}
			
			if(90-rightEarData[i].getY() < min) //find min value (max in gravity)
			{
				min = 90-rightEarData[i].getY();
			}
			
			if(i>0)
			{
				if((rightEarData[i-1].getY() - rightEarData[i].getY()) >= 0) //find rises and lowers
				{
					howManyRises++;
				}
				sum = sum + (rightEarData[i-1].getY() - rightEarData[i].getY());
			}
		}
		
		int onLeft = 2; //how many on the left of the max dB (min in gravity) check the points
		int onRight = 2; //how many on the left of the max dB (min in gravity) check the points
		boolean ifWasFirst = false;
		int first = 0;
		boolean ifWasLast = false;
		int last = rightEarData.length-1;
		if(max-min < 30 & ifAbove40) //if normal hearing
		{
			whichComments[0] = "+";
		}
		else if(howManyRises >= 2*(rightEarData.length/3) && sum > (rightEarData.length/4)*10) //if aging
		{
			whichComments[2] = "+";
		}
		else if(howManyBelow60 >= 3*(rightEarData.length/4)) //if deaf
		{
			whichComments[3] = "+";
		}
		else if(max-min >= 30 & min < 40) //if injury probability
		{
			if(whereMax < 3) //if there is not enough points on the left of max dB (min in gravity)
			{
				onLeft = whereMax;
			}
			
			if(whereMax > 5) //if there is not enough points on the right of max dB (min in gravity)
			{
				onRight = rightEarData.length - whereMax - 1;
			}
			
			for(int i=whereMax-onLeft; i<=whereMax+onRight; i++) //analyze these points
			{
				if(max-(90-rightEarData[i].getY()) <= 10 && !ifWasFirst)
				{
					first = i;
					ifWasFirst = true;
				}
				else if(ifWasLast && max-(90-rightEarData[i].getY()) <= 10)
				{
					ifWasLast = false;
					last = rightEarData.length-1;
				}
				else if(max-(90-rightEarData[i].getY()) >= 10 && !ifWasLast && ifWasFirst)
				{
					last = i-1;
					ifWasLast = true;
				}
			}
			
			if(last-first <= 3) //if injury
			{
				if(last!=first)
				{
					whichComments[1] = HzValues[first]+"-"+HzValues[last];
				}
				else
				{
					whichComments[1] = String.valueOf(HzValues[first]);
				}
			}//if not then something weird
			else
			{
				whichComments[4] = "+";
			}
		}
		else
		{
			whichComments[4] = "+";
		}
		
		return whichComments;
	}
	
	/**
	 * Get list of patients.
	 * @param ctx application context
	 * @return list of patients
	 */
	public List<String> getPatients(Context ctx)
	{
		List<String> patientNames = new ArrayList<String>();
		String line;
		try
		{
			FileInputStream fis = ctx.openFileInput("patients");
		    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		    if (fis!=null)
		    {                            
		        while ((line = reader.readLine()) != null)
		        {    
		        	patientNames.add(line);
		        }               
		    }       
		    fis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return patientNames;
	}
	
	/**
	 * Get procedures history of chosen patient.
	 * @param ctx application context
	 * @param patientName name of the chosen patient
	 * @return List of procedures titles.
	 */
	public List<String> getHistory(Context ctx, String patientName)
	{
		List<String> procedures = new ArrayList<String>();
		String line;
		try
		{
			FileInputStream fis = ctx.openFileInput(patientName);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		    if (fis!=null)
		    {                            
		        while ((line = reader.readLine()) != null)
		        {
		        	if(line.contains("Title:"))
		        	{
		        		line = line.substring(line.indexOf(":")+2);
			        	procedures.add(line);
		        	}
		        }               
		    }       
		    fis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return procedures;
	}

	/**
	 * Load results of the chosen procedure of the chosen patient.
	 * @param ctx application context
	 * @param patientName name of the chosen patient
	 * @param procedureName title of the chosen procedure
	 */
	public void loadEarData(Context ctx, String patientName, String procedureName)
	{
		earData = new ArrayList<double[][]>();
		double[][] tempEar;
		String line;
		int pointNmb;
		String[] splitter, splitter2;
		try
		{
			FileInputStream fis = ctx.openFileInput(patientName);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		    if (fis!=null)
		    {                            
		        while ((line = reader.readLine()) != null)
		        {
		        	if(line.contains(procedureName)) //if the chosen title was found
		        	{
		        		//left ear first:
		        		line = reader.readLine();
		        		line = line.substring(line.indexOf(":")+2);
		        		//split by data points:
		        		splitter = line.split("-");
		        		pointNmb = splitter.length;
		        		tempEar = new double[pointNmb][2];
		        		for(int i=0; i<pointNmb; i++)
		        		{
		        			//split by x and y:
		        			splitter2 = splitter[i].split(";");
		        			tempEar[i][0] = Double.parseDouble(splitter2[0]);
		        			tempEar[i][1] = Double.parseDouble(splitter2[1]);
		        		}
		        		earData.add(tempEar);
		        		
		        		//right ear second:
		        		line = reader.readLine();
		        		line = line.substring(line.indexOf(":")+2);
		        		//split by data points:
		        		splitter = line.split("-");
		        		pointNmb = splitter.length;
		        		tempEar = new double[pointNmb][2];
		        		for(int i=0; i<pointNmb; i++)
		        		{
		        			//split by x and y:
		        			splitter2 = splitter[i].split(";");
		        			tempEar[i][0] = Double.parseDouble(splitter2[0]);
		        			tempEar[i][1] = Double.parseDouble(splitter2[1]);
		        		}
		        		earData.add(tempEar);
		        		
		        		//diagnosis:
		        		line = reader.readLine();
		        		diagnosis = line.substring(line.indexOf(":")+2);
		        		break;	
		        	}
		        }               
		    }       
		    fis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Get results of both ears of the chosen procedure of the chosen patient.
	 * @return List of data for left and right ears
	 */
	public List<double[][]> getEarData()
	{
		return earData;
	}
	
	/**
	 * Get diagnosis of the chosen procedure of the chosen patient.
	 * @return Diagnosis of the chosen procedure of the chosen patient.
	 */
	public String getDiagnosis()
	{
		return diagnosis;
	}
}
