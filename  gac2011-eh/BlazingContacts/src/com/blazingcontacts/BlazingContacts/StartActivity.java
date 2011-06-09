package com.blazingcontacts.BlazingContacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends GroupActivity {

	public static final String LIFETIME_MILLIS = "Lifetime in milliseconds";
	public static final String MAX_PEOPLE = "Maximum number of people";

	Button start_begin;
	String array_spinner[] = { "sec", "min", "hour" };
	String currentChoice = "sec";

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.secondview);
		Spinner s = (Spinner) findViewById(R.id.timeSpinner);
		start_begin = (Button) findViewById(R.id.start_begin);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, array_spinner);
		s.setAdapter(adapter);

		start_begin.setOnClickListener(new OnClickListener() {
			/*
			 * The method that is called when the start button is clicked. Not
			 * yet fully implemented. Should close the current activity and
			 * start a ContactDownloadService.
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				Intent i = new Intent(StartActivity.this,
						ContactDownloadService.class);
				i.putExtra(GROUP_TYPE, TYPE_START);
				i.putExtra(GROUP_NAME,
						((EditText) findViewById(R.id.groupName)).getText()
								.toString());
				String name = ((TextView) findViewById(R.id.nameCB)).getText()
						.toString();
				String phone = ((TextView) findViewById(R.id.phoneNumberCB))
						.getText().toString();
				String email = ((TextView) findViewById(R.id.emailCB))
						.getText().toString();
				i.putExtra(MY_NAME, name);
				i.putExtra(MY_PHONE_NUMBER, phone);
				i.putExtra(MY_EMAIL, email);
				try {
					int time = Integer
							.parseInt(((EditText) findViewById(R.id.timeToWaitFor))
									.getText().toString());
					String units = (String) ((Spinner) findViewById(R.id.timeSpinner))
							.getSelectedItem();
					long milliseconds = 0;
					if (units.equals("sec")) {
						milliseconds = time * 1000;
					} else if (units.equals("min")) {
						milliseconds = time * 60000;
					} else {
						milliseconds = time * 3600000;
					}
					i.putExtra(LIFETIME_MILLIS, milliseconds);
					String numPeople = ((EditText) findViewById(R.id.peopleToWaitFor))
							.getText().toString();
					int numberOfPeople = WebWrapper.NO_GROUP_MAX;
					if (!numPeople.equals("")) {
						numberOfPeople = Integer.parseInt(numPeople);
					}
					i.putExtra(MAX_PEOPLE, numberOfPeople);
				} catch (NumberFormatException e) {
					Toast.makeText(StartActivity.this,
							"Time and Number of People fields must be numbers!",
							Toast.LENGTH_LONG).show();
					return;
				}
				startService(i);
				finish();
			}
		});
	}

	/*
	 * A method that updates the UI after receiving the Contact's name, phone
	 * number and email from onActivityResult in GroupActivity.
	 * 
	 * @see
	 * com.blazingcontacts.BlazingContacts.GroupActivity#populateContact(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	protected void populateContact(String name, String phoneNumber, String email) {
		TextView t = (TextView) findViewById(R.id.nameCB);
		((TextView) findViewById(R.id.nameCB)).setText(name);
		((TextView) findViewById(R.id.phoneNumberCB)).setText(phoneNumber);
		((TextView) findViewById(R.id.emailCB)).setText(email);
	}
}