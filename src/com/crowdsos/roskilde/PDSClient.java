package com.crowdsos.roskilde;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import edu.mit.media.openpds.client.PersonalDataStore;
import edu.mit.media.openpds.client.PreferencesWrapper;

public class PDSClient extends PersonalDataStore {

	public static String INCIDENT_API_URL = "/api/personal_data/incident/";
	public static String RECENT_INCIDENTS_API_URL = "/api/personal_data/answerlist/?key=RecentIncidents";
	private Context mContext;
	
	public PDSClient(Context context) throws Exception {
		super(context);
		mContext = context;
	}
	
	public List<Incident> getIncidents() {
		HttpGet getIncidentsRequest = new HttpGet(buildAbsoluteApiUrl(RECENT_INCIDENTS_API_URL));
		getIncidentsRequest.addHeader("Content-Type", "application/json");		
		HttpClient client = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = null;
		JsonParser parser = new JsonParser();
		List<Incident> incidentObjects = new ArrayList<Incident>();
		
		try {
			responseBody = client.execute(getIncidentsRequest, responseHandler);
		} catch (ClientProtocolException e) {
	        client.getConnectionManager().shutdown();  
			return incidentObjects;
		} catch (IOException e) {
	        client.getConnectionManager().shutdown();  
			return incidentObjects;
		}
		
		try {
			JsonObject jsonResponse = parser.parse(responseBody).getAsJsonObject();
			
			if (jsonResponse.has("objects") && jsonResponse.getAsJsonArray("objects").size() > 0) {
				JsonObject incidentsObject = jsonResponse.getAsJsonArray("objects").get(0).getAsJsonObject();
				IncidentFactory incidentFactory = new IncidentFactory(mContext);
				for (JsonElement incidentElement : incidentsObject.getAsJsonArray("value")) {
					Incident incident = incidentFactory.getIncident(incidentElement.getAsJsonObject());
					incidentObjects.add(incident);
				}
				Collections.sort(incidentObjects);
				return incidentObjects;
			}
		} catch (Exception e) {
			Log.w("PDSClient", "Error parsing incident response.");			
		}
		
		return incidentObjects;
	}

	public boolean reportIncident(JsonObject incidentJson) {
		HttpPost incidentRequest = new HttpPost(this.buildAbsoluteApiUrl(INCIDENT_API_URL));
		return postOrPut(incidentRequest, incidentJson.toString());
	}
	
	public boolean reportIncident(String type, String description, Location location) {
		// Note: date, source, and user_reported are filled in automatically
		// date by the default parameter in tastypie, source by our guid, and all reports from the app are user-reported
		PreferencesWrapper prefs = new PreferencesWrapper(this.getContext());
		JsonObject incident = new JsonObject();
		JsonObject locationJson = new JsonObject();
		locationJson.addProperty("lat", location.getLatitude());
		locationJson.addProperty("lng", location.getLongitude());
		locationJson.addProperty("accuracy", location.getAccuracy());
		Date now = new Date(); 
		
		incident.addProperty("type", type);
		incident.addProperty("description", description);
		incident.addProperty("user_reported", true);
		incident.addProperty("source", prefs.getUUID());
		incident.add("location", locationJson);
		incident.addProperty("date", now.getTime() / 1000.00);
		return reportIncident(incident);
	}
}
