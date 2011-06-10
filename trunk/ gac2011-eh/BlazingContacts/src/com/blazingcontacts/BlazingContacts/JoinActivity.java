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

		// Remove titlebar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.joinview);

		join_begin = (Button) findViewById(R.id.join_begin);
		join_group_name = (TextView) findViewById(R.id.groupName);
		join_name = (TextView) findViewById(R.id.nameCB);
		join_phone = (CheckBox) findViewById(R.id.phoneNumberCB);
		join_email = (CheckBox) findViewById(R.id.emailCB);

		join_begin.setOnClickListener(new OnClickListener() {
			/*
			 * The method that is called when the join button is clicked. Not
			 * yet fully implemented. Should close the current activity and
			 * start a ContactDownloadService.
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				joinClicked = true;
				Intent i = new Intent(JoinActivity.this,
						ContactDownloadService.class);
				i.putExtra(GROUP_TYPE, TYPE_JOIN);
				i.putExtra(GROUP_NAME, join_group_name.getText().toString());
				String name = join_name.getText().toString();
				String phone = "";
				CheckBox phoneCB = (CheckBox) findViewById(R.id.phoneNumberCB);
				if (phoneCB.isChecked()) {
					phone = phoneCB.getText().toString();
				}
				String email = "";
				CheckBox emailCB = (CheckBox) findViewById(R.id.emailCB);
				if (emailCB.isChecked()) {
					email = emailCB.getText().toString();
				}
				i.putExtra(MY_NAME, name);
				i.putExtra(MY_PHONE_NUMBER, phone);
				i.putExtra(MY_EMAIL, email);
				startService(i);
				finish();
			}
		});

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

	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences data = getSharedPreferences(
				getString(R.string.join_preference), MODE_PRIVATE);
		Editor e = data.edit();
		e.clear();
		CheckBox phoneCheck = (CheckBox) findViewById(R.id.phoneNumberCB);
		CheckBox emailCheck = (CheckBox) findViewById(R.id.emailCB);
		e.putString(getString(R.string.join_contact_name),
				((TextView) findViewById(R.id.nameCB)).getText().toString());
		e.putBoolean(getString(R.string.join_phone_checked),
				phoneCheck.isChecked());
		e.putString(getString(R.string.join_contact_phone), phoneCheck
				.getText().toString());
		e.putBoolean(getString(R.string.join_email_checked),
				emailCheck.isChecked());
		e.putString(getString(R.string.join_contact_email), emailCheck
				.getText().toString());
		if (!joinClicked) {
			e.putString(getString(R.string.join_group_name),
					((TextView) findViewById(R.id.groupName)).getText()
							.toString());
		}
		e.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		TextView name = (TextView) findViewById(R.id.nameCB);
		Button joinButton = (Button) findViewById(R.id.join_begin);
		if (name.getText().toString().equals("")) {
			joinButton.setVisibility(Button.INVISIBLE);
		} else {
			joinButton.setVisibility(Button.VISIBLE);
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
		((TextView) findViewById(R.id.nameCB)).setText(name);
		((CheckBox) findViewById(R.id.phoneNumberCB)).setText(phoneNumber);
		((CheckBox) findViewById(R.id.emailCB)).setText(email);
		((Button) findViewById(R.id.join_begin)).setVisibility(Button.VISIBLE);
	}
}