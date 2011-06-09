package com.blazingcontacts.BlazingContacts;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
	Button startSession;
	Button joinSession;
	final String TAG = "Main Activity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        startSession = (Button) findViewById(R.id.startButton);
        joinSession = (Button) findViewById(R.id.joinButton);
        
        startSession.setOnClickListener(new OnClickListener() {
	        public void onClick (View v) {
	        	Intent i = new Intent (getApplicationContext(), StartActivity.class);
	        	startActivity(i);
        }
        });
        joinSession.setOnClickListener(new OnClickListener() {
	        public void onClick (View v) {
	        	Intent i = new Intent (getApplicationContext(), JoinActivity.class);
	        	startActivity(i);
        }
        });
    }
}