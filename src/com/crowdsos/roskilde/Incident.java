package com.crowdsos.roskilde;

import java.util.Date;

import com.google.gson.JsonObject;

public class Incident implements Comparable<Incident> {

	public String type;
	public String description;
	public String source;
	public String id;
	public boolean user_reported;
	public Date date;
	public IncidentLocation location;
	public IncidentType incidentType;
	private JsonObject mIncidentJson;	
	
	public Incident(JsonObject incidentJson) {
		mIncidentJson = incidentJson;
		type = incidentJson.get("type").getAsString();
		description = incidentJson.get("description").getAsString();
		source = incidentJson.get("source").getAsString();
		id = incidentJson.get("_id").getAsString();
		user_reported = incidentJson.get("user_reported").getAsBoolean();
		date = new Date(incidentJson.get("date").getAsBigDecimal().longValue() * 1000L);
		JsonObject locationJson = incidentJson.getAsJsonObject("location");
		location = new IncidentLocation();
		location.lat = locationJson.get("lat").getAsDouble();
		location.lng = locationJson.get("lng").getAsDouble();
		location.accuracy = locationJson.get("accuracy").getAsDouble();
	}
	
	public JsonObject getAsJsonObject() {
		return mIncidentJson;
	}
	
	@Override
	public int compareTo(Incident another) {
		return another.date.compareTo(date);
	}
}
