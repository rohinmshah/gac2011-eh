package com.blazingcontacts.BlazingContacts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends GroupActivity {

	public static final String LIFETIME_MILLIS = "Lifetime in milliseconds";
	public static final String MAX_PEOPLE = "Maximum number of people";
	public static final int MAX_TIME_MILLIS = 21600000;

	private TextView start_group_name;
	private TextView start_name;
	private CheckBox start_phone;
	private CheckBox start_email;
	private TextView start_time;
	private Spinner start_time_units;
	private TextView start_max_people;
	private Button start_begin;
	private boolean startClicked;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		String array_spinner[] = { "sec", "min", "hour" };

		setContentView(R.layout.secondview);

		start_group_name = (TextView) findViewById(R.id.groupName);
		start_name = (TextView) findViewById(R.id.nameCB);
		start_phone = (CheckBox) findViewById(R.id.phoneNumberCB);
		start_email = (CheckBox) findViewById(R.id.emailCB);
		start_time = (TextView) findViewById(R.id.timeToWaitFor);
		start_max_people = (TextView) findViewById(R.id.peopleToWaitFor);
		start_time_units = (Spinner) findViewById(R.id.timeSpinner);
		start_begin = (Button) findViewById(R.id.start_begin);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, array_spinner);
		start_time_units.setAdapter(adapter);

		start_begin.setOnClickListener(new OnClickListener() {
			/*
			 * The method that is called when the start button is clicked. Not
			 * yet fully implemented. Should close the current activity and
			 * start a ContactDownloadService.
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				startClicked = true;
				SharedPreferences joinData = getSharedPreferences(
						getString(R.string.join_preference), MODE_PRIVATE);
				Editor e = joinData.edit();
				e.putString(getString(R.string.join_contact_name), start_name
						.getText().toString());
				e.putBoolean(getString(R.string.join_phone_checked),
						start_phone.isChecked());
				e.putString(getString(R.string.join_contact_phone), start_phone
						.getText().toString());
				e.putBoolean(getString(R.string.join_email_checked),
						start_email.isChecked());
				e.putString(getString(R.string.join_contact_email), start_email
						.getText().toString());
				e.commit();

				Intent i = new Intent(StartActivity.this,
						ContactDownloadService.class);
				i.putExtra(GROUP_TYPE, TYPE_START);
				i.putExtra(GROUP_NAME, start_group_name.getText().toString());
				String name = start_name.getText().toString();
				String phone = "";
				if (start_phone.isChecked()) {
					phone = start_phone.getText().toString();
				}
				String email = "";
				if (start_email.isChecked()) {
					email = start_email.getText().toString();
				}
				i.putExtra(MY_NAME, name);
				i.putExtra(MY_PHONE_NUMBER, phone);
				i.putExtra(MY_EMAIL, email);
				try {
					int time = Integer
							.parseInt(start_time.getText().toString());
					String units = (String) start_time_units.getSelectedItem();
					long milliseconds = 0;
					if (units.equals("sec")) {
						milliseconds = time * 1000;
					} else if (units.equals("min")) {
						milliseconds = time * 60000;
					} else {
						milliseconds = time * 3600000;
					}
					if (milliseconds > MAX_TIME_MILLIS) {
						Toast.makeText(StartActivity.this,
								"The time cannot be larger than 6 hours.",
								Toast.LENGTH_LONG).show();
						return;
					}
					i.putExtra(LIFETIME_MILLIS, milliseconds);
					String numPeople = start_max_people.getText().toString();
					int numberOfPeople = WebWrapper.NO_GROUP_MAX;
					if (!numPeople.equals("")) {
						numberOfPeople = Integer.parseInt(numPeople);
					}
					i.putExtra(MAX_PEOPLE, numberOfPeople);
				} catch (NumberFormatException nfe) {
					Toast.makeText(
							StartActivity.this,
							"Time and Number of People fields must be numbers!",
							Toast.LENGTH_LONG).show();
					return;
				}
				startService(i);
				finish();
			}
		});

		SharedPreferences data = getSharedPreferences(
				getString(R.string.start_preference), MODE_PRIVATE);
		start_group_name.setText(data.getString(
				getString(R.string.start_group_name), ""));
		start_time.setText(data.getString(
				getString(R.string.start_time_to_wait), "2"));
		start_time_units.setSelection(data.getInt(
				getString(R.string.start_time_units), 1));
		start_max_people.setText(data.getString(
				getString(R.string.start_max_people), ""));

		start_name.setText(data.getString(
				getString(R.string.start_contact_name), ""));
		start_phone.setChecked(data.getBoolean(
				getString(R.string.start_phone_checked), true));
		start_phone.setText(data.getString(
				getString(R.string.start_contact_phone), ""));
		start_email.setChecked(data.getBoolean(
				getString(R.string.start_email_checked), true));
		start_email.setText(data.getString(
				getString(R.string.start_contact_email), ""));
		startClicked = false;
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences data = getSharedPreferences(
				getString(R.string.start_preference), MODE_PRIVATE);
		Editor e = data.edit();
		e.clear();
		e.putString(getString(R.string.start_contact_name), start_name
				.getText().toString());
		e.putBoolean(getString(R.string.start_phone_checked),
				start_phone.isChecked());
		e.putString(getString(R.string.start_contact_phone), start_phone
				.getText().toString());
		e.putBoolean(getString(R.string.start_email_checked),
				start_email.isChecked());
		e.putString(getString(R.string.start_contact_email), start_email
				.getText().toString());
		if (!startClicked) {
			e.putString(getString(R.string.start_group_name), start_group_name
					.getText().toString());
			e.putString(getString(R.string.start_time_to_wait), start_time
					.getText().toString());
			e.putInt(getString(R.string.start_time_units),
					start_time_units.getSelectedItemPosition());
			e.putString(getString(R.string.start_max_people), start_max_people
					.getText().toString());
		}
		e.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (start_name.getText().toString().equals("")) {
			start_begin.setVisibility(Button.INVISIBLE);
		} else {
			start_begin.setVisibility(Button.VISIBLE);
		}
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
		start_name.setText(name);
		start_phone.setText(phoneNumber);
		start_email.setText(email);
		start_begin.setVisibility(Button.VISIBLE);
	}
}