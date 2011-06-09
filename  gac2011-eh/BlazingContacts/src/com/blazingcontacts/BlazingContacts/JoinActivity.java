package com.blazingcontacts.BlazingContacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class JoinActivity extends Activity {
    /** Called when the activity is first created. */
	Button join_begin;
	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.joinview);
	        
	        join_begin = (Button) findViewById(R.id.join_begin);
	        join_begin.setOnClickListener(new OnClickListener() {
		        public void onClick (View v) {
		        	Intent i = new Intent (getApplicationContext(), MainActivity.class);
		        	startActivity(i);
	        }
	        });
	    } 
}