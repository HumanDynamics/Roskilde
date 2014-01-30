package com.crowdsos.roskilde;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class IncidentDetailActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incident_detail);
		
		try {
			setTheme(R.style.Theme_CrowdSOS_OrangeActionBar);
		} catch (RuntimeException ex) {
			Log.w("IncidentDetailActivity", "Unable to theme action bar");
		}
		
		try {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		} catch (NoSuchMethodError ex) {
			Log.e("IncidentDetailActivity", "Unable to get Action Bar");
		}
		
		if (getIntent().getStringExtra("incident") != null) {
			JsonParser parser = new JsonParser();
			JsonObject incident = parser.parse(getIntent().getStringExtra("incident")).getAsJsonObject();
			
			TextView typeTextView = (TextView)findViewById(R.id.incident_details_type_text_view);
			TextView dateTextView = (TextView)findViewById(R.id.incident_details_date);
			final TextView addressTextView1 = (TextView)findViewById(R.id.incident_details_address1);
			final TextView addressTextView2 = (TextView)findViewById(R.id.incident_details_address2);
			TextView locationTextView = (TextView) findViewById(R.id.incident_details_location);
			EditText descriptionText = (EditText) findViewById(R.id.incident_details_description);
			
			typeTextView.setText(incident.get("type").getAsString());
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a z");
			dateTextView.setText(dateFormat.format(new Date(incident.get("date").getAsBigDecimal().longValue() * 1000L)));
				
			JsonObject latLongJson = incident.get("location").getAsJsonObject();
			final LatLng latLong = new LatLng(latLongJson.get("lat").getAsDouble(), latLongJson.get("lng").getAsDouble());
			
			new Thread() {
				public void run() {
					final List<Address> addresses;
					try {
						Geocoder geocoder = new Geocoder(IncidentDetailActivity.this);
						addresses = geocoder.getFromLocation(latLong.latitude, latLong.longitude, 1);
						if (!addresses.isEmpty()) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Address address = addresses.get(0);
									if (address.getAddressLine(0) != null) {
										addressTextView1.setText(address.getAddressLine(0));
									}
									if (address.getAddressLine(1) != null) {
										addressTextView2.setText(address.getAddressLine(1));
									}
								}
							});
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();


			DecimalFormat latlongFormat = new DecimalFormat("###.##");
			locationTextView.setText("lat/long: (" + latlongFormat.format(latLong.latitude) + "," + latlongFormat.format(latLong.longitude) + ")");
			
			descriptionText.setText(incident.get("description").getAsString());
			
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    	case android.R.id.home:
	    		finish();
	    		return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
