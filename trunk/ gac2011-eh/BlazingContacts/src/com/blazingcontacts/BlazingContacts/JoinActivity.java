package com.blazingcontacts.BlazingContacts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class JoinActivity extends GroupActivity {

	private TextView join_group_name;
	private TextView join_name;
	private CheckBox join_phone;
	private CheckBox join_email;
	private Button join_begin;
	private boolean joinClicked;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.joinview);

		// Get the various views in this activity
		join_begin = (Button) findViewById(R.id.join_begin);
		join_group_name = (TextView) findViewById(R.id.groupName);
		join_name = (TextView) findViewById(R.id.nameCB);
		join_phone = (CheckBox) findViewById(R.id.phoneNumberCB);
		join_email = (CheckBox) findViewById(R.id.emailCB);

		// Initialize the values for each of the views to whatever was held
		// previously, or the default if there was no previous value.
		SharedPreferences data = getSharedPreferences(
				getString(R.string.join_preference), MODE_PRIVATE);
		join_group_name.setText(data.getString(
				getString(R.string.join_group_name), ""));
		join_name.setText(data.getString(getString(R.string.join_contact_name),
				""));
		join_phone.setChecked(data.getBoolean(
				getString(R.string.join_phone_checked), true));
		join_phone.setText(data.getString(
				getString(R.string.join_contact_phone), ""));
		join_email.setChecked(data.getBoolean(
				getString(R.string.join_email_checked), true));
		join_email.setText(data.getString(
				getString(R.string.join_contact_email), ""));
		joinClicked = false;

		// Create the method that will be called when the join button is
		// clicked.
		join_begin.setOnClickListener(new OnClickListener() {
			/*
			 * The method that is called when the join button is clicked. Closes
			 * the current activity and starts a ContactDownloadService.
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				joinClicked = true;

				// TO DO: Put the data into the SharedPreferences startData (see
				// corresponding method in StartActivity. Better would be to
				// redesign the whole thing - see readme.

				// Create an intent to start the service that will join the
				// group and download the contacts, and put in the necessary
				// information.
				Intent i = new Intent(JoinActivity.this,
						ContactDownloadService.class);
				i.putExtra(GROUP_TYPE, TYPE_JOIN);
				i.putExtra(GROUP_NAME, join_group_name.getText().toString());
				String name = join_name.getText().toString();
				String phone = "";
				if (join_phone.isChecked()) {
					phone = join_phone.getText().toString();
				}
				String email = "";
				if (join_email.isChecked()) {
					email = join_email.getText().toString();
				}
				i.putExtra(MY_NAME, name);
				i.putExtra(MY_PHONE_NUMBER, phone);
				i.putExtra(MY_EMAIL, email);
				startService(i);
				// Close the current activity.
				finish();
			}
		});

	}

	@Override
	public void onPause() {
		super.onPause();

		// Save the data that has been put into the views.
		SharedPreferences data = getSharedPreferences(
				getString(R.string.join_preference), MODE_PRIVATE);
		Editor e = data.edit();
		e.clear();
		e.putString(getString(R.string.join_contact_name), join_name.getText()
				.toString());
		e.putBoolean(getString(R.string.join_phone_checked),
				join_phone.isChecked());
		e.putString(getString(R.string.join_contact_phone), join_phone
				.getText().toString());
		e.putBoolean(getString(R.string.join_email_checked),
				join_email.isChecked());
		e.putString(getString(R.string.join_contact_email), join_email
				.getText().toString());
		if (!joinClicked) {
			e.putString(getString(R.string.join_group_name), join_group_name
					.getText().toString());
		}
		e.commit();
	}

	@Override
	public void onResume() {
		// All of the fields are filled in by the call to super, so the only
		// requirement is to set the visibility of the join button.
		super.onResume();
		if (join_name.getText().toString().equals("")) {
			join_begin.setVisibility(Button.INVISIBLE);
		} else {
			join_begin.setVisibility(Button.VISIBLE);
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
		join_name.setText(name);
		join_phone.setText(phoneNumber);
		join_email.setText(email);
		join_begin.setVisibility(Button.VISIBLE);
	}
}