package com.blazingcontacts.BlazingContacts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ContactDownloadService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		this.stopSelf();
	}

}
