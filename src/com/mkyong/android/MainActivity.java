package com.mkyong.android;

//import statements
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * The main class for the app, "Safe." 
 * The user previously selects one of 2 options, and depending on which one is selected,
 * a call is triggered to that number when the button on the wearable is pressed.
 * @author Nikita Khan and Hashma Shahid
 *
 */
public class MainActivity extends Activity 
{

	final Context context = this;

	//call hotline radiobutton
	private RadioButton buttonHotline;

	//call a friend radiobutton
	private RadioButton buttonFriend;

	//app title
	private TextView appTitle;

	//enter friend number input box
	private EditText friendNumberInput;
	
	//on/off switch
	private ToggleButton onOffButton;
	
	//JSON reader to read the wearable url
	private JsonReader task;

	/**
	 * Method that initializes the display and the app
	 * @param Bundle -
	 */
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//initialize all the visual components
		buttonHotline = (RadioButton) findViewById(R.id.button);
		buttonFriend = (RadioButton) findViewById(R.id.friendButton);
		appTitle = (TextView) findViewById(R.id.title);
		friendNumberInput = (EditText) findViewById(R.id.editText1);
		onOffButton = (ToggleButton) findViewById(R.id.toggleButton1);
		
		//set on/off button to be ON initially
		onOffButton.setSelected(true);

		//make number input field invisible initially
		friendNumberInput.setVisibility(View.INVISIBLE);

		//change fonts of the radiobuttons and title
		buttonHotline.setTypeface(Typeface.SERIF, Typeface.BOLD);
		buttonFriend.setTypeface(Typeface.SERIF, Typeface.BOLD);
		appTitle.setTypeface(Typeface.SERIF, Typeface.BOLD);

		// add PhoneStateListener
		PhoneCallListener phoneListener = new PhoneCallListener();
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
		
		//create a new JsonReader which will read the webpage
		task = new JsonReader();
		
		//invoke method that will refresh webpage and trigger the call
		callNumber();
		
		//add button listener for on/off button
		onOffButton.setOnClickListener(new OnClickListener() 
		{

			/**
			 * Method that hides all the buttons if "OFF" is selected
			 * and makes all the buttons visible if "ON" is selected
			 */
			@Override
			public void onClick(View arg0) 
			{
				if(!onOffButton.isChecked())
				{
					//hide everything
					friendNumberInput.setVisibility(View.INVISIBLE);
					buttonHotline.setVisibility(View.INVISIBLE);
					buttonFriend.setVisibility(View.INVISIBLE);
				}
				
				else
				{
					//show everything
					friendNumberInput.setVisibility(View.VISIBLE);
					buttonHotline.setVisibility(View.VISIBLE);
					buttonFriend.setVisibility(View.VISIBLE);
				}

			}

		});

		// add button listener for hotline radio button
		buttonHotline.setOnClickListener(new OnClickListener() 
		{

			/**
			 * Method that executes when the "Call hotline" Radiobutton option is clicked
			 */
			@Override
			public void onClick(View arg0) 
			{
				//make sure input field is not visible
				friendNumberInput.setVisibility(View.INVISIBLE);
			}

		});

		// add button listener to friend radio button
		buttonFriend.setOnClickListener(new OnClickListener() 
		{

			/**
			 * Method that executes when the "Call a friend" Radiobutton option is clicked
			 */
			@Override
			public void onClick(View arg0) 
			{				
				//now make number input field visible
				friendNumberInput.setVisibility(View.VISIBLE);
			}

		});

	}
	
	/**
	 * Method to refresh the webpage every 2 seconds and determine whether button is pressed or not.
	 * If the button is pressed, trigger the call.
	 * Else, don't do anything.
	 */
	public void callNumber()
	{
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		
	    Runnable toRun = new Runnable() 
	    {
	        public void run() 
	        {
				//execute the task
				try 
				{

					//execute the task in background
					JSONObject result = task.execute().get();
					
					friendNumberInput.setText("hello");
					
				    //call helper function to trigger the call
				    callNumber2(result);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				} 
				catch (ExecutionException e) 
				{
					e.printStackTrace();
				}
	        }
	    };

	    ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(toRun, 2, 2, TimeUnit.SECONDS);
	}
	
	/**
	 * A method that triggers the corresponding call depending on which option is selected
	 * Helper method for callNumber function
	 * @param x is current state of button
	 */
	public void callNumber2(JSONObject x)
	{
		//if the button is pressed and the friend option is selected, trigger a call to the number that is inputted
		if (x.toString().equals("1") && buttonFriend.isChecked())
		{
			//get the number that is entered
			String y = friendNumberInput.getText().toString();
			String toCall = "tel:" + y;

			//make sure the input box is not empty
			if(!toCall.equals("tel:"))
			{
				//trigger a call to that number
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse(toCall));
				startActivity(callIntent);
			}
		}
		
		//if the button is pressed and the hotline option is selected, trigger a call to the hotline number
		else if (x.toString().equals("1") && buttonHotline.isChecked())
		{
			//trigger a call to the 24/7 domestic violence hotline number
			String toCall = "18007997233";
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse(toCall));
			startActivity(callIntent);
		}
	}

	/**
	 * A class for the phone call triggering
	 * @author Nikita Khan and Hashma Shahid
	 */
	private class PhoneCallListener extends PhoneStateListener 
	{
		private boolean isPhoneCalling = false;

		String LOG_TAG = "LOGGING 123";

		@Override
		public void onCallStateChanged(int state, String incomingNumber) 
		{

			if (TelephonyManager.CALL_STATE_RINGING == state) 
			{
				// phone ringing
				Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
			}

			if (TelephonyManager.CALL_STATE_OFFHOOK == state) 
			{
				// active
				Log.i(LOG_TAG, "OFFHOOK");

				isPhoneCalling = true;
			}

			if (TelephonyManager.CALL_STATE_IDLE == state) 
			{
				// run when class initial and phone call ended, need detect flag
				// from CALL_STATE_OFFHOOK
				Log.i(LOG_TAG, "IDLE");

				if (isPhoneCalling) 
				{

					Log.i(LOG_TAG, "restart app");

					// restart app
					Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
					
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					
					startActivity(i);

					isPhoneCalling = false;
				}

			}
		}
	}

}
