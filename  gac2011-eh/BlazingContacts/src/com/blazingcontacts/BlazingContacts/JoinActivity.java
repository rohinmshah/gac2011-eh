package com.blazingcontacts.BlazingContacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class JoinActivity extends GroupActivity {

	Button join_begin;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joinview);

		join_begin = (Button) findViewById(R.id.join_begin);
		join_begin.setOnClickListener(new OnClickListener() {
			/*
			 * The method that is called when the join button is clicked. Not
			 * yet fully implemented. Should close the current activity and
			 * start a ContactDownloadService.
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				Intent i = new Intent(JoinActivity.this,
						ContactDownloadService.class);
				i.putExtra(GROUP_TYPE, TYPE_JOIN);
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
		((TextView) findViewById(R.id.nameCB)).setText(name);
		((TextView) findViewById(R.id.phoneNumberCB)).setText(phoneNumber);
		((TextView) findViewById(R.id.emailCB)).setText(email);
	}
}