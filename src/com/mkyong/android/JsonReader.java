package com.mkyong.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

/**
 * Helper class for the main class that reads the wearable webpage and determine if button is pressed or not
 * @author Nikita Khan and Hashma Shahid
 */
public class JsonReader extends AsyncTask<String, String, JSONObject> 
{

	/**
	 * Helper method for the doInBackground method
	 * Reads the webpage by appending Strings
	 * @param rd -
	 * @return
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException 
	{
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}



	/**
	 * Method part of ASyncTask that will do the webpage reading as a separate thread
	 */
	@Override
	protected JSONObject doInBackground(String... arg0) 
	{
		//this is the website of the wearable
		String url = "http://ashleydeflumere.com";
		
		//create an input stream
		InputStream is = null;
		
		try 
		{
			//open the stream
			is = new URL(url).openStream();
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		try 
		{
			//create a buffered reader to the read from the webpage
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			
			//call readAll method
			String jsonText = readAll(rd);
			
			//find index of "Button", and then check if there is a "1" or "0" after it
			int buttonStringIndex = jsonText.indexOf("Button");
			Character buttonState = jsonText.charAt(buttonStringIndex + 2);
			String buttonStateString = buttonState.toString();
			
			//create a JOSNObject
		    JSONObject json = new JSONObject(buttonStateString);
		    
		    return json;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		} finally 
		{
			try 
			{
				is.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

		return null;
	}
}