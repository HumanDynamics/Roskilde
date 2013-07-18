package com.crowdsos.roskilde;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import edu.mit.media.openpds.client.NotificationService;
import edu.mit.media.openpds.client.PersonalDataStore;

public class GCMIntentService extends GCMBaseIntentService {	
	public GCMIntentService() {
		super();
	}
	
	@Override
	protected String[] getSenderIds(Context context) {
		return new String[] {getString(R.string.gcm_sender_id)};
	}

	@Override
	protected void onRegistered(final Context context, final String regId) {
		Log.v(getClass().getName(), "Device registered: regId = " + regId);

		try {
			PersonalDataStore pds = new PersonalDataStore(context);
			pds.registerGCMDevice(regId);
		} catch (Exception ex) {
			Log.e("GCMIntentService", ex.getMessage());
		}
	}
	
	@Override
	protected void onMessage(Context context, Intent intent) {
		if (intent.hasExtra("action")) {
			String action = intent.getStringExtra("action");
			
			if (action.equalsIgnoreCase("notify")) {
				Intent notificationServiceIntent = new Intent(context, NotificationService.class);
				context.startService(notificationServiceIntent);
			} else if (action.equalsIgnoreCase("update")) {
				// Run pipeline update here...
			} else if (action.equalsIgnoreCase("save")) {
				// Run pipeline save here...
			}
		}		
	}
	
	@Override
	protected void onError(Context context, String errorId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		// TODO Auto-generated method stub
		
	}


}
