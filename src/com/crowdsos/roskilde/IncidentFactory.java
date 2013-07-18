package com.crowdsos.roskilde;

import com.google.gson.JsonObject;

import android.content.Context;


public class IncidentFactory {
	
	private Context mContext;
	
	public IncidentFactory(Context context) {
		mContext = context;
	}
	
	public Incident getIncident(JsonObject incidentJson) {
		Incident incident = new Incident(incidentJson);
		incident.incidentType = getIncidentType(incident.type);
		return incident;
	}	
	
	public IncidentType getIncidentType(String type) {
		if (type.equals(mContext.getString(R.string.assault))) {
			return IncidentType.ASSAULT;
		} else if (type.equals(mContext.getString(R.string.sexual_assault))) {
			return IncidentType.SEXUAL_ASSAULT;
		} else if (type.equals(mContext.getString(R.string.find_me))) {
			return IncidentType.FIND_ME;
		} else if (type.equals(mContext.getString(R.string.buy_sell))) {
			return IncidentType.BUY_SELL;
		} else if (type.equals(mContext.getString(R.string.medical))) {
			return IncidentType.MEDICAL;
		} else if (type.equals(mContext.getString(R.string.overdose))) {
			return IncidentType.OVERDOSE;
		} else if (type.equals(mContext.getString(R.string.lost_found))) {
			return IncidentType.LOST_FOUND;
		} else if (type.equals(mContext.getString(R.string.theft))) {
			return IncidentType.THEFT;
		} 
		
		return IncidentType.OTHER;
	}
}
