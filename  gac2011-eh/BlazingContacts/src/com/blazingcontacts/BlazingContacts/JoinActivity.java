package com.blazingcontacts.BlazingContacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class JoinActivity extends GroupActivity {
	/** Called when the activity is first created. */
	Button join_begin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joinview);

		join_begin = (Button) findViewById(R.id.join_begin);
		join_begin.setOnClickListener(new OnClickListener() {
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