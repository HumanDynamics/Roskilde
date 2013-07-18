package com.crowdsos.roskilde;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class LoadIncidentsTask extends AsyncTask<String, Void, List<Incident>> {

	private Context mContext;
	private IncidentListAdapter mListAdapter;
	
	public LoadIncidentsTask(Context context, IncidentListAdapter adapter) {
		mContext = context;
		mListAdapter = adapter;
	}
	
	@Override
	protected List<Incident> doInBackground(String... params) {
		try {
			PDSClient pds = new PDSClient(mContext);
			return pds.getIncidents();
		} catch (Exception ex) {
			Log.e("LoadIncidentsTask", ex.getMessage());
		}
		
		return new ArrayList<Incident>();
	}
	
	@Override
	protected void onPostExecute(List<Incident> result) {
		mListAdapter.updateIncidents(result);
	}

}
