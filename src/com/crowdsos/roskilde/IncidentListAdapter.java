package com.crowdsos.roskilde;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IncidentListAdapter extends BaseAdapter {

	private Context mContext;
	private List<Incident> mIncidents;
	private List<Incident> mAllIncidents;
	private LayoutInflater mLayoutInflater;
	
	public IncidentListAdapter(Context context) {
		mContext = context;
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mIncidents = mAllIncidents = new ArrayList<Incident>();
	}
	
	public void updateIncidents(List<Incident> incidents) {
		mIncidents = mAllIncidents= incidents;
		notifyDataSetChanged();
	}
	
	public void applyTypeFilter(String type) {
		if (type.equals(mContext.getString(R.string.all_incidents))) {
			mIncidents = mAllIncidents;
		} else {			
			mIncidents = new ArrayList<Incident>();
			for (Incident incident : mAllIncidents) {
				if (incident.type.equals(type)) {
					mIncidents.add(incident);
				}
			}
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return (mIncidents == null) ? 0:mIncidents.size();
	}

	@Override
	public Object getItem(int position) {
		return (mIncidents == null)? new JsonObject() : mIncidents.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout incidentView;
		
		if (convertView == null) {
			incidentView = (RelativeLayout) mLayoutInflater.inflate(R.layout.incident_list_item, parent, false);
		} else {
			incidentView = (RelativeLayout) convertView;
		}
		
		TextView textView = (TextView) incidentView.findViewById(R.id.incident_list_item_description);
		
		final Incident incident = mIncidents.get(position);
		textView.setText(getIncidentSummary(incident));
		incidentView.setBackgroundResource(getIncidentBackgroundId(incident.type));
		//incidentView.setBackground(getIncidentBackground(incident));
		//arrowView.setText(">");
		//imageView.setImageDrawable(getIncidentIcon(incident));
		incidentView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent detailsIntent = new Intent(mContext, IncidentDetailActivity.class);
				detailsIntent.putExtra("incident", incident.getAsJsonObject().toString());
				mContext.startActivity(detailsIntent);
			}
		});
		return incidentView;
	}
	
	private String getIncidentSummary(Incident incident) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM HH:mm", Locale.ENGLISH);
		return incident.type + ", " + dateFormat.format(incident.date);
	}
	
	private Drawable getIncidentBackground(Incident incident) {
		return mContext.getResources().getDrawable(getIncidentBackgroundId(incident.type));
	}
	
	private int getIncidentBackgroundId(String type) {
		if (type.equals(mContext.getString(R.string.assault))) {
			return R.drawable.assault_list_item_background;
		} else if (type.equals(mContext.getString(R.string.medical))){
			return R.drawable.medical_list_item_background;
		} else if (type.equals(mContext.getString(R.string.theft))){
			return R.drawable.theft_list_item_background;
		} else if (type.equals(mContext.getString(R.string.sexual_assault))) {
			return R.drawable.sexual_assault_list_item_background;
		} else if (type.equals(mContext.getString(R.string.buy_sell))) {
			return R.drawable.buysell_list_item_background;
		} else if (type.equals(mContext.getString(R.string.lost_found))) {
			return R.drawable.lostfound_list_item_background;
		} else if (type.equals(mContext.getString(R.string.overdose))) {
			return R.drawable.overdose_list_item_background;
		} else {
			return R.drawable.other_list_item_background;
		}
	}

}
