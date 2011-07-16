package com.blazingcontacts.BlazingContacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {

	final String TAG = "Main Activity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);

		// Set the buttons so that, when clicked, the corresponding activity is
		// opened

		Button startSession = (Button) findViewById(R.id.startButton);
		Button joinSession = (Button) findViewById(R.id.joinButton);

		startSession.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						StartActivity.class);
				startActivity(i);
			}
		});

		joinSession.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						JoinActivity.class);
				startActivity(i);
			}
		});
	}
}