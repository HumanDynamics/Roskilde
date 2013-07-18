package com.crowdsos.roskilde;

public enum IncidentType {
	ASSAULT (R.drawable.assault_marker, R.drawable.assault_header),
	SEXUAL_ASSAULT (R.drawable.sexual_assault_marker, R.drawable.sexual_assault_header), 
	MEDICAL (R.drawable.medical_marker, R.drawable.medical_header), 
	OVERDOSE (R.drawable.overdose_marker, R.drawable.overdose_header),
	FIND_ME (R.drawable.findme_marker, R.drawable.findme_header),
	BUY_SELL (R.drawable.buysell_marker, R.drawable.buysell_header),
	LOST_FOUND (R.drawable.lostfound_marker, R.drawable.lostfound_header),
	THEFT (R.drawable.theft_marker, R.drawable.theft_header),
	OTHER (R.drawable.other_marker, R.drawable.other_header);
	
	private int mMarkerId;
	private int mHeaderId;
	
	private IncidentType(int markerId, int headerId) {
		mMarkerId = markerId;
		mHeaderId = headerId;
	}
	
	public int getMarkerId() {
		return mMarkerId;
	}
	
	public int getHeaderId() {
		return mHeaderId;
	}
}
