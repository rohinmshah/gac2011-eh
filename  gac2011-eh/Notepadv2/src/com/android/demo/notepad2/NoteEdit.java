package com.android.demo.notepad2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEdit extends Activity {

	private EditText mTitleText;
	private EditText mBodyText;
	private Long mRowId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_edit);
		setTitle(R.string.edit_note);
		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		Button confirmButton = (Button) findViewById(R.id.confirm);
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String title = extras.getString(NotesDbAdapter.KEY_TITLE);
			if (title != null) {
				mTitleText.setText(title);
			}

			String body = extras.getString(NotesDbAdapter.KEY_BODY);
			if (body != null) {
				mBodyText.setText(body);
			}

			mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
		}

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Bundle bundle = new Bundle();
				if (mRowId != null) {
					bundle.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
				}
				bundle.putString(NotesDbAdapter.KEY_TITLE, mTitleText.getText()
						.toString());
				bundle.putString(NotesDbAdapter.KEY_BODY, mBodyText.getText()
						.toString());

				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});

	}
}
