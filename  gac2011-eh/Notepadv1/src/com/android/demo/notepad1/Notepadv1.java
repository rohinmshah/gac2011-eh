/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.demo.notepad1;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class Notepadv1 extends ListActivity {
	private int mNoteNumber = 1;
	private NotesDbAdapter mDbHelper;

	public static final int INSERT_ID = Menu.FIRST;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notepad_list);
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		fillData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean res = super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
		return res;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createNote();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void createNote() {
		String noteName = "Note " + mNoteNumber++;
		mDbHelper.createNote(noteName, "");
		fillData();
	}

	public void fillData() {
		Cursor c = mDbHelper.fetchAllNotes();
		startManagingCursor(c);

		String[] from = { NotesDbAdapter.KEY_TITLE };
		int[] to = { R.id.text1 };

		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.notes_row, c, from, to);
		setListAdapter(notes);
	}
}
