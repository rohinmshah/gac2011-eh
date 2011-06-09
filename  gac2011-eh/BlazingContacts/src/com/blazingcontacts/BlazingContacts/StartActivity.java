package com.blazingcontacts.BlazingContacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class StartActivity extends GroupActivity {
	/** Called when the activity is first created. */
	Button start_begin;
	String array_spinner[] = { "sec", "min", "hour" };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.secondview);
		Spinner s = (Spinner) findViewById(R.id.timeSpinner);
		start_begin = (Button) findViewById(R.id.start_begin);

		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, array_spinner);
		s.setAdapter(adapter);

		start_begin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	protected void populateContact(String name, String phoneNumber, String email) {
		((TextView) findViewById(R.id.nameCB)).setText(name);
		((TextView) findViewById(R.id.phoneNumberCB)).setText(phoneNumber);
		((TextView) findViewById(R.id.emailCB)).setText(email);
	}
}