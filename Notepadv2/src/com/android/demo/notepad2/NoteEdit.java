package com.android.demo.notepad2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEdit extends Activity {
	
	EditText mTitleText, mBodyText;
	Long mRowId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_edit);
		setTitle(R.string.edit_note);
		
		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		
		// Set button callback
		Button confirmButton = (Button) findViewById(R.id.confirm);
		confirmButton.setOnClickListener(new View.OnClickListener() {

		    public void onClick(View view) {
		    	Bundle bundle = new Bundle();

		    	bundle.putString(NotesDbAdapter.KEY_TITLE, mTitleText.getText().toString());
		    	bundle.putString(NotesDbAdapter.KEY_BODY, mBodyText.getText().toString());
		    	if (mRowId != null) {
		    	    bundle.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
		    	}
		    	
		    	Intent mIntent = new Intent();
		    	mIntent.putExtras(bundle);
		    	setResult(RESULT_OK, mIntent);
		    	finish();
		    }

		});
		
		// Get bundle info and updating this view
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    String title = extras.getString(NotesDbAdapter.KEY_TITLE);
		    String body = extras.getString(NotesDbAdapter.KEY_BODY);
		    mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);

		    if (title != null) {
		        mTitleText.setText(title);
		    }
		    if (body != null) {
		        mBodyText.setText(body);
		    }
		}
	}

}
