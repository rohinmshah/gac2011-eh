package com.blazingcontacts.BlazingContacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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